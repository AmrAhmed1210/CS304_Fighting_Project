package entities;

import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import engine.TextureLoader;

public class Power {
    public float x, y;
    public float velX;
    public boolean active = false;
    public boolean isPlayer1;
    public Texture texture;
    public int hitTimer = 0;
    public boolean specialActive = false;

    public Power() {}

    public void activate(float sx, float sy, boolean p1, Texture tex, boolean sp) {
        x = sx;
        y = sy;
        active = true;
        isPlayer1 = p1;
        texture = tex;
        velX = isPlayer1 ? 4 : -4;
        if (sp) velX *= 1.5f;
        specialActive = sp;
        hitTimer = 0;
    }

    public void update() {
        if (active) {
            x += velX;
            if (hitTimer > 0) {
                hitTimer--;
                if (hitTimer == 0) active = false;
                return;
            }
            if (x < -100 || x > 1380) active = false;
        }
    }

    public void draw(GL gl, TextureLoader loader) {
        if (active && texture != null) {
            float s = specialActive ? 100 : 70;
            loader.draw(gl, texture, x, y, s, s);
        }
    }

    public boolean collidesWith(Player p) {
        if (!active || hitTimer > 0) return false;
        float w = specialActive ? 100 : 70;
        float h = w;
        boolean c = x < p.x + 180 && x + w > p.x && y < p.y + 180 && y + h > p.y;
        if (c) hitTimer = 10;
        return c;
    }
}