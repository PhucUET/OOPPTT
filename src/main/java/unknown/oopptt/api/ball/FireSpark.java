package unknown.oopptt.api.ball;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public  class FireSpark {
    final Circle node;
    double vx, vy, life = 1.0;

    public FireSpark(ImageView meteor) {
        node = new Circle(2 + Math.random() * 2, Color.rgb(255, 120, 0, 0.9));
        node.setTranslateX(meteor.getTranslateX() + meteor.getFitWidth() / 2);
        node.setTranslateY(meteor.getTranslateY() + meteor.getFitHeight() / 2);
        vx = (Math.random() - 0.5) * 150;
        vy = (Math.random() - 0.5) * 150;
    }

    void update(double dt) {
        life -= dt * 3.5;
        node.setOpacity(life);
        node.setTranslateX(node.getTranslateX() + vx * dt);
        node.setTranslateY(node.getTranslateY() + vy * dt);
    }
}