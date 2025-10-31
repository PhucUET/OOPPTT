package unknown.oopptt.api;
import unknown.oopptt.physic.CollisionInfo;
import java.awt.*;


public class Ball extends GameEntity {
    public double speed = 400.0;


    // ---------- Tham số & hàm hỗ trợ để tránh thẳng đứng + random ----------
    private static final double MAX_DEFLECT_DEG = 60.0; // lệch tối đa
    private static final double MIN_DEFLECT_DEG = 12.0; // lệch tối thiểu để tránh thẳng đứng
    private static final double JITTER_DEG = 8.0; // ngẫu nhiên thêm/bớt


    private static double toRad(double deg) { return deg * Math.PI / 180.0; }


    private static double enforceMinDeflect(double angleRad, double minRad) {
        double s = Math.signum(angleRad);
        if (s == 0) s = (Math.random() < 0.5 ? -1 : 1);
        double a = Math.abs(angleRad);
        if (a < minRad) a = minRad;
        return s * a;
    }


    private static double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }
// -----------------------------------------------------------------------


    public Ball(String id, double x, double y, double radius) {
        super(id, "ball", x, y, radius * 2.0, radius * 2.0);
    }


    public double radius() { return w / 2.0; }


    @Override public void render(Graphics2D g) {
        g.setColor(new Color(230, 230, 240));
        g.fillOval((int)(x), (int)(y), (int)(w), (int)(h));
    }


    @Override public void onCollision(GameEntity other, CollisionInfo info) {
// Phản xạ gương theo pháp tuyến (cho tường/brick)
        double dot = vx * info.normalX + vy * info.normalY;
        vx = vx - 2 * dot * info.normalX;
        vy = vy - 2 * dot * info.normalY;


// Nếu va vào paddle: tính lại góc bật ra để KHÔNG thẳng đứng + có random
        if ("paddle".equals(other.kind)) {
            double contactX = info.contactX;
            double centerPaddle = other.x + other.w / 2.0;
            double rel = (Double.isNaN(contactX) ? (this.x + this.w/2.0) : contactX) - centerPaddle;
            rel /= (other.w / 2.0); // [-1, 1]
            rel = clamp(rel, -1.0, 1.0);


// Góc cơ sở theo vị trí tiếp xúc
            double baseAngle = toRad(MAX_DEFLECT_DEG) * rel;


// Jitter ngẫu nhiên
            double jitter = toRad(JITTER_DEG) * (Math.random() * 2.0 - 1.0);


// Tổng góc lệch so với phương thẳng đứng
            double angle = baseAngle + jitter;


// Ép góc tối thiểu để tránh thẳng đứng
            double minDeflect = toRad(MIN_DEFLECT_DEG);
            angle = enforceMinDeflect(angle, minDeflect);


// Bảo toàn tốc độ, bắn lên trên
            double speedNow = Math.hypot(vx, vy);
            if (speedNow < 1e-6) speedNow = this.speed;
            vx = speedNow * Math.sin(angle);
            vy = -Math.abs(speedNow * Math.cos(angle));


// Nếu vẫn gần thẳng đứng (vx nhỏ), nêm thêm chút lệch nhỏ
            if (Math.abs(vx) < 20.0) {
                vx = (Math.random() < 0.5 ? -1 : 1) * 20.0;
            }
        }
    }
}