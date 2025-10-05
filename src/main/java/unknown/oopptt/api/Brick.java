package unknown.oopptt.api;

import java.awt.*;


public class Brick extends GameEntity {
    public int hp = 1;
    public int score = 50;
    public Color color;


    public Brick(String id, double x, double y, double w, double h, int hp, Color color) {
        super(id, "brick", x, y, w, h);
        this.hp = hp;
        this.color = color;
        this.score = 40 + hp * 20;
    }


    @Override public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)w, (int)h);
// viền
        g.setColor(new Color(0, 0, 0, 60));
        g.drawRect((int)x, (int)y, (int)w, (int)h);
    }


    @Override public void onCollision(GameEntity other, unknown.oopptt.physic.CollisionInfo info) {
        if ("ball".equals(other.kind)) {
            hp -= 1;
            if (hp <= 0) destroy();
        }
    }
}