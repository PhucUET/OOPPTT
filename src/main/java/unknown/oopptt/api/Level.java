package unknown.oopptt.api;

import java.io.File;

public final class Level {
    private int levelIndex = 0;
    private int score = 0;
    private File map;
    private String background;

    // --- Singleton (eager) ---
    private static final Level INSTANCE = new Level();
    private Level() {}
    public static Level getInstance() {           // đổi tên cho đúng convention
        return INSTANCE;                           // KHÔNG làm gì khác ở đây
    }

    // --- API điều khiển level ---
    public synchronized void start() {             // gọi một lần khi bắt đầu game
        this.levelIndex = 1;
        this.score = 0;
        loadLevel(this.levelIndex);
    }

    public synchronized void nextLevel() {
        this.levelIndex++;
        loadLevel(this.levelIndex);
    }

    public synchronized void onBrickDestroyed(int point, boolean isClear) {
        this.score += point;
        if (isClear) {                             // chỉ tăng MỘT lần
            nextLevel();
        }
    }

    public synchronized void resetNewGame() {
        start();
    }
    public synchronized void reset() {
        this.score = 0;
        loadLevel(this.levelIndex);
    }


    // --- getters ---
    public synchronized int getLevelIndex() { return levelIndex; }
    public synchronized int getScore() { return score; }
    public synchronized File getMap() { return map; }
    public synchronized String getBackground() { return background; }

    // --- private helpers ---
    private void loadLevel(int index) {
        this.map = getLevelMap(index);
    }

    private File getLevelMap(int levelIndex) {
        switch (levelIndex) {
            case 1:
                this.map = new File("src/main/resources/map/map1.txt");
                this.background = new File("src/main/resources/graphic/B2-Pale")
                        .toString();
                break;
            case 2:
                this.map = new File("src/main/resources/map/map2.txt");
                this.background = new File("src/main/resources/graphic/B3-Pale")
                        .toString();
                break;
            case 3:
                this.map = new File("src/main/resources/map/map3.txt");
                this.background = new File("src/main/resources/graphic/B4-Pale")
                        .toString();
                break;
            case 4:
                this.map = new File("src/main/resources/map/map4.txt");
                this.background = new File("src/main/resources/graphic/B5-Pale")
                        .toString();
                break;
            case 5:
                this.map = new File("src/main/resources/map/map5.txt");
                this.background = new File("src/main/resources/graphic/B6-Pale")
                        .toString();
                break;
            case 6:
                this.map = new File("src/main/resources/map/map6.txt");
                this.background = new File("src/main/resources/graphic/B7-Pale")
                        .toURI().toString();
                break;
            default:
                this.map = new File("src/main/resources/map/map6.txt");
                this.background = new File("src/main/resources/graphic/B7-Pale")
                        .toString();
        }
        return this.map;
    }
}