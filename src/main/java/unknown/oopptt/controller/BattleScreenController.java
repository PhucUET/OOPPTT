package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import unknown.oopptt.api.*;
import unknown.oopptt.net.client.GameClient;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * BattleScreenController
 * Quản lý 2 màn hình người chơi (Player1 vs Player2) cho chế độ LAN.
 * Gồm logic game, cập nhật trạng thái, power-up, và xử lý truyền dữ liệu mạng.
 */
public class BattleScreenController {

    // =============================================================
    // 1️⃣ THUỘC TÍNH & THAM SỐ
    // =============================================================
    private static final String background_Game =
            new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static final File mapBrick = new File("src/main/resources/map/map1.txt");
    public Object startGameLoop;

    private GameClient client;
    private String playerName = "Trung";
    private String serverIP;
    private final int serverPort = 5000;

    private boolean stillStick = true;
    private boolean isCatch1 = false, isCatch2 = false;
    private boolean inPaddle1 = true, inPaddle2 = true;

    private int move_Of_Paddle;
    private int ballStick;

    // --- Keyboard state ---
    private final java.util.Set<javafx.scene.input.KeyCode> keys = new java.util.HashSet<>();
    private javafx.animation.Timeline kbLoop;

    // Player 1
    private List<Brick> gameBricks1 = new LinkedList<>();
    private List<Ball> gameBall1 = new ArrayList<>();
    private List<Powerup> gamePowerup1 = new LinkedList<>();
    private Paddle paddleLogic1;
    private PowerBall powerBall1;
    private SpecialPaddle specialPaddle1;
    private BaseGame baseGame1 = new BaseGame(this);
    private Sheild sheild1 = new Sheild();
    private Shooter shooter1;
    // --- Tốc độ paddle khi dùng phím (px/s) ---
    private static final double PADDLE_SPEED = 350.0;

    // Player 2
    private List<Brick> gameBricks2 = new LinkedList<>();
    private List<Ball> gameBall2 = new ArrayList<>();
    private List<Powerup> gamePowerup2 = new LinkedList<>();
    private Paddle paddleLogic2;
    private PowerBall powerBall2;
    private SpecialPaddle specialPaddle2;
    private BaseGame baseGame2 = new BaseGame(this);
    private Sheild sheild2 = new Sheild();
    private Shooter shooter2;

    // =============================================================
    // 2️⃣ FXML THUỘC TÍNH
    // =============================================================
    @FXML private StackPane stack_root;
    @FXML private SplitPane split_pane;
    @FXML private AnchorPane player_1;
    @FXML private ImageView screenP1;
    @FXML private AnchorPane player_2;
    @FXML private ImageView screenP2;
    @FXML private ImageView Data_Sheet_P1;
    @FXML private ImageView Data_Sheet_P2;

    // =============================================================
    // 3️⃣ LIFECYCLE
    // =============================================================
    @FXML
    public void initialize() {
        connectToServer();
        set_Player1_BackGround();
        set_Player2_BackGround();
        upMap();
        startGameLoop();
        setOnKeyboard_Paddle();
        setOnMouse_Paddle();
    }

    public void onClose() {
        if (client != null) client.close();
    }

    // =============================================================
    // 4️⃣ NETWORK
    // =============================================================

    private void connectToServer() {
        client = new GameClient(client.findServerIP(), serverPort, playerName, msg -> {
            Platform.runLater(() -> {
                System.out.println("Server: " + msg);
                switch (msg.charAt(0)) {
                    case 'M' -> setMove_Of_Paddle(Integer.parseInt(msg.substring(5)));
                    case 'B' -> setBallStick(Integer.parseInt(msg.substring(10)));
                    case 'S' -> setStillStick(Boolean.parseBoolean(msg.substring(6)));
                }
            });
        });
    }

    // =============================================================
    // 5️⃣ SETUP MÀN HÌNH NGƯỜI CHƠI
    // =============================================================
    private void set_Player1_BackGround() {
        Platform.runLater(() -> {
            screenP1.setImage(new Image(background_Game));
            paddleLogic1 = new Paddle(screenP1.getBoundsInParent().getCenterX(),
                    screenP1.getBoundsInParent().getMaxY() - 20);
            player_1.getChildren().add(paddleLogic1.getImageView());
            specialPaddle1 = new SpecialPaddle(paddleLogic1);

            Ball first_ball = new Ball(paddleLogic1.getPos_x(), paddleLogic1.getPos_y() - 2, 1, 0);
            setBallStick((int) paddleLogic1.getPos_x());
            gameBall1.add(first_ball);
            player_1.getChildren().add(first_ball.getImageView());
            powerBall1 = new PowerBall(gameBall1);
            shooter1 = new Shooter(200, 2, 1, 3, player_1, paddleLogic1.getImageView(), 20);
        });
    }

    private void set_Player2_BackGround() {
        Platform.runLater(() -> {
            screenP2.setImage(new Image(background_Game));
            paddleLogic2 = new Paddle(screenP2.getBoundsInParent().getCenterX(),
                    screenP2.getBoundsInParent().getMaxY() - 20);
            player_2.getChildren().add(paddleLogic2.getImageView());
            specialPaddle2 = new SpecialPaddle(paddleLogic2);
            setMove_Of_Paddle((int) screenP2.getBoundsInParent().getCenterX());

            Ball first_ball = new Ball(paddleLogic2.getPos_x(), paddleLogic2.getPos_y() - 2, 1, 0);
            gameBall2.add(first_ball);
            player_2.getChildren().add(first_ball.getImageView());
            powerBall2 = new PowerBall(gameBall2);
            shooter2 = new Shooter(200, 2, 1, 3, player_2, paddleLogic2.getImageView(), 20);
        });
    }

    private void upMap() {
        int startX = 175, startY = 200;
        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick))) {
            String line; int row = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                for (int col = 0; col < data.length; col++) {
                    int type = Integer.parseInt(data[col]);
                    if (type > 0) {
                        int newX = startX + col * 33;
                        int newY = startY + row * 25;
                        Brick new_Brick = new Brick(newX, newY, type);
                        gameBricks1.add(new_Brick);
                        player_1.getChildren().add(new_Brick.getImageView());
                        Brick new_Brick2 = new Brick(newX, newY, type);
                        gameBricks2.add(new_Brick2);
                        player_2.getChildren().add(new_Brick2.getImageView());
                    }
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =============================================================
    // 6️⃣ ĐIỀU KHIỂN PADDLE (CHUỘT)
    // =============================================================
    private void setOnMouse_Paddle() {
        player_1.setCursor(Cursor.NONE);
        Platform.runLater(() -> {
            player_1.setOnMouseClicked(event -> {
                for (Ball ball : gameBall1) {
                    ball.setSticky(false);
                    client.send("Stick:" + ball.isSticky());
                }
            });

            player_1.setOnMouseMoved(event -> {
                int newX = (int) Math.round(
                        Math.max(
                                screenP1.getBoundsInParent().getMinX() + paddleLogic1.getWidth() / 2,
                                Math.min(event.getX() - paddleLogic1.getWidth() / 2,
                                        screenP1.getBoundsInParent().getMaxX() - paddleLogic1.getWidth() / 2)
                        )
                );
                paddleLogic1.setLocation(newX);
                client.send("Move:" + newX);
            });
        });
    }
    private void setOnKeyboard_Paddle() {
        // đảm bảo node nhận focus để bắt phím
        player_1.setFocusTraversable(true);
        Platform.runLater(player_1::requestFocus);

        // Lắng nghe phím nhấn/thả
        player_1.setOnKeyPressed(e -> {
            keys.add(e.getCode());






            // Space: thả bóng khỏi sticky (giống click chuột)
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                for (Ball ball : gameBall1) {
                    ball.setSticky(false);
                    client.send("Stick:" + ball.isSticky());
                }
            }
        });

        player_1.setOnKeyReleased(e -> keys.remove(e.getCode()));

        // Vòng lặp nhỏ 60FPS cập nhật vị trí theo phím
        if (kbLoop != null) kbLoop.stop();
        kbLoop = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(16), ev -> {
                    double vx = 0.0;

                    // A/LEFT sang trái, D/RIGHT sang phải
                    if (keys.contains(javafx.scene.input.KeyCode.A) || keys.contains(javafx.scene.input.KeyCode.LEFT)) {
                        vx -= PADDLE_SPEED;
                    }
                    if (keys.contains(javafx.scene.input.KeyCode.D) || keys.contains(javafx.scene.input.KeyCode.RIGHT)) {
                        vx += PADDLE_SPEED;
                    }

                    if (vx != 0.0) {
                        // dt xấp xỉ 1/60s
                        double dt = 1.0 / 60.0;

                        // Lấy vị trí "left X" hiện tại của paddle (chính là thứ bạn truyền vào setLocation)
                        double currX = paddleLogic1.getPos_x(); // hoặc phương thức tương đương để đọc left X hiện tại

                        // Tính giới hạn biên theo đúng công thức clamp bạn đang dùng
                        double minLeft = screenP1.getBoundsInParent().getMinX() + paddleLogic1.getWidth() / 2.0;
                        double maxLeft = screenP1.getBoundsInParent().getMaxX() - paddleLogic1.getWidth() / 2.0;

                        // Di chuyển theo vận tốc & dt, rồi clamp
                        double nextX = currX + vx * dt;
                        int newX = (int) Math.round(Math.max(minLeft, Math.min(nextX, maxLeft)));

                        // Cập nhật paddle + gửi mạng
                        paddleLogic1.setLocation(newX);
                        client.send("Move:" + newX);
                    }
                })
        );
        kbLoop.setCycleCount(javafx.animation.Animation.INDEFINITE);
        kbLoop.play();
    }


    // =============================================================
    // 7️⃣ GAME LOOP & COLLISION
    // =============================================================
    void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override public void handle(long now) {
                double dt = 0.016;
                gameBall(dt);
                setGamePowerup1();
            }
        };
        timer.start();
    }

    private void gameBall(double dt) {
        // Player 1
        for (int i = gameBall1.size() - 1; i >= 0; i--) {
            Ball ballLogic1 = gameBall1.get(i);
            if (ballLogic1.isSticky()) {
                double newX = paddleLogic1.getPos_x() + ballLogic1.getPosinPaddle();
                if (newX > paddleLogic1.getImageView().getBoundsInParent().getMaxX() ||
                        newX < paddleLogic1.getImageView().getBoundsInParent().getMinX()) {
                    newX = Math.max(paddleLogic1.getImageView().getBoundsInParent().getMinX(),
                            Math.min(paddleLogic1.getImageView().getBoundsInParent().getMaxX(),
                                    paddleLogic1.getWidth()));
                    ballLogic1.updateSpeedX(-ballLogic1.getSpeedX());
                }
                ballLogic1.setLocation(newX);
                ballLogic1.setPosinPaddle();
                client.send("BallStick:" + (int)newX);
                //client.send("Stick:" + ballLogic1.isSticky());
            } else {
                baseGame1.brickCollision(ballLogic1, gameBricks1, player_1, gamePowerup1);
                baseGame1.paddleballCollision(ballLogic1, paddleLogic1, isCatch1);
                baseGame1.wallCollision(ballLogic1, screenP1);
                ballLogic1.updatePos(dt);
            }
        }

        // Player 2
        paddleLogic2.setLocation(getMove_Of_Paddle());
        for (int i = gameBall2.size() - 1; i >= 0; i--) {
            Ball ballLogic2 = gameBall2.get(i);
            if (isStillStick()) {
                ballLogic2.setLocation(getBallStick());
                ballLogic2.setPosinPaddle();
            } else {
                baseGame2.brickCollision(ballLogic2, gameBricks2, player_2, gamePowerup2);
                baseGame2.paddleballCollision(ballLogic2, paddleLogic2, isCatch2);
                baseGame2.wallCollision(ballLogic2, screenP2);
                ballLogic2.updatePos(0.016);
            }
        }
    }

    private void setGamePowerup1() {
        for (int i = gamePowerup1.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup1.get(i);
            if (baseGame1.paddlePUCollision(powerup, paddleLogic1, "BTS1")) {
                gamePowerup1.remove(powerup);
                player_1.getChildren().remove(powerup.getImageView());
            } else if (baseGame1.outPowerup(powerup, screenP1)) {
                gamePowerup1.remove(powerup);
                player_1.getChildren().remove(powerup.getImageView());
            } else powerup.movedown();
        }
    }

    // =============================================================
    // 8️⃣ GETTER / SETTER
    // =============================================================
    public int getBallStick() { return ballStick; }
    public void setBallStick(int ballStick) { this.ballStick = ballStick; }
    public int getMove_Of_Paddle() { return move_Of_Paddle; }
    public void setMove_Of_Paddle(int move_Of_Paddle) { this.move_Of_Paddle = move_Of_Paddle; }
    public boolean isStillStick() { return stillStick; }
    public void setStillStick(boolean stillStick) { this.stillStick = stillStick; }

    // =============================================================
    // 9️⃣ POWER-UP / SKILL
    // =============================================================
    public void upBall1() { powerBall1.upBall(); }
    public void resetBall1() { powerBall1.downBall(); }
    public void slowBall1() { powerBall1.slowBall(); }
    public void resetSlowBall1() { powerBall1.normalBall(); }
    public void openSheild1() { sheild1.openSheild(gameBricks1, player_1, screenP1); }
    public void moreBall1() { powerBall1.moreBall(player_1); }
    public void upPaddle1() { specialPaddle1.upPaddle(); }
    public void resetPaddle1() { specialPaddle1.downPaddle(); }
    public void catchBall1() { isCatch1 = !isCatch1; }

    public void upBall2() { powerBall2.upBall(); }
    public void resetBall2() { powerBall2.downBall(); }
    public void slowBall2() { powerBall2.slowBall(); }
    public void resetSlowBall2() { powerBall2.normalBall(); }
    public void openSheild2() { sheild2.openSheild(gameBricks2, player_2, screenP2); }
    public void moreBall2() { powerBall2.moreBall(player_2); }
    public void upPaddle2() { specialPaddle2.upPaddle(); }
    public void resetPaddle2() { specialPaddle2.downPaddle(); }
    public void catchBall2() { isCatch2 = !isCatch2; }
}
