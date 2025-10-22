package unknown.oopptt.api;


import unknown.oopptt.physic.CollisionInfo;
import unknown.oopptt.physic.AABB;
import java.awt.Graphics2D;


public abstract class GameEntity implements IGameEntity {
    public String id, kind;
    public double x, y, w, h;
    public double vx = 0, vy = 0;
    public boolean isActive = true;


<<<<<<< Updated upstream
    protected GameEntity(String id, String kind, double x, double y, double w, double h) {
        this.id = id; this.kind = kind;
        this.x = x; this.y = y; this.w = w; this.h = h;
=======
    protected ImageView imageView;
    double pos_x, pos_y,width,height;

    /**
     * Constructor của GameEntity.
     * @param x Tọa độ X ban đầu.
     * @param y Tọa độ Y ban đầu.
     * @param width Chiều rộng của đối tượng.
     * @param height Chiều cao của đối tượng.
     */

    public GameEntity(double x, double y, double width, double height, String path) {
        imageView = new ImageView(new Image(path));
        imageView.setTranslateX(x - width/2);
        imageView.setTranslateY(y -  height/2);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        pos_x = x;
        pos_y = y;
        this.width = width;
        this.height = height;
>>>>>>> Stashed changes
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


<<<<<<< Updated upstream
    @Override public AABB getAABB() { return new AABB(x, y, w, h); }
=======
    public abstract void setLocation(double v);

    public double getPos_x() {return pos_x;}

    public double getPos_y() {return pos_y;}

    public double getWidth() {return width;}

    public double getHeight() {return height;}


}
>>>>>>> Stashed changes


    @Override public void onCollision(GameEntity other, unknown.oopptt.physic.CollisionInfo info) { /* override khi cần */ }


    @Override public void destroy() { isActive = false; }
}