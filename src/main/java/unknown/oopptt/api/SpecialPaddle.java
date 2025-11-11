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
        paddle.changeSize(100);
    }
}
