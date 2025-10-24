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
    private AnchorPane Player_1;
    @FXML
    private ImageView ScreenP1;
    @FXML
    private AnchorPane Player_2;
    @FXML
    private ImageView ScreenP2;
    @FXML
    private ImageView Data_Sheet_P1;
    @FXML
    private ImageView Data_Sheet_P2;
    @FXML
    public void initialize()
    {
        connectToServer();
        setPos((int) ScreenP1.getBoundsInParent().getCenterX());
        set_Player1_BackGround();
        set_Player2_BackGround();
        setOnMouse_Paddle();
        startgameloop1();

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

    private void set_Player2_BackGround()
    {
        Platform.runLater(()-> {
            ScreenP2.setImage(new Image(background_Game));
            paddleLogic2 = new Paddle(ScreenP2.getBoundsInParent().getCenterX(), ScreenP2.getBoundsInParent().getMaxY() - 20);
            Player_2.getChildren().add(paddleLogic2.getImageView());
            Ball first_ball = new Ball(paddleLogic2.getPos_x(),paddleLogic2.getPos_y() - 2,1,0);
            gameBall2.add(first_ball);
            Player_2.getChildren().add(first_ball.getImageView());

            powerBall2 = new PowerBall(gameBall1);

        });
    }
    private void set_Move_Opponent(){
        Platform.runLater(() -> {
            int low = (int) ScreenP2.getBoundsInParent().getMinX();
            int high = (int) ScreenP2.getBoundsInParent().getMaxX();
            int i = 0;
            while (i <= high && i >= low) {
                paddleLogic2.setLocation(i);
                if (i == high) i = low;
                i++;
            }
        });
    }

    private void setOnMouse_Paddle() {
        Player_1.setCursor(Cursor.NONE);
        Platform.runLater(() -> {
            Player_1.setOnMouseClicked(event -> {
                for (Ball ball : gameBall1) {
                    ball.setSticky(false);
                }
            });
            Player_1.setOnMouseMoved(event -> {
                int newX = (int) Math.round(
                        Math.max(
                                ScreenP1.getBoundsInParent().getMinX() + paddleLogic1.getWidth() / 2,
                                Math.min(event.getX() - paddleLogic1.getWidth() / 2,
                                        ScreenP1.getBoundsInParent().getMaxX()- paddleLogic1.getWidth() / 2)
                        )
                );
                //System.out.println(newX);
                paddleLogic1.setLocation(newX);
                client.send("Move:"+Integer.toString(newX));
            });

        });
    }
    private void set_Player1_BackGround()
    {
        Platform.runLater(()-> {
            ScreenP1.setImage(new Image(background_Game));
            paddleLogic1 = new Paddle(ScreenP1.getBoundsInParent().getCenterX(), ScreenP1.getBoundsInParent().getMaxY() - 20);
            Player_1.getChildren().add(paddleLogic1.getImageView());
            Ball first_ball = new Ball(paddleLogic1.getPos_x(),paddleLogic1.getPos_y() - 2,1,0);
            gameBall1.add(first_ball);
            Player_1.getChildren().add(first_ball.getImageView());

            powerBall1 = new PowerBall(gameBall1);
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
                        Player_1.getChildren().add(new_Brick.getImageView());
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
                        Player_2.getChildren().add(new_Brick.getImageView());
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

    private void gameBall() {
        for (Ball ballLogic: gameBall1) {
            if (ballLogic.isSticky()) {
                double newX = paddleLogic1.getPos_x() + ballLogic.getPosinPaddle();
                if (newX > paddleLogic1.getImageView().getBoundsInParent().getMaxX() ||
                        newX < paddleLogic1.getImageView().getBoundsInParent().getMinX()) {
                    newX = Math.max(paddleLogic1.getImageView().getBoundsInParent().getMinX(),
                            Math.min(paddleLogic1.getImageView().getBoundsInParent().getMaxX(), paddleLogic1.getWidth()));
                    ballLogic.updateSpeedX(-ballLogic.getSpeedX());
                }
                ballLogic.setLocation(newX);
                ballLogic.setPosinPaddle();
            } else {
                baseGame1.brickCollision(ballLogic,gameBricks1,Player_1, gamePowerup1);
                baseGame1.paddleballCollision(ballLogic, paddleLogic1, isCatch1);
                baseGame1.wallCollision(ballLogic,ScreenP1);
                //System.out.println(ballLogic.getSpeedX() + " " + ballLogic.getSpeedY());
                ballLogic.update();
            }
        }
    }
    private void setGamePowerup() {
        for (int i = gamePowerup1.size() - 1; i >= 0; i--) {
            Powerup powerup = gamePowerup1.get(i);
            if (baseGame1.paddlePUCollision(powerup,paddleLogic1,"BTS")) {
                gamePowerup1.remove(powerup);
                Player_1.getChildren().remove(powerup.getImageView());
            } else {
                powerup.movedown();
            }

        }
    }
    private void startgameloop1() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameBall();
                setGamePowerup();
                System.out.println(getPos());
                paddleLogic2.setLocation(getPos());

                //tem.out.println(layout_game.getBoundsInParent().getMinX() + " " + layout_game.getBoundsInParent().getMinY());
            }
        };
        timer.start();
    }
    public void upBall() {powerBall1.upBall();}

    public void resetBall() {powerBall1.downBall();}

    public void slowBall() {powerBall1.slowBall();}

    public void resetSlowBall() {powerBall1.normalBall();}

    public void openSheild() {sheild1.openSheild(gameBricks1, Player_1, ScreenP1);}

    public void moreBall() {powerBall1.moreBall(Player_1);}

    public void upPaddle() {specialPaddle1.upPaddle();}

    public void resetPaddle() {specialPaddle1.downPaddle();}


    public void catchBall() {
        isCatch1 = !isCatch1;
    }


}