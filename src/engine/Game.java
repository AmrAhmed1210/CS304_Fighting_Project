package engine;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import entities.Player;
import javax.media.opengl.*;
import com.sun.opengl.util.j2d.TextRenderer;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import engine.screens.*;
import java.io.*;

public class Game implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

    public enum State { MENU, ENTER_NAME, CHARACTER_SELECT, HOW_TO_PLAY, PLAYING, SETTINGS, LEVELS, PAUSE, LEVEL_COMPLETE }
    public State gameState = State.MENU;
    public AccountInputScreen inputScreen;
    CharacterSelectScreen characterSelectScreen;
    HowToPlayScreen howToPlayScreen;
    SoundSettingsScreen soundSettingsScreen;
    LevelsScreen levelsScreen;
    PauseMenu pauseMenu;
    LevelCompleteScreen levelCompleteScreen;

    TextureLoader loader = new TextureLoader();
    Texture bg;
    Texture menuBg;

    MainMenu mainMenu;

    public Player p1;
    public Player p2;
    AIController aiController;
    public boolean gameOver = false;
    public String winnerName = "";
    public int winTimer = 0;
    TextRenderer textRenderer;

    int mouseX = 0;
    int mouseY = 0;

    public String selectedCharacter = "BEE";
    public boolean vsComputer = true;

    public int currentLevel = 1;
    public int maxUnlockedLevel = 1;
    private static final String PROGRESS_FILE = "game_progress.dat";

    public static SoundManager soundManager;
    private boolean gameStartSoundPlayed = false;

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
        menuBg = loader.load("background/bg_menu.png");

        mainMenu = new MainMenu();
        inputScreen = new AccountInputScreen();
        characterSelectScreen = new CharacterSelectScreen();
        howToPlayScreen = new HowToPlayScreen();
        soundSettingsScreen = new SoundSettingsScreen();
        levelsScreen = new LevelsScreen();
        pauseMenu = new PauseMenu();
        levelCompleteScreen = new LevelCompleteScreen();

        loadProgress();

        PlayerAnimator a1 = new PlayerAnimator("player1");
        PlayerAnimator a2 = new PlayerAnimator("player2");

        p1 = new Player(a1, true, "Player 1");
        p2 = new Player(a2, false, "KREE");

        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 20), true, true);

        aiController = new AIController(p2, p1, currentLevel);

        soundManager = new SoundManager();
        soundManager.playStartSound();
    }

    private void loadProgress() {
        try {
            File file = new File(PROGRESS_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                maxUnlockedLevel = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("Could not load progress, starting from level 1");
            maxUnlockedLevel = 1;
        }
    }

    private void saveProgress() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_FILE));
            writer.write(String.valueOf(maxUnlockedLevel));
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not save progress");
        }
    }

    public void unlockNextLevel() {
        if (currentLevel == maxUnlockedLevel) {
            maxUnlockedLevel++;
            saveProgress();
        }
    }

    public void restartLevel() {
        gameOver = false;
        winnerName = "";
        winTimer = 0;
        gameStartSoundPlayed = false;

        if (p1 != null) p1.reset();
        if (p2 != null) p2.reset();

        resetGameLogic();

        if (aiController != null) {
            aiController = new AIController(p2, p1, currentLevel);
        }

        System.out.println("Level " + currentLevel + " restarted!");
    }

    public void display(GLAutoDrawable d) {
        GL gl = d.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (gameState == State.SETTINGS) {
            soundSettingsScreen.draw(gl, loader, mouseX, mouseY, this);
            return;
        }

        if (gameState == State.LEVELS) {
            levelsScreen.draw(gl, loader, mouseX, mouseY, maxUnlockedLevel);
            return;
        }

        if (gameState == State.PAUSE) {
            pauseMenu.draw(gl, loader, mouseX, mouseY);
            return;
        }

        if (gameState == State.LEVEL_COMPLETE) {
            levelCompleteScreen.draw(gl, loader, mouseX, mouseY, currentLevel, maxUnlockedLevel);
            return;
        }

        gl.glColor3f(1, 1, 1);

        if (gameState == State.MENU) {
            if (menuBg != null) {
                loader.draw(gl, menuBg, 0, 0, 1280, 720);
            }
            mainMenu.draw(gl, loader, mouseX, mouseY);
        }
        else if (gameState == State.ENTER_NAME) {
            inputScreen.draw(gl, loader, mouseX, mouseY);
        }
        else if (gameState == State.CHARACTER_SELECT) {
            characterSelectScreen.draw(gl, loader, mouseX, mouseY);
        }
        else if (gameState == State.HOW_TO_PLAY) {
            howToPlayScreen.draw(gl, loader, mouseX, mouseY);
        }
        else {
            if (!gameStartSoundPlayed && gameState == State.PLAYING) {
                soundManager.stopStartSound();
                soundManager.playGameBackground();
                soundManager.playGameStartSound();
                gameStartSoundPlayed = true;
            }
            playGame(gl);
        }
    }

    public void playGame(GL gl) {
        Texture levelBg = loader.load("background/bg_level" + currentLevel + ".png");
        if (levelBg == null) {
            loader.draw(gl, bg, 0, 0, 1280, 720);
        } else {
            loader.draw(gl, levelBg, 0, 0, 1280, 720);
        }

        if (!gameOver) {
            p1.update();
            p2.update();

            if (vsComputer) {
                aiController.update();
            }

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
                        soundManager.playWinSound();
                        unlockNextLevel();
                        resetGameLogic();
                        gameState = State.LEVEL_COMPLETE;
                        return;
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

                        if (vsComputer) {
                            soundManager.playGameOverSound();
                        } else {
                            soundManager.playWinSound();
                        }

                        resetGameLogic();
                        return;
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

        drawLevelInfo(gl);

        if (gameOver && gameState != State.LEVEL_COMPLETE) {
            winTimer++;
            drawWinMessage(gl);
        }
    }

    private void drawLevelInfo(GL gl) {
        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(1f, 1f, 0f, 1f);
        String levelText = "LEVEL " + currentLevel;
        textRenderer.draw(levelText, 1280/2 - (levelText.length() * 6), 30);
        textRenderer.endRendering();
    }

    public void resetGameLogic() {
        p1.powers.clear();
        p2.powers.clear();
        p1.left = p1.right = p1.up = p1.down = false;
        p2.left = p2.right = p2.up = p2.down = false;
    }

    private void drawPlayerNames(GL gl) {
        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(1f, 1f, 1f, 1f);
        textRenderer.draw(p1.playerName, 50, 80);
        String p2Name = vsComputer ? "COMPUTER" : p2.playerName;
        textRenderer.draw(p2Name, 1280 - 50 - (p2Name.length() * 12), 80);
        textRenderer.endRendering();
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
        drawCentered.accept("SPECIAL POWER", new java.awt.Point(p1BarX, p1PowerBarY));

        drawCentered.accept("HEALTH", new java.awt.Point(p2BarX, p2HealthBarY));
        drawCentered.accept("SPECIAL POWER", new java.awt.Point(p2BarX, p2PowerBarY));

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

        String winText = winnerName + " WINS!";

        if (vsComputer && winnerName.equals(p1.playerName)) {
            winText += " LEVEL " + currentLevel + " COMPLETED!";
        }

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

    public void resetSoundFlags() {
        gameStartSoundPlayed = false;
    }

    public void reshape(GLAutoDrawable a, int x, int y, int w, int h) {}
    public void displayChanged(GLAutoDrawable a, boolean b, boolean c) {}

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if (gameState == State.PAUSE) {
            pauseMenu.handleInput(k, this);
            return;
        }

        if (gameState == State.LEVEL_COMPLETE) {
            levelCompleteScreen.handleInput(k, this);
            return;
        }

        if (gameState == State.PLAYING && k == KeyEvent.VK_ESCAPE) {
            gameState = State.PAUSE;
            soundManager.stopGameBackground();
            return;
        }

        if (gameState == State.SETTINGS) {
            soundSettingsScreen.handleInput(k, this);
            return;
        }

        if (gameState == State.LEVELS) {
            levelsScreen.handleInput(k, this);
            return;
        }

        if (gameState == State.HOW_TO_PLAY) {
            if (k == KeyEvent.VK_ESCAPE) {
                gameState = State.MENU;
            }
            return;
        }

        if (gameState == State.CHARACTER_SELECT) {
            characterSelectScreen.handleInput(k, this);
            return;
        }

        if (gameState == State.ENTER_NAME) {
            char c = e.getKeyChar();

            if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_LEFT) {
                if (inputScreen.selectedIndex == 0) inputScreen.selectedIndex = 1;
                else inputScreen.selectedIndex = 0;
                return;
            }

            if (k == KeyEvent.VK_ENTER) {
                inputScreen.buttons.get(inputScreen.selectedIndex).onClick(this);
                return;
            }

            if (k == KeyEvent.VK_BACK_SPACE) {
                if (inputScreen.userName.length() > 0) {
                    inputScreen.userName.deleteCharAt(inputScreen.userName.length() - 1);
                }
            }
            else if (Character.isLetterOrDigit(c) || c == ' ') {
                if (inputScreen.userName.length() < 18) {
                    inputScreen.userName.append(c);
                }
            }
            return;
        }

        if (gameState == State.MENU) {
            if (k == KeyEvent.VK_UP) {
                mainMenu.selectedIndex--;
                if (mainMenu.selectedIndex < 0) {
                    mainMenu.selectedIndex = mainMenu.buttons.size() - 1;
                }
            }
            if (k == KeyEvent.VK_DOWN) {
                mainMenu.selectedIndex++;
                if (mainMenu.selectedIndex >= mainMenu.buttons.size()) {
                    mainMenu.selectedIndex = 0;
                }
            }
            if (k == KeyEvent.VK_ENTER) {
                mainMenu.buttons.get(mainMenu.selectedIndex).onClick(this);
            }
            return;
        }

        if (gameOver) return;

        if (!vsComputer) {
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
        } else {
            if (k == KeyEvent.VK_A) p1.left = true;
            if (k == KeyEvent.VK_D) p1.right = true;
            if (k == KeyEvent.VK_W) p1.up = true;
            if (k == KeyEvent.VK_S) p1.down = true;
            if (k == KeyEvent.VK_F) p1.attack = true;
            if (k == KeyEvent.VK_G) p1.special = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (gameState == State.MENU) return;

        int k = e.getKeyCode();

        if (!vsComputer) {
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
        } else {
            if (k == KeyEvent.VK_A) p1.left = false;
            if (k == KeyEvent.VK_D) p1.right = false;
            if (k == KeyEvent.VK_W) p1.up = false;
            if (k == KeyEvent.VK_S) p1.down = false;
            if (k == KeyEvent.VK_F) p1.attack = false;
            if (k == KeyEvent.VK_G) p1.special = false;
        }
    }

    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameState == State.PAUSE) {
            for (Button b : pauseMenu.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
            return;
        }

        if (gameState == State.LEVEL_COMPLETE) {
            for (Button b : levelCompleteScreen.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
            return;
        }

        if (gameState == State.SETTINGS) {
            for (Button b : soundSettingsScreen.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
            return;
        }

        if (gameState == State.LEVELS) {
            for (Button b : levelsScreen.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
            return;
        }

        if (gameState == State.MENU) {
            for (Button b : mainMenu.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
        }
        else if (gameState == State.ENTER_NAME) {
            for (Button b : inputScreen.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
        }
        else if (gameState == State.CHARACTER_SELECT) {
            for (Button b : characterSelectScreen.buttons) {
                if (b.isInside(mouseX, mouseY)) {
                    b.onClick(this);
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    public void start() {
        JFrame w = new JFrame("Fighting Game");
        GLCanvas c = new GLCanvas();
        c.addGLEventListener(this);
        c.addKeyListener(this);
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
        w.add(c);
        w.setSize(1280, 720);
        w.setResizable(false);
        w.setVisible(true);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.requestFocus();

        Animator a = new Animator(c);
        a.start();
    }

    public static void main(String[] args) {
        new Game().start();
    }
}