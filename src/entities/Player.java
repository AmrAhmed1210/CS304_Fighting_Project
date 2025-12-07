package entities;

import engine.PlayerAnimator;
import engine.TextureLoader;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;

public class Player {
    public float x;
    public float y = 300;
    public boolean left, right, up, down, attack, special;
    public int attackTimer = 0;
    public int specialCooldown = 1800;
    public boolean powerActive = false;
    public boolean specialActive = false;
    public float powerX, powerY;
    public float powerSpeed = 2;
    public boolean isPlayer1;

    PlayerAnimator anim;

    public Player(PlayerAnimator a, boolean player1) {
        anim = a;
        isPlayer1 = player1;
        x = player1 ? 400 : 800;
    }

    public void update() {
        if (attackTimer > 0) attackTimer--;

        if (specialCooldown > 0) specialCooldown--;

        if (left) x -= 2;
        if (right) x += 2;
        if (up) y -= 2;
        if (down) y += 2;

        if (x < 0) x = 0;
        if (x > 1100) x = 1100;
        if (y < 0) y = 0;
        if (y > 540) y = 540;

        if (attack && attackTimer == 0) {
            attackTimer = 25;
            powerActive = true;
            specialActive = false;
            powerX = x + (isPlayer1 ? 90 : -90);
            powerY = y + 50;
            powerSpeed = isPlayer1 ? 2 : -2;
        }

        if (special && attackTimer == 0) {
            if (specialCooldown == 0) {
                attackTimer = 35;
                specialCooldown = 1800;
                powerActive = true;
                specialActive = true;
                powerX = x + (isPlayer1 ? 90 : -90);
                powerY = y + 50;
                powerSpeed = isPlayer1 ? 3 : -3;
            } else {
                attackTimer = 25;
                powerActive = true;
                specialActive = false;
                powerX = x + (isPlayer1 ? 90 : -90);
                powerY = y + 50;
                powerSpeed = isPlayer1 ? 2 : -2;
            }
        }

        if (powerActive) {
            powerX += powerSpeed;
            if (powerX > 1300 || powerX < -100) {
                powerActive = false;
                if (specialActive) specialActive = false;
            }
        }
    }

    public Texture getFrame() {
        if (attackTimer > 0) return anim.punch.next();
        if (left || right || up || down) return anim.walk.next();
        return anim.idle.getCurrentFrame();
    }

    public void draw(GL gl, TextureLoader loader) {
        loader.draw(gl, getFrame(), x, y, 180, 180);

        drawSpecialBar(gl);

        if (powerActive) {
            String path;
            if (specialActive) {
                path = isPlayer1 ? "powers/bee_hit_power.png" : "powers/kree_hit_power.png";
            } else {
                path = isPlayer1 ? "powers/bee_power.png" : "powers/kree_power.png";
            }
            Texture t = loader.load(path);
            if (t != null) {
                float s = specialActive ? 100 : 70;
                loader.draw(gl, t, powerX, powerY, s, s);
            }
        }
    }

    private void drawSpecialBar(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float barWidth = 100;
        float charge = 1.0f - ((float)specialCooldown / 1800.0f);
        float barX = x + 40;
        float barY = y - 15;

        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + barWidth, barY);
        gl.glVertex2f(barX + barWidth, barY + 8);
        gl.glVertex2f(barX, barY + 8);
        gl.glEnd();

        float r = charge < 0.5f ? 0 : (charge - 0.5f) * 2f;
        float g = charge < 0.5f ? charge * 2f : 1f;

        gl.glColor3f(r, g, 0);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + (barWidth * charge), barY);
        gl.glVertex2f(barX + (barWidth * charge), barY + 8);
        gl.glVertex2f(barX, barY + 8);
        gl.glEnd();

        if (specialCooldown == 0) {
            gl.glColor4f(1, 0.2f, 0.2f, 0.3f);
            gl.glBegin(GL.GL_QUADS);
            gl.glVertex2f(barX - 3, barY - 3);
            gl.glVertex2f(barX + barWidth + 3, barY - 3);
            gl.glVertex2f(barX + barWidth + 3, barY + 11);
            gl.glVertex2f(barX - 3, barY + 11);
            gl.glEnd();
        }

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    public boolean hitTest(float px, float py) {
        return px > x && px < x + 180 && py > y && py < y + 180;
    }
}