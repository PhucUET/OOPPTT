package unknown.oopptt.api;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.awt.Rectangle;

/**
 * Lớp trừu tượng cơ sở (Abstract Base Class) cho tất cả các đối tượng
 * trong game Arkanoid.
 * Nó định nghĩa các thuộc tính cơ bản về vị trí, kích thước và trạng thái.
 */
public abstract class GameEntity {

    protected ImageView imageView;
    double pos_x, pos_y,width,height;

    /**
     * Constructor của GameEntity.
     * @param x Tọa độ X ban đầu.
     * @param y Tọa độ Y ban đầu.
     * @param width Chiều rộng của đối tượng.
     * @param height Chiều cao của đối tượng.
     */
    public GameEntity(int x, int y, int width, int height, String path) {
        imageView = new ImageView(new Image(path));
        imageView.setTranslateX(x - width/2);
        imageView.setTranslateY(y -  height/2);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        pos_x = x;
        pos_y = y;
        this.width = width;
        this.height = height;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public abstract void update();


    public abstract void setLocation(double v);

    public double getPos_x() {return pos_x;}

    public double getPos_y() {return pos_y;}

    public double getWidth() {return width;}

    public double getHeight() {return height;}
}


