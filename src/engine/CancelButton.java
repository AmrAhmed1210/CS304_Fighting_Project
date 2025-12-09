package engine;

public class CancelButton extends Button {
    public CancelButton(float x, float y, float width, float height) {
        super(x, y, width, height, "Cancel");
    }

    @Override
    public void onClick(Game game) {
        game.gameState = Game.State.MENU;
    }
}