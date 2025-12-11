package engine.screens;

import engine.Button;
import engine.Game;
import engine.TextureLoader;
import engine.buttons.BackButton;
import engine.buttons.LevelButton;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;

public class LevelsScreen {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int selectedIndex = 0;
    private TextRenderer titleRenderer;
    private TextRenderer levelNumberRenderer;
    private TextRenderer statusRenderer;

    public LevelsScreen() {
        titleRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 50), true, true);
        levelNumberRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 40), true, true);
        statusRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 20), true, true);

        float startX = 200;
        float startY = 200;
        float buttonSize = 150;
        float gapX = 200;
        float gapY = 200;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int level = row * 3 + col + 1;
                float x = startX + col * (buttonSize + gapX);
                float y = startY + row * (buttonSize + gapY);
                buttons.add(new LevelButton(x, y, buttonSize, buttonSize, level));
            }
        }

        buttons.add(new BackButton(50, 50, 150, 50));
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY, int maxUnlockedLevel) {
        Texture levelsBg = loader.load("background/bg.png");
        if (levelsBg != null) {
            loader.draw(gl, levelsBg, 0, 0, 1280, 720);
        }

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).isHovered = buttons.get(i).isInside(mouseX, mouseY);
            if (buttons.get(i).isHovered) {
                selectedIndex = i;
            }
        }

        drawTitle(gl);
        drawLevels(gl, maxUnlockedLevel);
        drawCursor(gl);
    }

    private void drawTitle(GL gl) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(1f, 1f, 0.8f, 1f);
        String title = "SELECT LEVEL";
        int titleWidth = (int) titleRenderer.getBounds(title).getWidth();
        int titleX = (1280 - titleWidth) / 2;
        int titleY = 720 - 80;
        titleRenderer.draw(title, titleX, titleY);
        titleRenderer.endRendering();
    }

    private void drawLevels(GL gl, int maxUnlockedLevel) {
        for (Button button : buttons) {
            if (button instanceof LevelButton) {
                LevelButton levelButton = (LevelButton) button;
                levelButton.draw(gl, levelNumberRenderer, statusRenderer, maxUnlockedLevel);
            } else {
                button.draw(gl, levelNumberRenderer);
            }
        }
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
        if (keyCode == java.awt.event.KeyEvent.VK_UP || keyCode == java.awt.event.KeyEvent.VK_LEFT) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = buttons.size() - 1;
            }
        }
        if (keyCode == java.awt.event.KeyEvent.VK_DOWN || keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
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