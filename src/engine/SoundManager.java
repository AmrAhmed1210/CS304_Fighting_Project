package engine;

import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {
    private Clip startSound;
    private Clip gameStartSound;
    private Clip gameBackgroundSound;
    private Clip shootSound;
    private Clip hitSound;
    private Clip winSound;
    private Clip gameOverSound;
    private boolean soundsLoaded = false;

    public SoundManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            startSound = loadClip("assets/Sounds/start.wav");
            gameStartSound = loadClip("assets/Sounds/startGame.wav");
            gameBackgroundSound = loadClip("assets/Sounds/background.wav");
            shootSound = loadClip("assets/Sounds/player1Shoots.wav");
            hitSound = loadClip("assets/Sounds/hit.wav");
            winSound = loadClip("assets/Sounds/win.wav");
            gameOverSound = loadClip("assets/Sounds/gameover.wav");

            soundsLoaded = true;
        } catch (Exception e) {
            System.err.println("Sound error: " + e.getMessage());
            soundsLoaded = false;
        }
    }

    private Clip loadClip(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) return null;
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(stream);
        return clip;
    }


    public void playStartSound() { playSound(startSound, true); }
    public void playGameStartSound() { playSound(gameStartSound, false); }
    public void playGameOverSound() {
        stopGameBackground();
        playSoundOnce(gameOverSound);
    }
    public void playGameBackground() {
        stopStartSound();
        playSound(gameBackgroundSound, true);
    }

    public void playShootSound() { playSoundOnce(shootSound); }

    public void playHitSound() { playSoundOnce(hitSound); }
    public void playWinSound() {
        stopGameBackground();
        playSoundOnce(winSound);
    }


    public void stopStartSound() { stopClip(startSound); }
    public void stopGameBackground() {
        if (gameBackgroundSound != null && gameBackgroundSound.isRunning()) {
            gameBackgroundSound.stop();
        }
    }

    public void stopAllSounds() {
        stopClip(startSound);
        stopClip(gameBackgroundSound);
        stopClip(gameStartSound);
        stopClip(winSound);
        stopClip(gameOverSound);
    }

    private void stopClip(Clip c) {
        if (c != null && c.isRunning()) c.stop();
    }

    private void playSound(Clip clip, boolean loop) {
        if (!soundsLoaded || clip == null) return;
        try {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            else clip.start();
        } catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
    }

    private void playSoundOnce(Clip clip) {
        if (!soundsLoaded || clip == null) return;
        try {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
    }
}