package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.Window;
import unknown.oopptt.api.*;
import unknown.oopptt.api.ball.Ball;
import unknown.oopptt.api.ball.PowerBall;
import unknown.oopptt.api.enemy.BasicEnemy;
import unknown.oopptt.api.enemy.BossEnemy;
import unknown.oopptt.api.enemy.Enemy;
import unknown.oopptt.api.enemy.ShooterEnemy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.animation.AnimationTimer;


public class Game_Screen_Controller {
    private int BRICK_CELL_W = 50;
    private int BRICK_CELL_H = 23;

    private static final String BG_IMAGE_PATH = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static final File MAP_FILE = new File("src/main/resources/map/map1.txt");

    private final List<Brick> gameBricks = new LinkedList<>();
    private Shooter shooter;
    //    private Level level = Level.getInstance();
    private BaseGame baseGame;


    @FXML
    private StackPane stack_root;
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView gameBackground;
    @FXML
    Label scoreLabel;
    @FXML
    Label scoreLabel1;
    @FXML
    Button btnRestart, btnNext, btnEscape;
    @FXML
    Button btnEscape1;
    @FXML
    private StackPane endGameOverlay;
    @FXML
    StackPane gameOver;
    private Paddle paddleLogic;
    @FXML
    private Group bgr;
    private String ngusi = new File("src/main/resources/graphic/B3-Pale").toString();
    private final List<Ball> gameBall = new ArrayList<>();
    private final List<Enemy> gameEnemies = new ArrayList<>();
    private final List<Powerup> gamePowerup = new ArrayList<>();
    private PowerBall powerBall;
    private SpecialPaddle specialPaddle;
    private ParallaxBackground bg = new ParallaxBackground(500, 640, ngusi);
    private Sheild shield;
    private NowPlay player = new NowPlay();
    private Data data = new Data();
    private Level level = Level.getInstance();

    @FXML
    Pane layout_game;

    private boolean isCatch = false;


    private boolean isSpam = false;

    @FXML
    public void initialize() {
        stack_root.setAlignment(Pos.CENTER);
        level.start();
        preloadAssets();
        stack_root.getChildren().add(0, bg.getRoot());
        stack_root.setAlignment(Pos.CENTER);
        setBackground(BG_IMAGE_PATH, MAP_FILE);
        ListenEventHandle();
        startGameloop();
        addHighScoreButtonOverlay();
    }

    public void setData(Data data) {
        this.data = data;
        player.addPlayer(data.getNamePlayer());
        baseGame = new BaseGame(this);
    }

    private void preloadAssets() {
        ImageCache.loadFolder("src/main/resources/graphic/Brick1");
        ImageCache.loadFolder("src/main/resources/graphic/Brick2");
        ImageCache.loadFolder("src/main/resources/graphic/Brick3");
        ImageCache.loadFolder("src/main/resources/graphic/EnemyIdle");
        ImageCache.loadFolder("src/main/resources/graphic/EnemyMove");
        ImageCache.loadFolder("src/main/resources/graphic/dropbrick1");
        ImageCache.loadFolder("src/main/resources/graphic/dropbrick2");
        ImageCache.loadFolder("src/main/resources/graphic/dropbrick3");
        ImageCache.loadFolder("src/main/resources/graphic/Plasma_ball_cycle");
        ImageCache.loadFolder("src/main/resources/graphic/Boss");
        ImageCache.loadFolder("src/main/resources/graphic/Shooter");
        ImageCache.loadFolder("src/main/resources/graphic/paddle");
        ImageCache.loadFolder("src/main/resources/graphic/PU/Anti-speed");
        ImageCache.loadFolder("src/main/resources/graphic/PU/attackx2");
        ImageCache.loadFolder("src/main/resources/graphic/PU/Defence");
        ImageCache.loadFolder("src/main/resources/graphic/PU/attackx3");
        ImageCache.loadFolder("src/main/resources/graphic/PU/HPx2");
        ImageCache.loadFolder("src/main/resources/graphic/PU/Immortality");
        ImageCache.loadFolder("src/main/resources/graphic/PU/magnet");
        ImageCache.loadFolder("src/main/resources/graphic/PU/Shield");

    }

    private void setBackground(String backgroundPath, File MAP_FILE) {
        //gameBackground.setImage(new Image(backgroundPath));

//        if(endGameOverlay.isVisible() == false) {
        paddleLogic = new Paddle(gameBackground.getBoundsInParent().getCenterX(),
                gameBackground.getBoundsInParent().getMaxY() - 40, gameBackground);

        layout_game.getChildren().add(paddleLogic.getImageView());
        specialPaddle = new SpecialPaddle(paddleLogic);

        Ball first_ball = new Ball(paddleLogic.getPos_x(),
                paddleLogic.getPos_y() - 5, 0.3, 0, layout_game);

        gameBall.add(first_ball);

        layout_game.getChildren().add(first_ball.getImageView());
        powerBall = new PowerBall(gameBall);

        shooter = new Shooter(200, 3, 60, 60, layout_game, paddleLogic.getImageView(), 20);

        shield = new Sheild(layout_game);
        upMap(MAP_FILE);


//            Platform.runLater(() -> {
//                BossEnemy bossEnemy = new BossEnemy(250, 300, 100, 100, 20, paddleLogic, this, layout_game);
//                gameEnemies.add(bossEnemy);
//                layout_game.getChildren().add(bossEnemy.getImageView());
//            });

        //  }
    }

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
                    if (type >= 10) {
                        type = type - 10;
                        int newX = startX + col * BRICK_CELL_W;
                        int newY = startY + row[0] * BRICK_CELL_H;
                        Enemy new_Enemy = null;
                        switch (type) {
                            case 0:
                                new_Enemy = new BasicEnemy(newX, newY, 30, 30);
                            case 2:
                                new_Enemy = new BossEnemy(newX, newY, 40, 40, 60, paddleLogic, this, layout_game);
                            case 1:
                                new_Enemy = new ShooterEnemy(newX, newY, 30, 30, layout_game, paddleLogic.getImageView(), paddleLogic);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + type);
                        }

                        if (new_Enemy != null) {
                            gameEnemies.add(new_Enemy);
                            layout_game.getChildren().add(new_Enemy.getImageView());
                        }
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

    public void setBallmove() {
        for (Ball ball : gameBall) {
            ball.setSticky(false);
        }
    }

    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT -> paddleLogic.setLeftHeld(true);
            case D, RIGHT -> paddleLogic.setRightHeld(true);
            case SHIFT -> paddleLogic.setMoveSpeed(600);
            case SPACE -> setBallmove();
            case P -> pauseGame();
        }
    }

    private void onKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT -> paddleLogic.setLeftHeld(false);
            case D, RIGHT -> paddleLogic.setRightHeld(false);
            case SHIFT -> paddleLogic.setMoveSpeed(500);
        }
    }

    private void ListenEventHandle() {
        layout_game.setCursor(Cursor.NONE);
        layout_game.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
                newValue.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);
                layout_game.requestFocus();
            }
        });
    }

    private void showEndGame() {
        layout_game.getChildren().remove(paddleLogic.getImageView());
        for (Ball ball : gameBall) {
            layout_game.getChildren().remove(ball.getImageView());
        }
        gameBall.clear();
        for (Powerup Pu : gamePowerup) {
            layout_game.getChildren().remove(Pu.getImageView());
        }
        for (Brick brick : gameBricks) {
            layout_game.getChildren().remove(brick.getImageView());
        }
        gameBricks.clear();
        gamePowerup.clear();
        scoreLabel1.setText("Your Score: " + player.getPoints(data.getNamePlayer()));
        gameOver.setVisible(true);
        btnEscape1.setOnAction(e -> {
            openHome();
        });
    }

    @FXML
    private void openHome() {
        try {
            // Load màn hình MenuRotate
            Parent menuRoot = FXMLLoader.load(getClass().getResource("/unknown/oopptt/MenuRotate.fxml"));

            // Nếu đang nằm trong một Scene: chỉ cần thay root để "xóa" màn hiện tại
            if (stack_root != null && stack_root.getScene() != null) {
                stack_root.getScene().setRoot(menuRoot);
            } else {
                // Dự phòng: chưa có Scene -> mở Stage mới
                Stage stage = new Stage();
                stage.setScene(new Scene(menuRoot));
                stage.setTitle("Menu");
                stage.show();

                // Đóng cửa sổ hiện tại nếu đang chạy độc lập
                closeWindowIfStandalone();
            }
        } catch (IOException e) {
            e.printStackTrace(); // hoặc log ra logger của bạn
        }
    }

    private void closeWindowIfStandalone() {
        if (stack_root != null && stack_root.getScene() != null) {
            Window w = stack_root.getScene().getWindow();
            if (w != null) {
                w.hide();
            }
        }
    }

    private void showEndGameScreen() {
        data.saveScore(data.getNamePlayer(), player.getPoints(data.getNamePlayer()), player.getPoints(data.getNamePlayer()), "SOLO");
        layout_game.getChildren().remove(paddleLogic.getImageView());
        for (Ball ball : gameBall) {
            layout_game.getChildren().remove(ball.getImageView());
        }
        gameBall.clear();
        for (Brick brick : gameBricks) {
            layout_game.getChildren().remove(brick.getImageView());
        }
        gameBricks.clear();
        for (Powerup Pu : gamePowerup) {
            layout_game.getChildren().remove(Pu.getImageView());
        }
        gamePowerup.clear();

        scoreLabel.setText("Your Score: " + player.getPoints(data.getNamePlayer()));
        endGameOverlay.setVisible(true);
        btnEscape.setOnAction(e -> System.exit(0));
        btnRestart.setOnAction(e -> {
            restartLevel();
            endGameOverlay.setVisible(false);
        });
        btnNext.setOnAction(e -> {
            loadNextLevel();
            endGameOverlay.setVisible(false);
        });
    }

    public void restartGame() {
        gameOver.setVisible(false);
        level.start();
        setBackground(level.getBackground(), level.getMap());
    }

    public void loadNextLevel() {
        endGameOverlay.setVisible(false);
        level.nextLevel();
        setBackground(level.getBackground(), level.getMap());
        bg.reset(level.getBackground());

    }

    public void restartLevel() {
        endGameOverlay.setVisible(false);
        setBackground(level.getBackground(), level.getMap());
    }


    // ================= gamel==================
    private static final double TARGET_FPS = 60;
    private static final double STEP = 1.0 / TARGET_FPS;

    private AnimationTimer gameLoop;
    private boolean paused = false;

    private void startGameloop() {
        gameLoop = new AnimationTimer() {
            private long lastTime = 0L;
            private double accumulator = 0.0;

            @Override
            public void handle(long now) {
                if (paused) return; // nếu đang pause thì bỏ qua cập nhật

                if (lastTime == 0L) {
                    lastTime = now;
                    return;
                }
                double dt = (now - lastTime) / 1e9;
                if (dt > 0.25) dt = 0.25;
                lastTime = now;
                accumulator += dt;

                if (accumulator > STEP) {
                    if (paddleLogic.getDead()) {
                        paddleLogic.setDead(false);
                        for (int i = gameBall.size() - 1; i >= 0; i--) {
                            Ball ball = gameBall.get(i);
                            layout_game.getChildren().remove(ball.getImageView());
                            gameBall.remove(i);
                        }
                    }
                    gameBall(STEP);
                    paddleLogic.update(STEP);
                    gameBricksUp(STEP);
                    setGamePowerup(STEP);
                    enemyGame(STEP);
                    bg.update(STEP);
                    shield.update(STEP);
                    accumulator -= STEP;
                    specialPaddle.applyAllEffects();
                }
                if (shooter.getEnabled()) shooter.tryFire();
                if (gameBricks.isEmpty() && player.getAlive(data.getNamePlayer()) > 0) {
                    showEndGameScreen();
                }
                if (player.getAlive(data.getNamePlayer()) == 0) {
                    showEndGame();
                }
            }
        };
        gameLoop.start();
    }

    public void pauseGame() {
        if (paused) return;
        paused = true;

        if (layout_game != null && layout_game.getScene() != null) {
            try {
                Scene scene = layout_game.getScene();

                // Lưu root & controller để Continue trả lại màn game và resume loop
                scene.getProperties().put("previousRoot", scene.getRoot());
                scene.getProperties().put("gameController", this);

                FXMLLoader fx = new FXMLLoader(getClass().getResource("/unknown/oopptt/PauseScreen.fxml"));

                Parent pauseRoot = fx.load();
                PauseScreenController controller = fx.getController();
                controller.setData(data);
                scene.setRoot(pauseRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (gameLoop != null) gameLoop.stop();
    }

    public void resumeGame() {
        paused = false;
        if (gameLoop != null) gameLoop.start();
        // lấy lại focus để nhận phím
        if (layout_game != null) layout_game.requestFocus();
    }

    private static double clamp(double v, double lo, double hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

    private void gameBricksUp(double dt) {
        for (int i = gameBricks.size() - 1; i >= 0; i--) {
            Brick brick = gameBricks.get(i);
            if (brick.isWait()) {
                if (brick.dropBrick(dt)) {
                    layout_game.getChildren().remove(brick.getImageView());
                    gameBricks.remove(i);
                    if (shouldDrop(0.3)) {
                        Powerup p = new Powerup(
                                brick.getImageView().getBoundsInParent().getCenterX(), brick.getImageView().getBoundsInParent().getCenterY());
                        layout_game.getChildren().add(p.getImageView());
                        gamePowerup.add(p);
                    }
                }
            }
            brick.updateAnimation(dt);
        }
    }

    private void enemyGame(double dt) {
        for (int i = gameEnemies.size() - 1; i >= 0; i--) {
            Enemy enemy = gameEnemies.get(i);

            if (enemy instanceof ShooterEnemy) {
                ShooterEnemy shooterEnemy = (ShooterEnemy) enemy;

                if (shooterEnemy.getHp() == 0) {
                    layout_game.getChildren().remove(shooterEnemy.getImageView());
                    shooterEnemy.clearShooter();
                    gameEnemies.remove(i);
                }
                shooterEnemy.update(dt);
            }
            if (enemy instanceof BasicEnemy) {
                if (enemy.getHp() == 0) {
                    layout_game.getChildren().remove(enemy.getImageView());
                    gameEnemies.remove(i);
                }
                BasicEnemy basicEnemy = (BasicEnemy) enemy;
                basicEnemy.update(dt);
            }

            if (enemy instanceof BossEnemy) {
                BossEnemy bossEnemy = (BossEnemy) enemy;
                if (enemy.getHp() == 0) {
                    layout_game.getChildren().remove(enemy.getImageView());
                    bossEnemy.clearShooter();
                    gameEnemies.remove(i);
                }
                bossEnemy.update(dt);
            }

        }
    }

    private void gameBall(double dt) {

        if (gameBall.isEmpty()) {
            if (player.getAlive(data.getNamePlayer()) > 0) {
                Ball newBall = new Ball(paddleLogic.getPos_x(),
                        paddleLogic.getPos_y() - 5, 0.3, 0, layout_game);
                gameBall.add(newBall);
                layout_game.getChildren().add(newBall.getImageView());
                player.subLive(data.getNamePlayer());
            }
        }

        for (int i = gameBall.size() - 1; i >= 0; i--) {
            Ball ballLogic = gameBall.get(i);
            if (ballLogic.isSticky()) {
                double newX = paddleLogic.getPos_x() + ballLogic.getOffsetOnPaddle();

                Bounds pb = paddleLogic.getImageView().getBoundsInParent();
                if (newX >= pb.getMaxX() || newX <= pb.getMinX()) {
                    newX = clamp(newX, pb.getMinX(), pb.getMaxX());
                    ballLogic.setSpeedX(-ballLogic.getSpeedX());
                }

                ballLogic.setLocation(newX, dt);
                ballLogic.addOffsetOnPaddle(dt);
                continue;
            }
            if (baseGame.outBall(ballLogic.getImageView().getBoundsInParent(), gameBackground)) {
                gameBall.remove(i);
                layout_game.getChildren().remove(ballLogic.getImageView());
                continue;
            }

            baseGame.brickCollision(ballLogic, gameBricks);
            baseGame.paddleballCollision(ballLogic, paddleLogic, isCatch);
            baseGame.enemyCollision(ballLogic, gameEnemies);
            baseGame.wallCollision(ballLogic, gameBackground);

            ballLogic.update(dt);
        }

    }

    private void setGamePowerup(double dt) {
        for (int i = gamePowerup.size() - 1; i >= 0; i--) {
            Powerup pu = gamePowerup.get(i);
            if (baseGame.paddlePUCollision(pu, paddleLogic)) {
                gamePowerup.remove(i);
                layout_game.getChildren().remove(pu.getImageView());
                continue;
            }
            if (baseGame.outPowerup(pu, gameBackground)) {
                gamePowerup.remove(i);
                layout_game.getChildren().remove(pu.getImageView());
                continue;
            }
            pu.movedown(dt);
        }
    }

    private boolean shouldDrop(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    public NowPlay getPlayer() {
        return player;
    }

    public void setPlayer(NowPlay player) {
        this.player = player;
    }

    public Data getData() {
        return data;
    }

    public void upBall() {
        powerBall.upBall();
    }

    public void resetBall() {
        powerBall.downBall();
    }

    public void slowBall() {
        powerBall.slowBall();
    }

    public void resetSlowBall() {
        powerBall.normalBall();
    }

    public void moreBall() {
        powerBall.moreBall(layout_game);
    }

    public void upPaddle() {
        if (paddleLogic.getWidth() * 2 <= gameBackground.getBoundsInParent().getWidth() / 2.0) {
            specialPaddle.setBig();
        }
    }

    public void offUpPaddle() {
        specialPaddle.setNormalSize();
        for (Ball balLogic : gameBall) {
            balLogic.setOffsetOnPaddle(balLogic.getOffsetOnPaddle() / 2);
        }
    }

    public void slowPaddle() {
        specialPaddle.setSlow();
    }

    public void offslowPaddle() {
        specialPaddle.setNormalSpeed();
    }

    public void downPaddle() {
        specialPaddle.setSmall();
    }

    public void offdownPaddle() {
        specialPaddle.setNormalSize();
    }

    public void fastPaddle() {
        specialPaddle.setFast();
    }

    public void createMinions() {

    }

    public void setCreateMinions() {
        isSpam = true;
    }

    public void setOffCreateMinions() {
        isSpam = false;
    }

    public void setRedirPaddle() {
        specialPaddle.setRedir(true);
    }

    public void offRedirPaddle() {
        specialPaddle.setRedir(false);
    }

    public void createFakePU() {
        Powerup newPU = new Powerup(gameBackground.getBoundsInParent().getCenterX() / 2, 0, Powerup.PowerupType.REDIR);
    }

    public void catchBall() {
        isCatch = !isCatch;
    }

    public void enableGun() {
        shooter.setEnabled(true);
    }

    public void unEnableGun() {
        shooter.setEnabled(false);
    }

    public void setShieldOn() {
        shield.setOpenShield(true);
    }

    public void upHp() {
        player.addLive(data.getNamePlayer());
    }

    // Mở HighScore.fxml và thay thế màn hiện tại
    private void openHighScore() {
        try {
            Parent highScoreRoot = FXMLLoader.load(
                    getClass().getResource("/unknown/oopptt/HighScore.fxml"));

            if (stack_root != null && stack_root.getScene() != null) {
                stack_root.getScene().setRoot(highScoreRoot);
                highScoreRoot.requestFocus();
            } else {
                Stage stage = new Stage();
                stage.setTitle("High Score");
                stage.setScene(new Scene(highScoreRoot, 960, 540));
                stage.show();
                closeWindowIfStandalone();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // imports cần: import javafx.geometry.Insets; import javafx.geometry.Pos;

    private void addHighScoreButtonOverlay() {
        Button hsBtn = new Button("HIGH SCORE");
        hsBtn.setFocusTraversable(false);
        hsBtn.setOnAction(e -> openHighScore());
        hsBtn.setStyle("""
        -fx-background-color: rgba(0,0,0,0.55);
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-padding: 6 12 6 12;
    """);

        // Đặt ở góc TRÁI TRÊN
        StackPane.setAlignment(hsBtn, Pos.TOP_LEFT);
        StackPane.setMargin(hsBtn, new Insets(12, 0, 0, 12)); // top=12, left=12

        stack_root.getChildren().add(hsBtn);
    }
}


