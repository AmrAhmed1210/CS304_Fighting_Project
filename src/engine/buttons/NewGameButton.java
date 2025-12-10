package engine.buttons;

import engine.Button;
import engine.Game;

public class NewGameButton extends Button {
    public NewGameButton(float x, float y, float width, float height) {
        super(x, y, width, height, "NEW GAME");
    }

    @Override
    public void onClick(Game game) {
        game.soundManager.stopStartSound();
        game.vsComputer = true;
        game.gameState = Game.State.CHARACTER_SELECT;
        game.inputScreen.userName = new StringBuilder();
        game.selectedCharacter = "BEE";
        game.resetSoundFlags();
    }
}