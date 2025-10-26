package unknown.oopptt.api;

import javafx.scene.layout.Pane;

import java.util.List;

public class PowerBall {
    private final List<Ball> balls;
    public PowerBall(List<Ball> balls) {
        this.balls = balls;
    }
    public void upBall() {
        for (Ball ballLogic : balls) {
            ballLogic.changeBallsize(20);
        }
    }
    public void downBall() {
        for (Ball ballLogic : balls) {
            ballLogic.changeBallsize(10);
        }
    }
    public void slowBall() {
        for (Ball ballLogic : balls) {
            ballLogic.setSpeedXY(ballLogic.getSpeedXY() + 2);
        }
    }
    public void normalBall() {
        for (Ball ballLogic : balls) {
            ballLogic.setSpeedXY(ballLogic.getSpeedXY() - 2);
        }
    }
    public void moreBall(Pane root) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball nowB = balls.get(i);
            Ball newB1 = new Ball(nowB);
            newB1.updateSpeedX(-nowB.getSpeedX());

            Ball newB2 =  new Ball(nowB);
            newB2.updateSpeedY(-nowB.getSpeedY());

            balls.add(newB1);
            balls.add(newB2);

            root.getChildren().addAll(newB1.getImageView(), newB2.getImageView());
        }
    }

}
