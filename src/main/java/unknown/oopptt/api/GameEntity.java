package unknown.oopptt.api;


import unknown.oopptt.physic.CollisionInfo;
import unknown.oopptt.physic.AABB;
import java.awt.Graphics2D;


public abstract class GameEntity implements IGameEntity {
    public String id, kind;
    public double x, y, w, h;
    public double vx = 0, vy = 0;
    public boolean isActive = true;


    protected GameEntity(String id, String kind, double x, double y, double w, double h) {
        this.id = id; this.kind = kind;
        this.x = x; this.y = y; this.w = w; this.h = h;
    }


    @Override public String id() { return id; }
    @Override public String kind() { return kind; }
    @Override public double x() { return x; }
    @Override public double y() { return y; }
    @Override public double w() { return w; }
    @Override public double h() { return h; }
    @Override public double vx() { return vx; }
    @Override public double vy() { return vy; }
    @Override public boolean isActive() { return isActive; }


    @Override public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
    }


    @Override public void render(Graphics2D g) { /* mặc định không vẽ */ }


    @Override public AABB getAABB() { return new AABB(x, y, w, h); }


    @Override public void onCollision(GameEntity other, unknown.oopptt.physic.CollisionInfo info) { /* override khi cần */ }


    @Override public void destroy() { isActive = false; }
}