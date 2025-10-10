package unknown.oopptt.api;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.io.File;

public abstract class Brick extends GameEntity {
    private static String background_Brick = new File("src/main/graphic/brick-yellow.png").toURI().toString();
    protected int hitPoints = 0;
    protected String powerupType; // Loại PowerUp (ví dụ: "EXPAND", "MULTI_BALL"), null nếu không có

    public Brick(int x, int y, int width, int height, int initialHitPoints, String type) {
        super(x, y, width, height, type);
        this.powerupType = type;
    }



    /**
     * Xử lý khi gạch bị bóng chạm.
     * @return Loại PowerUp nếu gạch bị phá, ngược lại trả về null.
     */
    public abstract boolean hit();

    @Override
    public void update() {

    }

    @Override
    public void setLocation(double v) {

    }
}