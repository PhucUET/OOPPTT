package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.List;

public class Sheild {
    private String path = new File("src/main/resources/graphic/shield/shield.png").toString();
    private boolean isActive = false;
    private Pane layout_game;
    private ImageView imageView;
    private double durantion = 10.0;
    public Sheild(Pane layout_game, Integer stt) {
        this.layout_game = layout_game;
        imageView = new ImageView(new Image(path));
        imageView.setFitHeight(5);
        imageView.setFitWidth(layout_game.getWidth());
        imageView.setTranslateX(0);
        imageView.setTranslateY(layout_game.getHeight());
        imageView.setVisible(true);
    }

    public void setOpenShield(boolean isOpen) {
        if (isOpen) {
            return;
        }
        this.isActive = isOpen;
        durantion = 10;
    }
    public void update(double dt) {
        if (durantion <= 0) {
            this.isActive = false;
            durantion = 0;
            imageView.setVisible(false);
            return;
        }
        durantion -= dt;
    }
}