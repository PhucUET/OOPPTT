package unknown.oopptt.api;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Brick extends GameEntity {
    private static String brick1 = new File("src/main/resources/graphic/Brick1").toString();
    private static String brick1Broken = new File("src/main/resources/graphic/dropbrick1").toString();
    private static String brick2 = new File("src/main/resources/graphic/Brick2").toString();
    private static String brick2Broken = new File("src/main/resources/graphic/dropbrick2").toString();
    private static String brick3 = new File("src/main/resources/graphic/Brick3").toString();
    private static String brick3Broken = new File("src/main/resources/graphic/dropbrick3").toString();
    private int hitPoints = 1;
    protected String powerupType; // Loại PowerUp (ví dụ: "EXPAND", "MULTI_BALL"), null nếu không có
    private final static int widthBrick = 50;
    private final static int heightBrick = 23;
    private int typeBrick = 0;
    private final ArrayList<Integer> pointList = new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));
    private double timeDrop = 0.3;
    private boolean wait = false;
    private int Point = 100;
    private int typePU;

    public int getTypePU() {
        return typePU;
    }

    public void setTypePU(int typePU) {
        this.typePU = typePU;
    }

    private static String Get_type(int t) {
        String type = "";
        switch (t) {
            case 1:
                type = brick1;
                break;
            case 2:
                type = brick2;
                break;
            case 3:
                type = brick3;
                break;
        }
        return type;
    }

    public boolean dropBrick(double dt) {
        timeDrop -= dt;
        if (timeDrop <= 0) {
            return true;
        }
        return false;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public int getPoint() {
        return Point;
    }

    public Brick(double x, double y, int t) {
        super(x + widthBrick / 2, y + heightBrick / 2, widthBrick, heightBrick, Get_type(t));
        hitPoints = t;
        typeBrick = t;
    }

    public Brick(double x, double y, int t, int typePU) {
        super(x + widthBrick / 2, y + heightBrick / 2, widthBrick, heightBrick, Get_type(t));
        hitPoints = t;
        typeBrick = t;
        this.typePU = typePU;
    }

    public Brick(double x, double y, double width, double height, int t) {
        super(x + width / 2, y + height / 2, width, height, Get_type(t));
        hitPoints = t;
        typeBrick = t;
    }

    public boolean hit() {
        if (typeBrick == 1) {
            setAnimation(brick1Broken);
        }
        if (typeBrick == 2) {
            setAnimation(brick2Broken);
        }
        if (typeBrick == 3) {
            setAnimation(brick3Broken);
        }
        hitPoints--;
        if (hitPoints == 0) {
            return false;
        }
        return true;
    }

    /**
     * Xử lý khi gạch bị bóng chạm.
     *
     * @return Loại PowerUp nếu gạch bị phá, ngược lại trả về null.
     */
    //public abstract boolean hit();
    @Override
    public void update(double dt) {

    }

    @Override
    public void setLocation(double v, double dt) {

    }
}