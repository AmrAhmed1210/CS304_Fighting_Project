package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public class DoneButton extends Button {
    public DoneButton(float x, float y, float width, float height) {
        super(x, y, width, height, "Done");
    }

    @Override
    public void onClick(Game game) {
        String name = game.inputScreen.userName.toString();

        if (name.trim().isEmpty()) {
            System.out.println("Please enter a name!");
            return;
        }

        System.out.println("Creating account for: " + name);

        game.gameState = Game.State.PLAYING;

    }

    @Override
    public void draw(GL gl, TextRenderer tr) {
        gl.glDisable(GL.GL_TEXTURE_2D);
        if (isHovered) gl.glColor4f(1f, 1f, 0f, 0.5f);
        else gl.glColor4f(0.8f, 0.8f, 0f, 0.5f);

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y); gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height); gl.glVertex2f(x, y + height);
        gl.glEnd();

        tr.beginRendering(1280, 720);
        tr.setColor(Color.BLACK);
        tr.draw(label, (int)x + 60, (int)(720 - y - 35));
        tr.endRendering();
        gl.glEnable(GL.GL_TEXTURE_2D);
    }
}