package unknown.oopptt.api;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.*;

public class ImageCache {
    private static final Map<String, List<Image>> folderCache  = new HashMap<>();
    private static final Map<String, Image> cache  = new HashMap<>();

    public static Image load(String path) {
        return cache.computeIfAbsent(path, p -> {
            try {
                return new Image(new File(p).toURI().toString(), false);
            } catch (Exception e) {
                System.err.println("⚠️ Không thể load ảnh: " + p);
                return null;
            }
        });
    }
    public static List<Image> loadFolder(String folderPath) {
        return folderCache.computeIfAbsent(folderPath, path -> {
            File folder = new File(path);
            File[] files = folder.listFiles(f -> f.getName().matches(".*\\.(png|jpg|jpeg)$"));
            if (files == null) return List.of();

            Arrays.sort(files, Comparator.comparing(File::getName));
            List<Image> list = new ArrayList<>();
            for (File f : files) {
                list.add(load(f.getAbsolutePath()));
            }
            System.out.println("✅ Cached " + list.size() + " frames from " + path);
            return list;
        });
    }
}
