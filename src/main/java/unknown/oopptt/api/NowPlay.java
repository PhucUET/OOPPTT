package unknown.oopptt.api;

public class NowPlay extends Manage{
    private int score;
    private int lives;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    //quản lý điểm số và mạng sống ban đầu
    public NowPlay() {
        this.score = 0;
        this.lives = 3;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void reduceScore(int points) {
        this.score = Math.max(0, this.score - points);
    }

    public void reduceLives() {
        if (this.lives > 0) {
            this.lives--;
        }
    }

    public void addLives() {
        this.lives++;
    }

    public boolean isGameOver() {
        return this.lives <= 0;
    }

    @Override
    public void reset() {
        this.score = 0;
        this.lives = 3;
    }

    public void displayStatus() {
        System.out.println("Score: " + score + " | Lives: " + lives);
    }
}
