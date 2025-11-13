package unknown.oopptt.api.render;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestRender extends Application {
    private Canvas canvas;
    private RenderFX render;
    private long startNanos;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(900, 520);

        // --- Dùng RenderFX mặc định (tự nạp Keratine) ---
        render = new RenderFX()
                .setFillColor(Color.web("#FFEE58"))
                .setOutline(Color.web("#1A1A1A"), 3)
                .setShadow(Color.rgb(0, 0, 0, 0.65), 3, 3);

        Group root = new Group(canvas);
        Scene scene = new Scene(root, 900, 520, Color.web("#121212"));
        stage.setTitle("RenderFX Demo — Font Keratine");
        stage.setScene(scene);
        stage.show();

        startNanos = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawFrame();
            }
        }.start();
    }

    private void drawFrame() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // Clear nền
        g.setFill(Color.web("#121212"));
        g.fillRect(0, 0, w, h);

        // Tiêu đề giữa màn hình — font Keratine
        render.setScale(1.0);
        render.drawText(g, "ARKANOID", w / 2.0, h / 2.0, RenderFX.Align.CENTER);

        // Dòng phụ — cỡ nhỏ hơn nhưng vẫn Keratine
        render.useDefaultFont(20).setScale(1.0).setFillColor(Color.web("#E0E0E0"));
        render.drawText(g, "OOPPTT", w / 2.0, h / 2.0 + 36, RenderFX.Align.CENTER);

        // Góc trái trên
        render.setScale(0.75).setFillColor(Color.WHITE).setOutline(Color.rgb(0,0,0,0.55), 2);
        render.drawTextTopLeft(g, "Deadline: 23:59 13/11/2025", 20, 20, RenderFX.Align.LEFT);

        // Đồng hồ thời gian
        double t = (System.nanoTime() - startNanos) / 1e9;
        render.setScale(0.7).setFillColor(Color.web("#90CAF9")).clearOutline();
        render.drawTextTopLeft(g, String.format("Time: %.1fs", t), 20, h - 60, RenderFX.Align.LEFT);

        // Chú thích đáy
        render.setScale(0.65).setFillColor(Color.web("#C8C8C8"));
        render.drawText(g, "Rat la met", w / 2.0, h - 30, RenderFX.Align.CENTER);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
