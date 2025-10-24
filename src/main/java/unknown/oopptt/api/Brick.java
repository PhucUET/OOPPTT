package unknown.oopptt.api;

import java.awt.*;


public class Brick extends GameEntity {
<<<<<<< Updated upstream
    public int hp = 1;
    public int score = 50;
    public Color color;
=======
    private static String brick1 = new File("src/main/resources/graphic/normal brick1.png").toURI().toString();
    private static String brick1Broken = new File("src/main/resources/graphic/broken brick1.png").toURI().toString();
    private static String brick2 = new File("src/main/resources/graphic/normal brick2.png").toURI().toString();
    private static String brick2Broken = new File("src/main/resources/graphic/broken brick2.png").toURI().toString();
    private static String brick3 = new File("src/main/resources/graphic/normal brick10.png").toURI().toString();
    private static String brick3Broken = new File("src/main/resources/graphic/broken brick10.png").toURI().toString();
    private int hitPoints = 1;
    protected String powerupType; // Loại PowerUp (ví dụ: "EXPAND", "MULTI_BALL"), null nếu không có
<<<<<<< Updated upstream
    private final static int widthBrick = 40;
    private final static int heightBrick = 20;
=======
    private final static int widthBrick = 35;
    private final static int heightBrick = 25;
>>>>>>> Stashed changes
    private int typeBrick = 0;
    private final ArrayList<Integer> pointList = new ArrayList<Integer>(Arrays.asList(50, 100, 150, 200));
>>>>>>> Stashed changes


    public Brick(String id, double x, double y, double w, double h, int hp, Color color) {
        super(id, "brick", x, y, w, h);
        this.hp = hp;
        this.color = color;
        this.score = 40 + hp * 20;
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