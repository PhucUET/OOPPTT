package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class GameScreen_controller {

    // === Constants / Assets ==================================================
    private static final String BG_VIDEO_PATH = new File("src/main/resources/graphic/video1.mp4").toURI().toString();
    private static final String BG_IMAGE_PATH = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static final File MAP_FILE = new File("src/main/resources/map/map1.txt");

    // Logical sizes (nếu bạn thực sự cần scale theo StackPane thì có thể dùng sau)
    private static final double LOGICAL_WIDTH  = 800;
    private static final double LOGICAL_HEIGHT = 600;

    // Brick cell size (đang dùng 60x30 trong upMap)
    private static final int BRICK_CELL_W = 60;
    private static final int BRICK_CELL_H = 30;

    // ========================================================================

    private boolean inPaddle = true;
    private final List<Brick> gameBricks = new LinkedList<>();
    private final List<Powerup> gamePowerup = new LinkedList<>();
    private final List<Enemy> gameEnemies = new LinkedList<>();
    private final Sheild sheild = new Sheild();
    private Shooter shooter;

    private final BaseGame baseGame = new BaseGame(this);

    @FXML private StackPane stack_root;
    @FXML private MediaView mediaView;
    @FXML private ImageView gameBackground;
    @FXML private Paddle paddleLogic;

    private final List<Ball> gameBall = new ArrayList<>();
    private PowerBall powerBall;
    private SpecialPaddle specialPaddle;

    @FXML AnchorPane gamePane;
    @FXML Pane layout_game;
    @FXML Group gameGroup;

    private boolean isCatch = false;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer1;

    // dt của frame gần nhất (nếu cần cho handler khác dùng)
    private double udt = 0.0;

    // Cache biên chơi để không gọi getBoundsInParent liên tục
    private double fieldMinX, fieldMaxX, fieldMinY, fieldMaxY;

    // ======================= INIT ============================================
    @FXML
    public void initialize() {
        stack_root.setAlignment(Pos.CENTER);
        // Nếu bạn không dùng video nền thì comment setBackground_Video()
        // setBackground_Video();

        set_Background();      // tạo paddle/ball/shooter + map
        setOnMouse_Paddle();   // input
        startGameLoop();       // game loop
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
    }


    // ======================= SCENE SETUP =====================================
    private void set_Background() {
        // 1) Set image nền
        gameBackground.setImage(new Image(BG_IMAGE_PATH));

        // 2) Tính biên chơi sau khi ImageView có Image
        //    Dùng layout_game size làm gốc; ở đây ta lấy bounds của background ngay thời điểm này
        //    Nếu bạn có scale/resize, nên lắng nghe thay đổi và cập nhật lại bounds.
        Platform.runLater(() -> {
            Bounds b = gameBackground.getBoundsInParent();
            fieldMinX = b.getMinX();
            fieldMaxX = b.getMaxX();
            fieldMinY = b.getMinY();
            fieldMaxY = b.getMaxY();
        });

        // 3) Paddle
        paddleLogic = new Paddle(
                gameBackground.getBoundsInParent().getCenterX(),
                gameBackground.getBoundsInParent().getMaxY() - 30
        );
        layout_game.getChildren().add(paddleLogic.getImageView());
        specialPaddle = new SpecialPaddle(paddleLogic);

        // 4) Ball
        Ball first_ball = new Ball(paddleLogic.getPos_x(), paddleLogic.getPos_y() - 5, 0.3, 0, layout_game);
        gameBall.add(first_ball);
        //System.out.println(first_ball.getImageView().getBoundsInParent().getWidth() + " " + first_ball.getImageView().getBoundsInParent().getHeight());
        layout_game.getChildren().add(first_ball.getImageView());
        powerBall = new PowerBall(gameBall);

        // 5) Shooter
        shooter = new Shooter(200, 2, 1, 3, layout_game, paddleLogic.getImageView(), 20);

        // 6) Map
        upMap();

    }

    // ======================= MAP LOAD (OPTIMIZED) ============================
    private void upMap() {
        final int startX = 0;
        final int startY = 0;

        if (!MAP_FILE.exists() || !MAP_FILE.isFile()) {
            throw new RuntimeException("Map file not found: " + MAP_FILE.getAbsolutePath());
        }

        try (Stream<String> lines = Files.lines(MAP_FILE.toPath(), StandardCharsets.UTF_8)) {
            final int[] row = {0};
            lines.forEach(line -> {
                // Chuẩn hóa: tách theo mọi khoảng trắng (tab, space...)
                String[] data = line.trim().split("\\s+");
                for (int col = 0; col < data.length; col++) {
                    if (data[col].isEmpty()) continue;
                    int type = safeParse(data[col]); // tránh NumberFormatException
                    if (type > 0) {
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
        ShooterEnemy meow = new ShooterEnemy(250, 300, 50,50,layout_game, paddleLogic.getImageView());
        gameEnemies.add(meow);
        layout_game.getChildren().add(meow.getImageView());
    }

    private int safeParse(String s) {
        try {
            // Xử lý chuỗi bẩn kiểu "1   0" hoặc chứa ký tự trắng
            return Integer.parseInt(s.trim().replaceAll("\\s+", ""));
        } catch (NumberFormatException e) {
            // Nếu thật sự là 2 số dính trong 1 token thì bạn cần quy ước map rõ ràng hơn
            // Ở đây mặc định về 0 để không crash
            return 0;
        }
    }

    // ======================= INPUT (PADDLE) ==================================
    private void setOnMouse_Paddle() {
        layout_game.setCursor(Cursor.NONE);

        // Click: thả bóng (tắt sticky)
        layout_game.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                for (Ball ball : gameBall) {
                    ball.setSticky(false);
                }
            }
        });

        // Move: cập nhật vị trí paddle mượt (dùng clamp với cache bounds)
        layout_game.setOnMouseMoved(event -> {
            final double halfW = paddleLogic.getWidth() / 2.0;

            // Nếu bounds chưa có (vừa khởi tạo), fallback sang bounds hiện tại
            double minX = fieldMinX;
            double maxX = fieldMaxX;
            if (maxX <= minX) {
                Bounds b = gameBackground.getBoundsInParent();
                minX = b.getMinX();
                maxX = b.getMaxX();
            }

            double target = clamp(event.getX() - halfW, minX + halfW, maxX - halfW);
            paddleLogic.setLocation(target, udt);
        });
    }

    private static double clamp(double v, double lo, double hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

    // ======================= GAME LOOP =======================================
    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastNs = 0L;

            @Override
            public void handle(long now) {
                if (lastNs == 0L) {
                    lastNs = now;
                    return;
                }

                double dt = (now - lastNs) / 1e9; // giây
                lastNs = now;

                if (dt < 0.1) {
                    gameBricksUp(dt);
                }

                update(dt);                // cập nhật state chung (udt, animation phụ...)
                if (shooter.getEnabled()) {
                    shooter.tryFire();
                }
                shooter.update(dt, gameBricks);
                gameBall(dt);
                enemyGame(dt);
                setGamePowerup();
            }
        };
        timer.start();
    }

    private void update(double dt) {
        udt = dt; // nếu Paddle/Ball cần dùng dt từ nơi khác
    }

    private boolean shouldDrop(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    private void gameBricksUp(double dt) {
        for (int i = gameBricks.size() - 1; i >= 0; i--) {
            Brick brick = gameBricks.get(i);
            if (brick.isWait()) {
                if (brick.dropBrick(dt)) {
                    layout_game.getChildren().remove(brick.getImageView());
                    gameBricks.remove(i);
                    if (shouldDrop(0.5)) {
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
            if (enemy.getHp() == 0) {
                layout_game.getChildren().remove(enemy.getImageView());
                gameEnemies.remove(i);
            }
            if (enemy instanceof ShooterEnemy) {
                ShooterEnemy shooterEnemy = (ShooterEnemy) enemy;
                shooterEnemy.update(dt);
            }
            if (enemy instanceof BasicEnemy) {
                BasicEnemy  basicEnemy = (BasicEnemy) enemy;
                basicEnemy.update(dt);
            }

        }
    }

    private void gameBall(double dt) {
        if (gameBall.isEmpty()) return;

        for (int i = gameBall.size() - 1; i >= 0; i--) {
            Ball ballLogic = gameBall.get(i);
            if (ballLogic.isSticky()) {
                double newX = paddleLogic.getPos_x() + ballLogic.getOffsetOnPaddle();

                Bounds pb = paddleLogic.getImageView().getBoundsInParent();
                if (newX > pb.getMaxX() || newX < pb.getMinX()) {
                    newX = clamp(newX, pb.getMinX(), pb.getMaxX());
                    ballLogic.setSpeedX(-ballLogic.getSpeedX());
                }

                ballLogic.setLocation(newX, dt);
                ballLogic.addOffsetOnPaddle(dt);
                continue;
            }
            //System.out.println(ballLogic.getImageView().getBoundsInParent().getMinX() + " " + ballLogic.getImageView().getBoundsInParent().getMinX() + " " + ballLogic.getImageView().getBoundsInParent().getWidth() + " " + ballLogic.getImageView().getBoundsInParent().getHeight());
            if (baseGame.outBall(ballLogic.getImageView().getBoundsInParent(), gameBackground)) {
                gameBall.remove(i);
                layout_game.getChildren().remove(ballLogic.getImageView());
                continue;
            }

            baseGame.brickCollision(ballLogic, gameBricks);
            baseGame.paddleballCollision(ballLogic, paddleLogic, isCatch);
            baseGame.enemyCollision(ballLogic,gameEnemies);
            baseGame.wallCollision(ballLogic, gameBackground);

            ballLogic.updatePos(dt);
        }

    }

    private void setGamePowerup() {
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
            pu.movedown();
        }
    }

    // ======================= POWER / CONTROL API =============================
    public void upBall()             { powerBall.upBall(); }
    public void resetBall()          { powerBall.downBall(); }
    public void slowBall()           { powerBall.slowBall(); }
    public void resetSlowBall()      { powerBall.normalBall(); }
    public void openSheild()         { sheild.openSheild(gameBricks, layout_game, gameBackground); }
    public void moreBall()           { powerBall.moreBall(layout_game); }

    public void upPaddle() {
        if (paddleLogic.getWidth() * 2 <= gameBackground.getBoundsInParent().getWidth() / 2.0)  {
            specialPaddle.upPaddle();
        }
    }

    public void resetPaddle()        { specialPaddle.downPaddle(); }
    public void catchBall()          { isCatch = !isCatch; }
    public void enableGun()          { shooter.setEnabled(true); }
    public void unEnableGun()        { shooter.setEnabled(false); }
}
