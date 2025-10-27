package unknown.oopptt.api;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.controller.BattleScreenController;
import unknown.oopptt.controller.GameScreen_controller;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Powerup  {



    private String upballview = new File("src/main/resources/graphic/ball.png" ).toURI().toString();

    private ImageView imageView;
    private TranslateTransition translateTransition = new TranslateTransition();
    private PowerupType type;
    private double pos_x;
    private double pos_y;
    private double width = 40;
    private double height = 20;
    private int randomIndex;
    private GameScreen_controller controller;
    private BattleScreenController BTcontroller;

    public int getRandomIndex() {
        return randomIndex;
    }

    public void setRandomIndex(int randomIndex) {
        this.randomIndex = randomIndex;
    }

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
    public PowerupType randomUniform(int index) {
        PowerupType[] vals = PowerupType.values();
        if(index == -1) {
            index = ThreadLocalRandom.current().nextInt(vals.length);
        }
        setRandomIndex(index);
        return vals[index];
    }

    public Powerup(double pos_x, double pos_y,int randomIndex) {
        this.type = randomUniform(randomIndex);
        this.pos_x = pos_x;
        this.pos_y = pos_y;
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
        this.pos_y += 2;
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
    public void WhenCollison(BattleScreenController BTcontroller,boolean player) {
        this.BTcontroller = BTcontroller;
        if (player) {
            switch (type) {
                case PowerupType.UPBALL:
                    System.out.println("UPBALL");
                    runTimeEffect(type.getDuration(), BTcontroller::upBall1, BTcontroller::resetBall1);
                    break;
                case PowerupType.UPPADDLE:
                    System.out.println("UPPADDLE");
                    runTimeEffect(type.getDuration(), BTcontroller::upPaddle1, BTcontroller::resetPaddle1);
                    break;
                case PowerupType.SHEILD:
                    BTcontroller.openSheild1();
                    break;
                case PowerupType.MOREBALL:
                    BTcontroller.moreBall1();
                    break;
                case PowerupType.SLOW:
                    runTimeEffect(type.getDuration(), BTcontroller::slowBall1, BTcontroller::resetSlowBall1);
                    break;
                case PowerupType.CATCHBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::catchBall1, BTcontroller::catchBall1);
                    break;
                case PowerupType.GUN:
                    //runTimeEffect(type.getDuration(), controller::gun, controller:: resetGun);
                    break;

            }
        } else {
            switch (type) {
                case PowerupType.UPBALL:
                    System.out.println("UPBALL");
                    runTimeEffect(type.getDuration(), BTcontroller::upBall2, BTcontroller::resetBall2);
                    break;
                case PowerupType.UPPADDLE:
                    System.out.println("UPPADDLE");
                    runTimeEffect(type.getDuration(), BTcontroller::upPaddle2, BTcontroller::resetPaddle2);
                    break;
                case PowerupType.SHEILD:
                    BTcontroller.openSheild2();
                    break;
                case PowerupType.MOREBALL:
                    BTcontroller.moreBall2();
                    break;
                case PowerupType.SLOW:
                    runTimeEffect(type.getDuration(), BTcontroller::slowBall2, BTcontroller::resetSlowBall2);
                    break;
                case PowerupType.CATCHBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::catchBall2, BTcontroller::catchBall2);
                    break;
                case PowerupType.GUN:
                    //runTimeEffect(type.getDuration(), controller::gun, controller:: resetGun);
                    break;
            }

        }
    }

    public void WhenCollison(GameScreen_controller controller) {
        this.controller = controller;
        switch (type) {
            case PowerupType.UPBALL:
                System.out.println("ditmecuocdoi");
                        runTimeEffect(type.getDuration(), controller::upBall, controller::resetBall);
                break;
            case PowerupType.UPPADDLE:
                runTimeEffect(type.getDuration(), controller::upPaddle, controller::resetPaddle);
                break;
            case PowerupType.SHEILD:
                controller.openSheild();
                break;
            case  PowerupType.MOREBALL:
                controller.moreBall();
                break;
            case PowerupType.SLOW:
                runTimeEffect(type.getDuration(), controller::slowBall, controller::resetSlowBall);
                break;
            case PowerupType.CATCHBALL:
                runTimeEffect(type.getDuration(),  controller::catchBall, controller::catchBall);
                break;
            case PowerupType.GUN:
                runTimeEffect(type.getDuration(),  controller::enableGun, controller::unEnableGun);
                break;

        }

    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public double getPos_x() {
        return pos_x;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }

    public double getPos_y() {
        return pos_y;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
    }
}
