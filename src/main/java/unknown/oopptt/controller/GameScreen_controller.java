package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import java.util.LinkedList;
import java.util.List;

public class GameScreen_controller {
    private static String background_Video = new File("src/main/resources/graphic/video1.mp4").toURI().toString();
    private static String background_Game = new File("src/main/resources/graphic/background10.jpg").toURI().toString();
    private static File mapBrick1 = new File("src/main/resources/map/map1.txt");

    private final static int SCNENE_WIDTH = 1440;
    private final static int SCENE_HEIGHT = 810;
    private boolean inPaddle = true;
    List<Brick> gameBricks = new LinkedList<Brick>();
    @FXML
    private StackPane stack_root;
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView gameBackground;
    @FXML
    private Paddle paddleLogic = new Paddle(720, 700);

    @FXML
    private Ball ballLogic = new Ball(720, 680, 1, 0);


    private double min_x;
    private double min_y;
    private double max_x;
    private double max_y;

    private MediaPlayer mediaPlayer = new MediaPlayer(new Media(background_Video));
    private MediaPlayer mediaPlayer1 = new MediaPlayer(new Media(background_Video));
    @FXML
    AnchorPane gamePane;
    @FXML
    Pane layout_game;

    private void set_layout(double x, double y, double width, double height) {
        min_x = x;
        min_y = y;
        max_x = x + width - 1;
        max_y = y + height - 1;

    }

    public void setBackground(String background) {
        gameBackground.setImage(new Image(background_Game));
        gameBackground.setPreserveRatio(false);
        gameBackground.setFitWidth(500);
        gameBackground.setFitHeight(650);

        Platform.runLater(() -> {
//            gameBackground.setTranslateX(stack_root.getWidth()/2 - gameBackground.getFitWidth()/2);
//            gameBackground.setTranslateY(stack_root.getHeight()/2 - gameBackground.getFitHeight()/2);

            set_layout(gameBackground.getBoundsInParent().getMinX(), gameBackground.getBoundsInParent().getMinY(),
                    gameBackground.getBoundsInParent().getWidth(), gameBackground.getBoundsInParent().getHeight());
        });


    }


    private void setLayout_game() {
        layout_game.prefWidthProperty().bind(stack_root.widthProperty());
        layout_game.prefHeightProperty().bind(stack_root.heightProperty());
        System.out.println(layout_game.getLayoutX() + " " + layout_game.getLayoutY() + " " + layout_game.getWidth() + " " + layout_game.getHeight());

        layout_game.getChildren().add(paddleLogic.getImageView());
        layout_game.getChildren().add(ballLogic.getImageView());
    }

    private void setOnMouse_Paddle() {
        layout_game.setCursor(Cursor.NONE);
        Platform.runLater(() -> {
            layout_game.setOnMouseClicked(event -> {
                if (inPaddle) {
                    inPaddle = false;
                    ballLogic.updateSpeedY(-1);
                    ballLogic.updateSpeedX(10 * (ballLogic.getPosinPaddle() / paddleLogic.getWidth()));
                    ballLogic.update();
                }
            });
            layout_game.setOnMouseMoved(event -> {
                int newX = (int) Math.round(
                        Math.max(
                                SCNENE_WIDTH / 2 - gameBackground.getFitWidth() / 2 + paddleLogic.getWidth() / 2,
                                Math.min(event.getX() - paddleLogic.getWidth() / 2, SCNENE_WIDTH / 2
                                        + gameBackground.getFitWidth() / 2 - paddleLogic.getWidth() / 2)
                        )
                );
                ;
                paddleLogic.setLocation(newX);
            });

        });

    }

    private void upMap() {
        int startX = 485;
        int startY = 400;
        try (BufferedReader br = new BufferedReader(new FileReader(mapBrick1))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\t");
                for (int col = 0; col < data.length; col++) {
                    int type = Integer.parseInt(data[col]);
                    if (type > 0) {
                        int newX = startX + col * 33;
                        int newY = startY + row * 15;
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


        gamePane.prefWidthProperty().bind(stack_root.widthProperty());
        gamePane.prefHeightProperty().bind(stack_root.heightProperty());

        // Set background

        setBackground("huhu");
            setBackground_Video();
        setLayout_game();

        upMap();

        // Set paddle
        setOnMouse_Paddle();
        startGameLoop();
    }

    private void checkWall() {
        if (ballLogic.getImageView().getBoundsInParent().getMinX() <= min_x || ballLogic.getImageView().getBoundsInParent().getMaxX() >= max_x) {
            ballLogic.updateSpeedX(-ballLogic.getSpeedX());
        }
        if (ballLogic.getImageView().getBoundsInParent().getMinY() <= min_y || ballLogic.getImageView().getBoundsInParent().getMaxY() >= max_y) {
            ballLogic.updateSpeedY(-ballLogic.getSpeedY());
        }
    }

    private void game_Ball() {
        if (inPaddle) {
            double new_X = paddleLogic.getPos_x() + ballLogic.getPosinPaddle();
            if (new_X < paddleLogic.getImageView().getBoundsInParent().getMinX()) {
                ballLogic.updateSpeedX(-ballLogic.getSpeedX());
            }
            if (new_X > paddleLogic.getImageView().getBoundsInParent().getMaxX()) {
                ballLogic.updateSpeedX(-ballLogic.getSpeedX());
            }
            ballLogic.setLocation(new_X);
            ballLogic.setPosinPaddle();
        } else {
            Collision_Paddle();
            checkWall();
            Collision_Brick();
            ballLogic.update();
        }
    }

    private double getDist(double x, double y) {
        return Math.hypot(ballLogic.getPos_x() - x, ballLogic.getPos_y() - y);
    }

    private void Collision_Brick() {
        for (Brick brick : gameBricks) {
            if (ballLogic.getImageView().getBoundsInParent().intersects(brick.getImageView().getBoundsInParent())) {
                Bounds b = ballLogic.getImageView().getBoundsInParent();
                Bounds r = brick.getImageView().getBoundsInParent();

                double overlapLeft = b.getMaxX() - r.getMinX();
                double overlapRight = r.getMaxX() - b.getMinX();
                double overlapTop = b.getMaxY() - r.getMinY();
                double overlapBottom = r.getMaxY() - b.getMinY();

                double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom));

                if (minOverlap == overlapLeft) {
                    ballLogic.updateSpeedX(-Math.abs(ballLogic.getSpeedX()));
                }
                if (minOverlap == overlapRight) {
                    ballLogic.updateSpeedX(Math.abs(ballLogic.getSpeedX()));
                }
                if (minOverlap == overlapTop) {
                    ballLogic.updateSpeedY(-Math.abs(ballLogic.getSpeedY()));
                }
                if (minOverlap == overlapBottom) {
                    ballLogic.updateSpeedY(Math.abs(ballLogic.getSpeedY()));
                }
                layout_game.getChildren().remove(brick.getImageView());
                if (!brick.hit()) {
                    gameBricks.remove(brick);
                } else {
                    layout_game.getChildren().add(brick.getImageView());
                }
                return;
            }
        }
    }

    private void Collision_Paddle() {
        Platform.runLater(() -> {
            Bounds ballB = ballLogic.getImageView().getBoundsInParent();
            Bounds padB = paddleLogic.getImageView().getBoundsInParent();

            if (ballLogic.getImageView().getBoundsInParent().intersects(paddleLogic.getImageView().getBoundsInParent())) {
                if (ballLogic.getSpeedY() > 0) {
                    double offset = (ballLogic.getPos_x() - paddleLogic.getPos_x()) / (paddleLogic.getWidth() / 2);
                    offset = Math.max(-1, Math.min(1, offset));
                    double newSpeedX = offset * 5;
                    ballLogic.updateSpeedX(newSpeedX);
                    ballLogic.updateSpeedY(-Math.abs(ballLogic.getSpeedY()));

                }
            }
        });

    }

    private long lastUpdateTime = 0;
    private final double MAX_SPEED = 1000;
    int cnt = 0;

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                game_Ball();
            }
        };
        timer.start();
    }

    private void chain(MediaPlayer a, MediaPlayer b, MediaView view) {
        a.setOnReady(() -> {
            Duration d = a.getMedia().getDuration();
            if (!Duration.UNKNOWN.equals(d) && d.greaterThan(Duration.ZERO)) {
                Duration stop = d.subtract(Duration.millis(100));
                if (stop.greaterThan(Duration.ZERO)) {
                    a.setStopTime(stop);
                }
            }
        });

        a.setOnEndOfMedia(() -> {
            a.stop();
            a.seek(Duration.ZERO);

            view.setMediaPlayer(b);
            b.seek(Duration.ZERO);
            b.play();
        });
    }

    void setBackground_Video() {
        chain(mediaPlayer, mediaPlayer1, mediaView);

        chain(mediaPlayer1, mediaPlayer, mediaView);

        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }


}
