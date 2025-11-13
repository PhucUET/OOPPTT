package unknown.oopptt.api.enemy;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import unknown.oopptt.api.Paddle;
import unknown.oopptt.api.Powerup;
import unknown.oopptt.api.Shooter;
import unknown.oopptt.controller.Game_Screen_Controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BossEnemy extends Enemy {
    private static String path = new File("src/main/resources/graphic/Boss").toString();

    private static int Point = 500;
    private static double centerX;
    private static double centerY;
    private static double radius = 200;
    private double targetX;
    private double targetY;

    private Paddle paddleLogic;
    private final Game_Screen_Controller controller;
    private Pane layout_game;
    private Shooter shooter;

    private double targetTTL = 0;
    private final double TARGET_MIN = 4.0;
    private final double TARGET_MAX = 5.5;
    private final Map<BOSSEFECT, Boolean> effectTimers = new HashMap<>();

    public BossEnemy(double x, double y, double height, double width, int health,
                     Paddle paddleLogic, Game_Screen_Controller controller, Pane layout_game) {
        super(x, y, height, width, path, 30, 30, health, Point);
        this.paddleLogic = java.util.Objects.requireNonNull(paddleLogic, "paddleLogic is null");
        this.controller = controller;
        this.layout_game = layout_game;
        centerX = x;
        centerY = y;
        System.out.println(centerX + " " + centerY);
        shooter = new Shooter(300, 3, 40, 40,
                layout_game, paddleLogic.getImageView(), 30, this.imageView, paddleLogic);

        shooter.setEnabled(true);
    }

    public enum BOSSEFECT {
        SLOWPADDLE(3), THINPADDLE(4), FAKEPU(-1), MINIONS(3);
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

    public static int random1to2() {
        return ThreadLocalRandom.current().nextInt(1, 2); // 1.0 <= x < 2.0
    }

    public static double random1to3() {
        return ThreadLocalRandom.current().nextDouble(1, 2); // 1.0 <= x < 2.0
    }

    private boolean shouldDrop(double probability) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }

    @Override
    public void update(double dt) {
        double dx = targetX - this.pos_x;
        double dy = targetY - this.pos_y;
        double dist = Math.hypot(dx, dy);

        if (dist > 1e-3) {
            double vx = (dx / dist) * this.speedX;
            double vy = (dy / dist) * this.speedY;

            this.pos_x += vx * dt;
            this.pos_y += vy * dt;
        }

        targetTTL -= dt;
        if (targetTTL <= 0 || dist < 0) {
            pickNewTarget();
            if (shouldDrop(0.1)) {
                setEffect(randomUniform());
            }
        }

        if (shooter.getEnabled()) {
            shooter.enemyTryFire(dt);
        }

        shooter.updateE(dt);
        this.getImageView().setTranslateX(this.pos_x - this.width / 2.0);
        this.getImageView().setTranslateY(this.pos_y - this.height / 2.0);
    }

    public void clearShooter() {
        shooter.reset();
    }

    private void pickNewTarget() {
        double u = ThreadLocalRandom.current().nextDouble();
        double v = ThreadLocalRandom.current().nextDouble();
        double ang = 2 * Math.PI * u;
        double rad = radius * Math.sqrt(v);      // sqrt để đều diện tích
        targetX = centerX + rad * Math.cos(ang);
        targetY = centerY + rad * Math.sin(ang);

        targetTTL = ThreadLocalRandom.current().nextDouble(TARGET_MIN, TARGET_MAX);
    }

    private void runTimeEffect(int duration, Runnable startEffect, Runnable endEffect) {
        new Thread(() -> {
            Platform.runLater((startEffect));
            try {
                Thread.sleep(duration * 1000L);
            } catch (InterruptedException e) {
            }
            Platform.runLater(endEffect);
        }).start();
    }

    static BOSSEFECT randomUniform() {
        BOSSEFECT[] vals = BOSSEFECT.values();
        int i = ThreadLocalRandom.current().nextInt(vals.length);
        return vals[i];
    }

    private void setEffect(BOSSEFECT id) {
        switch (id) {
            case BOSSEFECT.FAKEPU:
                controller.createFakePU();
                break;
            case BOSSEFECT.SLOWPADDLE:
                runTimeEffect(id.duration, controller::slowPaddle, controller::offslowPaddle);
                break;
            case BOSSEFECT.THINPADDLE:
                runTimeEffect(id.duration, controller::downPaddle, controller::offdownPaddle);
                break;
            case BOSSEFECT.MINIONS:
                runTimeEffect(id.duration, controller::setCreateMinions, controller::setOffCreateMinions);
                break;
        }
    }

}
