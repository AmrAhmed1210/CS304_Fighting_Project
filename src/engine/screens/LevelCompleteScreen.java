package engine.screens;

import engine.Button;
import engine.Game;
import engine.TextureLoader;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class LevelCompleteScreen {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int selectedIndex = 0;
    private TextRenderer titleRenderer;
    private TextRenderer messageRenderer;
    private TextRenderer buttonRenderer;

    public LevelCompleteScreen() {
        titleRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 60), true, true);
        messageRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 30), true, true);
        buttonRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 32), true, true);
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY, int currentLevel, int maxUnlockedLevel) {
        drawBackground(gl, loader);
        drawTitle(gl, currentLevel);
        drawMessage(gl, currentLevel);
        createButtons(currentLevel, maxUnlockedLevel);
        updateButtonHover(mouseX, mouseY);
        drawButtons(gl);
        drawCursor(gl);
    }

    private void drawBackground(GL gl, TextureLoader loader) {
        Texture bg = loader.load("background/bg.png");
        if (bg != null) {
            gl.glColor3f(1, 1, 1);
            loader.draw(gl, bg, 0, 0, 1280, 720);
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor4f(0, 0, 0, 0.7f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(1280, 0);
        gl.glVertex2f(1280, 720);
        gl.glVertex2f(0, 720);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawTitle(GL gl, int currentLevel) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(1f, 1f, 0f, 1f);
        String title = "LEVEL " + currentLevel + " COMPLETED!";
        int titleWidth = (int) titleRenderer.getBounds(title).getWidth();
        int titleX = (1280 - titleWidth) / 2;
        int titleY = 720 - 150;
        titleRenderer.draw(title, titleX, titleY);
        titleRenderer.endRendering();
    }

    private void drawMessage(GL gl, int currentLevel) {
        messageRenderer.beginRendering(1280, 720);
        messageRenderer.setColor(0.8f, 0.8f, 1f, 1f);

        String[] messages = {
                "Great job! You defeated the enemy!",
                "Excellent fighting skills!",
                "You're getting stronger!",
                "Masterful victory!",
                "Incredible performance!",
                "You are the ultimate champion!"
        };

        String message = messages[Math.min(currentLevel - 1, messages.length - 1)];
        int msgWidth = (int) messageRenderer.getBounds(message).getWidth();
        int msgX = (1280 - msgWidth) / 2;
        int msgY = 720 - 220;
        messageRenderer.draw(message, msgX, msgY);

        messageRenderer.endRendering();
    }

    private void createButtons(int currentLevel, int maxUnlockedLevel) {
        buttons.clear();

        float centerX = (1280 - 300) / 2;
        float startY = 250;
        float gap = 70;
        float btnWidth = 300;
        float btnHeight = 50;

        if (currentLevel < 6 && currentLevel < maxUnlockedLevel) {
            buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "NEXT LEVEL") {
                @Override
                public void onClick(Game game) {
                    game.currentLevel++;
                    game.restartLevel();
                    game.gameState = Game.State.PLAYING;
                    if (game.soundManager != null) {
                        game.soundManager.playGameBackground();
                    }
                }
            });
            startY += gap;
        }

        buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "PLAY AGAIN") {
            @Override
            public void onClick(Game game) {
                game.restartLevel();
                game.gameState = Game.State.PLAYING;
                if (game.soundManager != null) {
                    game.soundManager.playGameBackground();
                }
            }
        });
        startY += gap;

        buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "CHOOSE LEVEL") {
            @Override
            public void onClick(Game game) {
                game.gameState = Game.State.LEVELS;
                if (game.soundManager != null) {
                    game.soundManager.stopGameBackground();
                }
            }
        });
        startY += gap;

        buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "MAIN MENU") {
            @Override
            public void onClick(Game game) {
                game.gameState = Game.State.MENU;
                if (game.soundManager != null) {
                    game.soundManager.stopGameBackground();
                    game.soundManager.playStartSound();
                }
                game.resetSoundFlags();
            }
        });
    }

    private void updateButtonHover(int mouseX, int mouseY) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).isHovered = buttons.get(i).isInside(mouseX, mouseY);
            if (buttons.get(i).isHovered) {
                selectedIndex = i;
            }
        }
    }

    private void drawButtons(GL gl) {
        buttonRenderer.beginRendering(1280, 720);

        for (Button button : buttons) {
            int textWidth = (int) buttonRenderer.getBounds(button.label).getWidth();
            int textX = (int) (button.x + (button.width - textWidth) / 2);
            int textY = (int) (720 - (button.y + button.height / 2) - 12);

            buttonRenderer.setColor(0f, 0f, 0f, 1f);
            buttonRenderer.draw(button.label, textX + 2, textY - 2);

            if (button.isHovered) {
                buttonRenderer.setColor(1f, 1f, 0f, 1f);
            } else {
                buttonRenderer.setColor(1f, 1f, 1f, 1f);
            }

            buttonRenderer.draw(button.label, textX, textY);
        }

        buttonRenderer.endRendering();
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
            if (!buttons.isEmpty()) {
                buttons.get(selectedIndex).onClick(game);
            }
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            game.gameState = Game.State.LEVELS;
            if (game.soundManager != null) {
                game.soundManager.stopGameBackground();
            }
        }
    }
}