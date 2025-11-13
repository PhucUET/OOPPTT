package unknown.oopptt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unknown.oopptt.api.SoundManager;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/unknown/oopptt/Info.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("met moi qua");
        primaryStage.setFullScreen(true);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();
        SoundManager.playBackgroundMusic("loginMusic.mp3");
    }
}





