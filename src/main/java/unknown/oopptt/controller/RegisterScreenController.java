package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import unknown.oopptt.api.Data;
import unknown.oopptt.api.ParallaxBackground;
import unknown.oopptt.api.SoundManager;

public class RegisterScreenController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMessage;
    @FXML private ImageView background;
    @FXML private AnchorPane root;
    private ParallaxBackground bg = new ParallaxBackground(1440, 780);

    private final Data data = new Data();

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            bg = new ParallaxBackground(root.getWidth(), root.getHeight());
            root.getChildren().add(0, bg.getRoot());
            startGameloop();
        });

    }

    private static final double TARGET_FPS = 60;
    private static final  double STEP = 1.0/TARGET_FPS;
    AnimationTimer timer;
    private void startGameloop() {
        timer = new AnimationTimer() {
            private long lastTime = 0L;
            private double accumulator = 0.0;

            @Override
            public void handle(long now) {
                if (lastTime == 0L) {
                    lastTime = now;
                    return;
                }

                double dt = (now - lastTime) / 1e9;
                if (dt > 0.25) {
                    dt = 0.25;
                }

                lastTime = now;
                accumulator += dt;

                if (accumulator > STEP) {
                    bg.update(STEP);
                    accumulator -= STEP;

                }

            }
        };
        timer.start();
    }

    @FXML
    private void handleRegister() {
        final String username = txtUsername.getText().trim();
        final String password = txtPassword.getText().trim();
        final String confirm  = txtConfirmPassword.getText().trim();

        SoundManager.playSoundEffect("click.mp3");

        //validate nhẹ trên ui thread (không cần overlay nếu sai đầu vào)
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Please enter full information!");
            return;
        }
        if (!password.equals(confirm)) {
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Passwords do not match!");
            return;
        }

        //hiện overlay ngay lập tức
        final Parent overlay;
        final LoadingController loadingCtrl;
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/unknown/oopptt/Loading.fxml"));
            overlay = fx.load();
            loadingCtrl = fx.getController();
            overlay.setPickOnBounds(true); //chặn click xuyên
            AnchorPane.setTopAnchor(overlay, 0.0);
            AnchorPane.setRightAnchor(overlay, 0.0);
            AnchorPane.setBottomAnchor(overlay, 0.0);
            AnchorPane.setLeftAnchor(overlay, 0.0);
            root.getChildren().add(overlay);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMessage.setText("Cannot show loading overlay!");
            return;
        }

        //gọi register ở bgr (không chặn ui)
        javafx.concurrent.Task<String> registerTask = new javafx.concurrent.Task<>() {
            @Override protected String call() {
                return data.register(username, password);
            }
        };

        registerTask.setOnSucceeded(ev -> {
            //gỡ overlay + dừng animation
            root.getChildren().remove(overlay);
            loadingCtrl.stop();
            timer.stop();
            String result = registerTask.getValue();
            switch (result) {
                case "REGISTER_OK" -> {
                    SoundManager.playSoundEffect("clickLoginRegister.mp3");
                    lblMessage.setText("Register successfully!");
                }
                case "EXISTS" -> {
                    SoundManager.playSoundEffect("error.mp3");
                    lblMessage.setText("Account already exists!");
                }
                default -> {
                    SoundManager.playSoundEffect("error.mp3");
                    lblMessage.setText("Connection error or unable to register!");
                }
            }
        });

        registerTask.setOnFailed(ev -> {
            root.getChildren().remove(overlay);
            loadingCtrl.stop();
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Register failed (exception)!");
        });

        new Thread(registerTask, "register-task").start();
    }

    // khi nhấn nút quay lại đăng nhập
    @FXML
    private void openLogin() {
        try {
            SoundManager.playSoundEffect("click.mp3");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/LoginScreen.fxml"));
                        Parent loginRoot = loader.load();
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Error when opening login screen!");
        }
    }
}
