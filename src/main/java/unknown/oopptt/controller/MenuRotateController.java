package unknown.oopptt.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import unknown.oopptt.api.Data;
import unknown.oopptt.api.ParallaxBackground;
import unknown.oopptt.api.SoundManager;

public class MenuRotateController {

    @FXML private StackPane root;
    @FXML private ImageView background;

    @FXML private Button btnLeft, btnRight, btnMute, btnEsc;

    @FXML private ImageView imgLeft, imgCenter, imgRight;
    private ParallaxBackground bg = new ParallaxBackground(1500, 1000);

    private enum Mode { BATTLE, ADVENTURE, HELP }
    // Thứ tự hiển thị: [LEFT, CENTER, RIGHT]
    private final List<Mode> order = Arrays.asList(Mode.BATTLE, Mode.ADVENTURE, Mode.HELP);

    // Ảnh demo (sẽ fallback sang background10.jpg nếu thiếu)
    private Image imgBattle, imgAdventure, imgHelp, imgFallback,  imgBackGournd;

    private Data data = new Data();

    public void setData(Data data) {
        this.data = data;
    }

    @FXML
    private void initialize() {
        // Nền full màn
        //stack_root.setAlignment(Pos.CENTER);

//        if (root != null) {
//            Platform.runLater(() -> {
//                bg = new ParallaxBackground(root.getBoundsInLocal().getWidth(), root.getBoundsInLocal().getHeight());
//                System.out.println(root.getBoundsInLocal().getWidth() + " " + root.getBoundsInLocal().getHeight());
//                root.getChildren().add(0,bg.getRoot());
//                startGameloop();
//            });
//        }
        root.getChildren().add(0, bg.getRoot());
        bg.getRoot().prefWidthProperty().bind(root.widthProperty());
        bg.getRoot().prefHeightProperty().bind(root.heightProperty());

        startGameloop();
        // Nạp ảnh từ classpath
        imgBackGournd = firstAvailable("/graphic/battle.png", "/graphic/Space3.png");
        imgFallback  = load("/graphic/background10.jpg");
        imgBattle    = firstAvailable("/graphic/battle.png", "/graphic/background10.png");
        imgAdventure = firstAvailable("/graphic/adventure.png", "/graphic/background10.jpg");
        imgHelp      = firstAvailable("/graphic/help.png", "/graphic/ball_orange.png");

        render();

        // Phím tắt
        Platform.runLater(() -> {
            if (root.getScene() == null) return;
            root.getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.LEFT) onRotateLeft();
                else if (e.getCode() == KeyCode.RIGHT) onRotateRight();
                else if (e.getCode() == KeyCode.ESCAPE) onEscape();
                else if (e.getCode() == KeyCode.M) onToggleMute();
                else if (e.getCode() == KeyCode.ENTER ) onSelectCenter();
            });
        });
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

    /* ================== Actions ================== */

    @FXML
    public void onRotateLeft() {
        SoundManager.playSoundEffect("click.mp3");
        // Left -> Center (xoay trái)
        Collections.rotate(order, -1);
        render();
    }

    @FXML
    public void onRotateRight() {
        SoundManager.playSoundEffect("click.mp3");
        // Right -> Center (xoay phải)
        Collections.rotate(order, 1);
        render();
    }

    boolean sound = true;
    @FXML
    public void onToggleMute() {
        SoundManager.playSoundEffect("click.mp3");
        if(sound == true) {
            SoundManager.stopBackgroundMusic();
            sound = false;
        } else {
            SoundManager.playBackgroundMusic("menuBgrMusic.mp3");
            sound = true;
        }
        btnMute.setText(btnMute.getText().equals("🔊") ? "🔇" : "🔊");
        // TODO: nối với AudioManager thực tế
    }

    @FXML
    public void onEscape() {
        SoundManager.playSoundEffect("click.mp3");
        // TODO: quay lại Title hoặc thoát hẳn
        Platform.exit();
    }

    @FXML
    public void onSelectCenter(ActionEvent e) {
        handleSelect(e);             // gọi khi click nút (có event)
    }
    public void onSelectCenter() {    // gọi khi nhấn Enter/Space (không event)
        handleSelect(null);
    }
    private void handleSelect(ActionEvent e) {
        SoundManager.playSoundEffect("click.mp3");
        Mode center = order.get(1);
        switch (center) {
            case ADVENTURE -> startAdventure(e);
            case BATTLE    -> goBattle(e);
            case HELP      -> openHelp();
        }
    }

    /* ================== Navigation stubs ================== */

    private void startAdventure(ActionEvent e) {
        SoundManager.playSoundEffect("click.mp3");
        switchTo("/unknown/oopptt/GameScreen.fxml", e);
        // TODO: load AdventureScreen.fxml và setRoot
    }

    private void goBattle(ActionEvent e) {
        SoundManager.playSoundEffect("click.mp3");
        switchTo("/unknown/oopptt/BattleScreen.fxml", e);
        // TODO: load BattleScreen.fxml và setRoot
    }

    private void openHelp() {
        SoundManager.playSoundEffect("click.mp3");
        System.out.println("Open Help");
        // TODO: load Help.fxml và setRoot
    }

    /* ================== Render ================== */

    private void render() {
        // order: [LEFT, CENTER, RIGHT]
        setImage(imgLeft,  order.get(0));
        setImage(imgCenter,order.get(1));
        setImage(imgRight, order.get(2));
    }

    private void setImage(ImageView view, Mode mode) {
        if (view == null) return;
        Image img = switch (mode) {
            case BATTLE    -> imgBattle != null ? imgBattle : imgFallback;
            case ADVENTURE -> imgAdventure != null ? imgAdventure : imgFallback;
            case HELP      -> imgHelp != null ? imgHelp : imgFallback;
        };
        view.setImage(img);
    }

    /* ================== Utils ================== */

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm(), true);
    }

    private Image firstAvailable(String primary, String fallback) {
        Image a = load(primary);
        return a != null ? a : load(fallback);
    }
    private void switchTo(String fxml, ActionEvent e) {
        try {
            FXMLLoader nextFx = new FXMLLoader(getClass().getResource(fxml));
            Parent root = nextFx.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Game_Screen_Controller controller = nextFx.getController();
             stage.getScene().setRoot(root);
            controller.setData(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
