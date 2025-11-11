package unknown.oopptt.api;

public class SpecialPaddle {
    private final  Paddle paddle;
    private boolean upActive = false;
    private boolean downActive = false;
    private boolean fastActive = false;
    private boolean slowActive = false;
    private boolean dirActive = false;
    public SpecialPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public void upPaddle() {
        if (!upActive) {
            upActive = true;
            paddle.changeSize(paddle.getWidth() * 2);
        }
    }
    public void offupPaddle() {
        if (upActive) {
            upActive = false;
            paddle.changeSize(paddle.getWidth() * 2);
        }
    }
    public void downPaddle() {
        if (!downActive) {
            downActive = true;
            System.out.println("downPaddle" + "ngusi");
            paddle.changeSize(paddle.getWidth() / 2);
        }
    }
    public void offdownPaddle() {
        if (downActive) {
            downActive = false;
            System.out.println("offdownPaddle" + "ngusi");
            paddle.changeSize(paddle.getWidth() * 2);
        }
    }
    public void slowPaddle() {

        if (!slowActive) {
            slowActive = true;
            paddle.setMoveSpeed(paddle.getMoveSpeed() / 2);
        }

    }

    public void offslowPaddle() {
        if (slowActive) {
            slowActive = false;
            paddle.setMoveSpeed(paddle.getMoveSpeed() * 2);
        }
    }

    public void fastPaddle() {
        if (!fastActive) {
            fastActive = true;
            paddle.setMoveSpeed(paddle.getMoveSpeed() * 2);
            return;
        }
        if (slowActive) {
            slowActive = false;
            paddle.setMoveSpeed(paddle.getMoveSpeed() * 2);;
        }

    }
    public void setRedir() {
        if (dirActive) {
            return;
        }
        dirActive = true;
        paddle.setRedir(-1);
    }
    public void offRedir() {
        if (dirActive) {
            dirActive = false;
        }
        paddle.setRedir(1);
    }
}
