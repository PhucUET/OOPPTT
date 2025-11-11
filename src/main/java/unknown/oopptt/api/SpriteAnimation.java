package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SpriteAnimation {

    private List<Image> frames = new ArrayList<>();
    private final ImageView view;
    private double acc = 0;
    private double frameDuration;
    private int currentFrame = 0;

    public SpriteAnimation(String folderPath, double fps) {
        this.view = new ImageView();
        loadFrames(folderPath);
        setFps(fps);

        if (!frames.isEmpty()) {
            view.setImage(frames.get(0));
        }
    }

    public void changeFrames(String folderPath) {
        loadFrames(folderPath);
        if (!frames.isEmpty()) {
            view.setImage(frames.get(0));
        }
    }


    private void loadFrames(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println(" Không tìm thấy thư mục: " + folderPath);
            return;
        }

        File[] imageFiles = folder.listFiles(f ->
                f.isFile() && (f.getName().endsWith(".png") || f.getName().endsWith(".jpg"))
        );

        if (imageFiles != null) {
            frames = ImageCache.loadFolder(folderPath);
        }

        System.out.println("✅ Loaded " + frames.size() + " frames from " + folderPath);
    }


    private void setFps(double fps) {
        frameDuration = 1.0 / fps;
    }

    public void update(double dt) {
        if (frames.isEmpty()) return;
        acc += dt;
        while (acc >= frameDuration) {
            acc -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.size();
            view.setImage(frames.get(currentFrame));
        }
    }
    public void updateWhenHit() {
        if (frames.isEmpty()) return;
        currentFrame = (currentFrame + 1) % frames.size();
        view.setImage(frames.get(currentFrame));
    }

    public void setFpsDynamic(double fps) {
        frameDuration = 1.0 / fps;
    }

    public void setDisplaySize(double width, double height) {
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
    }

    public ImageView getView() {
        return view;
    }

    public int getFrameCount() {
        return frames.size();
    }
}
