package unknown.oopptt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unknown.oopptt.controller.GameScreen_controller;

/**
 * MainApp – Điểm khởi động chính của game Arkanoid JavaFX.
 *
 * Khi chạy:
 *  - Nạp GameScreen.fxml
 *  - Tạo controller GameScreenController
 *  - Hiển thị game toàn màn hình
 *  - Đảm bảo đóng kết nối mạng khi tắt cửa sổ
 */
public class MA extends Application {

    private GameScreen_controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Nạp file FXML và Controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/GameScreen.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Cấu hình cửa sổ game
        primaryStage.setTitle("Arkanoid Multiplayer1 - Doppelherz Edition1");
        primaryStage.setFullScreen(true);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Đảm bảo đóng kết nối mạng khi thoát
        primaryStage.setOnCloseRequest(event -> {
            if (controller != null) {
                controller.onClose();
            }
        });
    }
}
