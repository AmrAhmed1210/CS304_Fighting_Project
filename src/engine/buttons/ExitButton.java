package engine.buttons;

import engine.Button;
import engine.Game;

public class ExitButton extends Button {
    public ExitButton(float x, float y, float width, float height) {
        super(x, y, width, height, "EXIT");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        System.exit(0);
    }
}