package engine.buttons;

import engine.Button;
import engine.Game;

public class ContinueButton extends Button {
    public ContinueButton(float x, float y, float width, float height) {
        super(x, y, width, height, "CONTINUE");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        game.gameState = Game.State.PLAYING;
        game.resetSoundFlags();
    }
}