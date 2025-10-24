package unknown.oopptt.api;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.awt.Rectangle;
import java.io.File;

public  class Ball extends GameEntity {
    private static String path = new File("src/main/resources/graphic/ball_orange.png").toURI().toString();
    private double speedX;
    private double speedY;
    private double speedXY = 360;
    private boolean isSticky = true; // Đang dính vào thanh đỡ
    private double posinPaddle = 0;
    private Circle ball = new Circle();
    private int ball_size = 10;
    private static final int BALL_SIZE = 10;


    public int getBall_size() {
        return ball_size;
    }

    public Circle getBall() {
        return ball;
    }

    

    /**
     *
     * @param x
     * @param y
     * @param speedX
     * @param speedY
     */
    public Ball(double x, double y, double speedX, double speedY) {
        super(x, y, BALL_SIZE, BALL_SIZE,path);
        this.speedX = speedX;
        this.speedY = speedY;
        this.ball.setRadius(ball_size);
    }
    public Ball(Ball ball) {
        super(ball.getPos_x(), ball.getPos_y(), BALL_SIZE, BALL_SIZE,path);
        this.speedX = ball.getSpeedX();
        this.speedY = ball.getSpeedY();
        this.isSticky = false;
    }

    @Override
    public void update() {

    }

    public void updatePos(double dt) {
        double speedx = this.speedX * dt * speedXY;
        double speedy = this.speedY * dt * speedXY;
        System.out.println("speedx: " + speedx +  " speedy: " + speedy);
        this.pos_x += speedx;
        this.pos_y += speedy;
        ball.setCenterX(ball.getCenterX() + speedx);
        ball.setCenterY(ball.getCenterY() + speedy);
        this.imageView.setTranslateX(this.pos_x - ball_size/2);
        this.imageView.setTranslateY(this.pos_y - ball_size/2);
    }
    @Override
    public void setLocation(double v) {
        this.pos_x = v;
        this.imageView.setTranslateX(v - ball_size/2);
    }

    public void changeBallsize(int newBallsize) {
        this.ball_size = newBallsize;
        this.imageView.setTranslateX(this.pos_x - newBallsize/2);
        this.imageView.setTranslateY(this.pos_y - newBallsize/2);
        this.imageView.setFitWidth(newBallsize);
        this.imageView.setFitHeight(newBallsize);
    }

    public void updateSpeedX(double v) {
        this.speedX = v;
    }
    public void updateSpeedY(double v) {
        this.speedY = v;
    }

    public void stopBall(double x) {
        this.speedX = 1;
        this.speedY = 0;
        this.posinPaddle = x;
    }

    public double getSpeedX() {return speedX;}

    public double getSpeedY() {return speedY;}

    public double getPosinPaddle() {
        return posinPaddle;
    }

    public void setPosinPaddle() {
        this.posinPaddle = this.posinPaddle + getSpeedX();
    }

    public double getSpeedXY() {
        return speedXY;
    }

    public void setSpeedXY(double speedXY) {
        this.speedXY = speedXY;
    }

    public boolean isSticky() {
        return isSticky;
    }

    public void setSticky(boolean sticky) {
        isSticky = sticky;
    }
}