package unknown.oopptt.api.enemy;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.api.Shooter;

import java.io.File;

public class ShooterEnemy extends Enemy {
    private static String path = new File("src/main/resources/graphic/Enemyshooter").toString();
    private static String pathCanon = new File("src/main/resources/graphic/EnemyShoot/Plazma_cannon _head1.png").toURI().toString();
    private Shooter shooter;
    private final Pane gameLayout;
    private final ImageView imageCanon;
    public ShooterEnemy(double x, double y, double height, double width, Pane gameLayout, ImageView paddleView) {
        super(x,y,height,width,path,0,0,50,200);
        this.gameLayout = gameLayout;
        this.imageCanon = new ImageView(new Image(pathCanon));
        imageCanon.setFitHeight(height);
        imageCanon.setFitWidth(width);
        imageCanon.setTranslateX(x - width/2);
        imageCanon.setTranslateY(y + height/2);
        imageCanon.setRotate(180);
        gameLayout.getChildren().add(imageCanon);
        shooter = new Shooter(200, 2, 20,10, gameLayout, paddleView, 30, imageCanon);
        shooter.setEnabled(true);
    }
    @Override
    public void update(double dt) {
        shooter.enemyTryFire(dt);
        shooter.updateE(dt);
    }

    public void clearShooter() {
        shooter.reset();
    }
}