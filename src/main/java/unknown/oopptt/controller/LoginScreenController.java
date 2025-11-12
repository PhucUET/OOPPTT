package unknown.oopptt.controller;

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
import unknown.oopptt.api.SoundManager;

public class LoginScreenController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
    @FXML private ImageView background;
    @FXML private AnchorPane root;

    private  Data data = new Data();

    @FXML
    private void handleLogin() {
        final String username = txtUsername.getText().trim();
        final String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Please enter full username and password!");
            return;
        }

        SoundManager.playSoundEffect("click.mp3");

        final Parent overlay;
        final LoadingController loadingCtrl;
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/unknown/oopptt/Loading.fxml"));
            overlay = fx.load();
            loadingCtrl = fx.getController();

            overlay.setPickOnBounds(true); // chặn click xuyên
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

        javafx.concurrent.Task<String> loginTask = new javafx.concurrent.Task<>() {
            @Override protected String call() {
                //gọi Google Sheets
                return data.login(username, password);
            }
        };

        loginTask.setOnSucceeded(ev -> {
            String result = loginTask.getValue();
            if ("LOGIN_OK".equals(result)) {
                SoundManager.playSoundEffect("clickLoginRegister.mp3");

                //preload màn kế tiếp ở background rồi mới setScene (để chuyển mượt)
                javafx.concurrent.Task<Parent> preloadNext = new javafx.concurrent.Task<>() {
                    @Override protected Parent call() throws Exception {
                        FXMLLoader nextFx = new FXMLLoader(getClass().getResource("/unknown/oopptt/MenuRotate.fxml"));
                        return nextFx.load();
                    }
                };
                preloadNext.setOnSucceeded(done -> {
                    //gỡ overlay + chuyển màn
                    root.getChildren().remove(overlay);
                    loadingCtrl.stop();
                    try {
                        Stage stage = (Stage) root.getScene().getWindow();
                        stage.setScene(new Scene(preloadNext.getValue()));
                        stage.setFullScreen(true);
                        SoundManager.playBackgroundMusic("menuBgrMusic.mp3");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        lblMessage.setText("Error switching to MenuRotate!");
                    }
                });
                preloadNext.setOnFailed(err -> {
                    root.getChildren().remove(overlay);
                    loadingCtrl.stop();
                    lblMessage.setText("Failed to load next screen!");
                });
                new Thread(preloadNext, "preload-next").start();

            } else if ("INVALID".equals(result)) {
                //sai tài khoản → gỡ overlay, báo lỗi
                root.getChildren().remove(overlay);
                loadingCtrl.stop();
                SoundManager.playSoundEffect("error.mp3");
                lblMessage.setText("Wrong username or password!");
            } else {
                root.getChildren().remove(overlay);
                loadingCtrl.stop();
                SoundManager.playSoundEffect("error.mp3");
                lblMessage.setText("Error when login!");
            }
        });

        loginTask.setOnFailed(ev -> {
            root.getChildren().remove(overlay);
            loadingCtrl.stop();
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Login failed (exception)!");
        });

        new Thread(loginTask, "login-task").start();
    }

    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/RegisterScreen.fxml"));
            Parent rootNew = loader.load();
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene newScene = new Scene(rootNew);
            stage.setFullScreen(false);
            stage.setScene(newScene);
            SoundManager.playSoundEffect("click.mp3");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Error when open Register!");
        }
    }

    @FXML
    public void initialize() {
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }
}