package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import unknown.oopptt.api.*;
import unknown.oopptt.api.ball.Ball;
import unknown.oopptt.api.ball.PowerBall;
import unknown.oopptt.api.enemy.Enemy;
import unknown.oopptt.api.enemy.ShooterEnemy;
import unknown.oopptt.net.client.GameClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BattleScreenController {

    /* ==============================
     *  1. CONSTANTS & CONFIG
     * ============================== */

    private static final int BRICK_CELL_W = 50;
    private static final int BRICK_CELL_H = 23;
    private Data data = new Data();
    private NowPlay player = new NowPlay();

    public Data getData() {
        return data;
    }

    public NowPlay getPlayer() {
        return player;
    }

    public void setPlayer(NowPlay player) {
        this.player = player;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private static final String BG_IMAGE_PATH = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static final File MAP_FILE = new File("src/main/resources/map/BattleMap.txt");

    private static final double TARGET_FPS = 60;
    private static final double STEP = 1.0 / TARGET_FPS;

    private int countlife = 3;
    private int ballStick;
    private boolean stillStick = true;
    private int fakesignal = 0;
    private double Move;
    private double MoveX;
    private double MoveY;
    private GameClient client;
    private String playerName = "Trung";
    private String serverIP;
    private final int serverPort = 5000;

    /* ==============================
     *  2. FXML / UI REFERENCES
     * ============================== */

    @FXML StackPane stack_root;
    @FXML SplitPane split_pane;

    // Player 1 UI
    @FXML AnchorPane player_1;
    @FXML Pane Data1;
    @FXML ImageView bgDataPlayer1;
    @FXML private Label scorePlayer1;
    @FXML StackPane stackP1;
    @FXML ImageView bgP1;
    @FXML Pane player1;
    @FXML ImageView background_player1;

    // Player 2 UI
    @FXML AnchorPane player_2;
    @FXML Pane Data2;
    @FXML ImageView bgDataPlayer2;
    @FXML private Label scorePlayer2;
    @FXML StackPane stackP2;
    @FXML ImageView bgP2;
    @FXML Pane player2;
    @FXML ImageView background_player2;

    /* ==============================
     *  3. GAME STATE: PLAYER 1
     * ============================== */

    private final List<Ball> gameBall1 = new ArrayList<>();
    private final List<Powerup> gamePowerup1 = new ArrayList<>();
    private final List<Brick> gameBricks1 = new LinkedList<>();
    private final List<Enemy> gameEnemies1 = new ArrayList<>();
    private final List<Integer> gamePU1 = new LinkedList<>();
    private boolean isCatch1 = false;
    private boolean isSpam1 = false;

    private Shooter shooter1;
    private Sheild sheild1;
    private Paddle paddleLogic1;
    private PowerBall powerBall1;
    private SpecialPaddle specialPaddle1;
    private final BaseGame baseGame1 = new BaseGame(this);

    /* ==============================
     *  4. GAME STATE: PLAYER 2
     * ============================== */

    private final List<Ball> gameBall2 = new ArrayList<>();
    private final List<Powerup> gamePowerup2 = new ArrayList<>();
    private final List<Brick> gameBricks2 = new LinkedList<>();
    private final List<Integer> gamePU2 = new LinkedList<>();
    private final List<Enemy> gameEnemies2 = new ArrayList<>();
    private boolean isCatch2 = false;
    private boolean isSpam2 = false;

    private Shooter shooter2;
    private Sheild sheild2;
    private Paddle paddleLogic2;
    private PowerBall powerBall2;
    private SpecialPaddle specialPaddle2;
    private final BaseGame baseGame2 = new BaseGame(this);

    /* ==============================
     *  5. INITIALIZATION (JavaFX)
     * ============================== */

    @FXML
    void initialize() {
        preloadAssets();
        setBackground(BG_IMAGE_PATH, MAP_FILE);
        connectToServer();

        // Bind stack pane size
        stackP1.prefWidthProperty().bind(player_1.widthProperty());
        stackP1.prefHeightProperty().bind(player_1.heightProperty());
        stackP2.prefWidthProperty().bind(player_2.widthProperty());
        stackP2.prefHeightProperty().bind(player_2.heightProperty());
        stackP1.setAlignment(Pos.CENTER);
        stackP2.setAlignment(Pos.CENTER);

        System.out.println(background_player1.localToScene(background_player2.getBoundsInLocal()));

        startGameloop();
        ListenEventHandle();
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
    }

    /**
     * Khởi tạo background, paddle, bóng, shooter, map cho 2 player.
     */
    private void setBackground(String backgroundPath, File MAP_FILE) {
        background_player1.setImage(new Image(backgroundPath));
        background_player2.setImage(new Image(backgroundPath));

        // Paddle logic
        paddleLogic1 = new Paddle(
                background_player1.getBoundsInParent().getCenterX(),
                background_player1.getBoundsInParent().getMaxY() - 40,
                background_player1
        );
        paddleLogic2 = new Paddle(
                background_player2.getBoundsInParent().getCenterX(),
                background_player2.getBoundsInParent().getMaxY() - 40,
                background_player2
        );

        // Add paddle
        player1.getChildren().add(paddleLogic1.getImageView());
        specialPaddle1 = new SpecialPaddle(paddleLogic1);
        player2.getChildren().add(paddleLogic2.getImageView());
        specialPaddle2 = new SpecialPaddle(paddleLogic2);

        // Ball
        Ball first_ball = new Ball(
                paddleLogic1.getPos_x(),
                paddleLogic1.getPos_y() - 5,
                0.3, 0,
                player1
        );
        Ball second_ball = new Ball(
                paddleLogic2.getPos_x(),
                paddleLogic2.getPos_y() - 5,
                0.3, 0,
                player2
        );

        gameBall1.add(first_ball);
        gameBall2.add(second_ball);

        player1.getChildren().add(first_ball.getImageView());
        powerBall1 = new PowerBall(gameBall1);
        shooter1 = new Shooter(200, 2, 5, 10, player_1, paddleLogic1.getImageView(), 20);

        player2.getChildren().add(second_ball.getImageView());
        powerBall2 = new PowerBall(gameBall2);
        shooter2 = new Shooter(200, 2, 5, 10, player_2, paddleLogic2.getImageView(), 20);

        // Map
        upMap(MAP_FILE);
    }

    /**
     * Load map từ file và tạo brick cho cả 2 player.
     */
    private void upMap(File MAP_FILE) {
        final int startX = 0;
        final int startY = 0;

        if (!MAP_FILE.exists() || !MAP_FILE.isFile()) {
            throw new RuntimeException(MAP_FILE + " not found");
        }

        try (
                Stream<String> lines = Files.lines(MAP_FILE.toPath(), StandardCharsets.UTF_8)
        ) {
            final int[] row = {0};
            lines.forEach(line -> {
                String[] data = line.trim().split("\\s+");
                for (int col = 0; col < data.length; col++) {
                    if (data[col].isEmpty()) continue;
                    int type = safeParse(data[col]);
                    int typeB = type/10;
                    int typePU =type-10;
                    if (type > 0 && type < 100) {
                        int newX = startX + col * BRICK_CELL_W;
                        int newY = startY + row[0] * BRICK_CELL_H;

                        Brick new_Brick1 = new Brick(newX, newY, typeB,typePU);
                        gameBricks1.add(new_Brick1);
                        player1.getChildren().add(new_Brick1.getImageView());

                        Brick new_Brick2 = new Brick(newX, newY, typeB,typePU);
                        gameBricks2.add(new_Brick2);
                        player2.getChildren().add(new_Brick2.getImageView());
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

    /* ==============================
     *  6. NETWORK / MULTIPLAYER
     * ============================== */

    private void connectToServer() {
        client = new GameClient(
                client.findServerIP(), // chỗ này sau này bạn nên tách ra / sửa lại
                serverPort,
                playerName,
                msg -> Platform.runLater(() -> {
                    System.out.println("Server: " + msg);
                    switch (msg.charAt(0)) {
                        case 'B' -> setBallStick(Integer.parseInt(msg.substring(1)));
                        case 'S' -> setStillStick(Boolean.parseBoolean(msg.substring(1)));
                        case 'M' -> setMove(Double.parseDouble(msg.substring(1)));
                        case 'H' -> {
                            int indexM = msg.indexOf('M');
                            int indexX = msg.indexOf('X');
                            int indexY = msg.indexOf('Y');

                            String num1Str = msg.substring(indexM + 1, indexX);
                            String num2Str = msg.substring(indexX + 1, indexY);
                            String num3Str = msg.substring(indexY + 1);

                            setMove(Double.parseDouble(num1Str));
                            setMoveX(Double.parseDouble(num2Str));
                            setMoveY(Double.parseDouble(num3Str));
                        }
                    }
                })
        );
    }

    /* ==============================
     *  7. INPUT HANDLING
     * ============================== */

    private void ListenEventHandle() {
        player1.setCursor(Cursor.NONE);
        player1.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
                newValue.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);
                player1.requestFocus();
            }
        });
    }

    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT -> {
                paddleLogic1.setLeftHeld(true);
                client.send("1");
            }
            case D, RIGHT -> {
                paddleLogic1.setRightHeld(true);
                client.send("2");
            }
            case R -> openHome();
            // case SPACE -> setBallmove();
        }
    }

    private void onKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT -> paddleLogic1.setLeftHeld(false);
            case D, RIGHT -> paddleLogic1.setRightHeld(false);
            case SPACE -> {
                setBallmove(gameBall1);
                //setStillStick(false);
                client.send("S" + Boolean.toString(false));
            }
        }
    }

    /* ==============================
     *  8. GAME LOOP & UPDATE
     * ============================== */

    private void startGameloop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0L;
            private double accumulator = 0.0;

            @Override
            public void handle(long now) {
                if (lastTime == 0L) {
                    lastTime = now;
                    return;
                }

                double dt = (now - lastTime) / 1e9;
                if (dt > 0.25) {
                    dt = 0.25;
                }

                lastTime = now;
                accumulator += dt;

                if (accumulator > STEP) {
                    gameBall1(STEP);
                    gameBall2(STEP);
                    paddleLogic1.update(STEP);
                    paddleLogic2.update(STEP);
                    gameBricksUp1(STEP);
                    gameBricksUp2(STEP);
                    setGamePowerup(STEP);
                    // enemyGame(STEP);
                    accumulator -= STEP;
                }
            }
        };
        timer.start();
    }

    private static double clamp(double v, double lo, double hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

    /* ==============================
     *  9. BRICK UPDATE
     * ============================== */

    private void gameBricksUp1(double dt) {
        for (int i = gameBricks1.size() - 1; i >= 0; i--) {
            Brick brick = gameBricks1.get(i);
            if (brick.isWait()) {
                if (brick.dropBrick(dt)) {
                    player1.getChildren().remove(brick.getImageView());
                    gameBricks1.remove(i);
                    System.out.println(brick.getTypePU());
                    if (brick.getTypePU() != 0) {
                        Powerup p = new Powerup(
                                brick.getImageView().getBoundsInParent().getCenterX()
                                , brick.getImageView().getBoundsInParent().getCenterY()
                                , brick.getTypePU()
                        );
                        player1.getChildren().add(p.getImageView());
                        gamePowerup1.add(p);
                    }
                }
            }
            brick.updateAnimation(dt);
        }
    }
    private void gameBricksUp2(double dt) {
        for (int i = gameBricks2.size() - 1; i >= 0; i--) {
            Brick brick = gameBricks2.get(i);
            if (brick.isWait()) {
                if (brick.dropBrick(dt)) {
                    player2.getChildren().remove(brick.getImageView());
                    gameBricks2.remove(i);
                    System.out.println(brick.getTypePU());
                    if (brick.getTypePU() != 0) {
                        Powerup p = new Powerup(
                                brick.getImageView().getBoundsInParent().getCenterX()
                                , brick.getImageView().getBoundsInParent().getCenterY()
                                , brick.getTypePU()
                        );
                        player2.getChildren().add(p.getImageView());
                        gamePowerup2.add(p);
                    }
                }
            }
            brick.updateAnimation(dt);
        }
    }
    private void setGamePowerup(double dt) {
        for (int i = gamePowerup1.size() - 1; i >= 0; i--) {
            Powerup pu = gamePowerup1.get(i);
            if (baseGame1.paddlePUCollision(pu, paddleLogic1, "BTS1")) {
                gamePowerup1.remove(i);
                player1.getChildren().remove(pu.getImageView());
                continue;
            }
            if (baseGame1.outPowerup(pu, background_player1)) {
                gamePowerup1.remove(i);
                player1.getChildren().remove(pu.getImageView());
                continue;
            }
            pu.movedown(dt);
        }
        for (int i = gamePowerup2.size() - 1; i >= 0; i--) {
            Powerup pu = gamePowerup2.get(i);
            if (baseGame2.paddlePUCollision(pu, paddleLogic2, "BTS2")) {
                gamePowerup2.remove(i);
                player2.getChildren().remove(pu.getImageView());
                continue;
            }
            if (baseGame2.outPowerup(pu, background_player2)) {
                gamePowerup2.remove(i);
                player2.getChildren().remove(pu.getImageView());
                continue;
            }
            pu.movedown(dt);
        }
    }

    /* ==============================
     *  10. BALL UPDATE
     * ============================== */

    public void setBallmove(List<Ball> game_ball) {
        for (Ball ball : game_ball) {
            ball.setSticky(false);
        }
    }

    private void gameBall1(double dt) {
        for (int i = gameBall1.size() - 1; i >= 0; i--) {
            Ball ballLogic = gameBall1.get(i);

            if (ballLogic.isSticky()) {
                double newX = paddleLogic1.getPos_x() + ballLogic.getOffsetOnPaddle();

                Bounds pb = paddleLogic1.getImageView().getBoundsInParent();
                if (newX >= pb.getMaxX() || newX <= pb.getMinX()) {
                    newX = clamp(newX, pb.getMinX(), pb.getMaxX());
                    ballLogic.setSpeedX(-ballLogic.getSpeedX());
                }

                ballLogic.setLocation(newX, dt);
                client.send('M' + Double.toString(paddleLogic1.getPos_x()));
                continue;
            }

            if (baseGame1.outBall(ballLogic.getImageView().getBoundsInParent(), paddleLogic1.getImageView())) {
                gameBall1.remove(i);
                player1.getChildren().remove(ballLogic.getImageView());
                continue;
            }

            client.send('M' + Double.toString(paddleLogic1.getPos_x()));
            baseGame1.brickCollision(ballLogic, gameBricks1);
            baseGame1.paddleballCollision(ballLogic, paddleLogic1, isCatch1);
            baseGame1.enemyCollision(ballLogic, gameEnemies1);
            baseGame1.wallCollision(ballLogic, background_player1);

            ballLogic.update(dt);
        }
    }

    private void gameBall2(double dt) {
        paddleLogic2.setLocation(getMove(), 1 / 60.0);

        for (int i = gameBall2.size() - 1; i >= 0; i--) {
            Ball ballLogic = gameBall2.get(i);

            if (isStillStick()) {
                ballLogic.setLocation(paddleLogic2.getPos_x(), dt);
                // ballLogic.addOffsetOnPaddle(dt);
                continue;
            }

            if (baseGame2.outBall(ballLogic.getImageView().getBoundsInParent(), paddleLogic2.getImageView())) {
                gameBall2.remove(i);
                player2.getChildren().remove(ballLogic.getImageView());
                continue;
            }

            baseGame2.brickCollision(ballLogic, gameBricks2);
            baseGame2.paddleballCollision(ballLogic, paddleLogic2, isCatch2);
            baseGame2.enemyCollision(ballLogic, gameEnemies2);
            baseGame2.wallCollision(ballLogic, background_player2);

            ballLogic.update(dt);
        }
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

    /* ==============================
     *  11. POWERUP / PADDLE – PLAYER 2
     * ============================== */

    public void upBall2()            { powerBall2.upBall(); }
    public void resetBall2()         { powerBall2.downBall(); }
    public void slowBall2()          { powerBall2.slowBall(); }
    public void resetSlowBall2()     { powerBall2.normalBall(); }
    public void moreBall2()          { powerBall2.moreBall(player2); }

    public void upPaddle2() {
        if (paddleLogic2.getWidth() * 2 <= background_player2.getBoundsInParent().getWidth() / 2.0) {
            specialPaddle2.setBig();
        }
    }

    public void offUpPaddle2() {
        specialPaddle2.setNormalSize();
        for (Ball balLogic : gameBall1) {
            balLogic.setOffsetOnPaddle(balLogic.getOffsetOnPaddle() / 2);
        }
    }

    public void slowPaddle2()        { specialPaddle2.setSlow(); }
    public void offslowPaddle2()     { specialPaddle2.setNormalSpeed(); }

    public void downPaddle2()        { specialPaddle2.setSmall(); }
    public void offdownPaddle2()     { specialPaddle2.setNormalSize(); }

    public void fastPaddle2()        { specialPaddle2.setFast(); }

    public void createMinions2()     { /* TODO */ }

    public void setCreateMinions2()  { isSpam2 = true; }
    public void setOffCreateMinions2() { isSpam1 = false; }

    public void setRedirPaddle2()    { specialPaddle2.setRedir(true); }
    public void offRedirPaddle2()    { specialPaddle2.setRedir(false); }

    public void createFakePU2() {
        Powerup newPU = new Powerup(
                background_player2.getBoundsInParent().getCenterX() / 2,
                0,
                Powerup.PowerupType.REDIR
        );
    }

    public void catchBall2()          { isCatch2 = !isCatch2; }
    public void enableGun2()          { shooter2.setEnabled(true); }
    public void unEnableGun2()        { shooter2.setEnabled(false); }

    public void setShieldOn2() {
        sheild2.setOpenShield(true);
    }

    /* ==============================
     *  12. POWERUP / PADDLE – PLAYER 1
     * ============================== */

    public void upBall1()            { powerBall1.upBall(); }
    public void resetBall1()         { powerBall1.downBall(); }
    public void slowBall1()          { powerBall1.slowBall(); }
    public void resetSlowBall1()     { powerBall1.normalBall(); }
    public void moreBall1()          { powerBall1.moreBall(player1); }

    public void upPaddle1() {
        if (paddleLogic1.getWidth() * 2 <= background_player1.getBoundsInParent().getWidth() / 2.0) {
            specialPaddle1.setBig();
        }
    }

    public void offUpPaddle1() {
        specialPaddle1.setNormalSize();
        for (Ball balLogic : gameBall1) {
            balLogic.setOffsetOnPaddle(balLogic.getOffsetOnPaddle() / 2);
        }
    }

    public void slowPaddle1()        { specialPaddle1.setSlow(); }
    public void offslowPaddle1()     { specialPaddle1.setNormalSpeed(); }

    public void downPaddle1()        { specialPaddle1.setSmall(); }
    public void offdownPaddle1()     { specialPaddle1.setNormalSize(); }

    public void fastPaddle1()        { specialPaddle1.setFast(); }

    public void createMinions1()     { /* TODO */ }

    public void setCreateMinions1()  { isSpam1 = true; }
    public void setOffCreateMinions1() { isSpam1 = false; }

    public void setRedirPaddle1()    { specialPaddle1.setRedir(true); }
    public void offRedirPaddle1()    { specialPaddle1.setRedir(false); }

    public void createFakePU1() {
        Powerup newPU = new Powerup(
                background_player1.getBoundsInParent().getCenterX() / 2,
                0,
                Powerup.PowerupType.REDIR
        );
    }

    public void catchBall1()         { isCatch1 = !isCatch1; }
    public void enableGun1()         { shooter1.setEnabled(true); }
    public void unEnableGun1()       { shooter1.setEnabled(false); }

    public void setShieldOn1() {
        sheild1.setOpenShield(true);
    }

    /* ==============================
     *  13. GETTERS / SETTERS
     * ============================== */

    public double getMove()          { return Move; }
    public void setMove(double move) { Move = move; }

    public double getMoveY()         { return MoveY; }
    public void setMoveY(double moveY) { MoveY = moveY; }

    public double getMoveX()         { return MoveX; }
    public void setMoveX(double moveX) { MoveX = moveX; }

    public int getBallStick()        { return ballStick; }
    public void setBallStick(int ballStick) { this.ballStick = ballStick; }

    public boolean isStillStick()    { return stillStick; }
    public void setStillStick(boolean stillStick) { this.stillStick = stillStick; }
}