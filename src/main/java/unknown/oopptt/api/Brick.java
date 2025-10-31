package unknown.oopptt.api;

import java.awt.*;


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
    private final static int heightBrick = 25;
    private int typeBrick = 0;
    private final ArrayList<Integer> pointList = new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));
    private double timeDrop = 0.3;
    private boolean wait = false;
    private int Point = 100;
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


    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public boolean isWait() {
        return wait;
    }


    @Override public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)w, (int)h);
// viền
        g.setColor(new Color(0, 0, 0, 60));
        g.drawRect((int)x, (int)y, (int)w, (int)h);
    }


    @Override public void onCollision(GameEntity other, unknown.oopptt.physic.CollisionInfo info) {
        if ("ball".equals(other.kind)) {
            hp -= 1;
            if (hp <= 0) destroy();
        }
    }
}