package entities;

import engine.PlayerAnimator;
import engine.SpriteAnimator;
import engine.TextureLoader;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.util.ArrayList;

public class Player {
    public float x;
    public float y = 300;
    public boolean left, right, up, down, attack, special;
    public int specialCooldown = 0;
    public boolean isPlayer1;
    public int health = 100;
    public boolean defeated = false;
    public SpriteAnimator defeatAnim;
    public String playerName;
    public int punchAnimationTimer = 0;
    public int hitAnimationTimer = 0;
    public long hitStart = 0;
    public SpriteAnimator hitAnim;
    public ArrayList<PlayerPower> powers = new ArrayList<>();
    PlayerAnimator anim;

    public class PlayerPower {
        public float x, y;
        public float speed;
        public boolean isSpecial;
        public boolean active = true;

        public PlayerPower(float startX, float startY, float speed, boolean isSpecial) {
            this.x = startX;
            this.y = startY;
            this.speed = speed;
            this.isSpecial = isSpecial;
        }

        public void update() {
            if (active) {
                x += speed;
                if (x > 1300 || x < -100) active = false;
            }
        }

        public void draw(GL gl, TextureLoader loader) {
            if (!active) return;
            String path;
            if (isSpecial)
                path = isPlayer1 ? "powers/bee_hit_power.png" : "powers/kree_hit_power.png";
            else
                path = isPlayer1 ? "powers/bee_power.png" : "powers/kree_power.png";
            Texture t = loader.load(path);
            if (t != null) {
                float s = isSpecial ? 100 : 70;
                loader.draw(gl, t, x, y, s, s);
            }
        }
    }

    public Player(PlayerAnimator a, boolean player1, String name) {
        anim = a;
        isPlayer1 = player1;
        playerName = name;
        x = player1 ? 400 : 800;
        defeatAnim = new SpriteAnimator(isPlayer1 ? "sprites/player1/defeat.png" : "sprites/player2/defeat.png", 4, 50, 50);
        hitAnim = new SpriteAnimator(isPlayer1 ? "sprites/player1/hit.png" : "sprites/player2/hit.png", 5, 50, 50);
        specialCooldown = 180;
    }

    public void update() {
        if (defeated) {
            defeatAnim.next();
            return;
        }

        if (specialCooldown < 180) specialCooldown++;
        if (punchAnimationTimer > 0) punchAnimationTimer--;

        if (hitAnimationTimer > 0) {
            if (System.currentTimeMillis() - hitStart >= 2000) hitAnimationTimer = 0;
        }

        if (left) x -= 2;
        if (right) x += 2;
        if (up) y -= 2;
        if (down) y += 2;

        if (x < 0) x = 0;
        if (x > 1100) x = 1100;
        if (y < 0) y = 0;
        if (y > 540) y = 540;

        if (attack) {
            float speed = isPlayer1 ? 2 : -2;
            float sx = x + (isPlayer1 ? 90 : -90);
            float sy = y + 50;
            powers.add(new PlayerPower(sx, sy, speed, false));
            punchAnimationTimer = 15;
            attack = false;
        }

        if (special) {
            if (specialCooldown >= 180) {
                float speed = isPlayer1 ? 3 : -3;
                float sx = x + (isPlayer1 ? 90 : -90);
                float sy = y + 50;
                powers.add(new PlayerPower(sx, sy, speed, true));
                specialCooldown = 0;
                punchAnimationTimer = 20;
            } else {
                float speed = isPlayer1 ? 2 : -2;
                float sx = x + (isPlayer1 ? 90 : -90);
                float sy = y + 50;
                powers.add(new PlayerPower(sx, sy, speed, false));
                punchAnimationTimer = 15;
            }
            special = false;
        }

        for (int i = powers.size() - 1; i >= 0; i--) {
            PlayerPower p = powers.get(i);
            p.update();
            if (!p.active) powers.remove(i);
        }
    }

    public void takeDamage(int damage, boolean special) {
        if (defeated) return;
        int d = special ? damage * 2 : damage;
        health -= d;
        hitStart = System.currentTimeMillis();
        hitAnimationTimer = 1;
        hitAnim.reset();
        if (specialCooldown > 0) {
            specialCooldown -= 20;
            if (specialCooldown < 0) specialCooldown = 0;
        }
        if (health <= 0) {
            health = 0;
            defeated = true;
            defeatAnim.reset();
        }
    }

    public Texture getFrame() {
        if (defeated) return defeatAnim.getCurrentFrame();
        if (hitAnimationTimer > 0) return hitAnim.getCurrentFrame();
        if (punchAnimationTimer > 0) return anim.punch.next();
        if (left || right || up || down) return anim.walk.next();
        return anim.idle.getCurrentFrame();
    }

    public void draw(GL gl, TextureLoader loader) {
        Texture f = getFrame();
        if (f != null) loader.draw(gl, f, x, y, 180, 180);
        for (PlayerPower p : powers) p.draw(gl, loader);
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
