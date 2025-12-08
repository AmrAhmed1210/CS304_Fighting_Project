package engine;

public class PlayerAnimator {
    public SpriteAnimator idle;
    public SpriteAnimator walk;
    public SpriteAnimator punch;

    public PlayerAnimator(String base) {
        String f = "sprites/" + base + "/";
        idle = new SpriteAnimator(f + "idle.png", 5, 50, 50);
        walk = new SpriteAnimator(f + "walk.png", 5, 50, 50);
        punch = new SpriteAnimator(f + "punch.png", 5, 50, 50);
    }

    public PlayerAnimator() {}
}
