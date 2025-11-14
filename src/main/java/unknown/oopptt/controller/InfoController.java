package unknown.oopptt.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import unknown.oopptt.api.SoundManager;

import java.io.IOException;

public class InfoController {
    @FXML
    private StackPane root;

    @FXML
    public void handleOut() {
        SoundManager.playSoundEffect("click.mp3");
        try {
            // Load màn hình MenuRotate
            Parent menuRoot = FXMLLoader.load(
                    getClass().getResource("/unknown/oopptt/MenuRotate.fxml"));

            // Nếu đang nằm trong một Scene: chỉ cần thay root để "xóa" màn hiện tại
            if (root != null && root.getScene() != null) {
                root.getScene().setRoot(menuRoot);
            } else {
                // Dự phòng: chưa có Scene -> mở Stage mới
                Stage stage = new Stage();
                stage.setScene(new Scene(menuRoot));
                stage.setTitle("Menu");
                stage.show();

                // Đóng cửa sổ hiện tại nếu đang chạy độc lập
                closeWindowIfStandalone();
            }
        } catch (IOException e) {
            e.printStackTrace(); // hoặc log ra logger của bạn
        }
    }

    //Tiện ích: đóng Stage nếu màn này đang chạy trong 1 Stage riêng
    private void closeWindowIfStandalone() {
        if (root != null && root.getScene() != null) {
            Window w = root.getScene().getWindow();
            if (w != null) {
                w.hide();
            }
        }
    }

    @FXML
    public void handleNext() {
        SoundManager.playSoundEffect("click.mp3");
        try {
            Parent info2Root = FXMLLoader.load(
                    getClass().getResource("/unknown/oopptt/Info2.fxml"));

            if (root != null && root.getScene() != null) {
                // Thay thế màn hiện tại bằng Info2
                root.getScene().setRoot(info2Root);
                info2Root.requestFocus();
            } else {
                // Dự phòng: mở Stage mới nếu chưa có Scene
                Stage stage = new Stage();
                stage.setTitle("Info 2");
                stage.setScene(new Scene(info2Root, 960, 540));
                stage.show();

                // Đóng cửa sổ hiện tại nếu đang chạy độc lập
                closeWindowIfStandalone();
            }
        } catch (IOException e) {
            e.printStackTrace(); // hoặc log bằng logger của bạn
        }
    }
}