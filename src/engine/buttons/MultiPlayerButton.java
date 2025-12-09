package engine.buttons;

import engine.Button;
import engine.Game;

public class MultiPlayerButton extends Button {
    public MultiPlayerButton(float x, float y, float width, float height) {
        super(x, y, width, height, "MULTIPLAYER");
    }

    @Override
    public void onClick(Game game) {
        game.vsComputer = false;
        game.gameState = Game.State.PLAYING;
        game.p1.playerName = "PLAYER 1";
        game.p2.playerName = "PLAYER 2";
        game.p1.x = 400;
        game.p2.x = 800;
        game.gameOver = false;
        game.winnerName = "";
        game.winTimer = 0;
    }
}