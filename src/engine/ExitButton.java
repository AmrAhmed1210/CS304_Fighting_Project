package engine;

public class ExitButton extends Button {
    public ExitButton(float x, float y, float width, float height) {
        super(x, y, width, height, "EXIT");
    }

    @Override
    public void onClick(Game game) {
        System.exit(0);
    }
}