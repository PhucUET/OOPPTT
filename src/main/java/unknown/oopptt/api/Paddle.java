package unknown.oopptt.api;

import javafx.util.Pair;

public class Paddle extends GameEntity {
    private int moveSpeed = 8;
    private boolean isCatchEnabled = false; // PowerUp CATCH/Sticky Ball

    // Kích thước mặc định
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 15;

    public Paddle(int x, int y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Di chuyển thanh đỡ theo chiều ngang.
     * @param direction -1 cho trái, 1 cho phải.
     * @param screenWidth Chiều rộng màn hình (để kiểm tra giới hạn).
     */
    public void move(int direction, int screenWidth) {
        this.boundingBox.x += direction * moveSpeed;

        // Giới hạn di chuyển trong biên màn hình
        if (this.boundingBox.x < 0) {
            this.boundingBox.x = 0;
        }
        if (this.boundingBox.x + this.boundingBox.width > screenWidth) {
            this.boundingBox.x = screenWidth - this.boundingBox.width;
        }
    }

    // --- Phương thức Power-Up ---

    public void expand() {
        this.boundingBox.width = (int)(DEFAULT_WIDTH * 1.5);
    }

    public void resetSize() {
        this.boundingBox.width = DEFAULT_WIDTH;
    }

    public void enableCatch() {
        this.isCatchEnabled = true;
    }

    public void disableCatch() {
        this.isCatchEnabled = false;
    }

    @Override
    public void update() {
        // Cập nhật logic phụ (ví dụ: hết thời gian PowerUp)
    }

    @Override
    public void draw(Object graphicsContext) {
        // Vẽ thanh đỡ, có thể hiển thị trạng thái Catch (ví dụ: đổi màu)
    }

    @Override
    public void setLocation(int v) {
        this.boundingBox.x = v;
    }

    public Pair<Integer,Integer> getPosition() {
        return new Pair<>(this.boundingBox.x - this.boundingBox.width/2, this.boundingBox.y - this.boundingBox.height/2);
    }

    public boolean isCatchEnabled() {
        return isCatchEnabled;
    }
}