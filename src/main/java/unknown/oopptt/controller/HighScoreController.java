package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import unknown.oopptt.api.Data;
import unknown.oopptt.api.ParallaxBackground;
import unknown.oopptt.api.RenderFX;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HighScoreController {
    @FXML private StackPane root;
    @FXML private Canvas p1;
    @FXML private Canvas p2;
    @FXML private Canvas p3;
    @FXML private Canvas p4;
    @FXML private Canvas p5;
    @FXML private Canvas p6;

    private String path = new File("src/main/resources/graphic/B1-Pale").toString();
    private ParallaxBackground bg = new ParallaxBackground(2000, 1000, path);
    private Data data = new Data();
    private RenderFX renderFX = new RenderFX(18);
    public void setData(Data data) {
        this.data = data;
    }

    private void render(Canvas canvas, String name) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        renderFX
                .setFillColor(javafx.scene.paint.Color.YELLOW)
                .setOutline(javafx.scene.paint.Color.RED, 3)
                .setShadow(javafx.scene.paint.Color.color(0, 0, 0, 0.7), 3, 3)
                .setScale(1.0); // giữ nguyên size
        renderFX.drawText(gc, name,
                canvas.getWidth()/2,  // x
                20,                        // y (baseline)
                RenderFX.Align.CENTER);
    }
    @FXML
    private void initialize() {
        List<String> topScore =  data.fetchGlobalHighScores();
        for(int i = 0 ; i < 6 ; i ++) {
            if (i  == topScore.size()) {
                break;
            }
            String th = topScore.get(i);
            if (i == 0) {
                render(p1,th);
            }
            if (i == 1)  {
                render(p2,th);
            }
            if (i == 2)  {
                render(p3,th);
            }
            if (i == 3)  {
                render(p4,th);
            }
            if (i == 4)  {
                render(p5,th);
            }
            if (i == 5)  {
                render(p6,th);
            }
        }
        root.getChildren().add(0,bg.getRoot());
        startGameloop();
    }

    public void handleOut() {

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