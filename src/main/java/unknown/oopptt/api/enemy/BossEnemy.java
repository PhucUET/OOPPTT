package unknown.oopptt.api.enemy;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.api.Paddle;
import unknown.oopptt.api.Powerup;
import unknown.oopptt.api.Shooter;
import unknown.oopptt.controller.Game_Screen_Controller;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class BossEnemy extends Enemy {
    private static String path = new File("").toString();

    private double health;
    private static int Point = 500;
    private static double speedX = 0.3;
    private static double speedY = 0.7;
    private Paddle paddleLogic;
    private final Game_Screen_Controller controller;
    private Pane layout_game;
    private Shooter shooter = new Shooter(300, 20, 40, 40,
            layout_game, paddleLogic.getImageView(), 30);
    public BossEnemy(double x, double y, double height, double width, int health,
                     Paddle paddleLogic, Game_Screen_Controller controller, Pane layout_game) {
        super(x,y,height,width,path,speedX,speedY,health, Point);
        this.paddleLogic = paddleLogic;
        this.health = health;
        this.controller = controller;
        this.layout_game = layout_game;
    }

    public enum BOSSEFECT {
        SLOWPADDLE(10), THINPADDLE(8), FAKEPU(-1), SHEILD(-1), MINIONS(10);
        private final int duration;
        BOSSEFECT(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }
        public boolean haveDuration() {
            return duration > 0;
    }

    }


    private boolean shouldDrop(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }
    @Override
    public void update(double dt) {
        if (shouldDrop(0.1)) {
            setEffect(randomUniform());
        }
        this.pos_x += this.speedX * dt;
        this.pos_y += this.speedY * dt;
        this.getImageView().setTranslateX(this.pos_x - this.width / 2.0);
        this.getImageView().setTranslateY(this.pos_y - this.height / 2.0);
    }

    private void runTimeEffect(int duration, Runnable startEffect, Runnable endEffect) {
        new Thread(() -> {
            Platform.runLater((startEffect));
            try {
                Thread.sleep(duration * 1000L);
            } catch (InterruptedException e) {}
            Platform.runLater(endEffect);
        }).start();
    }

    static BossEnemy.BOSSEFECT randomUniform() {
        BossEnemy.BOSSEFECT[] vals = BossEnemy.BOSSEFECT.values();
        int i = ThreadLocalRandom.current().nextInt(vals.length);
        return vals[i];
    }

    private void setEffect(BOSSEFECT id) {
        switch (id) {
            case BOSSEFECT.FAKEPU: controller.createFakePU();break;
            case BOSSEFECT.SLOWPADDLE:runTimeEffect(id.duration,controller::slowPaddle,controller::fastPaddle);break;
            case BOSSEFECT.THINPADDLE:runTimeEffect(id.duration,controller::resetPaddle,controller::upPaddle);break;
            case BOSSEFECT.MINIONS:runTimeEffect(id.duration,controller::setCreateMinions, controller::setOffCreateMinions);break;
        }
    }

}
