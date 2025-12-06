package engine;

import javax.media.opengl.GL;
import com.sun.opengl.util.texture.Texture;
import entities.Player;

public class Renderer {

    public void drawBackground(GL gl, TextureLoader loader, Texture bg) {
        loader.draw(gl, bg, 0, 0, 1280, 720);
    }

    public void drawPlayer(GL gl, TextureLoader loader, Player p) {
        p.draw(gl, loader);
    }
}