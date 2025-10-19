package unknown.oopptt.api;

public class Launcher1 {
    public static void main(String[] args) {
        NowPlay current = new NowPlay();
        Data data = new Data();

        current.addScore(200);
        current.addLives();
        current.displayStatus();

        data.addScore(current.getScore());
        data.addScore(350);
        data.addScore(120);
        System.out.println("🏆 Highest Score: " + data.getHighestScore());

        data.reset();
        current.reset();
        current.displayStatus();
    }
}
