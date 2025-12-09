package engine;

import entities.Player;

public class AIController {
    private Player aiPlayer;
    private Player humanPlayer;
    private int actionTimer = 0;
    private int decisionDelay = 30;

    public AIController(Player aiPlayer, Player humanPlayer) {
        this.aiPlayer = aiPlayer;
        this.humanPlayer = humanPlayer;
    }

    public void update() {
        if (aiPlayer.defeated || humanPlayer.defeated) {
            resetControls();
            return;
        }

        actionTimer--;
        if (actionTimer > 0) return;

        makeDecision();
        actionTimer = decisionDelay + (int)(Math.random() * 20);
    }

    private void makeDecision() {
        resetControls();

        float distance = Math.abs(humanPlayer.x - aiPlayer.x);

        if (distance > 300) {
            approachPlayer();
        } else if (distance > 150) {
            maintainDistance();
        } else {
            attackPlayer();
        }
    }

    private void approachPlayer() {
        if (humanPlayer.x < aiPlayer.x) {
            aiPlayer.left = true;
        } else {
            aiPlayer.right = true;
        }

        if (Math.random() > 0.5 && humanPlayer.y < aiPlayer.y) {
            aiPlayer.up = true;
        } else if (Math.random() > 0.5 && humanPlayer.y > aiPlayer.y) {
            aiPlayer.down = true;
        }
    }

    private void maintainDistance() {
        if (humanPlayer.x < aiPlayer.x - 50) {
            aiPlayer.right = true;
        } else if (humanPlayer.x > aiPlayer.x + 50) {
            aiPlayer.left = true;
        }

        if (Math.random() > 0.7) {
            aiPlayer.attack = true;
        }
    }

    private void attackPlayer() {
        if (Math.random() > 0.8 && aiPlayer.specialCooldown >= 180) {
            aiPlayer.special = true;
        } else if (Math.random() > 0.6) {
            aiPlayer.attack = true;
        }

        if (humanPlayer.y < aiPlayer.y) {
            aiPlayer.up = true;
        } else if (humanPlayer.y > aiPlayer.y) {
            aiPlayer.down = true;
        }
    }

    private void resetControls() {
        aiPlayer.left = false;
        aiPlayer.right = false;
        aiPlayer.up = false;
        aiPlayer.down = false;
        aiPlayer.attack = false;
        aiPlayer.special = false;
    }
}