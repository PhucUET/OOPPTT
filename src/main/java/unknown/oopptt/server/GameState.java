package unknown.oopptt.server;

import java.util.concurrent.atomic.AtomicInteger;

public class GameState {
    private AtomicInteger frame = new AtomicInteger(0);

    // Ở bước sau sẽ có:
    // - danh sách bóng, paddle, powerup, điểm số...

    public int getFrame() {
        return frame.get();
    }

    public void nextFrame() {
        frame.incrementAndGet();
    }
}
