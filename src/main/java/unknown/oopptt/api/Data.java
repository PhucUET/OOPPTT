package unknown.oopptt.api;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Collections;

public class Data extends Manage{
    private PriorityQueue<Integer> scoreQueue; //hàng đợi ưu tiên để lấy highscore

    public Data() {
        this.scoreQueue = new PriorityQueue<>(Collections.reverseOrder());
    }

    //thêm điểm của từng lượt chơi
    public void addScore(int score) {
        this.scoreQueue.add(score); //thêm điểm vào hàng đợi ưu tiên
    }

    //lấy điểm cao nhất
    public int getHighestScore() {
        if (scoreQueue.isEmpty()) {
            return 0;
        }
        return scoreQueue.peek();
    }

    @Override
    public void reset() {
        this.scoreQueue.clear();
        System.out.println("🔁 Data reset: cleared all stored scores");
    }

    public List<Integer> getTopScores(int n) {
        List<Integer> topScores = new ArrayList<>(scoreQueue);
        Collections.sort(topScores, Collections.reverseOrder());
        return topScores.subList(0, Math.min(n, topScores.size()));
    }
}
