package unknown.oopptt.api;

import unknown.oopptt.physic.CollisionInfo;
import unknown.oopptt.physic.AABB;
import java.awt.Graphics2D;

public interface IGameEntity {
    String id();
    String kind();
    double x();
    double y();
    double w();
    double h();
    double vx();
    double vy();
    boolean isActive();


    void update(double dt);
    void render(Graphics2D g);
    AABB getAABB();
    void onCollision(GameEntity other, CollisionInfo info);
    void destroy();
}