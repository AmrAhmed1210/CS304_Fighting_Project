package engine.buttons;

import engine.Button;
import engine.Game;
import engine.TextureLoader;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CreditsButton extends Button {

    private static TextRenderer titleRenderer;
    private static TextRenderer headerRenderer;
    private static TextRenderer textRenderer;
    private static Texture bg;

    private static final String[] storyLines = {
            "Sector 7 was peaceful... until the 'Iron Syndicate' took over.",
            "They betrayed the 'Eclipse Unit'. They left you for dead.",
            "But a warrior's spirit never fades. You have been rebooted.",
            "Your mission is clear: Ascend the Tower. Destroy the Syndicate.",
            "Vengeance is the only path to redemption."
    };

    private static final String[] developers = {
            "Youssef Mohamed", "Amr Ahmed", "Ibrahim Alaa", "Mostafa Ashraf", "Ali Farag"
    };

    public CreditsButton(float x, float y, float width, float height) {
        super(x, y, width, height, "CREDITS");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        game.gameState = Game.State.CREDITS;
    }

    public static void handleInput(int key, Game game) {
        if (key == KeyEvent.VK_ESCAPE) {
            game.gameState = Game.State.MENU;
        }
    }

    public static void drawCreditsScreen(GL gl, TextureLoader loader) {
        if (titleRenderer == null) {
            titleRenderer = new TextRenderer(new Font("Impact", Font.BOLD, 60), true, true);
            headerRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 32), true, true);
            textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 22), true, true);
        }

        if (bg == null) bg = loader.load("background/bg.png");
        if (bg != null) {
            gl.glColor3f(1, 1, 1);
            loader.draw(gl, bg, 0, 0, 1280, 720);
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(0, 0, 0, 0.85f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0); gl.glVertex2f(1280, 0);
        gl.glVertex2f(1280, 720); gl.glVertex2f(0, 720);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);

        drawTitle(gl);
        drawPanel(gl, "THE STORY", storyLines, 140, 380, 1000, 220, Color.CYAN);
        drawPanel(gl, "DEVELOPERS", developers, 390, 100, 500, 240, Color.ORANGE);

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.GRAY);
        textRenderer.draw("PRESS [ESC] TO RETURN", 520, 30);
        textRenderer.endRendering();
    }

    private static void drawTitle(GL gl) {
        titleRenderer.beginRendering(1280, 720);
        titleRenderer.setColor(Color.YELLOW);
        titleRenderer.draw("CREDITS", 520, 650);
        titleRenderer.endRendering();
    }

    private static void drawPanel(GL gl, String header, String[] content, int x, int y, int w, int h, Color color) {
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.15f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y); gl.glVertex2f(x+w, y);
        gl.glVertex2f(x+w, y+h); gl.glVertex2f(x, y+h);
        gl.glEnd();

        gl.glLineWidth(2);
        gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 0.9f);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y); gl.glVertex2f(x+w, y);
        gl.glVertex2f(x+w, y+h); gl.glVertex2f(x, y+h);
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);

        headerRenderer.beginRendering(1280, 720);
        headerRenderer.setColor(color);
        headerRenderer.draw(header, x + (w/2) - 80, 720 - (y + 40));
        headerRenderer.endRendering();

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.WHITE);
        int textY = y + 80;
        for (String line : content) {
            int textX = x + (w/2) - (line.length() * 5);
            textRenderer.draw(line, textX, 720 - textY);
            textY += 30;
        }
        textRenderer.endRendering();
    }
}