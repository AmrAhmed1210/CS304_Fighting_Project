package engine.screens;

import engine.Button;
import engine.Game;
import engine.TextureLoader;
import engine.buttons.BackButton;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class SoundSettingsScreen {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int selectedIndex = 0;
    private TextRenderer titleRenderer;
    private TextRenderer optionRenderer;
    private TextRenderer valueRenderer;

    private Texture settingsBg;

    public SoundSettingsScreen() {
        titleRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 50), true, true);
        optionRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 30), true, true);
        valueRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 28), true, true);

        float centerX = (1280 - 400) / 2;
        float startY = 180;
        float gap = 70;
        float btnWidth = 400;
        float btnHeight = 45;

        buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "MUSIC") {
            @Override
            public void onClick(Game game) {
                if (game.soundManager != null) {
                    boolean current = game.soundManager.isMusicEnabled();
                    game.soundManager.setMusicEnabled(!current);

                    // إذا فعلنا الموسيقى وكاننا في القائمة الرئيسية، نعيد تشغيلها
                    if (!current && game.gameState == Game.State.MENU) {
                        game.soundManager.playStartSound();
                    }
                }
            }
        });

        buttons.add(new Button(centerX, startY + gap, btnWidth, btnHeight, "MUSIC VOLUME") {
            @Override
            public void onClick(Game game) {
                if (game.soundManager != null) {
                    float currentVolume = game.soundManager.getMusicVolume();
                    float newVolume = currentVolume - 0.2f;
                    if (newVolume < 0) newVolume = 1.0f;
                    game.soundManager.setMusicVolume(newVolume);
                }
            }
        });

        buttons.add(new Button(centerX, startY + gap * 2, btnWidth, btnHeight, "SFX") {
            @Override
            public void onClick(Game game) {
                if (game.soundManager != null) {
                    boolean current = game.soundManager.isSFXEnabled();
                    game.soundManager.setSFXEnabled(!current);
                }
            }
        });

        buttons.add(new Button(centerX, startY + gap * 3, btnWidth, btnHeight, "SFX VOLUME") {
            @Override
            public void onClick(Game game) {
                if (game.soundManager != null) {
                    float currentVolume = game.soundManager.getSFXVolume();
                    float newVolume = currentVolume - 0.2f;
                    if (newVolume < 0) newVolume = 1.0f;
                    game.soundManager.setSFXVolume(newVolume);
                }
            }
        });

        buttons.add(new Button(centerX, startY + gap * 4, btnWidth, btnHeight, "TEST SOUND") {
            @Override
            public void onClick(Game game) {
                if (game.soundManager != null) {
                    game.soundManager.playShootSound();
                }
            }
        });

        buttons.add(new BackButton(centerX, startY + gap * 5, btnWidth, btnHeight));
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY, Game game) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (settingsBg == null) {
            settingsBg = loader.load("background/bg.png");
        }

        if (settingsBg != null) {
            gl.glColor3f(1, 1, 1);
            loader.draw(gl, settingsBg, 0, 0, 1280, 720);
        } else {
            gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        }

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).isHovered = buttons.get(i).isInside(mouseX, mouseY);
            if (buttons.get(i).isHovered) {
                selectedIndex = i;
            }
        }

        drawTitle(gl);
        drawOptions(gl);
        drawValues(gl, game);
        drawSeparators(gl);
        drawCursor(gl);
    }

    private void drawTitle(GL gl) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(1f, 1f, 0.8f, 1f);
        String title = "SOUND SETTINGS";
        int titleWidth = (int) titleRenderer.getBounds(title).getWidth();
        int titleX = (1280 - titleWidth) / 2;
        int titleY = 720 - 80;
        titleRenderer.draw(title, titleX, titleY);
        titleRenderer.endRendering();
    }

    private void drawOptions(GL gl) {
        optionRenderer.beginRendering(1280, 720);

        for (Button button : buttons) {
            int textWidth = (int) optionRenderer.getBounds(button.label).getWidth();
            int textX = (int) (button.x + (button.width - textWidth) / 2);
            int textY = (int) (720 - (button.y + button.height / 2) - 10);

            if (button.isHovered) {
                optionRenderer.setColor(1f, 1f, 0f, 1f);
            } else {
                optionRenderer.setColor(1f, 1f, 1f, 1f);
            }

            optionRenderer.draw(button.label, textX, textY);
        }

        optionRenderer.endRendering();
    }

    private void drawValues(GL gl, Game game) {
        valueRenderer.beginRendering(1280, 720);

        float startY = 180;
        float gap = 70;
        float centerX = (1280 - 400) / 2;
        float valueX = centerX + 450;

        // قراءة القيم الحالية من SoundManager
        boolean musicEnabled = true;
        float musicVolume = 1.0f;
        boolean sfxEnabled = true;
        float sfxVolume = 1.0f;

        if (game.soundManager != null) {
            musicEnabled = game.soundManager.isMusicEnabled();
            musicVolume = game.soundManager.getMusicVolume();
            sfxEnabled = game.soundManager.isSFXEnabled();
            sfxVolume = game.soundManager.getSFXVolume();
        }

        drawValue("MUSIC", musicEnabled ? "ON" : "OFF", startY, valueX, musicEnabled ? Color.GREEN : Color.RED);
        drawVolumeBar(musicVolume, startY + gap, valueX);
        drawValue("SFX", sfxEnabled ? "ON" : "OFF", startY + gap * 2, valueX, sfxEnabled ? Color.GREEN : Color.RED);
        drawVolumeBar(sfxVolume, startY + gap * 3, valueX);
        drawValue("TEST", "CLICK TO TEST", startY + gap * 4, valueX, Color.CYAN);

        valueRenderer.endRendering();
    }

    private void drawValue(String option, String value, float y, float x, Color color) {
        valueRenderer.setColor(color);
        int textY = (int) (720 - (y + 45 / 2) - 10);
        valueRenderer.draw(value, (int)x, textY);
    }

    private void drawVolumeBar(float volume, float y, float x) {
        String volText = String.format("%d%%", (int)(volume * 100));
        Color color;
        if (volume > 0.7f) color = Color.GREEN;
        else if (volume > 0.3f) color = Color.YELLOW;
        else color = Color.RED;

        valueRenderer.setColor(color);
        int textY = (int) (720 - (y + 45 / 2) - 10);
        valueRenderer.draw(volText, (int)x, textY);
    }

    private void drawSeparators(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor4f(0.3f, 0.3f, 0.3f, 0.5f);
        gl.glLineWidth(2);

        float startY = 180;
        float gap = 70;
        float centerX = (1280 - 400) / 2;
        float lineY;

        for (int i = 0; i < 5; i++) {
            lineY = startY + gap * i + 50;
            gl.glBegin(GL.GL_LINES);
            gl.glVertex2f(centerX - 50, lineY);
            gl.glVertex2f(centerX + 450, lineY);
            gl.glEnd();
        }

        gl.glLineWidth(1);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    private void drawCursor(GL gl) {
        if (buttons.isEmpty()) return;

        Button selected = buttons.get(selectedIndex);

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1f, 1f, 0f);

        float cursorSize = 15;
        float cursorX = selected.x - 40;
        float cursorY = selected.y + (selected.height / 2);

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(cursorX + cursorSize, cursorY);
        gl.glVertex2f(cursorX, cursorY - cursorSize);
        gl.glVertex2f(cursorX, cursorY + cursorSize);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    public void handleInput(int keyCode, Game game) {
        if (keyCode == java.awt.event.KeyEvent.VK_UP) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = buttons.size() - 1;
            }
        }
        if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
            selectedIndex++;
            if (selectedIndex >= buttons.size()) {
                selectedIndex = 0;
            }
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ENTER) {
            buttons.get(selectedIndex).onClick(game);
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE || keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            game.gameState = Game.State.MENU;
        }
    }
}