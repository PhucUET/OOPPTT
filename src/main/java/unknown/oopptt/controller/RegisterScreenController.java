package unknown.oopptt.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import unknown.oopptt.api.Data;

public class RegisterScreenController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMessage;
    @FXML
    private ImageView background;
    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }

    private final Data data = new Data();

    @FXML
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirm)) {
            lblMessage.setText("Mật khẩu không khớp!");
            return;
        }

        String result = data.register(username, password);

        switch (result) {
            case "REGISTER_OK" -> lblMessage.setText("Đăng ký thành công!");
            case "EXISTS" -> lblMessage.setText("Tài khoản đã tồn tại!");
            default -> lblMessage.setText("Lỗi kết nối hoặc không thể đăng ký!");
        }
    }

    @FXML
    private void openLogin() {
        try {
            // Tải file LoginScreen.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/LoginScreen.fxml"));
            Parent loginRoot = loader.load();

            // Lấy stage hiện tại từ nút hoặc textfield bất kỳ
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            // Tạo scene mới và gán thẳng
            Scene scene = new Scene(loginRoot);
            stage.setFullScreen(false);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi khi mở màn hình đăng nhập!");
        }
    }

}
