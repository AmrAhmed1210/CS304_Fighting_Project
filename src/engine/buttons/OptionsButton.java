package engine.buttons;

import engine.Button;
import engine.Game;

public class OptionsButton extends Button {
    public OptionsButton(float x, float y, float width, float height) {
        super(x, y, width, height, "OPTIONS");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        System.out.println("Open Options Screen");
    }
}