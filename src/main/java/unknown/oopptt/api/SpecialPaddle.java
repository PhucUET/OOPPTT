package unknown.oopptt.api;

public class SpecialPaddle {
    private final  Paddle paddle;
    public SpecialPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public void upPaddle() {
        paddle.changeSize(paddle.getWidth() * 2);
    }
    public void downPaddle() {
        paddle.changeSize(paddle.getWidth() / 2);
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
