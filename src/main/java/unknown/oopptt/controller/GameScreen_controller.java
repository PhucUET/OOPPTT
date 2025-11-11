package unknown.oopptt.controller;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import unknown.oopptt.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.spi.AbstractResourceBundleProvider;
import java.io.File;
import java.util.stream.Stream;

public class GameScreen_controller {

    // =========================================================
    // 1) HẰNG SỐ / TÀI NGUYÊN
    // =========================================================
    private static String background_Video = new File("src/main/resources/graphic/video1.mp4").toURI().toString();
    private static String background_Game  = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static File mapBrick1          = new File("src/main/resources/map/map2.txt");

    final double LOGICAL_WIDTH  = 800;
    final double LOGICAL_HEIGHT = 600;
    final int BRICK_CELL_W = 56;
    final int BRICK_CELL_H = 26;

    private final static int SCNENE_WIDTH = 1440;
    private final static int SCENE_HEIGHT = 810;
    private static final double PADDLE_SPEED = 350.0;

    // =========================================================
    // 2) STATE GAMEPLAY & ĐỐI TƯỢNG CỐT LÕI
    // =========================================================
    private Level level = Level.getInstance();
    private boolean inPaddle = true;
    private boolean isCatch = false;

    List<Brick> gameBricks = new LinkedList<Brick>();
    List<Powerup> gamePowerup = new LinkedList<>();
    private List<Ball> gameBall = new ArrayList<Ball>();
    PowerBall powerBall;
    private SpecialPaddle specialPaddle;
    Sheild sheild = new Sheild();
    Shooter shooter;
    BaseGame baseGame = new BaseGame(this);

    // =========================================================
    // 3) THAM CHIẾU UI (FXML)
    // =========================================================
    @FXML private StackPane stack_root;
    @FXML private MediaView mediaView;
    @FXML private ImageView gameBackground;
    @FXML private Paddle paddleLogic;

    @FXML AnchorPane gamePane;
    @FXML Pane layout_game;
    @FXML Group gameGroup;

    @FXML private StackPane endGameOverlay;
    @FXML private Label scoreLabel;
    @FXML private Button btnEscape, btnRestart, btnNext;

    // =========================================================
    // 4) INPUT STATE (BÀN PHÍM)
    // =========================================================
    private final Set<KeyCode> keys = new HashSet<>();
    private Timeline kbLoop;

    // =========================================================
    // 5) MEDIA PLAYER
    // =========================================================
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer1;

    // =========================================================
    // 6) LIFECYCLE: initialize()
    // =========================================================
    @FXML
    public void initialize() {
        stack_root.setAlignment(Pos.CENTER);
        // setBackground_Video(); // nếu muốn video nều
        level.start();
        set_Background();
        startgameloop();
        setOnKeyboard_Paddle();
        setOnMouse_Paddle();
    }

    // =========================================================
    // 7) KHỞI TẠO NỀN, PADDLE, BALL, SHOOTER, MAP
    // =========================================================
    private void set_Background() {
        Platform.runLater(() -> {
            gameBackground.setImage(new Image(background_Game));

            paddleLogic = new Paddle(
                    gameBackground.getBoundsInParent().getCenterX(),
                    gameBackground.getBoundsInParent().getMaxY() - 20
            );
            layout_game.getChildren().add(paddleLogic.getImageView());
            specialPaddle = new SpecialPaddle(paddleLogic);

            Ball first_ball = new Ball(paddleLogic.getPos_x(), paddleLogic.getPos_y() - 2, 1, 0);
            gameBall.add(first_ball);
            layout_game.getChildren().add(first_ball.getImageView());
            powerBall = new PowerBall(gameBall);

            shooter = new Shooter(200, 2, 1, 3, layout_game, paddleLogic.getImageView(), 20);
            upMap(level.getMap());
            endGameOverlay.setVisible(false);
        });
    }

    // =========================================================
    // 8) MAP & TIỆN ÍCH ĐỌC MAP
    // =========================================================
    private void upMap(File MAP_FILE) {
        final int startX = 0;
        final int startY = 0;

        if (!MAP_FILE.exists() || !MAP_FILE.isFile()) {
            throw new RuntimeException(MAP_FILE + " not found");
        }
        try (Stream<String> lines = Files.lines(MAP_FILE.toPath(), StandardCharsets.UTF_8)) {
            final int[] row = {0};
            lines.forEach(line -> {
                String[] data = line.trim().split("\\s+");
                for (int col = 0; col < data.length; col++) {
                    if (data[col].isEmpty()) continue;
                    int type = safeParse(data[col]);
                    if (type > 0 && type < 10) {
                        int newX = startX + col * BRICK_CELL_W;
                        int newY = startY + row[0] * BRICK_CELL_H;
                        Brick new_Brick = new Brick(newX, newY, type);
                        gameBricks.add(new_Brick);
                        layout_game.getChildren().add(new_Brick.getImageView());
                    }
                }
                row[0]++;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int safeParse(String s) {
        try {
            return Integer.parseInt(s.trim().replaceAll("\\s+", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // =========================================================
    // 9) INPUT: CHUỘT & BÀN PHÍM
    // =========================================================
    private void setOnMouse_Paddle() {
        layout_game.setCursor(Cursor.NONE);
        Platform.runLater(() -> {
            layout_game.setOnMouseClicked(event -> {
                for (Ball ball : gameBall) {
                    ball.setSticky(false);
                }
            });
            layout_game.setOnMouseMoved(event -> {
                int newX = (int) Math.round(
                        Math.max(
                                gameBackground.getBoundsInParent().getMinX() + paddleLogic.getWidth() / 2,
                                Math.min(event.getX() - paddleLogic.getWidth() / 2,
                                        gameBackground.getBoundsInParent().getMaxX()- paddleLogic.getWidth() / 2)
                        )
                );
                paddleLogic.setLocation(newX);
            });
        });
    }

    private void setOnKeyboard_Paddle() {
        layout_game.setFocusTraversable(true);
        Platform.runLater(layout_game::requestFocus);

        layout_game.setOnKeyPressed(e -> {
            keys.add(e.getCode());
            if (e.getCode() == KeyCode.SPACE) {
                for (Ball ball : gameBall) {
                    ball.setSticky(false);
                }
            }
        });

        layout_game.setOnKeyReleased(e -> keys.remove(e.getCode()));

        if (kbLoop != null) kbLoop.stop();
        kbLoop = new Timeline(
                new KeyFrame(Duration.millis(16), ev -> {
                    double vx = 0.0;
                    if (keys.contains(KeyCode.A) || keys.contains(KeyCode.LEFT))  vx -= PADDLE_SPEED;
                    if (keys.contains(KeyCode.D) || keys.contains(KeyCode.RIGHT)) vx += PADDLE_SPEED;

                    if (vx != 0.0) {
                        double dt = 1.0 / 60.0;
                        double currX = paddleLogic.getPos_x();

                        double minLeft = gameBackground.getBoundsInParent().getMinX() + paddleLogic.getWidth() / 2.0;
                        double maxLeft = gameBackground.getBoundsInParent().getMaxX() - paddleLogic.getWidth() / 2.0;

                        double nextX = currX + vx * dt;
                        int newX = (int) Math.round(Math.max(minLeft, Math.min(nextX, maxLeft)));
                        paddleLogic.setLocation(newX);
                    }
                })
        );
        kbLoop.setCycleCount(Animation.INDEFINITE);
        kbLoop.play();
    }

    // =========================================================
    // 10) GAME LOOP & CẬP NHẬT FRAME
    // =========================================================
    private void showEndGameScreen() {
        layout_game.getChildren().remove(paddleLogic.getImageView());
        for(Ball ball : gameBall){
            layout_game.getChildren().remove(ball.getImageView());
        }
        gameBall.clear();
        for(Powerup Pu : gamePowerup){
            layout_game.getChildren().remove(Pu.getImageView());
        }
        gamePowerup.clear();
        Platform.runLater(() -> {
            scoreLabel.setText("Your Score: " + 0);
            endGameOverlay.setVisible(true);
        });

        btnEscape.setOnAction(e -> System.exit(0));
        btnRestart.setOnAction(e ->{ restartLevel();endGameOverlay.setVisible(false);});
        btnNext.setOnAction(e -> {loadNextLevel();endGameOverlay.setVisible(false);});
    }
    public void loadNextLevel() {
        endGameOverlay.setVisible(false);
        level.nextLevel();
        set_Background();

    }
    public void restartLevel() {
        endGameOverlay.setVisible(false);
        set_Background();
    }
    void startgameloop() {
        AnimationTimer timer = new AnimationTimer() {
            private Long lasts = 0L;
            @Override
            public void handle(long now) {
                if (lasts == 0) {
                    lasts = now;
                    return;
                }
                if (shooter.getEnabled()) {
                    shooter.tryFire();
                }
                double dt = (now - lasts) / 1e9;
                lasts = now;

                shooter.update(dt,gameBricks);
                gameBall(dt);
                setGamePowerup();
                if(gameBricks.isEmpty()){
                    showEndGameScreen();
                }
            }
        };
        timer.start();
    }

    // =========================================================
    // 11) CẬP NHẬT BÓNG & POWER-UP
    // =========================================================
    private void gameBall(double dt) {
        if  (gameBall.size() == 0) {
            return;
        }
        for (int i = gameBall.size() - 1; i >= 0; i--) {
            Ball ballLogic = gameBall.get(i);
            if (ballLogic.isSticky()) {
                double newX = paddleLogic.getPos_x() + ballLogic.getPosinPaddle();
                if (newX > paddleLogic.getImageView().getBoundsInParent().getMaxX() ||
                        newX < paddleLogic.getImageView().getBoundsInParent().getMinX()) {
                    newX = Math.max(paddleLogic.getImageView().getBoundsInParent().getMinX(),
                            Math.min(paddleLogic.getImageView().getBoundsInParent().getMaxX(), paddleLogic.getWidth()));
                    ballLogic.updateSpeedX(-ballLogic.getSpeedX());
                }
                ballLogic.setLocation(newX);
                ballLogic.setPosinPaddle();
                continue;
            }

//          if (baseGame.outBall(ballLogic, gameBackground)) {
//              gameBall.remove(i);
//              layout_game.getChildren().remove(ballLogic.getImageView());
//              continue;
//          }
            baseGame.brickCollision(ballLogic, gameBricks, layout_game, gamePowerup);
            baseGame.paddleballCollision(ballLogic, paddleLogic, isCatch);
            baseGame.wallCollision(ballLogic, gameBackground);
            ballLogic.updatePos(dt);
        }
    }

    private void setGamePowerup() {
        for (int i = gamePowerup.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup.get(i);
            if (baseGame.paddlePUCollision(powerup,paddleLogic,"GS")) {
                gamePowerup.remove(powerup);
                layout_game.getChildren().remove(powerup.getImageView());
            } else {
                if (baseGame.outPowerup(powerup, gameBackground)) {
                    gamePowerup.remove(powerup);
                    layout_game.getChildren().remove(powerup.getImageView());
                    continue;
                }
                powerup.movedown();
            }
        }
    }

    // =========================================================
    // 12) API POWER-UPS / PADDLE (PUBLIC)
    // =========================================================
    public void upBall() {powerBall.upBall();}
    public void resetBall() {powerBall.downBall();}
    public void slowBall() {powerBall.slowBall();}
    public void resetSlowBall() {powerBall.normalBall();}
    public void openSheild() {sheild.openSheild(gameBricks, layout_game, gameBackground);}
    public void moreBall() {powerBall.moreBall(layout_game);}
    public void upPaddle() {specialPaddle.upPaddle();}
    public void resetPaddle() {specialPaddle.downPaddle();}

    public void catchBall() { isCatch = !isCatch; }
    public void enableGun() { shooter.setEnabled(true); }
    public void unEnableGun() { shooter.setEnabled(false); }
}
