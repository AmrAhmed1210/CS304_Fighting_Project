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
    public String playerName;

    public int punchTimer = 0;
    public int hitTimer = 0;

    public SpriteAnimator defeatAnim;
    public SpriteAnimator hitAnim;
    PlayerAnimator anim;

    public ArrayList<PlayerPower> powers = new ArrayList<>();

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

        this.defeatAnim = a.defeat;
        this.hitAnim = a.hit;

        specialCooldown = 180;
    }

    public void update() {
        if (defeated) {
            return;
        }

        if (specialCooldown < 180) specialCooldown++;

        if (punchTimer > 0) punchTimer--;
        if (hitTimer > 0) hitTimer--;

        if (hitTimer == 0) {
            if (left) x -= 2;
            if (right) x += 2;
            if (up) y -= 2;
            if (down) y += 2;
        }

        if (x < 0) x = 0;
        if (x > 1100) x = 1100;
        if (y < 0) y = 0;
        if (y > 540) y = 540;

        if (attack && hitTimer == 0) {
            float speed = isPlayer1 ? 5 : -5;
            float sx = x + (isPlayer1 ? 90 : -90);
            float sy = y + 50;
            powers.add(new PlayerPower(sx, sy, speed, false));
            punchTimer = 15;
            anim.punch.reset();
            attack = false;
        }

        if (special && hitTimer == 0) {
            if (specialCooldown >= 180) {
                float speed = isPlayer1 ? 7 : -7;
                float sx = x + (isPlayer1 ? 90 : -90);
                float sy = y + 50;
                powers.add(new PlayerPower(sx, sy, speed, true));
                specialCooldown = 0;
                punchTimer = 20;
                anim.punch.reset();
            } else {
                float speed = isPlayer1 ? 5 : -5;
                float sx = x + (isPlayer1 ? 90 : -90);
                float sy = y + 50;
                powers.add(new PlayerPower(sx, sy, speed, false));
                punchTimer = 15;
                anim.punch.reset();
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

        hitTimer = 30;
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
        if (defeated) {
            int lastIndex = defeatAnim.frames.length - 1;
            if (lastIndex < 0) lastIndex = 0;
            return defeatAnim.frames[lastIndex];
        }

        if (hitTimer > 0) {
            int impactFrame = 2;
            if (impactFrame >= hitAnim.frames.length) impactFrame = 0;

            return hitAnim.frames[impactFrame];
        }

        if (punchTimer > 0) {
            int punchFrame = 2;
            if (punchFrame >= anim.punch.frames.length) punchFrame = 0;

            return anim.punch.frames[punchFrame];
        }

        if (left || right || up || down) {
            return anim.walk.next();
        }

        anim.walk.reset();

        return anim.idle.frames[0];
    }

    public void draw(GL gl, TextureLoader loader) {
        Texture f = getFrame();
        if (f != null) {
            loader.drawSprite(gl, f, x, y, 180, 180, !isPlayer1);
        }

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