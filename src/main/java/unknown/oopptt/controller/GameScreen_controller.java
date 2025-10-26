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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.spi.AbstractResourceBundleProvider;

public class GameScreen_controller {
    private static String background_Video = new File("src/main/resources/graphic/video1.mp4").toURI().toString();
    private static String background_Game = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static File mapBrick1 = new File("src/main/resources/map/map1.txt");
    final double LOGICAL_WIDTH = 800;
    final double LOGICAL_HEIGHT = 600;

    private final static int SCNENE_WIDTH = 1440;
    private final static int SCENE_HEIGHT = 810;
    private boolean inPaddle = true;
    List<Brick> gameBricks = new LinkedList<Brick>();
    List<Powerup> gamePowerup = new LinkedList<>();
    Sheild sheild = new Sheild();
    Shooter shooter;

    BaseGame baseGame = new BaseGame(this);

    @FXML
    private StackPane stack_root;
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView gameBackground;
    @FXML
    private Paddle paddleLogic;

    private List<Ball> gameBall = new ArrayList<Ball>();
    PowerBall powerBall;
    private SpecialPaddle specialPaddle;

    @FXML
    AnchorPane gamePane;
    @FXML
    Pane layout_game;
    @FXML
    Group gameGroup;
    private boolean isCatch = false;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer1;


    private void set_Background() {


        Platform.runLater(()-> {
            gameBackground.setImage(new Image(background_Game));

            paddleLogic = new Paddle(gameBackground.getBoundsInParent().getCenterX(), gameBackground.getBoundsInParent().getMaxY() - 20);
            layout_game.getChildren().add(paddleLogic.getImageView());
            specialPaddle = new SpecialPaddle(paddleLogic);

            Ball first_ball = new Ball(paddleLogic.getPos_x(),paddleLogic.getPos_y() - 2,1,0);
            gameBall.add(first_ball);
            layout_game.getChildren().add(first_ball.getImageView());
            powerBall = new PowerBall(gameBall);
            shooter = new Shooter(200, 2, 1,3,layout_game, paddleLogic.getImageView(),20);

            upMap();
        });
    }

    private void chain(MediaPlayer a, MediaPlayer b, MediaView view) {
        a.setOnReady(() -> {
            Duration total = a.getMedia().getDuration();
            a.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (total.greaterThan(Duration.ZERO)
                        && newTime.greaterThan(total.subtract(Duration.millis(200)))) {
                    if (b.getStatus() != MediaPlayer.Status.PLAYING) {
                        Platform.runLater(() -> {
                            view.setMediaPlayer(b);
                            b.seek(Duration.ZERO);
                            b.play();
                            a.stop();
                            a.seek(Duration.ZERO);
                        });
                    }
                }
            });
        });
    }

    void setBackground_Video() {
        mediaPlayer =  new MediaPlayer(new Media(background_Video));
        mediaPlayer1 =  new MediaPlayer(new Media(background_Video));


        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(stack_root.widthProperty());
        mediaView.fitHeightProperty().bind(stack_root.heightProperty());

        chain(mediaPlayer, mediaPlayer1, mediaView);

        chain(mediaPlayer1, mediaPlayer, mediaView);

        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }



    private void upMap() {
        int startX = 0;
        int startY = 0;
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
                        gameBricks.add(new_Brick);
                        layout_game.getChildren().add(new_Brick.getImageView());
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


    @FXML
    public void initialize() {

        // Giãn gamePane full stack_root
       // System.out.println(gameBackground.getBoundsInParent().getWidth() +  " " + gameBackground.getBoundsInParent().getHeight());
        stack_root.setAlignment(Pos.CENTER);
        // setlayout game
//        Platform.runLater(()-> {
//            System.out.println(stack_root.localToScene(stack_root.getBoundsInLocal()));
//
//            System.out.println(layout_game.localToScene(layout_game.getBoundsInLocal()));
//            System.out.println(layout_game.localToScene(layout_game.getBoundsInParent()));
//
//            System.out.println(layout_game.getBoundsInParent().getWidth() + " " + layout_game.getBoundsInParent().getHeight());
//        });
//        setBackground_Video();
        set_Background();



        // di chuyển paddle


        startgameloop();
        setOnMouse_Paddle();

    }



    /**
     * paddle di chuyển.
     */

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
                //System.out.println(newX);
                paddleLogic.setLocation(newX);
            });

        });


    }

    /**
     * Bóng di chuyển
     */

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
            } else {
//                if (baseGame.outBall(ballLogic, gameBackground)) {
//                    gameBall.remove(i);
//                    layout_game.getChildren().remove(ballLogic.getImageView());
//                    continue;
//                }
                baseGame.random = 0;
                baseGame.brickCollision(ballLogic,gameBricks,layout_game, gamePowerup);
                baseGame.paddleballCollision(ballLogic, paddleLogic, isCatch);
                baseGame.wallCollision(ballLogic,gameBackground);
               // System.out.println("ngusi" + ballLogic.getSpeedX() + " " + ballLogic.getSpeedY());
                ballLogic.updatePos(dt);
            }
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


    private void startgameloop() {
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
               // System.out.println("frame" + dt);
                lasts = now;
                shooter.update(dt,gameBricks);
                gameBall(dt);
                setGamePowerup();
            }
        };
        timer.start();
    }


    public void upBall() {powerBall.upBall();}

    public void resetBall() {powerBall.downBall();}

    public void slowBall() {powerBall.slowBall();}

    public void resetSlowBall() {powerBall.normalBall();}

    public void openSheild() {sheild.openSheild(gameBricks, layout_game, gameBackground);}

    public void moreBall() {powerBall.moreBall(layout_game);}

    public void upPaddle() {specialPaddle.upPaddle();}

    public void resetPaddle() {specialPaddle.downPaddle();}


    public void catchBall() {
        isCatch = !isCatch;
    }

    public void enableGun() {
        shooter.setEnabled(true);
    }

    public void unEnableGun() {
        shooter.setEnabled(false);
    }
}
