package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;

public class Paddle extends GameEntity {
    private static String path = new File("src/main/resources/graphic/paddle").toString();
    private double moveSpeed = 500;
    private boolean isCatchEnabled = false; // PowerUp CATCH/Sticky Ball
    private Rectangle Paddle;
    private ImageView backgroundGame;
    // Kích thước mặc định
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 50;

    private Boolean leftHeld = false;
    private Boolean rightHeld = false;
    private int redir = 1;
    private Boolean isDead = false;

    public Paddle(double x, double y, ImageView backgroundGame) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, path);
        Paddle = new Rectangle(x - DEFAULT_WIDTH, y - DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.backgroundGame = backgroundGame;
    }

    public Boolean getDead() {
        return isDead;
    }

    public void setDead(Boolean dead) {
        isDead = dead;
    }

    private static double clamp(double v, double lo, double hi) {
        return (v < lo) ? lo : (v > hi) ? hi : v;
    }

    @Override
    public void update(double dt) {
        double transit = 0;
        if (leftHeld) {
            transit = -1;
        }
        if (rightHeld) {
            transit = 1;
        }
        this.pos_x = this.pos_x + dt * moveSpeed * transit * redir;
        if (this.pos_x <= backgroundGame.getBoundsInParent().getMinX() + this.width / 2
                || this.pos_x >= backgroundGame.getBoundsInParent().getMaxX() - this.width / 2) {
            this.pos_x = clamp(this.pos_x, backgroundGame.getBoundsInParent().getMinX() + this.width / 2,
                    backgroundGame.getBoundsInParent().getMaxX() - this.width / 2);
        }
        imageView.setTranslateX(this.pos_x - this.width / 2);
        imageView.setTranslateY(this.pos_y - this.height / 2);

        this.updateAnimation(dt);
    }


    public void changeSize(double newSize) {
        this.width = newSize;
        imageView.setFitWidth(width);
        imageView.setTranslateX(pos_x - width / 2);

    }

    public void setRedir(int redir) {
        this.redir = redir;
    }


    @Override
    public void setLocation(double v, double dt) {
        this.pos_x = v;
        this.imageView.setTranslateX(this.pos_x - this.width / 2);
        updateAnimation(dt);
    }


    public Pair<Double, Double> getPosition() {
        return new Pair<>(this.pos_x - this.width / 2, this.pos_y - this.height / 2);
    }

    public Boolean getRightHeld() {
        return rightHeld;
    }

    public void setRightHeld(Boolean rightHeld) {
        this.rightHeld = rightHeld;
    }

    public Boolean getLeftHeld() {
        return leftHeld;
    }

    public void setLeftHeld(Boolean leftHeld) {
        this.leftHeld = leftHeld;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public boolean isCatchEnabled() {
        return isCatchEnabled;
    }
}