package unknown.oopptt.api;

import java.util.*;

public class NowPlay extends Manage {

    public static class Player {
        private final String name;
        private int score;
        private int highScore;
        private int lives;

        //phương thức khởi tạo, với mỗi player sẽ có tên khác nhau, 3 thuộc tính còn lại giống nhau
        public Player(String name) {
            this.name = name;
            this.score = 0;
            this.highScore = 0;
            this.lives = 3;
        }

        //các getter
        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public int getHighScore() {
            return highScore;
        }

        public int getLives() {
            return lives;
        }

        //cộng điểm, cập nhật highscore nếu điểm được cộng cao hơn
        public void addScore(int points) {
            this.score += points;
            this.highScore = Math.max(highScore, score);
        }

        //-1 mạng sống
        public void loseLife() {
            if (lives > 0) lives--;
        }

        //kiểm tra thua chưa
        public boolean isGameOver() {
            return lives <= 0;
        }

        //dùng khi bắt đầu trận mới, reset lại player
        public void reset() {
            this.score = 0;
            this.lives = 3;
        }
        public void addLive() {
            this.lives += 1;
        }
    }

    //enum là kiểu dữ liệu liệt kê, để liệt kê các mode game
    public enum Mode {
        SOLO, PK
    }

    private final Map<String, Player> players = new LinkedHashMap<>();
    private Mode mode = Mode.SOLO; //gán mode chơi mặc định ban đầu là solo



    //đặt lại mode chơi
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public Mode getMode() {
        return mode;
    }


    //thêm người chơi mới nếu chưa tồn tại
    public void addPlayer(String name) {
        players.putIfAbsent(name, new Player(name));
    }

    //cập nhật điểm số
    public void updateScore(String name, int points) {
        Player p = players.get(name); //lấy ng chơi theo tên
        if (p != null) p.addScore(points); //nếu tồn tại ng chơi p thì cộng điểm
    }


    //đưa ra bảng xếp hạng
    public List<Player> getRanking() {
        List<Player> list = new ArrayList<>(players.values()); //tạo ds ng chơi
        //sắp xếp danh sách theo điểm giảm dần
        list.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return list;
    }

    public void addLive(String name) {
        Player p = players.get(name);
        p.addLive();
    }

    //kết thúc trận đấu
    public void endMatch(Data data) {
        //in ra màn hình chế độ hiện tại
        System.out.println("End of match (" + mode + ")");
        //duyệt qua toàn bộ người chơi, gửi điểm của từng người lên database, ggs
        for (Player p : players.values()) {
            data.saveScore(p.getName(), p.getScore(), p.getHighScore(), mode.name());
        }

        //in ra bẳng xếp hạng
        printRanking();

        //nếu mode game là pk thì xong trận sẽ reset lại cho tất cả player
        if (mode == Mode.PK) {
            players.values().forEach(Player::reset);
        }
    }

    //in bảng xếp hạng
    public void printRanking() {
        int i = 1;
        for (Player p : getRanking()) {
            System.out.printf("%d. %s - %d điểm (Highscore: %d)%n",
                    i++, p.getName(), p.getScore(), p.getHighScore());
        }
    }

    @Override
    public void reset() {
        players.clear();
        mode = Mode.SOLO;
        System.out.println("NowPlay reset!");
    }
}