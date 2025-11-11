package unknown.oopptt.api.enemy;

import unknown.oopptt.api.GameEntity;

public abstract class Enemy extends GameEntity {
    private final int maxHp;
    private int hp = 0;
    private int point;

    protected double speedX;
    protected double speedY;

    protected Enemy(double x, double y, double width, double height,
                    String imagePath, double speedX, double speedY,
                    int hp, int point) {
        super(x, y, width, height, imagePath);
        this.maxHp = Math.max(1, hp);
        this.hp = Math.max(0, hp);
        this.point = Math.max(0, point);
        this.speedX = speedX;
        this.speedY = speedY;
    }


    public void onSpawn() { /* optional: animation/sfx */ }

    public int takeDamage(int amount) {
        if (amount <= 0 || isDead()) return 0;
        hp = Math.max(0, hp - amount);
        return isDead() ? point : 0;
    }

    public boolean isDead() { return hp <= 0; }
    public void healFull() { hp = maxHp; }

    public double getSpeedX() { return speedX; }
    public void setSpeedX(double speedX) { this.speedX = speedX; }
    public double getSpeedY() { return speedY; }
    public void setSpeedY(double speedY) { this.speedY = speedY; }

    public int getPoint() { return point; }
    public void setPoint(int point) { this.point = Math.max(0, point); }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }

    @Override
    public abstract void update(double dt);

    /**
     * Giải thích: v = X mới (absolute), dt dùng để cập nhật Y theo vận tốc.
     * - pos_x = v (gắn trực tiếp)
     * - pos_y += speedY * dt (tiến theo thời gian)
     * Sau đó đồng bộ translate của ImageView để hiển thị đúng (giữ tâm ở pos_x/pos_y).
     */
    @Override
    public void setLocation(double v, double dt) {
        // X mới là v
        this.pos_x = v;

        // Y chạy theo vận tốc theo thời gian dt
        this.pos_y += this.speedY * dt;

        // Đồng bộ ra ImageView (giữ tâm)
        this.getImageView().setTranslateX(this.pos_x - this.width / 2.0);
        this.getImageView().setTranslateY(this.pos_y - this.height / 2.0);
    }

}
