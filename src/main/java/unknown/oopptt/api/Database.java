package unknown.oopptt.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/arkanoid_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "150406";

    /**
     * Trả về connection mới mỗi lần gọi.
     * Tự động nạp driver nếu chưa nạp.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        // Kiểm tra
        if (conn != null && !conn.isClosed()) {
            System.out.println("Connected to MySQL successfully!");
        }
        return conn;
    }
}