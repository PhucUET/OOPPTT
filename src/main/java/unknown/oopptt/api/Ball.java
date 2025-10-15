package unknown.oopptt.api;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.awt.Rectangle;
import java.io.File;

public  class Ball extends GameEntity {
    private static String path = new File("src/main/resources/graphic/ball_orange.png").toURI().toString();
    private double speedX;
    private double speedY;
    private double speedXY = 6;
    private boolean isSticky = false; // Đang dính vào thanh đỡ
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
    public Ball(int x, int y, int speedX, int speedY) {
        super(x, y, BALL_SIZE, BALL_SIZE,path);
        this.speedX = speedX;
        this.speedY = speedY;
        this.ball.setRadius(ball_size);
    }
    public Ball(Ball ball) {
        super(ball.getPos_x(), ball.getPos_y(), BALL_SIZE, BALL_SIZE,path);
        this.speedX = ball.getSpeedX();
        this.speedY = ball.getSpeedY();
    }

    @Override
    public void update() {
        this.pos_x += this.speedX;
        this.pos_y += this.speedY;
        ball.setCenterX(ball.getCenterX() + this.speedX);
        ball.setCenterY(ball.getCenterY() + this.speedY);
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
        this.speedY = Math.signum(this.speedY) * Math.sqrt(this.speedXY * this.speedXY - v * v);
        System.out.println("speedX: " + this.speedX + " speedY: " + this.speedY + " speedXY: " + Math.sqrt(this.speedX*this.speedX + this.speedY*this.speedY));
    }
    public void updateSpeedY(double v) {
        this.speedY = v;
        this.speedX = Math.signum(this.speedX)*Math.sqrt(this.speedXY * this.speedXY - v * v);
        System.out.println("speedX: " + this.speedX + " speedY: " + this.speedY + " speedXY: " + Math.sqrt(this.speedX*this.speedX + this.speedY*this.speedY));
    }

    public void stopBall() {
        this.speedX = 0;
        this.speedY = 0;
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
}