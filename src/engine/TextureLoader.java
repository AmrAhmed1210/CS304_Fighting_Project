package engine;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

import javax.media.opengl.GL;
import java.io.File;

public class TextureLoader {

    public Texture load(String path) {
        try {
            File file = new File(PathsConfig.BASE + path);
            if (!file.exists()) {
                return null;
            }
            return TextureIO.newTexture(file, true);
        } catch (Exception e) {
            return null;
        }
    }

    public void draw(GL gl, Texture t, float x, float y, float w, float h) {
        if (t == null) return;

        try {
            t.enable();
            t.bind();

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(x, y);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(x + w, y);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(x + w, y + h);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(x, y + h);
            gl.glEnd();

            t.disable();
        } catch (Exception e) {}
    }
}