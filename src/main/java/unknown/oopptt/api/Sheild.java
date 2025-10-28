package unknown.oopptt.api;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

public class Sheild {
    public void openSheild(List<Brick> bricks, Pane layout_game, ImageView backgroundGame) {
        Brick newsheild = new Brick(backgroundGame.getBoundsInParent().getMinX(),
                backgroundGame.getBoundsInParent().getMaxY() - 5,layout_game.getLayoutBounds().getWidth(),2,1);
        bricks.add(newsheild);
    }
}