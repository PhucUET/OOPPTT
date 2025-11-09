package unknown.oopptt.api;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundManager {
    private static MediaPlayer backgroundMusic;

    //phat nhac nen
    public static void playBackgroundMusic(String fileName) {
        try {
            stopBackgroundMusic(); //dung lai neu nhac dang phat
            Media media = new Media(new File("src/main/resources/sound/" + fileName).toURI().toString());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); //loop
            backgroundMusic.setVolume(0.3); //am luong 30%
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Khong the phat nhac nen: " + e.getMessage());
        }
    }

    //dung nhac nen
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    //hieu ung am thanh
    public static void playSoundEffect(String fileName) {
        try {
            AudioClip audioClip = new AudioClip(new File("src/main/resources/sound/" + fileName).toURI().toString());
            audioClip.play();
        } catch (Exception e) {
            System.err.println("Khong the phat hieu ung am thanh: " + e.getMessage());
        }
    }
}
