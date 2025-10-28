package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;

public class Paddle extends GameEntity {
    private static String path = new File("src/main/resources/graphic/paddle").toString();
    private int moveSpeed = 8;
    private boolean isCatchEnabled = false; // PowerUp CATCH/Sticky Ball
    private Rectangle Paddle;
    // Kích thước mặc định
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 50 ;

    public Paddle(double x, double y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, path);
        Paddle = new Rectangle(x - DEFAULT_WIDTH, y - DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }


    @Override
    public void update() {

    }

    public void changeSize(double newSize) {
        this.width = newSize;
        imageView.setFitWidth(width);
        imageView.setTranslateX(pos_x - width/2);

    }


    @Override
    public void setLocation(double v, double dt) {
        this.pos_x = v;
        this.imageView.setTranslateX(this.pos_x - this.width/2);
        updateAnimation(dt);
    }

    public Pair<Double, Double> getPosition() {
        return new Pair<>(this.pos_x - this.width/2, this.pos_y - this.height/2);
    }

    public boolean isCatchEnabled() {
        return isCatchEnabled;
    }
}