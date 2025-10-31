package unknown.oopptt.api;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.Rectangle;
import java.io.File;

/**
 * Lớp trừu tượng cơ sở (Abstract Base Class) cho tất cả các đối tượng
 * trong game Arkanoid.
 * Nó định nghĩa các thuộc tính cơ bản về vị trí, kích thước và trạng thái.
 */
public abstract class GameEntity {

    private static String path = new File("src/main/resources/graphic/ball_orange.png").toURI().toString();
    protected ImageView imageView;
    double pos_x, pos_y,width,height;
    private static String superBall = new File("src/main/resources/graphic/Slime2_Attack_with_shadow.png").toURI().toString();
    private  SpriteAnimation animation;
    /**
     * Constructor của GameEntity.
     * @param x Tọa độ X ban đầu.
     * @param y Tọa độ Y ban đầu.
     * @param width Chiều rộng của đối tượng.
     * @param height Chiều cao của đối tượng.
     */
    public GameEntity(double x, double y, double width, double height, String path) {
        animation = new SpriteAnimation(path, 10 );
        animation.setDisplaySize(width, height);
        this.imageView = animation.getView();
        this.imageView.setTranslateX(x - width/2);
        this.imageView.setTranslateY(y -  height/2);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);

        pos_x = x;
        pos_y = y;
        this.width = width;
        this.height = height;
    }

    public void setAnimation(String newpath) {
       animation.changeFrames(newpath);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void updateAnimation(double dt) {
        animation.update(dt);
    }


    public abstract void update();


    public abstract void setLocation(double v, double dt);

    public double getPos_x() {return pos_x;}

    public double getPos_y() {return pos_y;}

    public double getWidth() {return width;}

    public double getHeight() {return height;}
}


