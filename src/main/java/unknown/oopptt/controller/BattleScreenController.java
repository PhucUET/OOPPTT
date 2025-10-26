package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;


import unknown.oopptt.api.*;
import unknown.oopptt.api.Brick;
import unknown.oopptt.net.client.GameClient;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class BattleScreenController {
    private static String background_Game = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static File mapBrick1 =  new File("src/main/resources/map/map1.txt");

    private GameClient client;
    private String playerName = "Me";
    private String serverIP = "127.0.0.1";  // IP LAN server
    private int serverPort = 5000;

    private boolean inPaddle1 = true;
    private boolean isCatch1 = false;
    List<Brick> gameBricks1 = new LinkedList<Brick>();
    private List<Ball> gameBall1 = new ArrayList<Ball>();
    List<Powerup> gamePowerup1 = new LinkedList<>();
    PowerBall powerBall1;
    private Paddle paddleLogic1;
    Sheild sheild1 = new Sheild();
    private SpecialPaddle specialPaddle1;
    BaseGame baseGame1 = new BaseGame(this);
    Sheild sheild = new Sheild();
    Shooter shooter1;

    private boolean inPaddle2 = true;
    private boolean isCatch2 = false;
    List<Brick> gameBricks2 = new LinkedList<Brick>();
    private List<Ball> gameBall2 = new ArrayList<Ball>();
    List<Powerup> gamePowerup2 = new LinkedList<>();
    PowerBall powerBall2;
    private Paddle paddleLogic2;
    Sheild sheild2 = new Sheild();
    private SpecialPaddle specialPaddle2;
    BaseGame baseGame2 = new BaseGame(this);
    Shooter shooter2;

    private int pos;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @FXML
    private StackPane stack_root;
    @FXML
    private SplitPane split_pane;
    @FXML
    private AnchorPane player_1;
    @FXML
    private ImageView screenP1;
    @FXML
    private AnchorPane player_2;
    @FXML
    private ImageView screenP2;
    @FXML
    private ImageView Data_Sheet_P1;
    @FXML
    private ImageView Data_Sheet_P2;
    @FXML
    public void initialize()
    {
        connectToServer();
        //setPos((int) screenP1.getBoundsInParent().getCenterX());
        set_Player1_BackGround();
        set_Player2_BackGround();
        startgameloop();
        setOnMouse_Paddle();

    }

    private void connectToServer() {
        client = new GameClient(serverIP, serverPort, playerName, msg -> {
            Platform.runLater(() -> {
                // Ở đây bạn có thể xử lý thông tin từ server
                String message = msg.substring(5);
                setPos(Integer.parseInt(message));
                //System.out.println("Server: " + message);
            });
        });

    }

    public void onClose() {
        if (client != null) client.close();
    }

    private void set_Player2_BackGround() {


        Platform.runLater(()-> {
            screenP2.setImage(new Image(background_Game));

            paddleLogic2 = new Paddle(screenP2.getBoundsInParent().getCenterX(), screenP2.getBoundsInParent().getMaxY() - 20);
            player_2.getChildren().add(paddleLogic2.getImageView());
            specialPaddle1 = new SpecialPaddle(paddleLogic2);

            Ball first_ball = new Ball(paddleLogic2.getPos_x(),paddleLogic2.getPos_y() - 2,1,0);
            gameBall2.add(first_ball);
            player_2.getChildren().add(first_ball.getImageView());
            powerBall2 = new PowerBall(gameBall2);
            shooter2 = new Shooter(200, 2, 1,3,player_2, paddleLogic2.getImageView(),20);

        });
    }

    private void setOnMouse_Paddle() {
        player_1.setCursor(Cursor.NONE);
        Platform.runLater(() -> {
            player_1.setOnMouseClicked(event -> {
                for (Ball ball : gameBall1) {
                    ball.setSticky(false);
                }
            });
            player_1.setOnMouseMoved(event -> {
                int newX = (int) Math.round(
                        Math.max(
                                screenP1.getBoundsInParent().getMinX() + paddleLogic1.getWidth() / 2,
                                Math.min(event.getX() - paddleLogic1.getWidth() / 2,
                                        screenP1.getBoundsInParent().getMaxX()- paddleLogic1.getWidth() / 2)
                        )
                );
                //System.out.println(newX);
                paddleLogic1.setLocation(newX);
                client.send("Move:"+Integer.toString(newX));
                paddleLogic2.setLocation(paddleLogic1.getPos_x());
            });

        });
    }
    private void set_Player1_BackGround() {


        Platform.runLater(()-> {
            screenP1.setImage(new Image(background_Game));

            paddleLogic1 = new Paddle(screenP1.getBoundsInParent().getCenterX(), screenP1.getBoundsInParent().getMaxY() - 20);
            player_1.getChildren().add(paddleLogic1.getImageView());
            specialPaddle1 = new SpecialPaddle(paddleLogic1);

            Ball first_ball = new Ball(paddleLogic1.getPos_x(),paddleLogic1.getPos_y() - 2,1,0);
            gameBall1.add(first_ball);
            player_1.getChildren().add(first_ball.getImageView());
            powerBall1 = new PowerBall(gameBall1);
            shooter1 = new Shooter(200, 2, 1,3,player_1, paddleLogic1.getImageView(),20);

            upMap();
        });
    }

    private void upMap() {
        int startX = 175;
        int startY = 200;
        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick1))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                for (int col = 0; col < data.length; col++) {
                    int type = Integer.parseInt(data[col]);
                    if (type > 0) {
                        int newX = startX + col * 33;
                        int newY = startY + row * 25;
                        Brick new_Brick = new Brick(newX,newY,type);
                        gameBricks1.add(new_Brick);
                        player_1.getChildren().add(new_Brick.getImageView());
                        //Player_2.getChildren().add(new_Brick.getImageView());
                    }
                }
                row = row + 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick1))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                for (int col = 0; col < data.length; col++) {
                    int type = Integer.parseInt(data[col]);
                    if (type > 0) {
                        int newX = startX + col * 33;
                        int newY = startY + row * 25;
                        Brick new_Brick = new Brick(newX,newY,type);
                        gameBricks2.add(new_Brick);
                        player_2.getChildren().add(new_Brick.getImageView());
                        //Player_2.getChildren().add(new_Brick.getImageView());
                    }
                }
                row = row + 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gameBall(double dt) {
        if  (gameBall1.size() == 0) {
            return;
        }
        for (int i = gameBall1.size() - 1; i >= 0; i--) {
            Ball ballLogic1 = gameBall1.get(i);
            Ball ballLogic2 = gameBall2.get(i);
            if (ballLogic1.isSticky()) {
                double newX = paddleLogic1.getPos_x() + ballLogic1.getPosinPaddle();
                if (newX > paddleLogic1.getImageView().getBoundsInParent().getMaxX() ||
                        newX < paddleLogic1.getImageView().getBoundsInParent().getMinX()) {
                    newX = Math.max(paddleLogic1.getImageView().getBoundsInParent().getMinX(),
                            Math.min(paddleLogic1.getImageView().getBoundsInParent().getMaxX(), paddleLogic1.getWidth()));
                    ballLogic1.updateSpeedX(-ballLogic1.getSpeedX());
                }
                ballLogic1.setLocation(newX);
                ballLogic1.setPosinPaddle();

                ballLogic2.setLocation(newX);
                ballLogic2.setPosinPaddle();
            } else {
//                if (baseGame1.outBall(ballLogic1, screenP1)) {
//                    gameBall1.remove(i);
//                    player_1.getChildren().remove(ballLogic1.getImageView());
//                    continue;
//                }
//                if (baseGame2.outBall(ballLogic1, screenP2)) {
//                    gameBall2.remove(i);
//                    player_2.getChildren().remove(ballLogic2.getImageView());
//                    continue;
//                }
                baseGame1.random = 0;
                baseGame1.randomIndex = -1;
                baseGame1.brickCollision(ballLogic1,gameBricks1,player_1, gamePowerup1);
                baseGame1.paddleballCollision(ballLogic1, paddleLogic1, isCatch1);
                baseGame1.wallCollision(ballLogic1,screenP1);
                ballLogic1.updatePos(dt);

                baseGame2.random = baseGame1.random;
                baseGame2.randomIndex = baseGame1.randomIndex;
                baseGame2.brickCollision(ballLogic2,gameBricks2,player_2, gamePowerup1);
                //System.out.println(baseGame2.shouldDrop());
                baseGame2.paddleballCollision(ballLogic2, paddleLogic2, isCatch2);
                baseGame2.wallCollision(ballLogic2,screenP2);
                ballLogic2.updatePos(dt);
//                if(ballLogic1.getPos_y() == ballLogic2.getPos_y()){
//                    System.out.println(true);
//                }
//                else {
//                    System.out.println(false);
//                }
                //paddleLogic2.setLocation(paddleLogic1.getPos_x());
            }
        }
    }
    private void setGamePowerup1() {
        for (int i = gamePowerup1.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup1.get(i);
            if (baseGame1.paddlePUCollision(powerup,paddleLogic1,"BTS")) {
                gamePowerup1.remove(powerup);
                player_1.getChildren().remove(powerup.getImageView());
            } else {
                if (baseGame1.outPowerup(powerup, screenP1)) {
                    gamePowerup1.remove(powerup);
                    player_1.getChildren().remove(powerup.getImageView());
                    continue;
                }
                powerup.movedown();
            }

        }
    }
    private void setGamePowerup2() {
        for (int i = gamePowerup2.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup2.get(i);
            if (baseGame2.paddlePUCollision(powerup,paddleLogic2,"BTS")) {
                gamePowerup2.remove(powerup);
                player_2.getChildren().remove(powerup.getImageView());
            } else {
                if (baseGame2.outPowerup(powerup, screenP2)) {
                    gamePowerup2.remove(powerup);
                    player_2.getChildren().remove(powerup.getImageView());
                    continue;
                }
                powerup.movedown();
            }

        }
    }
    private void startgameloop() {
        AnimationTimer timer = new AnimationTimer() {
            private Long lasts = 0L;
            @Override
            public void handle(long now) {
                if (lasts == 0) {
                    lasts = now;
                    return;
                }
                double dt = (now - lasts) / 1e9;
                lasts = now;
//                if (shooter.getEnabled()) {
//                    shooter.tryFire();
//                }
                //shooter.update(dt,gameBricks1);
                gameBall(dt);
               // setGamePowerup1();
               // setGamePowerup2();
            }
        };
        timer.start();
    }
    public void upBall() {powerBall1.upBall();}

    public void resetBall() {powerBall1.downBall();}

    public void slowBall() {powerBall1.slowBall();}

    public void resetSlowBall() {powerBall1.normalBall();}

    public void openSheild() {sheild1.openSheild(gameBricks1, player_1, screenP1);}

    public void moreBall() {powerBall1.moreBall(player_1);}

    public void upPaddle() {specialPaddle1.upPaddle();}

    public void resetPaddle() {specialPaddle1.downPaddle();}


    public void catchBall() {
        isCatch1 = !isCatch1;
    }


}