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

    // متغيرات التحكم في الصوت
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

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

            // تطبيق المستويات الحالية على كل المقاطع
            applyVolumes();

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

    // تطبيق المستويات الحجمية على كل المقاطع
    private void applyVolumes() {
        setClipVolume(startSound, musicVolume);
        setClipVolume(gameBackgroundSound, musicVolume);
        setClipVolume(gameStartSound, sfxVolume);
        setClipVolume(shootSound, sfxVolume);
        setClipVolume(hitSound, sfxVolume);
        setClipVolume(winSound, sfxVolume);
        setClipVolume(gameOverSound, sfxVolume);
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();

            // تحويل من 0.0-1.0 إلى dB
            float dB = (float) (Math.log10(volume) * 20.0);
            if (dB < min) dB = min;
            if (dB > max) dB = max;

            gainControl.setValue(dB);
        } catch (Exception e) {
            // بعض المقاطع قد لا تدعم التحكم في الصوت
            System.err.println("Cannot set volume for clip: " + e.getMessage());
        }
    }

    // دوال التحكم في الموسيقى
    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) {
            stopStartSound();
            stopGameBackground();
        } else {
            // إعادة تشغيل الموسيقى إذا كنا في القائمة الرئيسية
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        if (musicVolume < 0) musicVolume = 0;
        if (musicVolume > 1) musicVolume = 1;

        // تطبيق الصوت الجديد على مقاطع الموسيقى
        setClipVolume(startSound, musicVolume);
        setClipVolume(gameBackgroundSound, musicVolume);
    }

    // دوال التحكم في المؤثرات
    public void setSFXEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    public void setSFXVolume(float volume) {
        sfxVolume = volume;
        if (sfxVolume < 0) sfxVolume = 0;
        if (sfxVolume > 1) sfxVolume = 1;

        // تطبيق الصوت الجديد على مقاطع المؤثرات
        setClipVolume(gameStartSound, sfxVolume);
        setClipVolume(shootSound, sfxVolume);
        setClipVolume(hitSound, sfxVolume);
        setClipVolume(winSound, sfxVolume);
        setClipVolume(gameOverSound, sfxVolume);
    }

    // دوال الحصول على الحالة
    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSFXEnabled() { return sfxEnabled; }
    public float getMusicVolume() { return musicVolume; }
    public float getSFXVolume() { return sfxVolume; }

    // دوال التشغيل مع مراعاة الإعدادات
    public void playStartSound() {
        if (musicEnabled) {
            playSound(startSound, true);
        }
    }

    public void playGameStartSound() {
        if (sfxEnabled) {
            playSoundOnce(gameStartSound);
        }
    }

    public void playGameOverSound() {
        stopGameBackground();
        if (sfxEnabled) {
            playSoundOnce(gameOverSound);
        }
    }

    public void playGameBackground() {
        stopStartSound();
        if (musicEnabled) {
            playSound(gameBackgroundSound, true);
        }
    }

    public void playShootSound() {
        if (sfxEnabled) {
            playSoundOnce(shootSound);
        }
    }

    public void playHitSound() {
        if (sfxEnabled) {
            playSoundOnce(hitSound);
        }
    }

    public void playWinSound() {
        stopGameBackground();
        if (sfxEnabled) {
            playSoundOnce(winSound);
        }
    }

    public void stopStartSound() { stopClip(startSound); }

    public void stopGameBackground() {
        stopClip(gameBackgroundSound);
    }

    public void stopAllSounds() {
        stopClip(startSound);
        stopClip(gameBackgroundSound);
        stopClip(gameStartSound);
        stopClip(shootSound);
        stopClip(hitSound);
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
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void playSoundOnce(Clip clip) {
        if (!soundsLoaded || clip == null) return;
        try {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}