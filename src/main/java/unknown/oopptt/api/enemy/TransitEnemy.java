package unknown.oopptt.api.enemy;

import java.io.File;

public class TransitEnemy extends Enemy {
    private static final String path = new File("src/main/resources/graphic/TransitEnemy/Black-hole.png").toURI().toString();

    public TransitEnemy(double x, double y, double width, double height) {
        super(x, y, 30, 30, path, 0, 0, 10000, 10000);
    }

    @Override
    public void update(double dt) {

    }
}
