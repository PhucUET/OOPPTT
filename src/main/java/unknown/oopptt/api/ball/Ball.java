package unknown.oopptt.api.ball;

import javafx.animation.FadeTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import unknown.oopptt.api.GameEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class Ball – đại diện cho quả bóng / thiên thạch trong game Arkanoid.
 * Có hiệu ứng quầng sáng, vệt cháy, và tia lửa nhỏ khi bay.
 */
public class Ball extends GameEntity {

    // ====== CONSTANTS ======
    private static final String BALL_FOLDER = "src/main/resources/graphic/Ball";
    private static final double BASE_SPEED = 300.0;
    private static final int BALL_SIZE = 12;

    // ====== MOVEMENT ======
    private double speedX;
    private double speedY;
    private double speedScale = BASE_SPEED;

    // ====== STATE ======
    private boolean sticky = true;   // đang dính paddle
    private double offsetOnPaddle = 0;

    // ====== GRAPHIC ======
    private final Circle hitCircle = new Circle(BALL_SIZE / 2.0);
    private final DropShadow fireGlow;      // quầng sáng
    private final List<FireSpark> sparks = new ArrayList<>();
    private double trailTimer = 0;
    private Pane effectLayer;               // nơi vẽ vệt lửa, tia lửa

    // ============================================================== //
    // Constructors
    // ============================================================== //

    public Ball(double x, double y, double speedX, double speedY, Pane effectLayer) {
        super(x, y, BALL_SIZE, BALL_SIZE, BALL_FOLDER);
        this.speedX = speedX;
        this.speedY = speedY;


        // hiệu ứng ánh sáng xung quanh thiên thạch
        fireGlow = new DropShadow();
        fireGlow.setRadius(10);
        fireGlow.setSpread(0.6);
        fireGlow.setColor(Color.ORANGERED);
        imageView.setEffect(fireGlow);
        this.effectLayer = effectLayer;
    }

    /**
     * Copy constructor – tạo 1 bóng mới từ bóng gốc
     */
    public Ball(Ball other) {
        super(other.getPos_x(), other.getPos_y(), BALL_SIZE, BALL_SIZE, BALL_FOLDER);
        this.speedX = other.speedX;
        this.speedY = other.speedY;
        this.speedScale = other.speedScale;
        this.sticky = false;
        this.effectLayer = other.effectLayer;

        fireGlow = new DropShadow();
        fireGlow.setRadius(10);
        fireGlow.setSpread(0.6);
        fireGlow.setColor(Color.ORANGERED);
        imageView.setEffect(fireGlow);
    }

    // ============================================================== //
    // Hiệu ứng / setup layer
    // ============================================================== //

    /**
     * Gán lớp hiệu ứng (Pane) để vẽ vệt sáng và tia lửa
     */
    public void attachEffectLayer(Pane layer) {
        this.effectLayer = layer;
    }

    /**
     * Tạo vệt cháy mờ phía sau thiên thạch
     */
    private void spawnTrail() {
        if (effectLayer == null) return;

        ImageView trail = new ImageView(imageView.getImage());
        trail.setFitWidth(imageView.getFitWidth() * 0.9);
        trail.setFitHeight(imageView.getFitHeight() * 0.9);
        trail.setTranslateX(imageView.getTranslateX());
        trail.setTranslateY(imageView.getTranslateY());
        trail.setOpacity(0.5);
        trail.setEffect(fireGlow);

        effectLayer.getChildren().add(trail);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.4), trail);
        fade.setFromValue(0.5);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> effectLayer.getChildren().remove(trail));
        fade.play();
    }

    /**
     * Sinh tia lửa nhỏ bay tỏa ra từ thiên thạch
     */
    private void spawnSpark() {
        if (effectLayer == null) return;
        FireSpark spark = new FireSpark(imageView);
        sparks.add(spark);
        effectLayer.getChildren().add(spark.node);
    }

    /**
     * Cập nhật tia lửa
     */
    private void updateSparks(double dt) {
        if (sparks.isEmpty()) return;
        Iterator<FireSpark> it = sparks.iterator();
        while (it.hasNext()) {
            FireSpark s = it.next();
            s.update(dt);
            if (s.life <= 0) {
                effectLayer.getChildren().remove(s.node);
                it.remove();
            }
        }
    }

    // ============================================================== //
    // Update logic
    // ============================================================== //

    /**
     * Cập nhật vị trí bóng mỗi frame
     */
    @Override
    public void update(double dt) {
        //if (sticky) return;

        double dx = speedX * dt * speedScale;
        double dy = speedY * dt * speedScale;

        pos_x += dx;
        pos_y += dy;

        // cập nhật ImageView (UI)


        trailTimer += dt;
        if (trailTimer > 0.03) {
            spawnTrail();
            if (Math.random() < 0.3) spawnSpark();
            trailTimer = 0;
        }
        updateSparks(dt);

        updateAnimation(dt);

        imageView.setTranslateX(pos_x - BALL_SIZE / 2.0);
        imageView.setTranslateY(pos_y - BALL_SIZE / 2.0);
    }


    /**
     * Khi bóng đang dính vào paddle và paddle di chuyển
     */
    @Override
    public void setLocation(double newX, double dt) {
        this.pos_x = newX;
        imageView.setTranslateX(newX - BALL_SIZE / 2.0);
        updateAnimation(dt);
    }
    public void setLocation(double newX,double newY, double dt) {
        this.pos_x = newX;
        this.pos_y = newY;
        imageView.setTranslateX(newX - BALL_SIZE / 2.0);
        imageView.setTranslateY(newY - BALL_SIZE / 2.0);
        updateAnimation(dt);
    }
    // ============================================================== //
    // Game interaction helpers
    // ============================================================== //

    /**
     * Dừng bóng trên paddle
     */
    public void stopBall(double offsetX) {
        this.speedX = 0.3;
        this.speedY = 0;
        this.offsetOnPaddle = offsetX;
        this.sticky = true;
    }

    /**
     * Gỡ dính khỏi paddle
     */
    public void release() {
        this.sticky = false;
    }

    /**
     * Thay đổi kích thước bóng (power-up)
     */
    public void changeSize(int newSize) {
        imageView.setFitWidth(newSize);
        imageView.setFitHeight(newSize);
    }

    // ============================================================== //
    // Getters / Setters
    // ============================================================== //

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedScale() {
        return speedScale;
    }

    public void setSpeedScale(double newSpeed) {
        speedScale = newSpeed;
    }

    public void addOffsetOnPaddle(double dt) {
        offsetOnPaddle += dt * speedX * speedScale;
    }

    public void setOffsetOnPaddle(double offsetOnPaddle) {
        this.offsetOnPaddle = offsetOnPaddle;
    }

    public double getOffsetOnPaddle() {
        return offsetOnPaddle;
    }
}
