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

public class PauseMenu {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int selectedIndex = 0;
    private TextRenderer titleRenderer;
    private TextRenderer buttonRenderer;

    public PauseMenu() {
        titleRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 60), true, true);
        buttonRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 36), true, true);

        float centerX = (1280 - 300) / 2;
        float startY = 200;
        float gap = 80;
        float btnWidth = 300;
        float btnHeight = 60;

        buttons.add(new Button(centerX, startY, btnWidth, btnHeight, "CONTINUE") {
            @Override
            public void onClick(Game game) {
                game.gameState = Game.State.PLAYING;
                game.soundManager.playGameBackground();
            }
        });

        buttons.add(new Button(centerX, startY + gap, btnWidth, btnHeight, "RESTART") {
            @Override
            public void onClick(Game game) {
                game.gameState = Game.State.PLAYING;
                game.restartLevel();
                game.soundManager.playGameBackground();
            }
        });

        buttons.add(new Button(centerX, startY + gap * 2, btnWidth, btnHeight, "QUIT TO MENU") {
            @Override
            public void onClick(Game game) {
                game.gameState = Game.State.MENU;
                game.resetSoundFlags();
                game.soundManager.stopGameBackground();
                game.soundManager.playStartSound();
            }
        });
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).isHovered = buttons.get(i).isInside(mouseX, mouseY);
            if (buttons.get(i).isHovered) {
                selectedIndex = i;
            }
        }

        drawTitle(gl);
        drawButtons(gl);
        drawCursor(gl);
    }

    private void drawTitle(GL gl) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(1f, 1f, 0f, 1f);
        String title = "GAME PAUSED";
        int titleWidth = (int) titleRenderer.getBounds(title).getWidth();
        int titleX = (1280 - titleWidth) / 2;
        int titleY = 720 - 150;
        titleRenderer.draw(title, titleX, titleY);
        titleRenderer.endRendering();
    }

    private void drawButtons(GL gl) {
        buttonRenderer.beginRendering(1280, 720);

        for (Button button : buttons) {
            int textWidth = (int) buttonRenderer.getBounds(button.label).getWidth();
            int textX = (int) (button.x + (button.width - textWidth) / 2);
            int textY = (int) (720 - (button.y + button.height / 2) - 15);

            // ظل النص (ممكن تزيله لو مش عايز)
            buttonRenderer.setColor(0f, 0f, 0f, 1f);
            buttonRenderer.draw(button.label, textX + 2, textY - 2);

            // لون النص الرئيسي
            if (button.isHovered) {
                buttonRenderer.setColor(1f, 1f, 0f, 1f); // أصفر عند hover
            } else {
                buttonRenderer.setColor(1f, 1f, 1f, 1f); // أبيض
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
            buttons.get(selectedIndex).onClick(game);
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            game.gameState = Game.State.PLAYING;
            game.soundManager.playGameBackground();
        }
    }
}