package unknown.oopptt.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class Data extends Manage {

    private static final String SCRIPT_URL =
            "https://script.google.com/macros/s/AKfycbwnvhxGNndF8LUSxJW7I-hDCxWv5aWd0ouQdiTS9PxksOsi4DwOb1xU2GmLLXu-RV8ZTQ/exec";

    /**
     * Lưu điểm của người chơi hiện tại lên Google Sheets.
     */
    public void saveScore(String playerName, int score, int highScore, String mode) {
        try {
            //tạo kết nối HTTP
            URL url = new URL(SCRIPT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //mở 1 kết nối đến gg apps script bằng phương thức post
            conn.setRequestMethod("POST");
            //gửi dữ liệu dạng json
            conn.setRequestProperty("Content-Type", "application/json");
            //cho phép gửi nội dung kèm theo
            conn.setDoOutput(true);

            //tạo nội dung json
            String json = String.format(
                    "{\"action\":\"save_score\",\"player_name\":\"%s\",\"score\":%d,\"highscore\":%d,\"mode\":\"%s\"}",
                    playerName, score, highScore, mode
            );

            //gửi dữ liệu đi, mở OutputStream đến sever
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("UTF-8")); //
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Gửi dữ liệu lên Google Sheets thành công!");
            } else {
                System.out.println("Google Sheets trả về mã: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.err.println("Lỗi khi gửi dữ liệu: " + e.getMessage());
        }
    }

    //đăng ký
    public String register(String username, String password) {
        try {
            URL url = new URL(SCRIPT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"action\":\"register\",\"username\":\"%s\",\"password\":\"%s\"}",
                    username, password
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("UTF-8"));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();

            if (response.contains("REGISTER_OK")) return "REGISTER_OK";
            if (response.contains("EXISTS")) return "EXISTS";
            return "ERROR";

        } catch (Exception e) {
            System.err.println("Lỗi khi đăng ký: " + e.getMessage());
            return "ERROR";
        }
    }

    //đăng nhập
    public String login(String username, String password) {
        try {
            String query = String.format("action=login&username=%s&password=%s",
                    URLEncoder.encode(username, "UTF-8"),
                    URLEncoder.encode(password, "UTF-8"));

            URL url = new URL(SCRIPT_URL + "?" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String response = reader.readLine();
            reader.close();

            if (response.contains("LOGIN_OK")) return "LOGIN_OK";
            if (response.contains("INVALID")) return "INVALID";
            return "ERROR";

        } catch (Exception e) {
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
            return "ERROR";
        }
    }



    /**
     * Tạm thời lưu top điểm trong bộ nhớ RAM (không cần SQL)
     * để hiển thị bảng xếp hạng nhanh trong cùng phiên chơi.
     */
    private final List<String> localScores = new ArrayList<>();

    public void addLocalScore(String player, int score) {
        localScores.add(player + " - " + score);
    }

    public List<String> getLocalScores() {
        return new ArrayList<>(localScores);
    }

    /**
     * Lấy bảng xếp hạng highscore toàn cầu (chỉ chế độ SOLO)
     * từ Google Sheets và sắp xếp giảm dần.
     */
    public List<String> fetchGlobalHighScores() {
        List<String> topList = new ArrayList<>();
        try {
            URL url = new URL(SCRIPT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Đọc dữ liệu phản hồi từ Apps Script
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);
            reader.close();

            // Phân tích JSON
            JSONArray arr = new JSONArray(response.toString());

            // Map để gom điểm cao nhất theo tên
            Map<String, Integer> playerHighscores = new HashMap<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String mode = obj.optString("mode", "");
                String name = obj.optString("player_name", "Unknown");
                int highscore = obj.optInt("highscore", 0);

                // Chỉ lấy dữ liệu chế độ SOLO
                if (mode.equalsIgnoreCase("SOLO")) {
                    // Nếu tên đã tồn tại -> lấy điểm cao hơn
                    playerHighscores.merge(name, highscore, Math::max);
                }
            }

            // Chuyển thành danh sách PlayerRecord để sắp xếp
            List<PlayerRecord> records = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : playerHighscores.entrySet()) {
                records.add(new PlayerRecord(entry.getKey(), entry.getValue()));
            }

            // Sắp xếp giảm dần theo điểm
            records.sort(Comparator.comparingInt(PlayerRecord::getHighscore).reversed());

            // Lấy top 10 người
            int rank = 1;
            for (PlayerRecord p : records.subList(0, Math.min(10, records.size()))) {
                topList.add(rank++ + ". " + p.getName() + " - " + p.getHighscore() + " điểm");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu từ Google Sheets: " + e.getMessage());
        }

        return topList;
    }

    /**
     * Lớp phụ trợ để lưu tạm player & điểm cao nhất.
     */
    private static class PlayerRecord {
        private final String name;
        private final int highscore;

        public PlayerRecord(String name, int highscore) {
            this.name = name;
            this.highscore = highscore;
        }

        public String getName() { return name; }
        public int getHighscore() { return highscore; }
    }

    /**
     * Xóa dữ liệu cục bộ (RAM). Không ảnh hưởng dữ liệu trên Google Sheets.
     */
    @Override
    public void reset() {
        localScores.clear();
        System.out.println("Dữ liệu cục bộ đã được reset (Sheets vẫn giữ nguyên).");
    }
}