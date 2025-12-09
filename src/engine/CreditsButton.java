package engine;

public class CreditsButton extends Button {
    public CreditsButton(float x, float y, float width, float height) {
        super(x, y, width, height, "CREDITS");
    }

    @Override
    public void onClick(Game game) {
        System.out.println("Show Credits");
    }
}