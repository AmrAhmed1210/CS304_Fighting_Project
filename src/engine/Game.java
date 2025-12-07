package engine;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import entities.Player;
import javax.media.opengl.*;
import javax.swing.*;
import java.awt.event.*;

public class Game implements GLEventListener, KeyListener {
    TextureLoader loader = new TextureLoader();
    Texture bg;
    Player p1, p2;

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

        p1 = new Player(a1, true);
        p2 = new Player(a2, false);
    }

    public void display(GLAutoDrawable d) {
        GL gl = d.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1, 1, 1);

        loader.draw(gl, bg, 0, 0, 1280, 720);

        p1.update();
        p2.update();

        float hitRange = 30;

        if (p1.powerActive) {
            float px = p1.powerX + 35;
            float py = p1.powerY + 35;
            if (p2.hitTest(px, py)) {
                p1.powerActive = false;
            }
        }

        if (p2.powerActive) {
            float px = p2.powerX + 35;
            float py = p2.powerY + 35;
            if (p1.hitTest(px, py)) {
                p2.powerActive = false;
            }
        }

        p1.draw(gl, loader);
        p2.draw(gl, loader);
    }

    public void reshape(GLAutoDrawable a, int x, int y, int w, int h) {}
    public void displayChanged(GLAutoDrawable a, boolean b, boolean c) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
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