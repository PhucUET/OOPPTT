package unknown.oopptt.api;

public class SpecialBrick extends Brick {

    /**
     * Gạch đặc biệt, mặc định cần 2 lần chạm và luôn thả PowerUp.
     */
    public SpecialBrick(int x, int y, int width, int height, String guaranteedPowerupType) {
        // Kế thừa: hitPoints=2 và powerupType cố định
        super(x, y, width, height, 2, guaranteedPowerupType);
    }

    @Override
    public String hit() {
        // Gọi logic hit của lớp cha (giảm hitPoints)
        String droppedPowerUp = super.hit();

        // Logic hiển thị đặc biệt cho gạch đặc biệt
        if (this.hitPoints == 1) {
            // Ví dụ: thay đổi màu/hình ảnh để báo hiệu lần chạm cuối
        }

        return droppedPowerUp;
    }

    // update() và draw() kế thừa từ Brick (nếu không cần thay đổi gì thêm)
}