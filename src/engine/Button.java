package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public abstract class Button {
    public float x, y, width, height;
    public String label;
    public boolean isHovered = false;

    public Button(float x, float y, float width, float height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    public void draw(GL gl, TextRenderer tr) {
        tr.beginRendering(1280, 720);

        int textWidth = (int) tr.getBounds(label).getWidth();
        int textX = (int) (x + (width - textWidth) / 2);
        int textY = (int) (720 - (y + height / 2) - 10);

        tr.setColor(Color.BLACK);
        tr.draw(label, textX + 4, textY - 4);

        if (isHovered) {
            tr.setColor(Color.YELLOW);
        } else {
            tr.setColor(Color.WHITE);
        }
        tr.draw(label, textX, textY);

        tr.endRendering();
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public abstract void onClick(Game game);
}