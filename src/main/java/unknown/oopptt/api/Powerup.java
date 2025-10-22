package unknown.oopptt.api;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import unknown.oopptt.controller.GameScreen_controller;

import java.io.File;


public class Powerup {

    private String upballview = new File("src/main/resources/graphic/ball.png" ).toURI().toString();

    private ImageView imageView;
    private PowerupType type;
    private int pos_x;
    private int pos_y;
    private int width = 40;
    private int height = 20;
    private final GameScreen_controller controller;

    public enum PowerupType {
        UPBALL(10), UPPADDLE(8), SHEILD(-1), MOREBALL(-1), SLOW(10), CATCHBALL(10), GUN(8);;
        private final int duration;
        PowerupType(int duration) {
            this.duration = duration;
        }
        public int getDuration() {
            return duration;
        }
        public boolean haveDuration() {
            return duration > 0;
        }

    }

    public Powerup(PowerupType type, int pos_x, int pos_y, GameScreen_controller controller) {
        this.type = type;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.type = type;
        this.controller = controller;
        switch (type) {
            case PowerupType.UPBALL:
                imageView = new ImageView(new Image(upballview));
                break;
            case PowerupType.UPPADDLE:
                imageView = new ImageView(new Image(upballview));
                break;
            case PowerupType.SHEILD:
                imageView = new ImageView(new Image(upballview));
                break;
            case  PowerupType.MOREBALL:
                imageView = new ImageView(new Image(upballview));
                break;
            case PowerupType.SLOW:
                imageView = new ImageView(new Image(upballview));
                break;
            case PowerupType.CATCHBALL:
                imageView = new ImageView(new Image(upballview));
                break;
            case PowerupType.GUN:
                imageView = new ImageView(new Image(upballview));
                break;
        }
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setTranslateX(pos_x -  imageView.getFitWidth() / 2);
        imageView.setTranslateY(pos_y -  imageView.getFitHeight() / 2);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void movedown() {
        this.pos_y += 4.5;
        imageView.setTranslateY(pos_y);
    }

    private void runTimeEffect(int duration, Runnable startEffect, Runnable endEffect) {
        new Thread(() -> {
            Platform.runLater((startEffect));
            try {
                Thread.sleep(duration * 1000L);
            } catch (InterruptedException e) {}
            Platform.runLater(endEffect);
        }).start();
    }

    public void WhenCollison() {
        switch (type) {
            case PowerupType.UPBALL:
                System.out.println("ditmecuocdoi");
                runTimeEffect(type.getDuration(), controller::upBall, controller::resetBall);
                break;
            case PowerupType.UPPADDLE:
                runTimeEffect(type.getDuration(), controller::upPaddle, controller::resetpaddle);
                break;
            case PowerupType.SHEILD:
                controller.sheild();
                break;
            case  PowerupType.MOREBALL:
                controller.moreBall();
                break;
            case PowerupType.SLOW:
                runTimeEffect(type.getDuration(), controller::slowBall, controller::resetSlowBall);
                break;
            case PowerupType.CATCHBALL:
                runTimeEffect(type.getDuration(),  controller::catchBall, controller::resetBall);
                break;
            case PowerupType.GUN:
                controller.gun();
                break;

        }
        if (type.haveDuration()) {
            new Thread(()->{

            }).start();
        }
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getPos_x() {
        return pos_x;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }

    public int getPos_y() {
        return pos_y;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
    }
}
