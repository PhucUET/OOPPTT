package unknown.oopptt.api;

import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.io.File;

public class Paddle extends GameEntity {
    private static String path = new File("src/main/resources/graphic/n-paddle0.png").toURI().toString();
    private int moveSpeed = 8;
    private boolean isCatchEnabled = false; // PowerUp CATCH/Sticky Ball
    private Rectangle Paddle;
    // Kích thước mặc định
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 15;

    public Paddle(int x, int y) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, path);
        Paddle = new Rectangle(x - DEFAULT_WIDTH, y - DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Di chuyển thanh đỡ theo chiều ngang.
     * @param direction -1 cho trái, 1 cho phải.
     * @param screenWidth Chiều rộng màn hình (để kiểm tra giới hạn).
     */
    public void move(int direction, int screenWidth) {
        this.pos_x += direction * moveSpeed;

        // Giới hạn di chuyển trong biên màn hình
        if (this.pos_x < 0) {
            this.pos_x = 0;
        }
        if (this.pos_x + this.width > screenWidth) {
            this.pos_x = screenWidth - this.width;
        }
    }

    public Rectangle getPaddle() {
        return Paddle;
    }
    // --- Phương thức Power-Up ---


    @Override
    public void update() {
        // Cập nhật logic phụ (ví dụ: hết thời gian PowerUp)
    }


    @Override
    public void setLocation(double v) {
        this.pos_x = v;
        this.imageView.setTranslateX(this.pos_x - this.width/2);
    }

    public Pair<Double, Double> getPosition() {
        return new Pair<>(this.pos_x - this.width/2, this.pos_y - this.height/2);
    }

    public boolean isCatchEnabled() {
        return isCatchEnabled;
    }
}