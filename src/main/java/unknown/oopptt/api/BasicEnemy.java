package unknown.oopptt.api;

import javafx.scene.image.ImageView;

import javax.swing.*;
import java.io.File;

public class BasicEnemy extends Enemy {
    private static String path = new File("src/main/resources/graphic/BasicEnemy").toString();
    private static double speedX = 150;
    private static double speedY = 0;
    private static int hp = 50;
    private static int point = 150;

    public BasicEnemy(double x, double y, double width, double height) {
        super(x,y,width, height, path, speedX, speedY, hp, point);
    }

    public void update(double dt) {
        this.pos_x += dt * speedX;
    }
    public int oneHit() {
        hp -= 50;
        if (hp <= 0) {
            return this.point;
        }
        return 0;
    }
}
