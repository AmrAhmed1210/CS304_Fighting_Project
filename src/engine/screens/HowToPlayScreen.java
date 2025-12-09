package engine.screens;

import engine.TextureLoader;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HowToPlayScreen {
    private TextRenderer titleRenderer;
    private TextRenderer sectionRenderer;
    private TextRenderer textRenderer;
    private TextRenderer keyRenderer;
    private Texture bg;

    private final Color TITLE_COLOR = new Color(255, 215, 0);
    private final Color PLAYER1_COLOR = new Color(0, 200, 255);
    private final Color PLAYER2_COLOR = new Color(255, 100, 0);
    private final Color OBJECTIVE_COLOR = new Color(100, 255, 100);
    private final Color TIP_COLOR = new Color(255, 215, 100);

    private String[] player1Keys = {"R", "LEFT", "DOWN", "RIGHT", "NORMAL", "SPECIAL"};
    private String[] player1Actions = {"Move Up R", "Move Left", "Move Down", "Move Right", "Normal Attack", "Special Attack"};

    private String[] player2Keys = {"↑", "←", "↓", "→", "ENTER", "SHIFT"};
    private String[] player2Actions = {"Move Up", "Move Left", "Move Down", "Move Right", "Normal Attack", "Special Attack"};

    private String[] objectives = {
            "First player to defeat opponent wins!",
            "Defeat your opponent by reducing their health to zero!",
            "Normal attacks deal 10 damage.",
            "Special attacks deal 20 damage (double damage).",
            "Special attacks require full power bar.",
            "Power bar fills automatically over time.",
            "Move strategically to avoid enemy attacks."
    };

    private String[] tips = {
            "🔥 Keep moving to dodge attacks!",
            "💡 Use special attacks when power bar is full!",
            "🎯 Attack from safe distance!",
            "⚡ Mix normal and special attacks!",
            "🚫 Avoid direct confrontation!",
            "🎮 First to detect opponent wins!"
    };

    public HowToPlayScreen() {
        titleRenderer = new TextRenderer(new Font("Arial Black", Font.BOLD, 60), true, true);
        sectionRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 32), true, true);
        textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), true, true);
        keyRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 20), true, true);
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        if (bg == null) {
            bg = loader.load("background/bg.png");
        }

        if (bg != null) {
            loader.draw(gl, bg, 0, 0, 1280, 720);
        } else {
            drawGradientBackground(gl);
        }

        drawTitle(gl);
        drawControlsSection(gl);
        drawObjectiveSection(gl);
        drawTipsSection(gl);
        drawFooter(gl);
    }

    private void drawGradientBackground(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glBegin(GL.GL_QUADS);

        gl.glColor3f(0.05f, 0.05f, 0.15f);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(1280, 0);

        gl.glColor3f(0.0f, 0.0f, 0.0f);
        gl.glVertex2f(1280, 720);
        gl.glVertex2f(0, 720);

        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawTitle(GL gl) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(TITLE_COLOR);

        String title = "HOW TO PLAY";
        Rectangle2D bounds = titleRenderer.getBounds(title);
        int x = (int)((1280 - bounds.getWidth()) / 2);

        drawTextBackground(gl, x - 30, 630, (int)bounds.getWidth() + 60, 80, new Color(0, 0, 0, 150));
        drawGlowingBorder(gl, x - 40, 620, (int)bounds.getWidth() + 80, 100, TITLE_COLOR);

        titleRenderer.draw(title, x, 650);
        titleRenderer.endRendering();
    }

    private void drawControlsSection(GL gl) {
        int player1X = 150;
        int player2X = 750;
        int startY = 500;

        drawPlayerSection(gl, "PLAYER 1", player1X, startY, PLAYER1_COLOR, player1Keys, player1Actions);
        drawPlayerSection(gl, "PLAYER 2", player2X, startY, PLAYER2_COLOR, player2Keys, player2Actions);
    }

    private void drawPlayerSection(GL gl, String title, int x, int y, Color color, String[] keys, String[] actions) {
        drawSectionBackground(gl, x, y - 280, 380, 300, color);

        sectionRenderer.beginRendering(1280, 720);
        sectionRenderer.setColor(color);

        Rectangle2D bounds = sectionRenderer.getBounds(title);
        int titleX = (int)(x + (380 - bounds.getWidth()) / 2);
        sectionRenderer.draw(title, titleX, 720 - y + 20);
        sectionRenderer.endRendering();

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.WHITE);

        int controlY = y - 40;
        for (int i = 0; i < keys.length; i++) {
            drawKeyBox(gl, x + 20, controlY, keys[i], color);
            textRenderer.draw(actions[i], x + 120, 720 - controlY - 15);
            controlY -= 40;
        }

        textRenderer.endRendering();
    }

    private void drawKeyBox(GL gl, float x, float y, String key, Color color) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.8f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + 70, y);
        gl.glVertex2f(x + 70, y + 30);
        gl.glVertex2f(x, y + 30);
        gl.glEnd();

        gl.glColor4f(1, 1, 1, 0.9f);
        gl.glLineWidth(2);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + 70, y);
        gl.glVertex2f(x + 70, y + 30);
        gl.glVertex2f(x, y + 30);
        gl.glEnd();
        gl.glLineWidth(1);

        gl.glEnable(GL.GL_TEXTURE_2D);

        keyRenderer.beginRendering(1280, 720);
        keyRenderer.setColor(Color.WHITE);

        int textWidth = (int) keyRenderer.getBounds(key).getWidth();
        int textX = (int)(x + (70 - textWidth) / 2);
        keyRenderer.draw(key, textX, (int)(720 - y - 22));

        keyRenderer.endRendering();
    }

    private void drawObjectiveSection(GL gl) {
        int x = 150;
        int y = 200;

        drawSectionBackground(gl, x, y - 150, 980, 170, OBJECTIVE_COLOR);

        sectionRenderer.beginRendering(1280, 720);
        sectionRenderer.setColor(OBJECTIVE_COLOR);
        sectionRenderer.draw("GAME OBJECTIVE", x + 340, 720 - y + 20);
        sectionRenderer.endRendering();

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.WHITE);

        int textY = y - 40;
        for (String objective : objectives) {
            textRenderer.draw("• " + objective, x + 20, 720 - textY);
            textY -= 25;
        }

        textRenderer.endRendering();
    }

    private void drawTipsSection(GL gl) {
        int x = 150;
        int y = 50;

        drawSectionBackground(gl, x, y - 60, 980, 80, TIP_COLOR);

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(TIP_COLOR);

        int tipX = x + 20;
        int tipY = y - 20;

        for (int i = 0; i < tips.length; i++) {
            textRenderer.draw(tips[i], tipX, 720 - tipY);

            if (i % 2 == 0) {
                tipX += 450;
            } else {
                tipX = x + 20;
                tipY -= 30;
            }
        }

        textRenderer.endRendering();
    }

    private void drawSectionBackground(GL gl, int x, int y, int width, int height, Color color) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        Color bgColor = new Color(color.getRed()/10, color.getGreen()/10, color.getBlue()/10, 100);

        gl.glColor4f(bgColor.getRed()/255f, bgColor.getGreen()/255f, bgColor.getBlue()/255f, bgColor.getAlpha()/255f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        drawGlowingBorder(gl, x, y, width, height, color);

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawGlowingBorder(GL gl, int x, int y, int width, int height, Color color) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.7f);
        gl.glLineWidth(3);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();
        gl.glLineWidth(1);

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawTextBackground(GL gl, int x, int y, int width, int height, Color color) {
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawFooter(GL gl) {
        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.YELLOW);

        String footer = "Press ESC to return to Main Menu";
        Rectangle2D bounds = textRenderer.getBounds(footer);
        int x = (int)((1280 - bounds.getWidth()) / 2);

        drawTextBackground(gl, x - 20, 20, (int)bounds.getWidth() + 40, 40, new Color(0, 0, 0, 150));

        textRenderer.draw(footer, x, 40);
        textRenderer.endRendering();
    }
}
