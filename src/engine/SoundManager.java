package engine;

import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {
    private Clip startSound;
    private Clip gameStartSound;
    private boolean soundsLoaded = false;

    public SoundManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            File startFile = new File("assets/Sounds/start.wav");
            AudioInputStream startStream = AudioSystem.getAudioInputStream(startFile);
            startSound = AudioSystem.getClip();
            startSound.open(startStream);

            File gameStartFile = new File("assets/Sounds/ko.wav");
            AudioInputStream gameStartStream = AudioSystem.getAudioInputStream(gameStartFile);
            gameStartSound = AudioSystem.getClip();
            gameStartSound.open(gameStartStream);

            soundsLoaded = true;
        } catch (Exception e) {
            System.err.println("Sound error: " + e.getMessage());
            soundsLoaded = false;
        }
    }

    public void playStartSound() {
        if (!soundsLoaded) return;
        try {
            if (startSound != null) {
                if (startSound.isRunning()) startSound.stop();
                startSound.setFramePosition(0);
                startSound.start();
            }
        } catch (Exception e) {
            System.err.println("Start sound error: " + e.getMessage());
        }
    }

    public void playGameStartSound() {
        if (!soundsLoaded) return;
        try {
            if (gameStartSound != null) {
                if (gameStartSound.isRunning()) gameStartSound.stop();
                gameStartSound.setFramePosition(0);
                gameStartSound.start();
            }
        } catch (Exception e) {
            System.err.println("Game sound error: " + e.getMessage());
        }
    }

    // دالة جديدة لتوقف صوت البداية فقط
    public void stopStartSound() {
        if (startSound != null && startSound.isRunning()) {
            startSound.stop();
        }
    }

    public void stopAllSounds() {
        if (startSound != null && startSound.isRunning()) startSound.stop();
        if (gameStartSound != null && gameStartSound.isRunning()) gameStartSound.stop();
    }
}