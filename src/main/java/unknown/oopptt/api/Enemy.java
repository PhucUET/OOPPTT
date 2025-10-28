package unknown.oopptt.api;

import java.io.File;

public class Enemy extends GameEntity {
    private static String path =  new File("src/main/resources/graphic/Idle/Idle_000.png").toURI().toString();
    private double speedX;
    private double speedY;
    private int hp = 50;
    private int point;

    public Enemy(double x, double y, double width, double height, String path, double speedX, double speedY, int hp, int point) {
        super(x, y , width, height, path);
        this.hp = hp;
        this.point = point;
        this.speedX = speedX;
        this.speedY = speedY;
    }


    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public void update() {

    }

    @Override
    public void setLocation(double v, double dt) {

    }
}
