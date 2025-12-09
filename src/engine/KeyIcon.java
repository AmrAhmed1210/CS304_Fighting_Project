package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public class KeyIcon {
    private float x, y, width, height;
    private String key;
    private Color color;
    private boolean hovered;

    public KeyIcon(float x, float y, float width, float height, String key, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.key = key;
        this.color = color;
    }

    private float clamp(float v) {
        return v > 1f ? 1f : v;
    }

    public void draw(GL gl, TextRenderer textRenderer) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        if (hovered) {
            gl.glColor4f(clamp(r + 0.25f), clamp(g + 0.25f), clamp(b + 0.25f), 1f);
        } else {
            gl.glColor4f(r, g, b, 0.9f);
        }

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        if (hovered) {
            gl.glColor4f(clamp(r + 0.4f), clamp(g + 0.3f), clamp(b + 0.3f), 1f);
            gl.glLineWidth(4);
        } else {
            gl.glColor4f(1f, 1f, 1f, 0.8f);
            gl.glLineWidth(2);
        }

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        gl.glLineWidth(1);
        gl.glEnable(GL.GL_TEXTURE_2D);

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(1f, 1f, 1f, 1f);

        int tw = (int) textRenderer.getBounds(key).getWidth();
        int th = (int) textRenderer.getBounds(key).getHeight();

        int tx = (int)(x + (width - tw) / 2);
        int ty = (int)(y + (height - th) / 2);

        textRenderer.draw(key, tx, ty);
        textRenderer.endRendering();
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isInside(int mx, int my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }
}
