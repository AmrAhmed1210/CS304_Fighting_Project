package engine;

import entities.Player;

public class AIController {
    private Player aiPlayer;
    private Player humanPlayer;
    private int actionTimer = 0;
    private int decisionDelay = 30;
    private int level;

    public AIController(Player aiPlayer, Player humanPlayer, int level) {
        this.aiPlayer = aiPlayer;
        this.humanPlayer = humanPlayer;
        this.level = level;

        // صعوبة AI تزيد مع كل مستوى
        switch(level) {
            case 1: decisionDelay = 40; break; // أبطأ
            case 2: decisionDelay = 35; break;
            case 3: decisionDelay = 30; break; // متوسط
            case 4: decisionDelay = 25; break;
            case 5: decisionDelay = 20; break; // سريع
            case 6: decisionDelay = 15; break; // سريع جداً
            default: decisionDelay = 30;
        }
    }

    public void update() {
        if (aiPlayer.defeated || humanPlayer.defeated) {
            resetControls();
            return;
        }

        actionTimer--;
        if (actionTimer > 0) return;

        makeDecision();
        actionTimer = decisionDelay + (int)(Math.random() * 10);
    }

    private void makeDecision() {
        resetControls();

        float distance = Math.abs(humanPlayer.x - aiPlayer.x);

        // AI يصبح أكثر ذكاءً مع زيادة المستوى
        float aggression = 0.5f + (level * 0.1f); // 0.6 إلى 1.1
        if (aggression > 0.9f) aggression = 0.9f;

        float defense = 0.3f - (level * 0.04f); // 0.26 إلى 0.06
        if (defense < 0.1f) defense = 0.1f;

        if (distance > 300) {
            approachPlayer(aggression);
        } else if (distance > 150) {
            maintainDistance(aggression, defense);
        } else {
            attackPlayer(aggression);
        }

        // في المستويات العليا، AI يهرب من الهجمات
        if (level >= 4 && Math.random() < 0.3) {
            dodgeAttacks();
        }
    }

    private void approachPlayer(float aggression) {
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

        // في المستويات العليا، يهجم أثناء الاقتراب
        if (level >= 3 && Math.random() < 0.3) {
            aiPlayer.attack = true;
        }
    }

    private void maintainDistance(float aggression, float defense) {
        float moveThreshold = 50 - (level * 5);
        if (moveThreshold < 20) moveThreshold = 20;

        if (humanPlayer.x < aiPlayer.x - moveThreshold) {
            aiPlayer.right = true;
        } else if (humanPlayer.x > aiPlayer.x + moveThreshold) {
            aiPlayer.left = true;
        }

        if (Math.random() < aggression) {
            aiPlayer.attack = true;
        }

        // في المستويات العليا، يستخدم الهجمات الخاصة
        if (level >= 4 && Math.random() < 0.2 && aiPlayer.specialCooldown >= 180) {
            aiPlayer.special = true;
        }
    }

    private void attackPlayer(float aggression) {
        // زيادة احتمالية الهجوم مع زيادة المستوى
        if (Math.random() < (0.3 + level * 0.1) && aiPlayer.specialCooldown >= 180) {
            aiPlayer.special = true;
        } else if (Math.random() < aggression) {
            aiPlayer.attack = true;
        }

        // حركة دفاعية أثناء الهجوم
        if (Math.random() > 0.7 && humanPlayer.y < aiPlayer.y) {
            aiPlayer.up = true;
        } else if (Math.random() > 0.7 && humanPlayer.y > aiPlayer.y) {
            aiPlayer.down = true;
        }
    }

    private void dodgeAttacks() {
        // يحاول الهروب إذا كان قريباً جداً
        if (Math.abs(humanPlayer.x - aiPlayer.x) < 100) {
            if (humanPlayer.x < aiPlayer.x) {
                aiPlayer.right = true;
            } else {
                aiPlayer.left = true;
            }
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