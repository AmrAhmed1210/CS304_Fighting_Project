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

    public Power() {
    }

    public void activate(float startX, float startY, boolean isP1, Texture tex) {
        x = startX;
        y = startY;
        active = true;
        isPlayer1 = isP1;
        texture = tex;
        velX = isP1 ? 5 : -5;
        hitTimer = 0;
    }

    public void update() {
        if (active) {
            x += velX;


            if (hitTimer > 0) {
                hitTimer--;
                if (hitTimer == 0) {
                    active = false;
                }
                return;
            }


            if (x < -100 || x > 1380) {
                active = false;
            }
        }
    }

    public void draw(GL gl, TextureLoader loader) {
        if (active && texture != null) {
            loader.draw(gl, texture, x, y, 80, 80);
        }
    }

    public boolean collidesWith(Player p) {
        if (!active || hitTimer > 0) return false;

        boolean collision = x < p.x + 180 &&
                x + 80 > p.x &&
                y < p.y + 180 &&
                y + 80 > p.y;

        if (collision) {
            hitTimer = 10;
        }

        return collision;
    }
}