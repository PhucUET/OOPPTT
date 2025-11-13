package unknown.oopptt.api;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParallaxBackground extends Pane {

    private final List<ImageView> layers = new ArrayList<>();
    private final List<Double> speeds = new ArrayList<>();
    private List<Image> frames = new ArrayList<>();

    //private String path = new File("src/main/resources/graphic/B1-Pale").toString();

    private Pane root;

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

    private double random(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    public void reset(String folderPath) {

        frames.clear();
        loadFrames(folderPath);
        for (Image image : frames) {
            ImageView imageView = new ImageView(image);
            ImageView imageView2 = new ImageView(image);

            imageView.setPreserveRatio(false);
            imageView2.setPreserveRatio(false);

            imageView.setFitWidth(root.getPrefWidth());
            imageView2.setFitWidth(root.getPrefWidth());

            imageView2.setFitHeight(root.getPrefHeight());
            imageView.setFitHeight(root.getPrefHeight());

            imageView.setTranslateY(0);
            imageView2.setTranslateY(-root.getPrefHeight());
            root.getChildren().add(imageView);
            root.getChildren().add(imageView2);
            double speed = random(-20, 20);
            speeds.add(speed);
            speeds.add(speed);
            layers.add(imageView);
            layers.add(imageView2);
        }
    }

    public ParallaxBackground(double width, double height, String path) {
        root = new Pane();
        root.setPrefSize(width, height);
        root.setMaxSize(width, height);
        root.setMinSize(width, height);
        root.setPickOnBounds(true);
        Rectangle rect = new Rectangle(width, height);
        root.setClip(rect);
        loadFrames(path);
        for (Image image : frames) {
            ImageView imageView = new ImageView(image);
            ImageView imageView2 = new ImageView(image);

            imageView.setPreserveRatio(false);
            imageView2.setPreserveRatio(false);

            imageView.setFitWidth(root.getPrefWidth());
            imageView2.setFitWidth(root.getPrefWidth());

            imageView2.setFitHeight(root.getPrefHeight());
            imageView.setFitHeight(root.getPrefHeight());

            imageView.setTranslateY(0);
            imageView2.setTranslateY(-root.getPrefHeight());
            root.getChildren().add(imageView);
            root.getChildren().add(imageView2);
            double speed = random(-20, 20);
            speeds.add(speed);
            speeds.add(speed);
            layers.add(imageView);
            layers.add(imageView2);
        }
    }


    public Pane getRoot() {
        return root;
    }

    public void update(double deltaTime) {
        double baseSpeed = 60.0;
        ImageView base = layers.get(0);
        for (int i = 2; i < layers.size(); i += 2) {
            ImageView imageView = layers.get(i);
            ImageView imageView2 = layers.get(i + 1);
            double speed = speeds.get(i);
            imageView.setTranslateY(imageView.getTranslateY() + deltaTime * (baseSpeed + speed));
            imageView2.setTranslateY(imageView2.getTranslateY() + deltaTime * (baseSpeed + speed));
            if (imageView.getTranslateY() >= base.getBoundsInParent().getHeight()) {
                imageView.setTranslateY(-root.getPrefHeight());
            }
            if (imageView2.getTranslateY() >= base.getBoundsInParent().getHeight()) {
                imageView2.setTranslateY(-root.getPrefHeight());
            }
        }
    }
}