package unknown.oopptt.api;

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
    }
}
