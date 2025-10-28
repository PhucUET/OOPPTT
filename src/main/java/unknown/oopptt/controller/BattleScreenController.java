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
    private static File mapBrick =  new File("src/main/resources/map/map1.txt");

    private GameClient client;
    private String playerName = "Me";
    private String serverIP = "127.0.0.1";  // IP LAN server
    private int serverPort = 5000;
    private int move_Of_Paddle;
    private int indexPU;
    private int random_PU;
    private int ballStick;
    private boolean stillStick = true;

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
                switch (msg.charAt(0)) {
                    case 'M':
                        String move = msg.substring(5);
                        setMove_Of_Paddle(Integer.parseInt(move));
                        break;
                    case 'I':
                        String IndexPU = msg.substring(8);
                        setIndexPU(Integer.parseInt(IndexPU));
                        break;
                    case 'R':
                        String random = msg.substring(9);
                        setRandom_PU(Integer.parseInt(random));
                        break;
                    case 'B':
                        String ballStick = msg.substring(10);
                        setBallStick(Integer.parseInt(ballStick));
                        break;
                    case 'S':
                        String isStick = msg.substring(6);
                        setStillStick(Boolean.parseBoolean(isStick));
                        break;
                }
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
            specialPaddle2 = new SpecialPaddle(paddleLogic2);
            setMove_Of_Paddle((int) screenP2.getBoundsInParent().getCenterX());

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
                paddleLogic2.setLocation(getMove_Of_Paddle());
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
        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick))) {
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
                    }
                }
                row = row + 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick))) {
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
            if(isStillStick()) {
                ballLogic2.setLocation(getBallStick());
                ballLogic2.setPosinPaddle();
            }
            else {
                baseGame2.random = getRandom_PU();
                baseGame2.randomIndex = getIndexPU();
                baseGame2.brickCollision(ballLogic2,gameBricks2,player_2, gamePowerup2);
                //System.out.println(baseGame2.shouldDrop());
                baseGame2.paddleballCollision(ballLogic2, paddleLogic2, isCatch2);
                baseGame2.wallCollision(ballLogic2,screenP2);
                ballLogic2.updatePos(dt);
            }
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
                client.send("BallStick:"+Integer.toString((int) newX));
                client.send("Stick:"+Boolean.toString(ballLogic1.isSticky()));
            }
            else {
                client.send("Stick:"+Boolean.toString(ballLogic1.isSticky()));
                baseGame1.random = 0;
                // randomIndex -1 sẽ random bất kì, từ 0 đến 8 sẽ là đúng cái PU đó
                baseGame1.randomIndex = -1;
                baseGame1.brickCollision(ballLogic1,gameBricks1,player_1, gamePowerup1);
                baseGame1.paddleballCollision(ballLogic1, paddleLogic1, isCatch1);
                baseGame1.wallCollision(ballLogic1,screenP1);
                ballLogic1.updatePos(dt);
                client.send("RandomPU:"+Integer.toString(baseGame1.random));
                client.send("IndexPU:"+Integer.toString(baseGame1.randomIndex));
            }
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


//
//                baseGame2.random = baseGame1.random;
//                baseGame2.randomIndex = baseGame1.randomIndex;
//                baseGame2.brickCollision(ballLogic2,gameBricks2,player_2, gamePowerup2);
//                //System.out.println(baseGame2.shouldDrop());
//                baseGame2.paddleballCollision(ballLogic2, paddleLogic2, isCatch2);
//                baseGame2.wallCollision(ballLogic2,screenP2);
//                ballLogic2.updatePos(dt);
//                if(ballLogic1.getPos_y() == ballLogic2.getPos_y()){
//                    System.out.println(true);
//                }
//                else {
//                    System.out.println(false);
//                }
                System.out.println(getMove_Of_Paddle());
                //paddleLogic2.setLocation(paddleLogic1.getPos_x());

        }
    }
    private void setGamePowerup1() {
        for (int i = gamePowerup1.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup1.get(i);
            if (baseGame1.paddlePUCollision(powerup,paddleLogic1,"BTS1")) {
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
            if (baseGame2.paddlePUCollision(powerup,paddleLogic2,"BTS2")) {
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
                setGamePowerup1();
                setGamePowerup2();
            }
        };
        timer.start();
    }

    public int getBallStick() {
        return ballStick;
    }

    public void setBallStick(int ballStick) {
        this.ballStick = ballStick;
    }

    public int getIndexPU() {
        return indexPU;
    }

    public void setIndexPU(int indexPU) {
        this.indexPU = indexPU;
    }

    public int getMove_Of_Paddle() {
        return move_Of_Paddle;
    }

    public void setMove_Of_Paddle(int move_Of_Paddle) {
        this.move_Of_Paddle = move_Of_Paddle;
    }

    public int getRandom_PU() {
        return random_PU;
    }

    public void setRandom_PU(int random_PU) {
        this.random_PU = random_PU;
    }

    public boolean isStillStick() {
        return stillStick;
    }

    public void setStillStick(boolean stillStick) {
        this.stillStick = stillStick;
    }

    public void upBall1() {powerBall1.upBall();}

    public void resetBall1() {powerBall1.downBall();}

    public void slowBall1() {powerBall1.slowBall();}

    public void resetSlowBall1() {powerBall1.normalBall();}

    public void openSheild1() {sheild1.openSheild(gameBricks1, player_1, screenP1);}

    public void moreBall1() {powerBall1.moreBall(player_1);}

    public void upPaddle1() {specialPaddle1.upPaddle();}

    public void resetPaddle1() {specialPaddle1.downPaddle();}

    public void catchBall1() {
        isCatch1 = !isCatch1;
    }


    public void upBall2() {powerBall2.upBall();}

    public void resetBall2() {powerBall2.downBall();}

    public void slowBall2() {powerBall2.slowBall();}

    public void resetSlowBall2() {powerBall2.normalBall();}

    public void openSheild2() {sheild1.openSheild(gameBricks2, player_2, screenP2);}

    public void moreBall2() {powerBall2.moreBall(player_2);}

    public void upPaddle2() {specialPaddle2.upPaddle();}

    public void resetPaddle2() {specialPaddle2.downPaddle();}

    public void catchBall2() {
        isCatch2 = !isCatch2;
    }


}