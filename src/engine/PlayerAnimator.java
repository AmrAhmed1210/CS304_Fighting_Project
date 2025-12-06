package engine;

public class PlayerAnimator {
    public SpriteAnimator idle;
    public SpriteAnimator walk;
    public SpriteAnimator punch;

    public PlayerAnimator(String basePath) {
        String folder = "sprites/" + basePath + "/";
        idle = new SpriteAnimator(folder + "idle.png", 5, 50, 50);
        walk = new SpriteAnimator(folder + "walk.png", 5, 50, 50);
        punch = new SpriteAnimator(folder + "punch.png", 5, 50, 50);
    }

    public PlayerAnimator() {
    }
}