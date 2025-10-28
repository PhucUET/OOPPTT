package unknown.oopptt.api;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import unknown.oopptt.controller.GameScreen_controller;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BaseGame {

    private final GameScreen_controller controller;

    public BaseGame(GameScreen_controller controller) {
        this.controller = controller;
    }

    // ======================================================
    // WALL COLLISION (Giới hạn tường)
    // ======================================================
    public void wallCollision(Ball ball, ImageView background) {
        Bounds ballBounds = ball.getImageView().getBoundsInParent();
        Bounds wallBounds = background.getBoundsInParent();

        // co lại biên kiểm tra 10px để tránh phản xạ sớm / xuyên
        double padding = 10;

        double leftWall  = wallBounds.getMinX() + padding;
        double rightWall = wallBounds.getMaxX() - padding;
        double topWall   = wallBounds.getMinY() + padding;
        double bottomWall= wallBounds.getMaxY() - padding;

        double bxLeft  = ballBounds.getMinX();
        double bxRight = ballBounds.getMaxX();
        double byTop   = ballBounds.getMinY();
        double byBottom= ballBounds.getMaxY();

        double vx = ball.getSpeedX();
        double vy = ball.getSpeedY();

        // Phản xạ trái/phải
        if ((bxLeft <= leftWall && vx < 0) || (bxRight >= rightWall && vx > 0)) {
            ball.setSpeedX(-vx);
        }

        // Phản xạ trên
        if (byTop <= topWall && vy < 0) {
            ball.setSpeedY(-vy);
        }

        // Không phản xạ dưới (để mất bóng)
    }

    // ======================================================
    // PADDLE COLLISION
    // ======================================================
    public void paddleballCollision(Ball ball, Paddle paddle, boolean isCatch) {
        Bounds bb = ball.getImageView().getBoundsInParent();
        Bounds pb = paddle.getImageView().getBoundsInParent();

        // Co nhỏ 10px để tránh phản xạ lặp
        double shrink = 10;
        if (bb.intersects(
                pb.getMinX() + shrink, pb.getMinY() + shrink,
                pb.getWidth() - 2 * shrink, pb.getHeight() - 2 * shrink)) {

            // Nếu đang có hiệu ứng dính
            if (isCatch && ball.getSpeedY() != 0) {
                ball.stopBall(ball.getPos_x() - paddle.getPos_x());
                return;
            }

            // Tính góc phản xạ theo vị trí va chạm
            double t = 2 * (bb.getCenterX() - pb.getCenterX()) / pb.getWidth();
            t = Math.max(-0.98, Math.min(0.98, t));

            double minAngle = 15;
            double maxAngle = 165;
            double deg = (maxAngle - minAngle) * (t + 1) / 2 + minAngle;

            double rad = Math.toRadians(-deg);
            double dirX = -Math.cos(rad);
            double dirY = Math.sin(rad);

            // Chuẩn hóa vector
            double len = Math.hypot(dirX, dirY);
            dirX /= len;
            dirY /= len;

            ball.setSpeedX(dirX);
            ball.setSpeedY(dirY);
        }
    }

    // ======================================================
    // BRICK COLLISION
    // ======================================================
    public void brickCollision(Ball ball, List<Brick> bricks) {
        ImageView bv = ball.getImageView();
        Bounds bb = bv.getBoundsInParent();

        for (int i = bricks.size() - 1; i >= 0; i--) {
            Brick brick = bricks.get(i);
            ImageView rv = brick.getImageView();
            if (brick.isWait()) continue;

            // co lại vùng kiểm tra 10px để tránh phản xạ sai
            Bounds r = rv.getBoundsInParent();
            Bounds bReduced = new javafx.geometry.BoundingBox(
                    bb.getMinX() + 10, bb.getMinY() + 10,
                    bb.getWidth() - 20, bb.getHeight() - 20);

            if (!bReduced.intersects(r)) continue;

            // Tính chồng lấn
            double overlapL = bb.getMaxX() - r.getMinX();
            double overlapR = r.getMaxX() - bb.getMinX();
            double overlapT = bb.getMaxY() - r.getMinY();
            double overlapB = r.getMaxY() - bb.getMinY();

            double overlapX = Math.min(overlapL, overlapR);
            double overlapY = Math.min(overlapT, overlapB);

            // Phản xạ theo hướng ít chồng lấn hơn (tránh xuyên)
            if (overlapX < overlapY) {
                double push = (overlapL < overlapR) ? -overlapL : overlapR;
                bv.setTranslateX(bv.getTranslateX() + push);
                ball.setSpeedX(-ball.getSpeedX());
            } else {
                double push = (overlapT < overlapB) ? -overlapT : overlapB;
                bv.setTranslateY(bv.getTranslateY() + push);
                ball.setSpeedY(-ball.getSpeedY());
            }

            // Cập nhật trạng thái gạch
            if (!brick.hit()) {

                brick.setWait(true);

            }

            // Chỉ xử lý 1 gạch mỗi frame
            break;
        }
    }

    // ======================================================
    // POWERUP COLLISION
    // ======================================================
    public boolean paddlePUCollision(Powerup p, Paddle paddle) {
        Bounds pb = paddle.getImageView().getBoundsInParent();
        Bounds pw = p.getImageView().getBoundsInParent();

        if (pw.intersects(
                pb.getMinX() + 10, pb.getMinY() + 10,
                pb.getWidth() - 20, pb.getHeight() - 20)) {
            p.WhenCollison(controller);
            return true;
        }
        return false;
    }

    // ======================================================
    // UTILITIES
    // ======================================================


    public boolean outBall(Bounds ball, ImageView background) {
        //System.out.println(ball.getImageView().getBoundsInParent().getMaxY() + " " + background.getBoundsInParent().getMaxY());;
        return ball.getMaxY()
                >= background.getBoundsInParent().getMaxY();
    }

    public boolean outPowerup(Powerup p, ImageView background) {
        return p.getImageView().getBoundsInParent().getMaxY()
                >= background.getBoundsInParent().getMaxY();
    }
}
