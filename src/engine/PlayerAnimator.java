package engine;

public class PlayerAnimator {
    public SpriteAnimator idle;
    public SpriteAnimator walk;
    public SpriteAnimator punch;
    public SpriteAnimator hit;
    public SpriteAnimator defeat;

    public PlayerAnimator(String base) {
        String f = "sprites/" + base + "/";


        idle = new SpriteAnimator(f + "idle.png", 5);
        walk = new SpriteAnimator(f + "walk.png", 5);
        punch = new SpriteAnimator(f + "punch.png", 5);


        hit = new SpriteAnimator(f + "hit.png", 5);
        defeat = new SpriteAnimator(f + "defeat.png", 4);
    }

    public PlayerAnimator() {}
}