package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import java.awt.Color;

public class VersusButton extends Button {

    public VersusButton(float x, float y, float width, float height) {
        super(x, y, width, height, "VS PLAYER");
    }

    @Override
    public void onClick(Game game) {
        game.vsComputer = false;
        game.gameState = Game.State.PLAYING;
        game.p1.playerName = "PLAYER 1";
        game.p2.playerName = "PLAYER 2";
    }

    @Override
    public void draw(GL gl, TextRenderer tr) {
        gl.glDisable(GL.GL_TEXTURE_2D);
        if (isHovered) {
            gl.glColor4f(1f, 0f, 0f, 0.5f);
        } else {
            gl.glColor4f(0.5f, 0f, 0f, 0.5f);
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