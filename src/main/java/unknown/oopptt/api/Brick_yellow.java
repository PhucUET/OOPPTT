package unknown.oopptt.api;

import java.io.File;

public class Brick_yellow extends Brick {
    private int pointDrop = 120;
    private static String  path = new File("src/main/graphic/brick-yellow.png").toURI().toString();
    public Brick_yellow (int pos_x, int pos_y, int width, int height) {
        super(pos_x, pos_y, width, height, 2, path);
    }


    @Override
    public boolean hit() {
        this.hitPoints -= 1;
        if (this.hitPoints <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public void setLocation(double v) {

    }
}
