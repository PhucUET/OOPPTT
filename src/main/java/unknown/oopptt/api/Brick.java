package unknown.oopptt.api;

import java.awt.Rectangle;

public class Brick extends GameEntity {
    protected int hitPoints;
    protected String powerupType; // Loại PowerUp (ví dụ: "EXPAND", "MULTI_BALL"), null nếu không có

    public Brick(int x, int y, int width, int height, int initialHitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = initialHitPoints;
        this.powerupType = type;
    }

    /**
     * Xử lý khi gạch bị bóng chạm.
     * @return Loại PowerUp nếu gạch bị phá, ngược lại trả về null.
     */
    public String hit() {
        if (!isActive()) return null;

        this.hitPoints--;

        // Cập nhật trạng thái màu sắc/hình ảnh tại đây (ví dụ: đổi màu)
        // ...

        if (this.hitPoints <= 0) {
            deactivate(); // Đặt isActive = false
            return this.powerupType;
        }
        return null;
    }

    @Override
    public void update() {
        // Gạch thường không di chuyển, nên phương thức này thường để trống
    }

    @Override
    public void draw(Object graphicsContext) {
        // Vẽ gạch, có thể dựa vào hitPoints để hiển thị độ 'hỏng'
    }
}