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
        try {
            // Tải FXML mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/RegisterScreen.fxml"));
            Parent rootNew = loader.load();

            // Lấy scene hiện tại
            Scene scene = txtUsername.getScene();
            Stage stage = (Stage) scene.getWindow();

            // Tạo hiệu ứng mờ dần cho giao diện cũ
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), scene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                // Khi fadeOut xong thì chuyển scene mới
                Scene newScene = new Scene(rootNew);
                stage.setScene(newScene);
                stage.setFullScreen(true);

                // Sau khi gán scene mới, fadeIn để mờ dần hiện ra
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), rootNew);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            // Bắt đầu hiệu ứng mờ dần biến mất
            fadeOut.play();

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
