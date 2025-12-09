package engine.screens;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import engine.Button;
import engine.Game;
import engine.TextureLoader;

import javax.media.opengl.GL;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class CharacterSelectScreen {
    public ArrayList<Button> buttons = new ArrayList<>();
    TextRenderer textRenderer;
    Texture bg;
    int selectedIndex = 0;

    String[] characters = {"BEE", "KREE"};
    Texture[] charPreviews = new Texture[2];

    public CharacterSelectScreen() {
        textRenderer = new TextRenderer(new Font("Impact", Font.BOLD, 40), true, true);

        buttons.add(new Button(400, 300, 200, 200, "BEE") {
            @Override
            public void onClick(Game game) {
                game.selectedCharacter = "BEE";
                game.gameState = Game.State.ENTER_NAME;
            }

            @Override
            public void draw(GL gl, TextRenderer tr) {
                gl.glDisable(GL.GL_TEXTURE_2D);
                if (selectedIndex == 0) {
                    gl.glColor4f(1, 1, 0, 0.8f);
                } else {
                    gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
                }

                gl.glLineWidth(3);
                gl.glBegin(GL.GL_LINE_LOOP);
                gl.glVertex2f(x, y);
                gl.glVertex2f(x + width, y);
                gl.glVertex2f(x + width, y + height);
                gl.glVertex2f(x, y + height);
                gl.glEnd();
                gl.glLineWidth(1);

                if (charPreviews[0] != null) {
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    new TextureLoader().draw(gl, charPreviews[0], x + 20, y + 20, 160, 160);
                }

                tr.beginRendering(1280, 720);
                tr.setColor(Color.YELLOW);
                tr.draw(label, (int)x + 70, (int)(720 - y - 20));
                tr.endRendering();
            }
        });

        buttons.add(new Button(680, 300, 200, 200, "KREE") {
            @Override
            public void onClick(Game game) {
                game.selectedCharacter = "KREE";
                game.gameState = Game.State.ENTER_NAME;
            }

            @Override
            public void draw(GL gl, TextRenderer tr) {
                gl.glDisable(GL.GL_TEXTURE_2D);
                if (selectedIndex == 1) {
                    gl.glColor4f(1, 1, 0, 0.8f);
                } else {
                    gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
                }

                gl.glLineWidth(3);
                gl.glBegin(GL.GL_LINE_LOOP);
                gl.glVertex2f(x, y);
                gl.glVertex2f(x + width, y);
                gl.glVertex2f(x + width, y + height);
                gl.glVertex2f(x, y + height);
                gl.glEnd();
                gl.glLineWidth(1);

                if (charPreviews[1] != null) {
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    new TextureLoader().draw(gl, charPreviews[1], x + 20, y + 20, 160, 160);
                }

                tr.beginRendering(1280, 720);
                tr.setColor(Color.YELLOW);
                tr.draw(label, (int)x + 70, (int)(720 - y - 20));
                tr.endRendering();
            }
        });
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        if (bg == null) {
            bg = loader.load("background/bg.png");
        }

        if (bg != null) {
            loader.draw(gl, bg, 0, 0, 1280, 720);
        }

        if (charPreviews[0] == null) {
            charPreviews[0] = loader.load("sprites/0.png");
        }
        if (charPreviews[1] == null) {
            charPreviews[1] = loader.load("sprites/1.png");
        }

        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isInside(mouseX, mouseY)) {
                selectedIndex = i;
            }
        }

        textRenderer.beginRendering(1280, 720);
        textRenderer.setColor(Color.YELLOW);
        String title = "SELECT YOUR CHARACTER";
        int titleWidth = (int) textRenderer.getBounds(title).getWidth();
        textRenderer.draw(title, (1280 - titleWidth) / 2, 650);
        textRenderer.endRendering();

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).draw(gl, textRenderer);
        }

        drawCursor(gl);
    }

    private void drawCursor(GL gl) {
        Button selected = buttons.get(selectedIndex);

        float cursorX = selected.x - 40;
        float cursorY = selected.y + (selected.height / 2);

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1f, 1f, 0f);

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(cursorX + 20, cursorY);
        gl.glVertex2f(cursorX, cursorY - 15);
        gl.glVertex2f(cursorX, cursorY + 15);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    public void handleInput(int keyCode, Game game) {
        if (keyCode == java.awt.event.KeyEvent.VK_LEFT) {
            selectedIndex = 0;
        } else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT) {
            selectedIndex = 1;
        } else if (keyCode == java.awt.event.KeyEvent.VK_ENTER) {
            buttons.get(selectedIndex).onClick(game);
        } else if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            game.gameState = Game.State.MENU;
        }
    }
}