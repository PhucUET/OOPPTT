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

        SoundManager.playSoundEffect("click.mp3");

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

        String result = data.register(username, password);

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
    }

    //khi nhan nut quay tro lai dang nhap
    @FXML
    private void openLogin() {
        try {
            SoundManager.playSoundEffect("click.mp3");
            //tai file LoginScreen.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/LoginScreen.fxml"));
            Parent loginRoot = loader.load();

            //lay stage hien tai tu nut hoac textfield bat ki
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            //tao scene moi va gan thang
            Scene scene = new Scene(loginRoot);
            stage.setFullScreen(false);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            SoundManager.playSoundEffect("error.mp3");
            lblMessage.setText("Error when opening login screen!");
        }
    }

}
