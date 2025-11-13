package unknown.oopptt.api;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RenderFX {
    public enum Align {
        LEFT, CENTER, RIGHT
    }

    //font mặc định là keratine
    private static final String DEFAULT_FONT_PATH = "/graphic/fonts/keratine.ttf";
    private static final String DEFAULT_FAMILY;

    static {
        Font loaded = Fonts.loadFromResource(DEFAULT_FONT_PATH, 36);
        if (loaded == null) {
            throw new IllegalStateException("Không tìm thấy " + DEFAULT_FONT_PATH + ". Hãy đặt keratine.ttf vào src/main/resources/fonts/"
            );
        }
        DEFAULT_FAMILY = loaded.getFamily();
        System.out.println("Font Keratine loaded: " + DEFAULT_FAMILY);
    }

    private Font font;
    private Color fillColor = Color.WHITE; //mặc định màu chữ là trắng
    private Color outlineColor = null; //màu viền chữ: k có
    private double outlineWidth = 2.0; //độ dày viền chữ
    private Color shadowColor = null; //màu bóng đổ của chữ
    private double shadowDx = 2, shadowDy = 2; //độ dịch chuyển bóng theo trục xy
    private double scale = 1.0; //hệ số phóng to/thu nhỏ chữ (nhân với cỡ chữ của font)

    public static final class Fonts {
        private Fonts() {

        }

        public static Font loadFromResource(String resourcePath, double size) {
            try (InputStream is = RenderFX.class.getResourceAsStream(resourcePath)) {
                if (is == null) return null;
                return Font.loadFont(is, size);
            } catch (IOException e) {
                return null;
            }
        }

        public static Font loadFromFile(Path path, double size) {
            try (InputStream is = Files.newInputStream(path)) {
                return Font.loadFont(is, size);
            } catch (IOException e) {
                return null;
            }
        }

        public static java.util.List<String> families() {
            return Font.getFamilies();
        }
    }

    //constructor
    public RenderFX() {
        this(Font.font(DEFAULT_FAMILY, 36)); // mặc định 36pt
    }

    public RenderFX(double sizePt) {
        this(Font.font(DEFAULT_FAMILY, sizePt));
    }

    public RenderFX(Font font) {
        setFont(font);
    }

    //cấu hình
    public RenderFX setFont(Font font) {
        if (font == null) throw new IllegalArgumentException("Font không được null");
        this.font = font;
        return this;
    }

    //đặt font về keratine mặc định với kích thước mới
    public RenderFX useDefaultFont(double size) {
        this.font = Font.font(DEFAULT_FAMILY, size);
        return this;
    }

    public RenderFX setFillColor(Color c) {
        this.fillColor = c;
        return this;
    }

    public RenderFX setOutline(Color c, double widthPx) {
        this.outlineColor = c;
        this.outlineWidth = Math.max(0.5, widthPx);
        return this;
    }

    public RenderFX clearOutline() {
        this.outlineColor = null;
        return this;
    }

    public RenderFX setShadow(Color c, double dx, double dy) {
        this.shadowColor = c;
        this.shadowDx = dx;
        this.shadowDy = dy;
        return this;
    }

    public RenderFX clearShadow() {
        this.shadowColor = null;
        return this;
    }

    public RenderFX setScale(double scale) {
        this.scale = Math.max(0.1, scale);
        return this;
    }

    public Font getScaledFont() {
        return Font.font(font.getFamily(), font.getSize() * scale);
    }

    //api chính
    //vẽ chữ tại toạ độ (x, y) theo baseline của font
    public void drawText(GraphicsContext gc, String text, double x, double y, Align align) {
        if (text == null || text.isEmpty()) return;

        Font useFont = getScaledFont();
        gc.setFont(useFont);

        double drawX = alignedX(text, gc, useFont, x, align);

        if (shadowColor != null) {
            gc.setFill(shadowColor);
            gc.fillText(text, drawX + shadowDx, y + shadowDy);
        }

        if (outlineColor != null && outlineWidth > 0) {
            gc.setStroke(outlineColor);
            gc.setLineWidth(outlineWidth);
            gc.strokeText(text, drawX, y);
        }

        gc.setFill(fillColor);
        gc.fillText(text, drawX, y);
    }

    //vẽ chữ theo toạ độ top-left (góc trái trên)
    public void drawTextTopLeft(GraphicsContext gc, String text, double left, double top, Align align) {
        if (text == null || text.isEmpty()) return;
        Font useFont = getScaledFont();
        gc.setFont(useFont);
        Text measurer = makeText(text, useFont);
        double baseline = measurer.getBaselineOffset();
        double drawX = alignedX(text, gc, useFont, left, align);
        drawText(gc, text, drawX, top + baseline, Align.LEFT);
    }

    //helper
    private double alignedX(String text, GraphicsContext gc, Font f, double x, Align align) {
        Text measurer = makeText(text, f);
        Bounds b = measurer.getLayoutBounds();
        return switch (align) {
            case LEFT -> x;
            case CENTER -> x - b.getWidth() / 2.0;
            case RIGHT -> x - b.getWidth();
        };
    }

    private static Text makeText(String s, Font f) {
        Text t = new Text(s);
        t.setFont(f);
        return t;
    }
}