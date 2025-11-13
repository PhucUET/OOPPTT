package unknown.oopptt.api;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.controller.BattleScreenController;
import unknown.oopptt.controller.Game_Screen_Controller;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Powerup {


    private String upballview = new File("src/main/resources/graphic/ball.png").toURI().toString();
    private String catchBall = new File("src/main/resources/graphic/PU/magnet").toString();
    private String shield = new File("src/main/resources/graphic/PU/Defence").toString();
    private String addHp = new File("src/main/resources/graphic/PU/HPx2").toString();
    private String moreBall = new File("src/main/resources/graphic/PU/attackx3").toString();
    private String slowBall = new File("src/main/resources/graphic/PU/Anti-speed").toString();
    private String Gun = new File("src/main/resources/graphic/PU/doppelganger").toString();
    private String reDir = new File("src/main/resources/graphic/PU/redir").toString();
    private ImageView imageView;
    private TranslateTransition translateTransition = new TranslateTransition();
    private PowerupType type;
    private double pos_x;
    private double pos_y;
    private double width = 60;
    private double height = 60;
    private Game_Screen_Controller controller;
    private BattleScreenController BTcontroller;
    private SpriteAnimation spriteAnimation;

    public enum PowerupType {
        MOREBALL(-1), UPBALL(10), UPPADDLE(8), SLOW(10),
        CATCHBALL(10), GUN(8), REDIR(10), ADDHP(-1), SHIELD(-1);
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

    static PowerupType randomUniform() {
        PowerupType[] vals = PowerupType.values();
        int i = ThreadLocalRandom.current().nextInt(vals.length);
        return vals[i];
    }

    static PowerupType randomUniform(int x) {
        PowerupType[] vals = PowerupType.values();
        return vals[x];
    }

    public Powerup(double pos_x, double pos_y) {
        this.type = randomUniform();
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        switch (type) {
            case PowerupType.ADDHP:
                spriteAnimation = new SpriteAnimation(addHp, 30);
            case PowerupType.UPBALL:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.UPPADDLE:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.MOREBALL:
                spriteAnimation = new SpriteAnimation(moreBall, 30);
                break;
            case PowerupType.SLOW:
                spriteAnimation = new SpriteAnimation(slowBall, 30);
                break;
            case PowerupType.CATCHBALL:
                spriteAnimation = new SpriteAnimation(catchBall, 30);
                break;
            case PowerupType.GUN:
                spriteAnimation = new SpriteAnimation(Gun, 30);
                break;
            case PowerupType.REDIR:
                spriteAnimation = new SpriteAnimation(reDir, 30);
                break;
            case PowerupType.SHIELD:
                spriteAnimation = new SpriteAnimation(shield, 30);
                break;
        }
        imageView = spriteAnimation.getView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setTranslateX(pos_x - imageView.getFitWidth() / 2);
        imageView.setTranslateY(pos_y - imageView.getFitHeight() / 2);
    }

    public Powerup(double pos_x, double pos_y, int typePu) {
        this.type = randomUniform(typePu);
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        switch (type) {
            case PowerupType.ADDHP:
                spriteAnimation = new SpriteAnimation(addHp, 30);
            case PowerupType.UPBALL:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.UPPADDLE:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.MOREBALL:
                spriteAnimation = new SpriteAnimation(moreBall, 30);
                break;
            case PowerupType.SLOW:
                spriteAnimation = new SpriteAnimation(slowBall, 30);
                break;
            case PowerupType.CATCHBALL:
                spriteAnimation = new SpriteAnimation(catchBall, 30);
                break;
            case PowerupType.GUN:
                spriteAnimation = new SpriteAnimation(Gun, 30);
                break;
            case PowerupType.REDIR:
                spriteAnimation = new SpriteAnimation(reDir, 30);
                break;
            case PowerupType.SHIELD:
                spriteAnimation = new SpriteAnimation(shield, 30);
                break;
        }
        imageView = spriteAnimation.getView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setTranslateX(pos_x - imageView.getFitWidth() / 2);
        imageView.setTranslateY(pos_y - imageView.getFitHeight() / 2);
    }

    public void WhenCollison(BattleScreenController controller, boolean player) {
        this.BTcontroller = controller;
        if (player) {
            switch (type) {
                case PowerupType.UPBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::upBall1, BTcontroller::resetBall1);
                    break;
                case PowerupType.UPPADDLE:
                    runTimeEffect(type.getDuration(), BTcontroller::upPaddle1, BTcontroller::offUpPaddle1);
                    break;
                case PowerupType.MOREBALL:
                    controller.moreBall1();
                    break;
                case PowerupType.SLOW:
                    runTimeEffect(type.getDuration(), BTcontroller::slowBall1, BTcontroller::resetSlowBall1);
                    break;
                case PowerupType.CATCHBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::catchBall1, BTcontroller::catchBall1);
                    break;
                case PowerupType.GUN:
                    runTimeEffect(type.getDuration(), BTcontroller::enableGun1, BTcontroller::unEnableGun1);
                    break;
                case PowerupType.REDIR:
                    runTimeEffect(type.getDuration(), BTcontroller::setRedirPaddle1, BTcontroller::offRedirPaddle1);
                    break;
                case PowerupType.SHIELD:
                    BTcontroller.setShieldOn1();
                    break;

            }
        } else {
            switch (type) {
                case PowerupType.UPBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::upBall2, BTcontroller::resetBall2);
                    break;
                case PowerupType.UPPADDLE:
                    runTimeEffect(type.getDuration(), BTcontroller::upPaddle2, BTcontroller::offUpPaddle2);
                    break;
                case PowerupType.MOREBALL:
                    BTcontroller.moreBall1();
                    break;
                case PowerupType.SLOW:
                    runTimeEffect(type.getDuration(), BTcontroller::slowBall2, BTcontroller::resetSlowBall2);
                    break;
                case PowerupType.CATCHBALL:
                    runTimeEffect(type.getDuration(), BTcontroller::catchBall2, BTcontroller::catchBall2);
                    break;
                case PowerupType.GUN:
                    runTimeEffect(type.getDuration(), BTcontroller::enableGun2, BTcontroller::unEnableGun2);
                    break;
                case PowerupType.REDIR:
                    runTimeEffect(type.getDuration(), BTcontroller::setRedirPaddle2, BTcontroller::offRedirPaddle2);
                    break;
                case PowerupType.SHIELD:
                    BTcontroller.setShieldOn2();
                    break;
            }
        }

    }

    public Powerup(double pos_x, double pos_y, PowerupType type) {
        this.type = type;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        switch (type) {
            case PowerupType.ADDHP:
                spriteAnimation = new SpriteAnimation(addHp, 30);
            case PowerupType.UPBALL:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.UPPADDLE:
                spriteAnimation = new SpriteAnimation(upballview, 30);
                break;
            case PowerupType.MOREBALL:
                spriteAnimation = new SpriteAnimation(moreBall, 30);
                break;
            case PowerupType.SLOW:
                spriteAnimation = new SpriteAnimation(slowBall, 30);
                break;
            case PowerupType.CATCHBALL:
                spriteAnimation = new SpriteAnimation(catchBall, 30);
                break;
            case PowerupType.GUN:
                spriteAnimation = new SpriteAnimation(Gun, 30);
                break;
            case PowerupType.REDIR:
                spriteAnimation = new SpriteAnimation(reDir, 30);
                break;
            case PowerupType.SHIELD:
                spriteAnimation = new SpriteAnimation(shield, 30);
                break;
        }
        imageView = spriteAnimation.getView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setTranslateX(pos_x - imageView.getFitWidth() / 2);
        imageView.setTranslateY(pos_y - imageView.getFitHeight() / 2);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void movedown(double dt) {
        this.pos_y += 400 * dt;
        imageView.setTranslateY(pos_y - imageView.getFitHeight() / 2);
        spriteAnimation.update(dt);
    }

    private void runTimeEffect(int duration, Runnable startEffect, Runnable endEffect) {
        new Thread(() -> {
            Platform.runLater((startEffect));
            try {
                Thread.sleep(duration * 1000L);
            } catch (InterruptedException e) {
            }
            Platform.runLater(endEffect);
        }).start();
    }

    public void WhenCollison(Game_Screen_Controller controller) {
        this.controller = controller;
        switch (type) {
            case PowerupType.UPBALL:
                runTimeEffect(type.getDuration(), controller::upBall, controller::resetBall);
                break;
            case PowerupType.UPPADDLE:
                runTimeEffect(type.getDuration(), controller::upPaddle, controller::offUpPaddle);
                break;
            case PowerupType.MOREBALL:
                controller.moreBall();
                break;
            case PowerupType.SLOW:
                runTimeEffect(type.getDuration(), controller::slowBall, controller::resetSlowBall);
                break;
            case PowerupType.CATCHBALL:
                runTimeEffect(type.getDuration(), controller::catchBall, controller::catchBall);
                break;
            case PowerupType.GUN:
                runTimeEffect(type.getDuration(), controller::enableGun, controller::unEnableGun);
                break;
            case PowerupType.REDIR:
                runTimeEffect(type.getDuration(), controller::setRedirPaddle, controller::offRedirPaddle);
                break;
            case PowerupType.SHIELD:
                controller.setShieldOn();
                break;
            case PowerupType.ADDHP:
                controller.upHp();
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
