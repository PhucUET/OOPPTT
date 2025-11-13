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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import unknown.oopptt.api.Data;
import unknown.oopptt.api.ParallaxBackground;
import unknown.oopptt.api.SoundManager;

import java.io.File;

public class LoginScreenController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
    @FXML private StackPane root;
    private String path = new File("src/main/resources/graphic/B1-Pale").toString();

    private ParallaxBackground bg = new ParallaxBackground(1900, 1200, path);

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
                javafx.concurrent.Task<Pair<Parent, MenuRotateController>> preloadNext =
                        new javafx.concurrent.Task<>() {
                            @Override
                            protected Pair<Parent, MenuRotateController> call() throws Exception {
                                FXMLLoader nextFx = new FXMLLoader(
                                        getClass().getResource("/unknown/oopptt/MenuRotate.fxml")
                                );

                                Parent root = nextFx.load();                             // 1) load trước
                                MenuRotateController controller = nextFx.getController(); // 2) rồi mới lấy controller

                                return new Pair<>(root, controller);                      // 3) trả về cả root + controller
                            }
                        };

                preloadNext.setOnSucceeded(done -> {
                    //gỡ overlay + chuyển màn
                    root.getChildren().remove(overlay);
                    loadingCtrl.stop();
                    try {
                        Stage stage = (Stage) root.getScene().getWindow();
                        Parent newScene = (Parent) (preloadNext.getValue().getKey());
                        MenuRotateController controller = (MenuRotateController) preloadNext.getValue().getValue();
                        stage.getScene().setRoot(newScene);
                        controller.setData(data);
                        stage.setFullScreen(true);
                        //timer.stop();
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
            stage.setTitle("Register");

            stage.getScene().setRoot(rootNew);

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
        root.getChildren().add(0, bg.getRoot());
        bg.getRoot().prefWidthProperty().bind(root.widthProperty());
        bg.getRoot().prefHeightProperty().bind(root.heightProperty());
        startGameloop();
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
}