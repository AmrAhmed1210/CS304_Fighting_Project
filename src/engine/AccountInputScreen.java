package engine;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class AccountInputScreen {
    public StringBuilder userName = new StringBuilder();
    public ArrayList<Button> buttons = new ArrayList<>();
    TextRenderer inputRenderer;

    public int selectedIndex = 0;

    public Button doneBtn;
    public Button cancelBtn;

    public AccountInputScreen() {
        inputRenderer = new TextRenderer(new Font("Monospaced", Font.BOLD, 40), true, true);


        float doneX = 415;
        float doneY = 470;
        float btnW = 200;
        float btnH = 60;

        float cancelX = 665;
        float cancelY = 470;

        doneBtn = new DoneButton(doneX, doneY, btnW, btnH);
        cancelBtn = new CancelButton(cancelX, cancelY, btnW, btnH);

        buttons.add(doneBtn);
        buttons.add(cancelBtn);
    }

    public void draw(GL gl, TextureLoader loader, int mouseX, int mouseY) {
        Texture bg = loader.load("background/bg_newaccount.png");
        if (bg != null) {
            loader.draw(gl, bg, 0, 0, 1280, 720);
        }

        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isInside(mouseX, mouseY)) {
                selectedIndex = i;
            }
        }

        drawCursor(gl);

        drawInputText(gl);
    }

    private void drawInputText(GL gl) {
        float inputBoxY = 375;

        boolean showCursor = (System.currentTimeMillis() / 500) % 2 == 0;
        String displayString = userName.toString() + (showCursor ? "|" : "");

        inputRenderer.beginRendering(1280, 720);
        inputRenderer.setColor(Color.WHITE);

        int textStartX = 365;

        inputRenderer.draw(displayString, textStartX, (int)inputBoxY);
        inputRenderer.endRendering();
    }

    private void drawCursor(GL gl) {
        if (buttons.isEmpty()) return;

        Button selected = buttons.get(selectedIndex);

        float cursorX = selected.x - 35;
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