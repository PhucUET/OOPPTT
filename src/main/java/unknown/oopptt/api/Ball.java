package unknown.oopptt.api;
import java.awt.Rectangle;

public  class Ball extends GameEntity {
    private int speedX;
    private int speedY;
    private boolean isSticky = false; // Đang dính vào thanh đỡ

    private static final int BALL_SIZE = 10;

    public Ball(int x, int y, int speedX, int speedY) {
        super(x, y, BALL_SIZE, BALL_SIZE);
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public void update() {
        if (isSticky) {
            // Khi dính, vị trí bóng sẽ được đồng bộ với Paddle trong GameManager
            return;
        }

        this.boundingBox.x += this.speedX;
        this.boundingBox.y += this.speedY;

        // Xử lý va chạm với tường (Cần biết kích thước màn hình)
        // Logic này thường được đặt trong GameManager, nhưng cơ bản là:
        // if (getX() <= 0 || getX() + getWidth() >= SCREEN_WIDTH) speedX *= -1;
        // if (getY() <= 0) speedY *= -1;
    }

    @Override
    public void draw(Object graphicsContext) {
        // Vẽ quả bóng
    }

    @Override
    public void setLocation(int v) {

    }

    /**
     * Đảo chiều bóng khi va chạm.
     * @param hitEntity Đối tượng mà bóng va chạm (Paddle, Brick, Wall).
     */
    public void reverseDirection(GameEntity hitEntity) {
        // Logic phức tạp để xác định va chạm ngang/dọc cần được triển khai

        // Đảo chiều Y (Mô phỏng nảy lên/xuống)
        this.speedY *= -1;

        // Nếu là va chạm với Paddle, nên thêm logic thay đổi speedX
        // dựa trên vị trí chạm trên Paddle.
    }

    public void stickToPaddle() {
        this.isSticky = true;
        this.speedX = 0;
        this.speedY = 0;
    }

    public void launch(int launchSpeedX, int launchSpeedY) {
        if (isSticky) {
            this.isSticky = false;
            this.speedX = launchSpeedX;
            this.speedY = -Math.abs(launchSpeedY); // Đảm bảo luôn ném lên
        }
    }

    // Dùng cho PowerUp Multi-Ball
    public Ball cloneAndChangeDirection() {
        return new Ball(this.getX(), this.getY(), -this.speedX, this.speedY) {
            @Override
            public void setLocation(int v) {

            }
        };
    }

    // Getters and Setters cho speedX/Y
    public int getSpeedX() { return speedX; }
    public int getSpeedY() { return speedY; }
    public void setSpeedX(int speedX) { this.speedX = speedX; }
    public void setSpeedY(int speedY) { this.speedY = speedY; }
    public boolean isSticky() { return isSticky; }
}