package unknown.oopptt.api.ball;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý các hiệu ứng Power-up liên quan đến bóng trong game.
 * Bao gồm: tăng kích thước, giảm tốc, phục hồi tốc độ, nhân đôi bóng.
 */
public class PowerBall {

    private final List<Ball> balls;

    // Giới hạn an toàn
    private static final double MIN_SPEED_SCALE = 150.0;
    private static final double MAX_SPEED_SCALE = 600.0;
    private static final int MAX_BALLS = 8;
    private static final int DEFAULT_SIZE = 15;
    private static final int BIG_SIZE = 25;
    private static final int SMALL_SIZE = 10;
    private boolean slowActive;

    public PowerBall(List<Ball> balls) {
        this.balls = balls;
    }

    public void upBall() {
        for (Ball ball : balls) {
            ball.changeSize(BIG_SIZE);
        }
    }

    public void downBall() {
        for (Ball ball : balls) {
            ball.changeSize(DEFAULT_SIZE);
        }
    }


    public void slowBall() {
        if (slowActive) {
            return;
        }
        slowActive = true;
        for (Ball ball : balls) {
            double newSpeed = Math.max(MIN_SPEED_SCALE, ball.getSpeedScale() * 0.8);
            ball.setSpeedScale(newSpeed);
        }
    }

    public void normalBall() {
        slowActive = false;
        for (Ball ball : balls) {
            ball.setSpeedScale(DEFAULT_SPEED);
        }
    }

    public void speedUpBall() {
        for (Ball ball : balls) {
            double newSpeed = Math.min(MAX_SPEED_SCALE, ball.getSpeedScale() * 1.2);
            ball.setSpeedScale(newSpeed);
        }
    }

    public void moreBall(Pane root) {
        if (balls.size() >= MAX_BALLS) return;

        List<Ball> newBalls = new ArrayList<>();

        for (Ball original : balls) {
            Ball left = new Ball(original);
            Ball right = new Ball(original);

            left.setSpeedX(-original.getSpeedX());
            right.setSpeedX(original.getSpeedX());

            left.setSpeedY(original.getSpeedY() * 0.9);
            right.setSpeedY(original.getSpeedY() * 1.1);

            newBalls.add(left);
            newBalls.add(right);
        }

        while (balls.size() + newBalls.size() > MAX_BALLS) {
            newBalls.remove(newBalls.size() - 1);
        }

        balls.addAll(newBalls);
        for (Ball b : newBalls) {
            root.getChildren().add(b.getImageView());
        }
    }

    private static final double DEFAULT_SPEED = 360.0;

    public void resetSpeedAll() {
        for (Ball ball : balls) {
            ball.setSpeedScale(DEFAULT_SPEED);
        }
    }
}
