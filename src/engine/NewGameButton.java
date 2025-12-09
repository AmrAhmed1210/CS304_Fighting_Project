package engine;

public class NewGameButton extends Button {
    public NewGameButton(float x, float y, float width, float height) {
        super(x, y, width, height, "NEW GAME");
    }

    @Override
    public void onClick(Game game) {
        game.gameState = Game.State.ENTER_NAME;
        game.inputScreen.userName = new StringBuilder();
    }
}