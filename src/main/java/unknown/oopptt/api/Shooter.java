package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.*;

public class Shooter {
    private static final String path = new File("src/main/resources/graphic/shoots.png").toURI().toString();

    private final double BULLET_SPEED;
    private final double COLD_DOWN;
    private final double BULLET_W,BULLET_H;


    private final Pane game_Layout;
    private final Image bulletImg;

    private final ImageView paddleView;
    private boolean enabled = false;
    private double timeSinceLastShoot;


    private final Deque<Bullet> bulletPool = new ArrayDeque<>();
    private final List<Bullet> active = new ArrayList<>();

    public Shooter(double bulletSpeed, double coldDown, double bulletW, double bulletH, Pane gameLayout, ImageView paddleView, int prewarmPoolSize) {
        BULLET_SPEED = bulletSpeed;
        COLD_DOWN = coldDown;
        BULLET_W = bulletW;
        BULLET_H = bulletH;
        game_Layout = gameLayout;
        this.paddleView = paddleView;
        this.bulletImg = new Image(path);

        for (int i = 0 ; i < prewarmPoolSize ; i++){
            Bullet bullet = new Bullet(new ImageView(bulletImg));
            bulletPool.push(bullet);
            bullet.imageView.setVisible(false);
            bullet.imageView.setFitWidth(BULLET_W);
            bullet.imageView.setFitHeight(BULLET_H);
            bullet.imageView.setPreserveRatio(false);
            game_Layout.getChildren().add(bullet.imageView);
        }
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void update(double dtsecond, List<Brick> bricks){
        timeSinceLastShoot += dtsecond;

        if (!active.isEmpty()){
            Iterator<Bullet> it = active.iterator();
            while (it.hasNext()){
                Bullet bullet = it.next();
                if (!bullet.alive) {
                    it.remove();
                    continue;
                }
                bullet.y += bullet.vy * dtsecond;
                bullet.imageView.setTranslateY(bullet.y);
                bullet.imageView.setTranslateX(bullet.x);

                if (bullet.y == 0) {
                    recycle(it, bullet);
                    continue;
                }

                for (int i = bricks.size() - 1; i >= 0; i--){
                    Brick brick = bricks.get(i);
                    if (brick.getImageView().getBoundsInParent().intersects(bullet.imageView.getBoundsInParent())){
                        if (!brick.hit()) {
                            game_Layout.getChildren().remove(brick.imageView);
                            bricks.remove(brick);
                        }
                        recycle(it, bullet);
                        break;
                    }
                }

            }
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void tryFire() {
        if (!enabled || paddleView == null) {
            return;
        }
        if (timeSinceLastShoot < COLD_DOWN) {
            return;
        }
        Fire();
        timeSinceLastShoot = 0;
    }

    private void Fire() {
        double leftX = paddleView.getBoundsInParent().getMinX();
        double rightX = paddleView.getBoundsInParent().getMaxX();
        double py =  paddleView.getBoundsInParent().getMinY();

        Bullet  lBullet = getBullet();
        lBullet.x = leftX - BULLET_W/2.0;
        lBullet.y = py - BULLET_H/2.0;
        lBullet.vy = -BULLET_SPEED;
        lBullet.imageView.setTranslateX(lBullet.x);
        lBullet.imageView.setTranslateY(lBullet.y);
        lBullet.imageView.setVisible(true);
        lBullet.alive = true;
        active.add(lBullet);

        Bullet rBullet = getBullet();
        rBullet.x = rightX - BULLET_W/2.0;
        rBullet.y = py - BULLET_H/2.0;
        rBullet.vy = -BULLET_SPEED;
        rBullet.imageView.setTranslateX(rBullet.x);
        rBullet.imageView.setTranslateY(rBullet.y);
        rBullet.imageView.setVisible(true);
        rBullet.alive = true;
        active.add(rBullet);

        timeSinceLastShoot = 0;

    }

    private Bullet getBullet() {
        Bullet b = bulletPool.pollFirst();
        if (b == null) {
            ImageView iv = new ImageView();
            iv.setMouseTransparent(true);
            iv.setFitWidth(BULLET_W);
            iv.setFitHeight(BULLET_H);
            iv.setPreserveRatio(false);
            if (bulletImg != null) iv.setImage(bulletImg);
            iv.setVisible(false);
            game_Layout.getChildren().add(iv);
            b = new Bullet(iv);
        }
        return b;
    }

    private void recycle(Iterator<Bullet> it, Bullet bullet){
        bullet.alive = false;
        bullet.imageView.setVisible(false);
        it.remove();
        bulletPool.addLast(bullet);
    }
}
