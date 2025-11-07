package unknown.oopptt.api;

import java.util.List;

public class TestAPI {
    public static void main(String[] args) {
        Data data = new Data();

        // ✅ Test gửi dữ liệu lên Google Sheets
        System.out.println("Đang gửi dữ liệu thử...");
//        data.saveScore("TesterThuy", 120, 200, "SOLO");
//        data.saveScore("Tester", 120, 360, "SOLO");
//        data.saveScore("Thuy", 36, 98, "SOLO");
//        data.saveScore("T", 120, 200, "PK");
//        data.saveScore("T", 360, 980, "SOLO");
//        data.saveScore("ThinThiThuy", 100, 1000, "SOLO");
//        data.saveScore("PhamAnhChuc", 34, 363636, "SOLO");

        // ✅ Test lấy dữ liệu highscore toàn cầu
        System.out.println("\nĐang lấy dữ liệu từ Google Sheets...");
        List<String> top = data.fetchGlobalHighScores();

        // In kết quả ra console
        System.out.println("\nBảng xếp hạng toàn cầu:");
        top.forEach(System.out::println);
    }
}
