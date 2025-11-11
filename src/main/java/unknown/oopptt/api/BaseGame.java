package unknown.oopptt.api;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import unknown.oopptt.api.ball.Ball;
import unknown.oopptt.api.enemy.Enemy;
import unknown.oopptt.controller.Game_Screen_Controller;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BaseGame {

    private final Game_Screen_Controller controller;
    private int myPoint = 0;

    public BaseGame(Game_Screen_Controller controller) {
        this.controller = controller;
    }

    // ======================================================
    // WALL COLLISION (Giới hạn tường)
    // ======================================================
    public boolean wallCollision(Ball ball, ImageView background) {
        Bounds ballBounds = ball.getImageView().getBoundsInParent();
        Bounds wallBounds = background.getBoundsInParent();

        double padding = 5;

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

        if ((bxLeft <= leftWall && vx < 0) || (bxRight >= rightWall && vx > 0)) {
            ball.setSpeedX(-vx);
        }

        if (byTop <= topWall && vy < 0) {
            ball.setSpeedY(-vy);
        }

        if (byBottom <= bottomWall ) {
            return false;
        }
        return true;

    }

    // ======================================================
    // PADDLE COLLISION
    // ======================================================
    public void paddleballCollision(Ball ball, Paddle paddle, boolean isCatch) {
        Bounds bb = ball.getImageView().getBoundsInParent();
        Bounds pb = paddle.getImageView().getBoundsInParent();

        double shrink = 20;
        if (bb.intersects(
                pb.getMinX() + shrink, pb.getMinY() + shrink,
                pb.getWidth() - 2 * shrink, pb.getHeight() - 2 * shrink)) {

            if (isCatch && ball.getSpeedY() != 0) {
                ball.stopBall(ball.getPos_x() - paddle.getPos_x());
                return;
            }

            double t = 2 * (bb.getCenterX() - pb.getCenterX()) / pb.getWidth();
            t = Math.max(-0.98, Math.min(0.98, t));

            double minAngle = 15;
            double maxAngle = 165;
            double deg = (maxAngle - minAngle) * (t + 1) / 2 + minAngle;

            double rad = Math.toRadians(-deg);
            double dirX = -Math.cos(rad);
            double dirY = Math.sin(rad);

            double len = Math.hypot(dirX, dirY);
            dirX /= len;
            dirY /= len;

            ball.setSpeedX(dirX);
            ball.setSpeedY(dirY);
        }
    }


    private boolean simpleCollision(Ball ball, Bounds r) {
        ImageView bv = ball.getImageView();
        Bounds bb = bv.getBoundsInParent();

        Bounds bReduced = new javafx.geometry.BoundingBox(
                bb.getMinX() + 10, bb.getMinY() + 10,
                bb.getWidth() - 20, bb.getHeight() - 20);

        if (!bReduced.intersects(r)) return false;

        double ballCenterX = bb.getMinX() + bb.getWidth() / 2.0;
        double ballCenterY = bb.getMinY() + bb.getHeight() / 2.0;
        double brickCenterX = r.getMinX() + r.getWidth() / 2.0;
        double brickCenterY = r.getMinY() + r.getHeight() / 2.0;

        double dx = ballCenterX - brickCenterX;
        double dy = ballCenterY - brickCenterY;

        double combinedHalfWidth = (bb.getWidth() + r.getWidth()) / 2.0;
        double combinedHalfHeight = (bb.getHeight() + r.getHeight()) / 2.0;

        double overlapX = combinedHalfWidth - Math.abs(dx);
        double overlapY = combinedHalfHeight - Math.abs(dy);

        // Nếu thực sự có overlap (tránh false positive)
        if (overlapX > 0 && overlapY > 0) {
            // Nếu chồng theo X ít hơn → phản xạ theo X
            if (overlapX < overlapY) {
                double push = (dx > 0) ? overlapX : -overlapX;
                bv.setTranslateX(bv.getTranslateX() + push);
                ball.setSpeedX(-ball.getSpeedX());
            }
            // Ngược lại phản xạ theo Y
            else {
                double push = (dy > 0) ? overlapY : -overlapY;
                bv.setTranslateY(bv.getTranslateY() + push);
                ball.setSpeedY(-ball.getSpeedY());
            }
            return true;
        }

        return false;
    }


    // ======================================================
    // BRICK COLLISION
    // ======================================================


    public void brickCollision(Ball ball, List<Brick> bricks) {


        for (int i = bricks.size() - 1; i >= 0; i--) {
            Brick brick = bricks.get(i);
            ImageView rv = brick.getImageView();
            if (brick.isWait()) continue;

            // co lại vùng kiểm tra 10px để tránh phản xạ sai
            Bounds r = rv.getBoundsInParent();

            if (simpleCollision(ball, r)) {
                // Cập nhật trạng thái gạch
                if (!brick.hit()) {
                    myPoint += brick.getHitPoints();
                    brick.setWait(true);
                    break;
                }
            }


            // Chỉ xử lý 1 gạch mỗi frame
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

    public static int rand1to20() {
        return ThreadLocalRandom.current().nextInt(-20, 21); // [1, 21)
    }

    public void enemyCollision(Ball ballLogic, List<Enemy> enemys) {

        for (Enemy enemy :  enemys) {
            Bounds en = enemy.getImageView().getBoundsInParent();

            if (simpleCollision(ballLogic, en)) {
                myPoint +=  enemy.takeDamage(10);
                if (enemy instanceof TransitEnemy) {
                    ballLogic.setLocation(ballLogic.pos_x + rand1to20() ,  ballLogic.pos_y + rand1to20());
                }
            }
        }
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
