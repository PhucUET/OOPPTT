package unknown.oopptt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import unknown.oopptt.api.Data;

public class LoginScreenController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    private final Data data = new Data();

    /**
     * Xử lý khi người dùng nhấn nút "Đăng nhập"
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Gọi hàm login() từ Data để kiểm tra với Google Sheets
        String result = data.login(username, password);

        switch (result) {
            case "LOGIN_OK" -> lblMessage.setText("Đăng nhập thành công!");
            case "INVALID" -> lblMessage.setText("Sai tên hoặc mật khẩu!");
            default -> lblMessage.setText("Lỗi kết nối tới server!");
        }
    }

    /**
     * Xử lý khi người dùng nhấn vào link "Đăng ký tài khoản mới"
     */
    @FXML
    private void openRegister() {
        lblMessage.setText("Chuyển sang màn hình đăng ký");
        // Sau này bạn có thể dùng lệnh chuyển Scene ở đây
    }
}
