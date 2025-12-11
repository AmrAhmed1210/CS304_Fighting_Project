package engine.buttons;

import engine.Button;
import engine.Game;

public class BackButton extends Button {
    public BackButton(float x, float y, float width, float height) {
        super(x, y, width, height, "BACK");
    }

    @Override
    public void onClick(Game game) {
        game.gameState = Game.State.MENU;
    }
}