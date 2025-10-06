package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.w3c.dom.ls.LSOutput;
import unknown.oopptt.api.Paddle;

import java.io.File;
import java.sql.SQLOutput;

public class GameScreen_controller {
    private static String background_Video = new File("src/main/graphic/video1.mp4").toURI().toString();
    private static String background_Game = new File("src/main/graphic/background10.jpg").toURI().toString();
    private static String background_Paddle = new File("src/main/graphic/n-paddle0.png").toURI().toString();
    @FXML
    private Pane stack_root;
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView gameBackground;
    @FXML
    private ImageView paddle;
    @FXML
    private Paddle paddleLogic = new Paddle(720,700);

    private MediaPlayer mediaPlayer = new MediaPlayer(new Media(background_Video));
    private MediaPlayer mediaPlayer1 = new MediaPlayer(new Media(background_Video));
    @FXML
    AnchorPane gamePane;
    @FXML
    Pane layout_game;
    @FXML
    void setBackground_Video() {
        StackPane.setAlignment(mediaView, Pos.CENTER);
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(stack_root.widthProperty());
        mediaView.fitHeightProperty().bind(stack_root.heightProperty());
        mediaPlayer.setOnPlaying(() ->{
            Duration duration = mediaPlayer.getMedia().getDuration();
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (duration.greaterThan(Duration.ZERO) && newTime.greaterThan(duration.subtract(Duration.seconds(0.1)))) {
                    if (mediaPlayer1.getStatus() != MediaPlayer.Status.PLAYING) {
                        mediaView.setMediaPlayer(mediaPlayer1);
                        mediaPlayer1.play();
                        mediaPlayer.stop();
                        mediaPlayer.seek(Duration.ZERO);
                    }
                }
            });
        });
        mediaPlayer1.setOnPlaying(() ->{
            Duration duration = mediaPlayer1.getMedia().getDuration();
            mediaPlayer1.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (duration.greaterThan(Duration.ZERO) && newTime.greaterThan(duration.subtract(Duration.seconds(0.1)))) {
                    if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                        mediaView.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                        mediaPlayer1.stop();
                        mediaPlayer1.seek(Duration.ZERO);
                    }
                }
            });
        });
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }

    @FXML
    public void initialize() {
        setBackground_Video();

        // Giãn gamePane full stack_root


        gamePane.prefWidthProperty().bind(stack_root.widthProperty());
        gamePane.prefHeightProperty().bind(stack_root.heightProperty());

        layout_game.prefWidthProperty().bind(stack_root.widthProperty());
        layout_game.prefHeightProperty().bind(stack_root.heightProperty());

        // Set background

        gameBackground.setImage(new Image(background_Game));
        gameBackground.setPreserveRatio(false);
        gameBackground.setFitWidth(600);
        gameBackground.setFitHeight(650);

        gameBackground.setTranslateX(720 - 300);
        gameBackground.setTranslateY(410 - 325);


        // Set paddle
        paddle.setImage(new Image(background_Paddle));
        paddle.setPreserveRatio(false);
        paddle.setFitWidth(100);
        paddle.setFitHeight(15);


        paddle.setTranslateX(paddleLogic.getPosition().getKey());
        paddle.setTranslateY(paddleLogic.getPosition().getValue());
        layout_game.setCursor(Cursor.NONE);

        layout_game.setOnMouseMoved(event -> {
            int newX = (int)Math.round(
                    Math.max(
                            720 - 600/2 + paddleLogic.getWidth()/2,
                            Math.min(event.getX() - paddle.getFitWidth()/2,720 + 600/2 - paddleLogic.getWidth()/2)
                    )
            );;
            paddleLogic.setLocation(newX);
            System.out.println(event.getX() +" "+newX + " " + paddleLogic.getX());
            paddle.setTranslateX(paddleLogic.getPosition().getKey());
        });

//        startGameLoop();
    }

//    private void startGameLoop() {
//        AnimationTimer timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//
//            }
//        }
//    }




}
