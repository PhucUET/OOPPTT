package unknown.oopptt.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class MainMenuController {
    @FXML
    private ImageView menubackground;
    @FXML
    public StackPane stack_root;
    @FXML
    private void onBattle(ActionEvent e) {
        switchTo("/unknown/oopptt/BattleScreen.fxml", e);
    }

    @FXML
    private void onAdventure(ActionEvent e) {
        switchTo("/unknown/oopptt/GameScreen.fxml", e); // tạo sau
    }

    @FXML
    private void onExit(ActionEvent e) {
        // an toàn khi đang fullscreen
        Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
        st.close();
    }
    @FXML
    public void initialize() {
        stack_root.setAlignment(Pos.CENTER);
        var url = getClass().getResource("/graphic/Space3.png");
        if (url != null) {
            var img = new javafx.scene.image.Image(url.toExternalForm(), true);
            menubackground.setImage(img);
        }

        // Phủ kín khung: tắt giữ tỉ lệ & bind theo kích thước StackPane
        menubackground.setPreserveRatio(false);
        menubackground.fitWidthProperty().bind(stack_root.widthProperty());
        menubackground.fitHeightProperty().bind(stack_root.heightProperty());

        // (tùy chọn) không tham gia layout để không đẩy các node khác
        menubackground.setManaged(false);
    }

    private void switchTo(String fxml, ActionEvent e) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            // Reuse Scene để giảm giật khi chuyển
            Scene scene = stage.getScene();
            scene.setRoot(root);
            // nếu cần, gọi requestFocus cho node đầu tiên của màn mới
            root.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
