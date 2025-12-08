package engine;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import entities.Player;
import javax.media.opengl.*;
import com.sun.opengl.util.j2d.TextRenderer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;

public class Game implements GLEventListener, KeyListener {
    TextureLoader loader = new TextureLoader();
    Texture bg;
    Player p1, p2;
    boolean gameOver = false;
    String winnerName = "";
    int winTimer = 0;
    TextRenderer textRenderer;
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

        p1 = new Player(a1, true, "BEE");
        p2 = new Player(a2, false, "KREE");

        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 20), true, true);
    }

    public void display(GLAutoDrawable d) {
        GL gl = d.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1, 1, 1);

        loader.draw(gl, bg, 0, 0, 1280, 720);

        if (!gameOver) {
            p1.update();
            p2.update();

            for (int i = p1.powers.size() - 1; i >= 0; i--) {
                Player.PlayerPower power = p1.powers.get(i);
                if (!power.active) {
                    p1.powers.remove(i);
                    continue;
                }
                float pw = power.isSpecial ? 100f : 70f;
                float ph = pw;
                if (power.x < p2.x + 180 && power.x + pw > p2.x && power.y < p2.y + 180 && power.y + ph > p2.y) {
                    p1.powers.remove(i);
                    p2.takeDamage(power.isSpecial ? 20 : 10, power.isSpecial);
                    if (p2.defeated) {
                        gameOver = true;
                        winnerName = p1.playerName;
                        p1.powers.clear();
                        p2.powers.clear();
                        p1.left = p1.right = p1.up = p1.down = false;
                        p2.left = p2.right = p2.up = p2.down = false;
                    }
                }
            }

            for (int i = p2.powers.size() - 1; i >= 0; i--) {
                Player.PlayerPower power = p2.powers.get(i);
                if (!power.active) {
                    p2.powers.remove(i);
                    continue;
                }
                float pw = power.isSpecial ? 100f : 70f;
                float ph = pw;
                if (power.x < p1.x + 180 && power.x + pw > p1.x && power.y < p1.y + 180 && power.y + ph > p1.y) {
                    p2.powers.remove(i);
                    p1.takeDamage(power.isSpecial ? 20 : 10, power.isSpecial);
                    if (p1.defeated) {
                        gameOver = true;
                        winnerName = p2.playerName;
                        p1.powers.clear();
                        p2.powers.clear();
                        p1.left = p1.right = p1.up = p1.down = false;
                        p2.left = p2.right = p2.up = p2.down = false;
                    }
                }
            }
        }

        drawPlayerNames(gl);
        p1.drawHealthBar(gl);
        p2.drawHealthBar(gl);
        p1.drawPowerBar(gl);
        p2.drawPowerBar(gl);
        drawBarLabels(gl);

        p1.draw(gl, loader);
        p2.draw(gl, loader);

        if (gameOver) {
            winTimer++;
            drawWinMessage(gl);
        }
    }

    private void drawPlayerNames(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        textRenderer.beginRendering(1280, 720);
        textRenderer.setSmoothing(true);
        textRenderer.setColor(1f, 1f, 1f, 1f);

        String p1Name = p1.playerName;
        String p2Name = p2.playerName;

        int p1X = 50;
        int p1Y = 80;
        int p2X = 1280 - 50 - (p2Name.length() * 12);
        int p2Y = 80;

        textRenderer.draw(p1Name, p1X, p1Y);
        textRenderer.draw(p2Name, p2X, p2Y);

        textRenderer.endRendering();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    private void drawBarLabels(GL gl) {
        com.sun.opengl.util.j2d.TextRenderer tr = new com.sun.opengl.util.j2d.TextRenderer(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        int barWidth = 200;
        int barHeight = 25;

        int p1BarX = 1280 - 50 - barWidth;
        int p1HealthBarY = 100;
        int p1PowerBarY = 50;

        int p2BarX = 50;
        int p2HealthBarY = 100;
        int p2PowerBarY = 50;

        tr.beginRendering(1280, 720);
        tr.setColor(1f, 1f, 1f, 1f);

        java.util.function.BiConsumer<java.lang.String, java.awt.Point> drawCentered = (label, pos) -> {
            int textApproxWidth = label.length() * 8;
            int textX = pos.x + (barWidth / 2) - (textApproxWidth / 2);

            int centerYTop = pos.y + (barHeight / 2);
            int textY = 720 - centerYTop - 7;
            tr.draw(label, textX, textY);
        };

        drawCentered.accept("HEALTH", new java.awt.Point(p1BarX, p1HealthBarY));
        drawCentered.accept("SPICAL POWER", new java.awt.Point(p1BarX, p1PowerBarY));

        drawCentered.accept("HEALTH", new java.awt.Point(p2BarX, p2HealthBarY));
        drawCentered.accept("SPICAL POWER", new java.awt.Point(p2BarX, p2PowerBarY));

        tr.endRendering();
    }

    private void drawWinMessage(GL gl) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float centerX = 1280 / 2;
        float centerY = 720 / 2;

        gl.glColor4f(0, 0, 0, 0.8f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(centerX - 350, centerY - 60);
        gl.glVertex2f(centerX + 350, centerY - 60);
        gl.glVertex2f(centerX + 350, centerY + 60);
        gl.glVertex2f(centerX - 350, centerY + 60);
        gl.glEnd();

        gl.glColor4f(1, 1, 0, 0.8f);
        gl.glLineWidth(5);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(centerX - 350, centerY - 60);
        gl.glVertex2f(centerX + 350, centerY - 60);
        gl.glVertex2f(centerX + 350, centerY + 60);
        gl.glVertex2f(centerX - 350, centerY + 60);
        gl.glEnd();
        gl.glLineWidth(1);

        long currentTime = System.currentTimeMillis();
        float pulse = (float) Math.sin(currentTime * 0.01) * 0.3f + 0.7f;

        if (winTimer < 30) {
            gl.glColor4f(1, 0.5f, 0, pulse);
        } else if (winTimer < 60) {
            gl.glColor4f(1, 1, 0, pulse);
        } else {
            gl.glColor4f(0, 1, 0, pulse);
        }

        String winText = winnerName + " IS WIIIIIIINNNNNN!!!";

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(1f, 1f, 1f, 1f);

        int textWidth = winText.length() * 15;
        int textX = (int) (centerX - textWidth / 2);
        int textY = (int) centerY - 10;

        textRenderer.draw(winText, textX, textY);
        textRenderer.endRendering();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }

    public void reshape(GLAutoDrawable a, int x, int y, int w, int h) {
    }

    public void displayChanged(GLAutoDrawable a, boolean b, boolean c) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        int k = e.getKeyCode();
        if (k == KeyEvent.VK_A) p1.left = true;
        if (k == KeyEvent.VK_D) p1.right = true;
        if (k == KeyEvent.VK_W) p1.up = true;
        if (k == KeyEvent.VK_S) p1.down = true;
        if (k == KeyEvent.VK_F) p1.attack = true;
        if (k == KeyEvent.VK_G) p1.special = true;

        if (k == KeyEvent.VK_LEFT) p2.left = true;
        if (k == KeyEvent.VK_RIGHT) p2.right = true;
        if (k == KeyEvent.VK_UP) p2.up = true;
        if (k == KeyEvent.VK_DOWN) p2.down = true;
        if (k == KeyEvent.VK_ENTER) p2.attack = true;
        if (k == KeyEvent.VK_SHIFT) p2.special = true;
    }

    public void keyReleased(KeyEvent e) {
        if (gameOver) return;

        int k = e.getKeyCode();
        if (k == KeyEvent.VK_A) p1.left = false;
        if (k == KeyEvent.VK_D) p1.right = false;
        if (k == KeyEvent.VK_W) p1.up = false;
        if (k == KeyEvent.VK_S) p1.down = false;
        if (k == KeyEvent.VK_F) p1.attack = false;
        if (k == KeyEvent.VK_G) p1.special = false;

        if (k == KeyEvent.VK_LEFT) p2.left = false;
        if (k == KeyEvent.VK_RIGHT) p2.right = false;
        if (k == KeyEvent.VK_UP) p2.up = false;
        if (k == KeyEvent.VK_DOWN) p2.down = false;
        if (k == KeyEvent.VK_ENTER) p2.attack = false;
        if (k == KeyEvent.VK_SHIFT) p2.special = false;
    }

    public void start() {
        JFrame w = new JFrame("Fighting Game");
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
