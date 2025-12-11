package engine.screens;

import engine.TextureLoader;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class HowToPlayScreen {
    private TextRenderer titleRenderer;
    private TextRenderer headerRenderer;
    private TextRenderer textRenderer;
    private TextRenderer keyRenderer;

    private Texture bg;

    private final Color COLOR_BG_OVERLAY = new Color(0, 0, 0, 200); // Dark transparency
    private final Color COLOR_P1_THEME = new Color(0, 255, 255);    // Cyan Neon
    private final Color COLOR_P2_THEME = new Color(255, 100, 0);    // Orange Neon
    private final Color COLOR_OBJ_THEME = new Color(0, 255, 100);   // Green Neon
    private final Color COLOR_TEXT_WHITE = new Color(240, 240, 255);

    private String[] player1Keys = {"W", "A", "S", "D", "F", "G"};
    private String[] player1Actions = {"Move Up", "Move Left", "Move Down", "Move Right", "Attack", "Special"};

    private String[] player2Keys = {"UP", "LEFT", "DOWN", "RIGHT", "ENTER", "SHIFT"};
    private String[] player2Actions = {"Move Up", "Move Left", "Move Down", "Move Right", "Attack", "Special"};

    private String[] objectives = {
            "• Defeat opponent by reducing health to zero.",
            "• Normal attacks deal 10 damage.",
            "• Special attacks deal 20 damage (Need Full Power).",
            "• Power bar fills automatically over time."
    };

    public HowToPlayScreen() {
        titleRenderer = new TextRenderer(new Font("Impact", Font.BOLD, 55), true, true);
        headerRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 30), true, true);
        textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 20), true, true);
        keyRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 18), true, true);
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        if (bg == null) bg = loader.load("background/bg.png");
        if (bg != null) {
            gl.glColor3f(1, 1, 1);
            loader.draw(gl, bg, 0, 0, 1280, 720);
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glColor4f(0, 0, 0, 0.7f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0);
        gl.glVertex2f(1280, 0);
        gl.glVertex2f(1280, 720);
        gl.glVertex2f(0, 720);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);

        drawTitle(gl);
        drawObjectives(gl);
        drawControls(gl);
        drawFooter(gl);
    }

    private void drawTitle(GL gl) {
        String title = "HOW TO PLAY";
        Rectangle2D bounds = titleRenderer.getBounds(title);
        int x = (int) (1280 - bounds.getWidth()) / 2;
        int y = 80;

        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(new Color(255, 255, 0, 100));
        titleRenderer.draw(title, x + 4, 720 - y - 4);
        titleRenderer.setColor(Color.YELLOW);
        titleRenderer.draw(title, x, 720 - y);
        titleRenderer.endRendering();
    }

    private void drawObjectives(GL gl) {
        int panelX = 240;
        int panelY = 100;
        int panelW = 800;
        int panelH = 220;

        drawCyberpunkPanel(gl, panelX, panelY, panelW, panelH, COLOR_OBJ_THEME);

        headerRenderer.beginRendering(1280, 720);
        headerRenderer.setColor(COLOR_OBJ_THEME);
        headerRenderer.draw("GAME OBJECTIVES", 550, 720 - (panelY + 50));
        headerRenderer.endRendering();

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(COLOR_TEXT_WHITE);
        int textStartY = panelY + 90;
        for (String obj : objectives) {
            textRenderer.draw(obj, panelX + 40, 720 - textStartY);
            textStartY += 35;
        }
        textRenderer.endRendering();
    }

    private void drawControls(GL gl) {
        int startY = 360;

        int p1X = 140;
        int p2X = 740;
        int panelW = 400;
        int panelH = 320;

        drawCyberpunkPanel(gl, p1X, startY, panelW, panelH, COLOR_P1_THEME);

        headerRenderer.beginRendering(1280, 720);
        headerRenderer.setColor(COLOR_P1_THEME);
        headerRenderer.draw("PLAYER 1 (LEFT)", p1X + 80, 720 - (startY + 40));
        headerRenderer.endRendering();

        drawKeyList(gl, p1X + 30, startY + 70, player1Keys, player1Actions, COLOR_P1_THEME);

        drawCyberpunkPanel(gl, p2X, startY, panelW, panelH, COLOR_P2_THEME);

        headerRenderer.beginRendering(1280, 720);
        headerRenderer.setColor(COLOR_P2_THEME);
        headerRenderer.draw("PLAYER 2 (RIGHT)", p2X + 80, 720 - (startY + 40));
        headerRenderer.endRendering();

        drawKeyList(gl, p2X + 30, startY + 70, player2Keys, player2Actions, COLOR_P2_THEME);
    }

    private void drawKeyList(GL gl, int x, int startY, String[] keys, String[] actions, Color themeColor) {
        int currentY = startY;

        for (int i = 0; i < keys.length; i++) {
            drawKeyBox(gl, x, currentY, keys[i], themeColor);

            textRenderer.beginRendering(1280, 720);
            textRenderer.setColor(Color.WHITE);
            textRenderer.draw(actions[i], x + 100, 720 - (currentY + 22));
            textRenderer.endRendering();

            currentY += 40;
        }
    }

    private void drawKeyBox(GL gl, int x, int y, String key, Color color) {
        int width = 80;
        int height = 30;

        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.3f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        gl.glLineWidth(2.0f);
        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1.0f);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);

        keyRenderer.beginRendering(1280, 720);
        keyRenderer.setColor(Color.WHITE);
        Rectangle2D bounds = keyRenderer.getBounds(key);
        int textX = x + (width - (int)bounds.getWidth()) / 2;
        int textY = 720 - (y + 22);
        keyRenderer.draw(key, textX, textY);
        keyRenderer.endRendering();
    }

    private void drawCyberpunkPanel(GL gl, int x, int y, int w, int h, Color borderColor) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor4f(0.05f, 0.05f, 0.1f, 0.85f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + w, y);
        gl.glVertex2f(x + w, y + h);
        gl.glVertex2f(x, y + h);
        gl.glEnd();

        gl.glLineWidth(3.0f);
        gl.glColor4f(borderColor.getRed()/255f, borderColor.getGreen()/255f, borderColor.getBlue()/255f, 0.8f);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + w, y);
        gl.glVertex2f(x + w, y + h);
        gl.glVertex2f(x, y + h);
        gl.glEnd();

        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(x, y + 20);
        gl.glVertex2f(x + 20, y);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private void drawFooter(GL gl) {
        String text = "PRESS [ESC] TO RETURN";
        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.GRAY);
        Rectangle2D bounds = textRenderer.getBounds(text);
        textRenderer.draw(text, (int)(1280 - bounds.getWidth())/2, 30);
        textRenderer.endRendering();
    }
}