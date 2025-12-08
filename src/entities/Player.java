package entities;

import engine.PlayerAnimator;
import engine.SpriteAnimator;
import engine.TextureLoader;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;

public class Player {
    public float x;
    public float y = 300;
    public boolean left, right, up, down, attack, special;
    public int attackTimer = 0;
    public int specialCooldown = 0;
    public boolean powerActive = false;
    public boolean specialActive = false;
    public float powerX, powerY;
    public float powerSpeed = 2;
    public boolean isPlayer1;
    public int health = 100;
    public boolean defeated = false;
    public SpriteAnimator defeatAnim;
    public String playerName;

    PlayerAnimator anim;

    public Player(PlayerAnimator a, boolean player1, String name) {
        anim = a;
        isPlayer1 = player1;
        playerName = name;
        x = player1 ? 400 : 800;

        String defeatPath = isPlayer1 ? "sprites/player1/defeat.png" : "sprites/player2/defeat.png";
        defeatAnim = new SpriteAnimator(defeatPath, 4, 50, 50);
        specialCooldown = 0;
    }

    public void update() {
        if (defeated) {
            if (defeatAnim != null) {
                defeatAnim.next();
            }
            return;
        }

        if (attackTimer > 0) attackTimer--;

        if (specialCooldown < 180) specialCooldown++;

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
            if (specialCooldown >= 180) {
                attackTimer = 35;
                specialCooldown = 0;
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

    public void takeDamage(int damage, boolean isSpecial) {
        if (defeated) return;

        int finalDamage = isSpecial ? damage * 2 : damage;
        health -= finalDamage;

        if (specialCooldown > 0) {
            specialCooldown -= 20;
            if (specialCooldown < 0) specialCooldown = 0;
        }

        if (health <= 0) {
            health = 0;
            defeated = true;
            if (defeatAnim != null) {
                defeatAnim.reset();
            }
        }
    }

    public Texture getFrame() {
        if (defeated) {
            if (defeatAnim != null) {
                return defeatAnim.getCurrentFrame();
            }
            return null;
        }

        if (attackTimer > 0) return anim.punch.next();
        if (left || right || up || down) return anim.walk.next();
        return anim.idle.getCurrentFrame();
    }

    public void draw(GL gl, TextureLoader loader) {
        Texture frame = getFrame();
        if (frame != null) {
            loader.draw(gl, frame, x, y, 180, 180);
        }

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

    public void drawHealthBar(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float barWidth = 200;
        float barHeight = 25;
        float healthPercent = health / 100.0f;

        float barX = isPlayer1 ? 50 : 1280 - 50 - barWidth;
        float barY = 100;

        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + barWidth, barY);
        gl.glVertex2f(barX + barWidth, barY + barHeight);
        gl.glVertex2f(barX, barY + barHeight);
        gl.glEnd();

        float r, g;
        if (healthPercent > 0.5f) {
            r = (1.0f - healthPercent) * 2.0f;
            g = 1.0f;
        } else {
            r = 1.0f;
            g = healthPercent * 2.0f;
        }

        gl.glColor3f(r, g, 0);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + (barWidth * healthPercent), barY);
        gl.glVertex2f(barX + (barWidth * healthPercent), barY + barHeight);
        gl.glVertex2f(barX, barY + barHeight);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    public void drawPowerBar(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float barWidth = 200;
        float barHeight = 25;
        float powerPercent = specialCooldown / 180.0f;

        float barX = isPlayer1 ? 50 : 1280 - 50 - barWidth;
        float barY = 50;

        gl.glColor3f(0.1f, 0.1f, 0.3f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + barWidth, barY);
        gl.glVertex2f(barX + barWidth, barY + barHeight);
        gl.glVertex2f(barX, barY + barHeight);
        gl.glEnd();

        gl.glColor3f(0, 0.5f, 1);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(barX, barY);
        gl.glVertex2f(barX + (barWidth * powerPercent), barY);
        gl.glVertex2f(barX + (barWidth * powerPercent), barY + barHeight);
        gl.glVertex2f(barX, barY + barHeight);
        gl.glEnd();

        if (specialCooldown >= 180) {
            gl.glColor4f(1, 1, 0, 0.8f);
            gl.glLineWidth(3);
            gl.glBegin(GL.GL_LINE_LOOP);
            gl.glVertex2f(barX - 2, barY - 2);
            gl.glVertex2f(barX + barWidth + 2, barY - 2);
            gl.glVertex2f(barX + barWidth + 2, barY + barHeight + 2);
            gl.glVertex2f(barX - 2, barY + barHeight + 2);
            gl.glEnd();
            gl.glLineWidth(1);
        }

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    public boolean hitTest(float px, float py) {
        if (defeated) return false;
        return px > x && px < x + 180 && py > y && py < y + 180;
    }
}


