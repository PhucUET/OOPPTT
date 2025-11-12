package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import unknown.oopptt.api.SpriteAnimation;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadingController {
    private ImageView anima = new ImageView();
    private Timeline spin;
    private String spriteName = new File("src/main/resources/graphic/immortality").toString();
    private SpriteAnimation spriteAnimation = new SpriteAnimation(spriteName,30);

    @FXML private VBox layout;
    @FXML
    private void initialize() {
        anima = spriteAnimation.getView();
        anima.setFitWidth(90);
        anima.setFitHeight(90);
        anima.setTranslateX(0);
        anima.setTranslateY(0);
        layout.getChildren().add(anima);
        startGameloop();
    }
    private static final double TARGET_FPS = 60;
    private static final  double STEP = 1.0/TARGET_FPS;
    AnimationTimer timer;
    private void startGameloop() {
         timer = new AnimationTimer() {
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
                   spriteAnimation.update(dt);
                }

            }
        };
        timer.start();
    }
    public void stop() {
        timer.stop();
    }

}
