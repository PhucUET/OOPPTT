package unknown.oopptt.api;

import java.awt.Rectangle;

/**
 * Lớp trừu tượng cơ sở (Abstract Base Class) cho tất cả các đối tượng
 * trong game Arkanoid.
 * Nó định nghĩa các thuộc tính cơ bản về vị trí, kích thước và trạng thái.
 */
public abstract class GameEntity {

    // Sử dụng Rectangle để quản lý vị trí (x, y) và kích thước (width, height)
    // Giúp dễ dàng kiểm tra va chạm hơn.
    protected Rectangle boundingBox;

    protected boolean isActive; // True nếu đối tượng còn hoạt động trong game

    /**
     * Constructor của GameEntity.
     * @param x Tọa độ X ban đầu.
     * @param y Tọa độ Y ban đầu.
     * @param width Chiều rộng của đối tượng.
     * @param height Chiều cao của đối tượng.
     */
    public GameEntity(int x, int y, int width, int height) {
        this.boundingBox = new Rectangle(x, y, width, height);
        this.isActive = true;
    }

    /**
     * Phương thức trừu tượng: Cập nhật logic (vị trí, trạng thái) của đối tượng
     * trong mỗi khung hình. Bắt buộc các lớp con phải cài đặt.
     */
    public abstract void update();

    /**
     * Phương thức trừu tượng: Vẽ đối tượng lên màn hình.
     * Trong môi trường game thực tế, nó nhận vào đối tượng Graphics hoặc Context.
     * * @param graphicsContext Đối tượng dùng để vẽ (ví dụ: java.awt.Graphics).
     */
    public abstract void draw(Object graphicsContext);

    // -------------------------------------------------------------------
    //                       PHƯƠNG THỨC HỖ TRỢ CHUNG
    // -------------------------------------------------------------------

    /**
     * Kiểm tra xem đối tượng này có va chạm với đối tượng GameEntity khác hay không.
     * Phương thức này sử dụng hàm intersects() có sẵn của lớp Rectangle.
     * * @param other Đối tượng GameEntity khác.
     * @return True nếu có va chạm (hitbox overlap), False nếu ngược lại.
     */
    public boolean checkCollision(GameEntity other) {
        if (!this.isActive || !other.isActive) {
            return false; // Không kiểm tra va chạm nếu một trong hai đối tượng không hoạt động
        }
        return this.boundingBox.intersects(other.boundingBox);
    }

    /**
     * Đặt đối tượng ở trạng thái không hoạt động (đánh dấu để xóa khỏi game).
     */
    public void deactivate() {
        this.isActive = false;
    }

    // -------------------------------------------------------------------
    //                          GETTERS & SETTERS
    // -------------------------------------------------------------------

    public int getX() {
        return boundingBox.x;
    }

    public int getY() {
        return boundingBox.y;
    }

    public int getWidth() {
        return boundingBox.width;
    }

    public int getHeight() {
        return boundingBox.height;
    }

    public boolean isActive() {
        return isActive;
    }

    // Phương thức trả về đối tượng Rectangle để các lớp con dễ dàng truy cập/thay đổi
    // vị trí trực tiếp trong phương thức update() của chúng.
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setLocation(int x, int y) {
        this.boundingBox.setLocation(x, y);
    }

    public abstract void setLocation(int v);
}


