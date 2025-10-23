package unknown.oopptt.api;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data extends Manage {

    /**
     * Đảm bảo người chơi tồn tại trong bảng players.
     * Nếu chưa có, sẽ tự động thêm mới.
     */
    private void ensurePlayerExists(String playerName) throws SQLException {
        String checkSQL = "SELECT name FROM players WHERE name = ?";
        String insertSQL = "INSERT INTO players (name) VALUES (?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSQL)) {

            psCheck.setString(1, playerName);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) { // nếu chưa tồn tại
                try (PreparedStatement psInsert = conn.prepareStatement(insertSQL)) {
                    psInsert.setString(1, playerName);
                    psInsert.executeUpdate();
                    System.out.println("Added new player: " + playerName);
                }
            }
        }
    }

    /**
     * Lưu điểm của người chơi vào bảng highscores.
     * Tự động thêm người chơi mới nếu chưa tồn tại.
     */
    public void saveScore(String playerName, int score, int highScore, String mode) {
        String sql = "INSERT INTO highscores (player_name, score, highscore, mode) VALUES (?, ?, ?, ?)";

        try {
            // đảm bảo player tồn tại trước khi insert
            ensurePlayerExists(playerName);

            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, playerName);
                ps.setInt(2, score);
                ps.setInt(3, highScore);
                ps.setString(4, mode);
                ps.executeUpdate();

                System.out.printf("Saved score for %s | Score=%d | High=%d | Mode=%s%n",
                        playerName, score, highScore, mode);

                uploadToGoogleSheets(playerName, score, highScore, mode);
            }

        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách top N người chơi có điểm cao nhất.
     * Gộp theo người chơi để tránh trùng lặp.
     */
    public List<String> getTopScores(int limit) {
        List<String> top = new ArrayList<>();
        String sql = """
                SELECT player_name, MAX(highscore) AS best
                FROM highscores
                GROUP BY player_name
                ORDER BY best DESC
                LIMIT ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("player_name");
                int best = rs.getInt("best");
                top.add(name + " - " + best);
            }

        } catch (SQLException e) {
            System.err.println("Error loading top scores: " + e.getMessage());
        }

        return top;
    }

    /**
     * Lấy điểm cao nhất của một người chơi cụ thể.
     */
    public int getPlayerHighScore(String playerName) {
        String sql = "SELECT MAX(highscore) AS best FROM highscores WHERE player_name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("best");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching highscore: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Xóa toàn bộ dữ liệu điểm (reset highscores).
     */
    @Override
    public void reset() {
        String sql = "DELETE FROM highscores";

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate(sql);
            System.out.println("Data reset: cleared highscores table.");

        } catch (SQLException e) {
            System.err.println("Error resetting data: " + e.getMessage());
        }
    }

    private static final String SCRIPT_URL =
            "https://script.google.com/macros/s/AKfycbwnvhxGNndF8LUSxJW7I-hDCxWv5aWd0ouQdiTS9PxksOsi4DwOb1xU2GmLLXu-RV8ZTQ/exec";

    public void uploadToGoogleSheets(String playerName, int score, int highscore, String mode) {
        try {
            URL url = new URL(SCRIPT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON body
            String json = String.format(
                    "{\"player_name\":\"%s\",\"score\":%d,\"highscore\":%d,\"mode\":\"%s\"}",
                    playerName, score, highscore, mode
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Sent data to Google Sheets successfully!");
            } else {
                System.out.println("⚠️ Google Sheets returned code: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}