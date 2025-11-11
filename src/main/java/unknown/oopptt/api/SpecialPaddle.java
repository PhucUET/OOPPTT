package unknown.oopptt.api;

import javafx.scene.layout.Background;

public class SpecialPaddle {
    private final  Paddle paddle;
    public SpecialPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public void upPaddle() {
        if(paddle.getWidth() < 500) {
            paddle.changeSize(paddle.getWidth() * 2);
        }
        else {
            paddle.changeSize(500);
        }
    }
    public void downPaddle() {
        paddle.changeSize(150);
    }
    public void slowPaddle() {
        paddle.setMoveSpeed(paddle.getMoveSpeed() / 2);
    }
    public void fastPaddle() {
        paddle.setMoveSpeed(paddle.getMoveSpeed() * 2);
    }
    public void setRedir() {
        paddle.setRedir(-1);
    }
    public void offRedir() {
        paddle.setRedir(1);
    }
}
