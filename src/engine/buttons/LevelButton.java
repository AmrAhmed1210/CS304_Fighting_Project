package engine.buttons;

import engine.Button;
import engine.Game;
import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public class LevelButton extends Button {
    private int levelNumber;

    public LevelButton(float x, float y, float width, float height, int levelNumber) {
        super(x, y, width, height, "LEVEL " + levelNumber);
        this.levelNumber = levelNumber;
    }

    public void draw(GL gl, TextRenderer numberRenderer, TextRenderer statusRenderer, int maxUnlockedLevel) {
        boolean isUnlocked = levelNumber <= maxUnlockedLevel;
        boolean isCompleted = levelNumber < maxUnlockedLevel;

        // رسم خلفية الزر
        gl.glDisable(GL.GL_TEXTURE_2D);

        if (isUnlocked) {
            if (isCompleted) {
                gl.glColor3f(0f, 0.6f, 0f); // أخضر للمستويات المكتملة
            } else if (levelNumber == maxUnlockedLevel) {
                gl.glColor3f(0.9f, 0.9f, 0f); // أصفر للمستوى الحالي
            } else {
                gl.glColor3f(0.2f, 0.5f, 0.8f); // أزرق للمستويات المفتوحة
            }
        } else {
            gl.glColor3f(0.3f, 0.3f, 0.3f); // رمادي للمستويات المقفولة
        }

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        // حدود الزر
        if (isHovered && isUnlocked) {
            gl.glColor3f(1f, 1f, 0f);
        } else {
            gl.glColor3f(0.8f, 0.8f, 0.8f);
        }

        gl.glLineWidth(3);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();
        gl.glLineWidth(1);

        gl.glEnable(GL.GL_TEXTURE_2D);

        // رسم رقم المستوى
        numberRenderer.beginRendering(1280, 720);

        if (isUnlocked) {
            numberRenderer.setColor(Color.WHITE);
        } else {
            numberRenderer.setColor(Color.GRAY);
        }

        String levelStr = String.valueOf(levelNumber);
        int numWidth = (int) numberRenderer.getBounds(levelStr).getWidth();
        int numX = (int) (x + (width - numWidth) / 2);
        int numY = (int) (720 - (y + height / 2) - 15);
        numberRenderer.draw(levelStr, numX, numY);

        numberRenderer.endRendering();

        // رسم حالة المستوى
        statusRenderer.beginRendering(1280, 720);

        if (isUnlocked) {
            if (isCompleted) {
                statusRenderer.setColor(Color.GREEN);
                statusRenderer.draw("COMPLETED", (int)x + 25, (int)(720 - (y + height) + 20));
            } else {
                statusRenderer.setColor(Color.YELLOW);
                statusRenderer.draw("AVAILABLE", (int)x + 30, (int)(720 - (y + height) + 20));
            }
        } else {
            statusRenderer.setColor(Color.RED);
            statusRenderer.draw("LOCKED", (int)x + 45, (int)(720 - (y + height) + 20));
        }

        statusRenderer.endRendering();
    }

    @Override
    public void onClick(Game game) {
        if (levelNumber <= game.maxUnlockedLevel) {
            game.currentLevel = levelNumber;
            game.gameState = Game.State.CHARACTER_SELECT;
            game.resetSoundFlags();
        }
    }
}