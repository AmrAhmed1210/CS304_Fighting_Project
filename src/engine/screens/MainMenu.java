package engine.screens;

import engine.Button;
import engine.TextureLoader;
import engine.buttons.*;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.Font;
import java.util.ArrayList;

public class MainMenu {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int selectedIndex = 0;
    TextRenderer textRenderer;

    public MainMenu() {
        textRenderer = new TextRenderer(new Font("Impact", Font.BOLD, 50), true, true);

        float centerX = (1280 - 300) / 2;
        float startY = 270;
        float gap = 70;
        float btnWidth = 300;
        float btnHeight = 40;

        buttons.add(new NewGameButton(centerX, startY, btnWidth, btnHeight));
        buttons.add(new MultiPlayerButton(centerX, startY + gap, btnWidth, btnHeight));
        buttons.add(new ContinueButton(centerX, startY + (gap * 2), btnWidth, btnHeight));
        buttons.add(new OptionsButton(centerX, startY + (gap * 3), btnWidth, btnHeight));
        buttons.add(new HowToPlayButton(centerX, startY + (gap * 4), btnWidth, btnHeight));
        buttons.add(new CreditsButton(centerX, startY + (gap * 5), btnWidth, btnHeight));
        buttons.add(new ExitButton(centerX, startY + (gap * 6), btnWidth, btnHeight));
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        Texture menuBg = loader.load("background/bg_menu.png");
        if (menuBg != null) {
            loader.draw(gl, menuBg, 0, 0, 1280, 720);
        }

        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isInside(mouseX, mouseY)) {
                selectedIndex = i;
            }
        }

        drawCursor(gl);
    }

    private void drawCursor(GL gl) {
        if (buttons.isEmpty()) return;

        Button selected = buttons.get(selectedIndex);

        double rawWidth = textRenderer.getBounds(selected.label).getWidth();

        float estimatedPixelWidth = (float) (rawWidth * 1.4);

        float screenCenterX = 1280f / 2f;

        float textStartX = screenCenterX+10 - (estimatedPixelWidth / 2f);

        float cursorX = textStartX - 50;

        float cursorY = selected.y + (selected.height / 2);

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1f, 1f, 0f);

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(cursorX + 20, cursorY);
        gl.glVertex2f(cursorX, cursorY - 15);
        gl.glVertex2f(cursorX, cursorY + 15);
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
    }
}