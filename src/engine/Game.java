package engine;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.j2d.TextRenderer;
import entities.Player;
import javax.media.opengl.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Color;

public class Game implements GLEventListener, KeyListener {
    TextureLoader loader = new TextureLoader();
    Texture bg;
    Player bee, kree;

    private boolean gameOver = false;
    private String winnerMessage = "";
    private long lastUpdateTime;

    private float beeHealth = 100.0f;
    private float kreeHealth = 100.0f;
    private float beeSpecial = 100.0f;
    private float kreeSpecial = 100.0f;

    private long lastNormalAttackBee = 0;
    private long lastNormalAttackKree = 0;
    private final long NORMAL_ATTACK_COOLDOWN = 800;
    private final int NORMAL_ATTACK_DAMAGE = 10;
    private final int SPECIAL_ATTACK_DAMAGE = 25;

    private TextRenderer textRenderer;
    private Color roseGold = new Color(0.92f, 0.71f, 0.75f);

    public void init(GLAutoDrawable d) {
        GL gl = d.getGL();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glClearColor(0, 0, 0, 1);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 1280, 720, 0, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        bg = loader.load("background/bg.png");

        PlayerAnimator a1 = new PlayerAnimator();
        a1.idle = new SpriteAnimator("sprites/player1/idle.png", 5, 50, 50);
        a1.walk = new SpriteAnimator("sprites/player1/walk.png", 5, 50, 50);
        a1.punch = new SpriteAnimator("sprites/player1/punch.png", 5, 50, 50);

        PlayerAnimator a2 = new PlayerAnimator();
        a2.idle = new SpriteAnimator("sprites/player2/idle.png", 5, 50, 50);
        a2.walk = new SpriteAnimator("sprites/player2/walk.png", 5, 50, 50);
        a2.punch = new SpriteAnimator("sprites/player2/punch.png", 5, 50, 50);

        bee = new Player(a1, true);     // BEE (اليسار) تبص لليمين (على KREE)
        kree = new Player(a2, false);   // KREE (اليمين) تبص لليسار (على BEE)

        bee.x = 300;
        bee.y = 300;
        kree.x = 500;
        kree.y = 300;

        lastUpdateTime = System.currentTimeMillis();
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 36));
    }

    private void updateGame() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;

        if (beeSpecial < 100.0f) {
            beeSpecial += (100.0f / 7.0f) * deltaTime;
            if (beeSpecial > 100.0f) beeSpecial = 100.0f;
        }

        if (kreeSpecial < 100.0f) {
            kreeSpecial += (100.0f / 7.0f) * deltaTime;
            if (kreeSpecial > 100.0f) kreeSpecial = 100.0f;
        }

        lastUpdateTime = currentTime;
    }

    public void display(GLAutoDrawable d) {
        GL gl = d.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1, 1, 1);

        loader.draw(gl, bg, 0, 0, 1280, 720);
        updateGame();
        bee.update();
        kree.update();

        if (bee.powerActive) {
            float px = bee.powerX + 35;
            float py = bee.powerY + 35;
            if (kree.hitTest(px, py)) {
                bee.powerActive = false;
                kreeHealth -= SPECIAL_ATTACK_DAMAGE;
                if (kreeHealth <= 0) {
                    kreeHealth = 0;
                    gameOver = true;
                    winnerMessage = "BEE WINS!";
                }
            }
        }

        if (kree.powerActive) {
            float px = kree.powerX + 35;
            float py = kree.powerY + 35;
            if (bee.hitTest(px, py)) {
                kree.powerActive = false;
                beeHealth -= SPECIAL_ATTACK_DAMAGE;
                if (beeHealth <= 0) {
                    beeHealth = 0;
                    gameOver = true;
                    winnerMessage = "KREE WINS!";
                }
            }
        }

        if (!gameOver) {
            if (beeHealth > 0) {
                bee.draw(gl, loader);
            }
            if (kreeHealth > 0) {
                kree.draw(gl, loader);
            }
        } else {
            if (winnerMessage.contains("BEE")) {
                bee.draw(gl, loader);
            } else {
                kree.draw(gl, loader);
            }
        }

        drawHealthAndSpecialBars(gl);

        if (gameOver) {
            drawWinnerMessage();
        }
    }

    private void drawHealthAndSpecialBars(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        drawRect(gl, 50, 50, 200, 20);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        float beeHealthWidth = (beeHealth / 100.0f) * 200;
        if (beeHealthWidth < 0) beeHealthWidth = 0;
        drawRect(gl, 50, 50, beeHealthWidth, 20);


        gl.glColor3f(1.0f, 0.0f, 0.0f);
        drawRect(gl, 1030, 50, 200, 20);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        float kreeHealthWidth = (kreeHealth / 100.0f) * 200;
        if (kreeHealthWidth < 0) kreeHealthWidth = 0;
        drawRect(gl, 1030, 50, kreeHealthWidth, 20);

        gl.glEnable(GL.GL_TEXTURE_2D);

        drawText("BEE", 30, 60, 24, roseGold);
        drawText("KREE", 1010, 60, 24, roseGold);

        drawText(String.format("%.0f%%", beeHealth), 260, 55, 20, Color.WHITE);
        drawText(String.format("%.0f%%", kreeHealth), 1240, 55, 20, Color.WHITE);
    }

    private void drawText(String text, float x, float y, int size, Color color) {
        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(color.getRed()/255f, color.getGreen()/255f,
                color.getBlue()/255f, color.getAlpha()/255f);
        textRenderer.draw(text, (int)x, (int)y);
        textRenderer.endRendering();
    }

    private void drawWinnerMessage() {
        textRenderer.beginRendering(1280, 720);

        textRenderer.setColor(0.0f, 0.0f, 0.0f, 0.7f);
        for (int i = 0; i < 200; i++) {
            textRenderer.draw(" ", 440, 260 + i);
        }

        textRenderer.setColor(roseGold.getRed()/255f, roseGold.getGreen()/255f,
                roseGold.getBlue()/255f, 1.0f);
        textRenderer.draw(winnerMessage, 500, 350);

        textRenderer.endRendering();
    }

    private void drawRect(GL gl, float x, float y, float width, float height) {
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x + width, y);
        gl.glVertex2f(x + width, y + height);
        gl.glVertex2f(x, y + height);
        gl.glEnd();
    }

    public void reshape(GLAutoDrawable a, int x, int y, int w, int h) {}
    public void displayChanged(GLAutoDrawable a, boolean b, boolean c) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        long currentTime = System.currentTimeMillis();

        if (k == KeyEvent.VK_A) bee.left = true;
        if (k == KeyEvent.VK_D) bee.right = true;
        if (k == KeyEvent.VK_W) bee.up = true;
        if (k == KeyEvent.VK_S) bee.down = true;

        if (k == KeyEvent.VK_LEFT) kree.left = true;
        if (k == KeyEvent.VK_RIGHT) kree.right = true;
        if (k == KeyEvent.VK_UP) kree.up = true;
        if (k == KeyEvent.VK_DOWN) kree.down = true;

        if (k == KeyEvent.VK_F) {
            bee.attack = true;
            if (checkCollision(bee, kree) && (currentTime - lastNormalAttackBee >= NORMAL_ATTACK_COOLDOWN)) {
                kreeHealth -= NORMAL_ATTACK_DAMAGE;
                lastNormalAttackBee = currentTime;

                if (kreeHealth <= 0) {
                    kreeHealth = 0;
                    gameOver = true;
                    winnerMessage = "BEE WINS!";
                }
            }
        }

        if (k == KeyEvent.VK_G) {
            if (beeSpecial >= 100) {
                bee.special = true;
                beeSpecial = 0;
            }
        }

        if (k == KeyEvent.VK_ENTER) {
            kree.attack = true;
            if (checkCollision(kree, bee) && (currentTime - lastNormalAttackKree >= NORMAL_ATTACK_COOLDOWN)) {
                beeHealth -= NORMAL_ATTACK_DAMAGE;
                lastNormalAttackKree = currentTime;

                if (beeHealth <= 0) {
                    beeHealth = 0;
                    gameOver = true;
                    winnerMessage = "KREE WINS!";
                }
            }
        }

        if (k == KeyEvent.VK_SHIFT) {
            if (kreeSpecial >= 100) {
                kree.special = true;
                kreeSpecial = 0;
            }
        }
    }

    private boolean checkCollision(Player attacker, Player target) {
        float attackerX = attacker.x;
        float attackerY = attacker.y;
        float targetX = target.x;
        float targetY = target.y;

        return Math.abs(attackerX - targetX) < 60 &&
                Math.abs(attackerY - targetY) < 60;
    }

    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_A) bee.left = false;
        if (k == KeyEvent.VK_D) bee.right = false;
        if (k == KeyEvent.VK_W) bee.up = false;
        if (k == KeyEvent.VK_S) bee.down = false;
        if (k == KeyEvent.VK_F) bee.attack = false;
        if (k == KeyEvent.VK_G) bee.special = false;

        if (k == KeyEvent.VK_LEFT) kree.left = false;
        if (k == KeyEvent.VK_RIGHT) kree.right = false;
        if (k == KeyEvent.VK_UP) kree.up = false;
        if (k == KeyEvent.VK_DOWN) kree.down = false;
        if (k == KeyEvent.VK_ENTER) kree.attack = false;
        if (k == KeyEvent.VK_SHIFT) kree.special = false;
    }

    public void start() {
        JFrame w = new JFrame("BEE vs KREE - Fighting Game");
        GLCanvas c = new GLCanvas();
        c.addGLEventListener(this);
        c.addKeyListener(this);
        w.add(c);
        w.setSize(1280, 720);
        w.setResizable(false);
        w.setVisible(true);
        c.requestFocus();

        Animator a = new Animator(c);
        a.start();
    }

    public static void main(String[] args) {
        new Game().start();
    }
}