package unknown.oopptt.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

public class PauseScreenController {

    @FXML private AnchorPane root;
    @FXML private ImageView background;

    @FXML
    public void initialize() {
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }

    @FXML
    private void handleContinue() {

    }

    @FXML
    private void openInfo() {

    }

    @FXML
    private void openHome() {

    }

    @FXML
    private void openNewGame() {

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
