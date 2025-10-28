package unknown.oopptt.api;

import javafx.scene.image.ImageView;

public  final class Bullet{
    double x, y;
    double vy;
    final ImageView imageView;
    boolean alive;
    Bullet(ImageView imageView){this.imageView = imageView;}
}