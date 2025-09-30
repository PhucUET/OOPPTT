package unknown.oopptt.api;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;

public class Data extends Manage{
    private ArrayList<Integer> scores; //lưu toàn bộ điểm
    private PriorityQueue<Integer> scoreQueue; //hàng đợi ưu tiên để lấy highscore

    public Data() {
        this.scores = new ArrayList<>();
        this.scoreQueue = new PriorityQueue<>(Collections.reverseOrder());
    }

    //thêm điểm của từng lượt chơi
    public void addScore(int score) {
        this.scores.add(score); //thêm vào danh sách điểm các lượt chơi
        this.scoreQueue.add(score); //thêm điểm vào hàng đợi ưu tiên
    }

    //lấy danh sách điểm chưa sắp xếp
    public ArrayList<Integer> getScores() {
        return scores;
    }

    //lấy điểm cao nhất
    public int getHighestScore() {
        if (scoreQueue.isEmpty()) {
            return 0;
        }
        return scoreQueue.peek();
    }

    //lấy danh sách các lượt chơi đã được sắp xếp theo điểm từ cao đến thấp
    public ArrayList<Integer> getSortedScores() {
        ArrayList<Integer> sortedScores = new ArrayList<>(scoreQueue);
        Collections.sort(sortedScores,  Collections.reverseOrder());
        return sortedScores;
    }

    @Override
    public void reset() {
        this.scores.clear();
        this.scoreQueue.clear();
    }
}
