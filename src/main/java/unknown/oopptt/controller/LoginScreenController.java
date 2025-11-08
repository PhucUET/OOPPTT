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

public class LoginScreenController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private ImageView background;

    @FXML
    private AnchorPane root;

    private final Data data = new Data();

    private void goToMenuRotate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/MenuRotate.fxml"));
            Parent rotateRoot = loader.load();

            // Lấy stage hiện tại
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            // Tạo scene mới và gán trực tiếp
            Scene scene = new Scene(rotateRoot);
            stage.setFullScreen(true);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi tải màn hình MenuRotate!");
        }
    }

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
            case "LOGIN_OK" -> {
                lblMessage.setText("Đăng nhập thành công!");
                goToMenuRotate();
            }
            case "INVALID" -> lblMessage.setText("Sai tên hoặc mật khẩu!");
            default -> lblMessage.setText("Lỗi kết nối tới server!");
        }
    }

    /**
     * Xử lý khi người dùng nhấn vào link "Đăng ký tài khoản mới"
     */
    @FXML
    private void openRegister() {
        try {
            // Tải FXML mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/RegisterScreen.fxml"));
            Parent rootNew = loader.load();

            // Lấy stage hiện tại
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            // Tạo scene mới và gán thẳng
            Scene newScene = new Scene(rootNew);
            stage.setFullScreen(false);
            stage.setScene(newScene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi khi mở màn hình đăng ký!");
        }
    }


    @FXML
    public void initialize() {
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        System.out.println(root.sceneToLocal(root.getBoundsInParent()));
        System.out.println(root.getBoundsInParent().getWidth() + " " +  root.getBoundsInParent().getHeight());
        System.out.println(background.getBoundsInParent().getMinX() + " " +  background.getBoundsInParent().getMinY());
    }
}
