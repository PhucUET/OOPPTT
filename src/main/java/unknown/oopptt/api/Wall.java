package unknown.oopptt.api;
import java.awt.*;


public class Wall extends GameEntity {
    public Wall(String id, double x, double y, double w, double h) {
        super(id, "wall", x, y, w, h);
    }
    @Override public void render(Graphics2D g) { /* ẩn */ }
}