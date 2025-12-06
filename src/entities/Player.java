package entities;

import engine.PlayerAnimator;
import engine.TextureLoader;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;

public class Player {
    public float x = 200;
    public float y = 100;
    public boolean left, right, up, down, attack;
    public int attackTimer = 0;
    public boolean powerActive = false;
    public float powerX, powerY;
    public float powerSpeed = 5;
    public boolean isPlayer1;

    PlayerAnimator anim;

    public Player(PlayerAnimator a, boolean player1) {
        anim = a;
        isPlayer1 = player1;
        if (!player1) x = 900;
    }

    public void update() {
        if (attackTimer > 0) {
            attackTimer--;
        }

        if (left) x -= 2.5f;
        if (right) x += 2.5f;
        if (up) y -= 2.5f;
        if (down) y += 2.5f;

        if (x < 0) x = 0;
        if (x > 1100) x = 1100;
        if (y < 0) y = 0;
        if (y > 540) y = 540;

        if (attack && attackTimer == 0) {
            attackTimer = 15;
            powerActive = true;
            powerX = x + (isPlayer1 ? 90 : -90);
            powerY = y + 50;
            powerSpeed = isPlayer1 ? 5 : -5;
        }

        if (powerActive) {
            powerX += powerSpeed;
            if (powerX > 1300 || powerX < -100) {
                powerActive = false;
            }
        }
    }

    public Texture getFrame() {
        if (attackTimer > 0) {
            return anim.punch.next();
        }
        if (left || right || up || down) {
            return anim.walk.next();
        }
        return anim.idle.getCurrentFrame();
    }

    public void draw(GL gl, TextureLoader loader) {
        loader.draw(gl, getFrame(), x, y, 180, 180);

        if (powerActive) {
            String powerPath = isPlayer1 ?
                    "powers/bee_power.png" :
                    "powers/kree_power.png";
            Texture powerTex = loader.load(powerPath);
            if (powerTex != null) {
                loader.draw(gl, powerTex, powerX, powerY, 70, 70);
            }
        }
    }

    public boolean hitTest(float px, float py) {
        return px > x && px < x + 180 && py > y && py < y + 180;
    }
}