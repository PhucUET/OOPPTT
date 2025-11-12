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
import unknown.oopptt.api.SoundManager;

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

    private  Data data = new Data();

    private void goToMenuRotate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/MenuRotate.fxml"));
            Parent rotateRoot = loader.load();

            //lay stage hien tai
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            //tao scene moi va gan truc tiep
            Scene scene = new Scene(rotateRoot);
            stage.setFullScreen(true);
            stage.setScene(scene);
            stage.show();

            SoundManager.playBackgroundMusic("menuBgrMusic.mp3");

        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Error when open MenuRotate!");
        }
    }

    //khi nhan nut dang nhap
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        SoundManager.playSoundEffect("click.mp3");

        if (username.isEmpty() || password.isEmpty()) {
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Please enter full username and password!");
            return;
        }

        //goi ham login() tu data de kiem tra voi ggsheets
        String result = data.login(username, password);

        switch (result) {
            case "LOGIN_OK" -> {
                lblMessage.setText("Login successful!");
                SoundManager.playSoundEffect("clickLoginRegister.mp3");
                goToMenuRotate();
            }
            case "INVALID" -> {
                SoundManager.playSoundEffect("error.mp3");
                lblMessage.setText("Wrong username or password!");
            }
            default -> {
                SoundManager.playSoundEffect("error.mp3");
                lblMessage.setText("Error when login!");
            }
        }
    }

    //khi nhan nut dang ky tai khoan moi
    @FXML
    private void openRegister() {
        try {
            //tai fxml moi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/RegisterScreen.fxml"));
            Parent rootNew = loader.load();

            //lay stage hien tai
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            //tao scene moi va gan thang
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
        System.out.println(root.sceneToLocal(root.getBoundsInParent()));
        System.out.println(root.getBoundsInParent().getWidth() + " " +  root.getBoundsInParent().getHeight());
        System.out.println(background.getBoundsInParent().getMinX() + " " +  background.getBoundsInParent().getMinY());
    }
}
