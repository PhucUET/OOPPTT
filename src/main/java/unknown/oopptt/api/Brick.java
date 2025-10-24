package unknown.oopptt.api;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Brick extends GameEntity {
    private static String brick1 = new File("src/main/resources/graphic/pixil-frame-0 (1).png").toURI().toString();
    private static String brick1Broken = new File("src/main/resources/graphic/broken brick1.png").toURI().toString();
    private static String brick2 = new File("src/main/resources/graphic/normal brick2.png").toURI().toString();
    private static String brick2Broken = new File("src/main/resources/graphic/broken brick2.png").toURI().toString();
    private static String brick3 = new File("src/main/resources/graphic/normal brick10.png").toURI().toString();
    private static String brick3Broken = new File("src/main/resources/graphic/broken brick10.png").toURI().toString();
    private int hitPoints = 1;
    protected String powerupType; // Loại PowerUp (ví dụ: "EXPAND", "MULTI_BALL"), null nếu không có
    private final static int widthBrick = 33;
    private final static int heightBrick = 25;
    private int typeBrick = 0;
    private final ArrayList<Integer> pointList = new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));

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

    public Brick(double x, double y, int t) {
        super(x + widthBrick/2, y + heightBrick/2, widthBrick, heightBrick, Get_type(t));
        hitPoints = t;
        typeBrick = t;
    }

    public Brick(double x, double y, double width, double height , int t) {
        super(x + width/2, y + height/2 , width, height, Get_type(t));
        hitPoints = t;
        typeBrick = t;
    }


    public boolean hit() {
        if (typeBrick == 1) setImageView(new Image(brick1Broken));
        if (typeBrick == 2) setImageView(new Image(brick2Broken));
        if (typeBrick == 3) setImageView(new  Image(brick3Broken));
        hitPoints--;
        if (hitPoints == 0) {
            return false;
        }
        return true;
    }
    /**
     * Xử lý khi gạch bị bóng chạm.
     * @return Loại PowerUp nếu gạch bị phá, ngược lại trả về null.
     */
    //public abstract boolean hit();

    @Override
    public void update() {

    }

    @Override
    public void setLocation(double v) {

    }
}