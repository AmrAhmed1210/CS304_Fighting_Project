package engine.buttons;

import engine.Button;
import engine.Game;

public class HowToPlayButton extends Button {
    public HowToPlayButton(float x, float y, float width, float height) {
        super(x, y, width, height, "HOW TO PLAY");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        game.gameState = Game.State.HOW_TO_PLAY;
    }
}