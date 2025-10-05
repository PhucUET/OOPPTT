package unknown.oopptt.api;
import java.awt.*;


public class Paddle extends GameEntity {
    public double moveSpeed = 560.0;


    public Paddle(String id, double x, double y, double w, double h) {
        super(id, "paddle", x, y, w, h);
    }


    @Override public void render(Graphics2D g) {
        g.setColor(new Color(120, 180, 250));
        g.fillRoundRect((int)x, (int)y, (int)w, (int)h, 10, 10);
    }
}