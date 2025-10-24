package unknown.oopptt.api;


import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.controller.BattleScreenController;
import unknown.oopptt.controller.GameScreen_controller;

import javax.sound.midi.MidiFileFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BaseGame {
    private GameScreen_controller controller;
    private BattleScreenController BTcontroller;
    public BaseGame(BattleScreenController BTcontroller){
        this.BTcontroller = BTcontroller;
    }
    public BaseGame(GameScreen_controller controller) {
        this.controller = controller;
    }
    public void wallCollision(Ball balllogic, ImageView gameBackground) {

        double ballXl = balllogic.getImageView().getBoundsInParent().getMinX();
        double ballXr = balllogic.getImageView().getBoundsInParent().getMaxX();
        double ballYl = balllogic.getImageView().getBoundsInParent().getMinY();
        double ballYr = balllogic.getImageView().getBoundsInParent().getMaxY();
        double speedX = balllogic.getSpeedX();
        double speedY = balllogic.getSpeedY();

        double wallXl = gameBackground.getBoundsInParent().getMinX() + 2;
        double wallXr = gameBackground.getBoundsInParent().getMaxX() - 2;
        double wallYl = gameBackground.getBoundsInParent().getMinY();
        double wallYr = gameBackground.getBoundsInParent().getMaxY();


        if (ballXl <= wallXl && speedX <= 0) {
            balllogic.updateSpeedX(-balllogic.getSpeedX());
        }
        if (ballXr >= wallXr && speedX >= 0) {
            balllogic.updateSpeedX(-balllogic.getSpeedX());
        }
        if (ballYl<= wallYl || ballYr >= wallYr) {
            balllogic.updateSpeedY(-balllogic.getSpeedY());
        }
    }

    public void paddleballCollision(Ball balllogic, Paddle paddlelogic, boolean isCatch) {
        if (balllogic.getImageView().getBoundsInParent().intersects(paddlelogic.getImageView().getBoundsInParent())) {
            if (isCatch && balllogic.getSpeedY() != 0) {
                balllogic.updateSpeedX(1);
                balllogic.updateSpeedY(0);
                balllogic.setSticky(true);
                return ;
            }

            double t = 2 * (balllogic.getImageView().getBoundsInParent().getCenterX()
                    - paddlelogic.getImageView().getBoundsInParent().getCenterX()) / paddlelogic.getWidth();
            t = Math.max(-0.98, Math.min(0.98, t));

            double minAngle = 15;
            double maxAngle = 165;
            double deg = (maxAngle - minAngle)*(t + 1)/2 + minAngle;


            double rad = Math.toRadians(-deg);
            double dirX = - Math.cos(rad);
            double dirY = Math.sin(rad);

            double len = Math.hypot(dirX, dirY);
            dirX = dirX / len;
            dirY = dirY / len;

            balllogic.updateSpeedX(balllogic.getSpeedXY() * dirX);
            balllogic.updateSpeedY(balllogic.getSpeedXY() * dirY);
        }

    }
    public boolean paddlePUCollision(Powerup p, Paddle paddlelogic,String define) {
        if (p.getImageView().getBoundsInParent().intersects(paddlelogic.getImageView().getBoundsInParent())) {
            if(define.equals("GS")){
                p.WhenCollison(controller);
            }
            if(define.equals("BTS")){
                p.WhenCollison(BTcontroller);
            }
            return true;
        }
        return false;

    }

    public void brickCollision(Ball ball, List<Brick> bricks, Pane root, List<Powerup> powerups) {
        ImageView bv = ball.getImageView();
        for (int i = bricks.size() - 1; i >= 0; i--) {
            Brick brick = bricks.get(i);
            ImageView rv = brick.getImageView();
            if (!bv.getBoundsInParent().intersects(rv.getBoundsInParent())) continue;

            Bounds b = bv.getBoundsInParent();
            Bounds r = rv.getBoundsInParent();

            double oL = b.getMaxX() - r.getMinX();
            double oR = r.getMaxX() - b.getMinX();
            double oT = b.getMaxY() - r.getMinY();
            double oB = r.getMaxY() - b.getMinY();

            double oX = Math.min(oL, oR);
            double oY = Math.min(oT, oB);

            if (oX < oY) {
                double push = (oL < oR) ? -oL : oR;
                bv.setTranslateX(bv.getTranslateX() + push);
                ball.updateSpeedX(-ball.getSpeedX());
            } else {
                double push = (oT < oB) ? -oT : oB;
                bv.setTranslateY(bv.getTranslateY() + push);
                ball.updateSpeedY(-ball.getSpeedY());
            }

            if (!brick.hit()) {
                rv.setVisible(false);
                root.getChildren().remove(rv);
                bricks.remove(i);

                if (shouldDrop(0.5)) {
                    Powerup p = new Powerup(rv.getBoundsInParent().getCenterX(), rv.getBoundsInParent().getCenterY());
                    root.getChildren().add(p.getImageView());
                    powerups.add(p);
                }
            }
            break;
        }
    }
    public boolean outBall(Ball ballLogic, ImageView gameBackground) {
        if (ballLogic.getImageView().getBoundsInParent().getMaxY() >= gameBackground.getBoundsInParent().getMaxY()) {
            return true;
        }
        return false;
    }
    public boolean outPowerup(Powerup p, ImageView gameBackground) {
        if (p.getImageView().getBoundsInParent().getMaxY() >= gameBackground.getBoundsInParent().getMaxY()) {
            return true;
        }
        return false;
    }

    boolean shouldDrop(double p) {
        return ThreadLocalRandom.current().nextDouble() < p; // p ∈ [0..1]
    }
}

