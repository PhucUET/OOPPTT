package unknown.oopptt.api;

enum SpeedState {
    NORMAL,
    FAST,
    SLOW
}

enum SizeState {
    NORMAL,
    BIG,
    SMALL
}

public class SpecialPaddle {
    private final Paddle paddle;

    private final double baseMoveSpeed;
    private final double baseWidth;

    // 3. Quản lý trạng thái hiện tại
    private SpeedState currentSpeedState = SpeedState.NORMAL;
    private SizeState currentSizeState = SizeState.NORMAL;
    private boolean redirActive = false;

    public SpecialPaddle(Paddle paddle) {
        this.paddle = paddle;
        this.baseMoveSpeed = paddle.getMoveSpeed();
        this.baseWidth = paddle.getWidth();
    }

    /**
     * "Bộ não" của lớp.
     * Hàm này được gọi mỗi khi có thay đổi trạng thái.
     * Nó tính toán lại MỌI THỨ từ giá trị GỐC.
     */
    public void applyAllEffects() {

        double newSpeed = baseMoveSpeed;
        if (currentSpeedState == SpeedState.FAST) {
            newSpeed *= 2.0;
        } else if (currentSpeedState == SpeedState.SLOW) {
            newSpeed /= 2.0;
        }
        paddle.setMoveSpeed(newSpeed);
        double newWidth = baseWidth;
        if (currentSizeState == SizeState.BIG) {
            newWidth *= 2.0;
        } else if (currentSizeState == SizeState.SMALL) {
            newWidth /= 2.0;
        }
        paddle.changeSize(newWidth);

        paddle.setRedir(redirActive ? -1 : 1);
    }


    public void setFast() {
        currentSpeedState = SpeedState.FAST;
        applyAllEffects(); // Tính toán lại
    }

    public void setSlow() {
        currentSpeedState = SpeedState.SLOW;
        applyAllEffects();
    }

    public void setNormalSpeed() {
        currentSpeedState = SpeedState.NORMAL;
        applyAllEffects();
    }

    public void setBig() {
        currentSizeState = SizeState.BIG;
        applyAllEffects();
    }

    public void setSmall() {
        currentSizeState = SizeState.SMALL;
        applyAllEffects();
    }

    public void setNormalSize() {
        currentSizeState = SizeState.NORMAL;
        applyAllEffects();
    }

    public void setRedir(boolean active) {
        redirActive = active;
        applyAllEffects();
    }
}