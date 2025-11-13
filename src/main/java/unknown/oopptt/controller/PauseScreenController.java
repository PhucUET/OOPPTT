package unknown.oopptt.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class PauseScreenController {

    @FXML private StackPane root;

//    @FXML
//    public void initialize() {
//        background.setPreserveRatio(false);
//        background.fitWidthProperty().bind(root.widthProperty());
//        background.fitHeightProperty().bind(root.heightProperty());
//    }

    @FXML
    private void handleContinue() {
        if (root == null || root.getScene() == null) { closeWindowIfStandalone(); return; }
        Scene scene = root.getScene();
        Object prev = scene.getProperties().get("previousRoot");
        if (prev instanceof Parent previousRoot) {
            scene.setRoot(previousRoot);
            previousRoot.requestFocus();
            Object gc = scene.getProperties().get("gameController");
            if (gc instanceof unknown.oopptt.controller.Game_Screen_Controller gsc) {
                gsc.resumeGame();
            }
            return;
        }
        closeWindowIfStandalone();
    }


    @FXML
    private void openHome() {
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

    @FXML
    private void openNewGame() {
        try {
            // Load màn hình game mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/GameScreen.fxml"));
            Parent gameRoot = loader.load();

            // Nếu Pause đang nằm trong một Scene: thay root để "xóa" màn hiện tại
            if (root != null && root.getScene() != null) {
                root.getScene().setRoot(gameRoot);
                gameRoot.requestFocus(); // đảm bảo nhận input ngay
            } else {
                // Dự phòng: chưa có Scene -> mở Stage mới
                Stage stage = new Stage();
                stage.setTitle("Game");
                stage.setScene(new Scene(gameRoot, 960, 540));
                stage.show();

                // Đóng cửa sổ hiện tại nếu đang chạy độc lập
                closeWindowIfStandalone();
            }
        } catch (IOException e) {
            e.printStackTrace(); // hoặc log bằng logger của bạn
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
}
