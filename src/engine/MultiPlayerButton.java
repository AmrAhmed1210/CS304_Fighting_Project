package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public class MultiPlayerButton extends Button {

    public MultiPlayerButton(float x, float y, float width, float height) {
        super(x, y, width, height, "Multi Player");
    }

    @Override
    public void onClick(Game game) {
        System.out.println("Starting Game...");
        game.gameState = Game.State.PLAYING;
    }

    @Override
    public void draw(GL gl, TextRenderer tr) {

        gl.glDisable(GL.GL_TEXTURE_2D);
        if (isHovered) {
            gl.glColor4f(0f, 1f, 0f, 0.5f);
        } else {
            gl.glColor4f(0f, 0.5f, 0f, 0.5f);
        }

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();

        tr.beginRendering(1280, 720);
        tr.setColor(Color.WHITE);
        tr.draw(label, (int)x + 80, (int)(720 - y - 35));
        tr.endRendering();

        gl.glEnable(GL.GL_TEXTURE_2D);
    }
}