package com.roguelike.core.entities;

import com.roguelike.core.ai.AIStrategy;

/**
 * Enemy class - extends Entity with AI
 *
 * Features:
 * - AI Strategy pattern for different behaviors
 * - Experience and gold rewards
 * - Multiple enemy types
 * - Configurable stats based on type
 */
public class Enemy extends Entity {
    private AIStrategy aiStrategy;
    private int experienceReward;
    private int goldReward;
    private String enemyType;
    private int level;

    /**
     * Constructor for Enemy
     */
    public Enemy(int x, int y, String enemyType, float health, AIStrategy aiStrategy) {
        super(x, y, health);
        this.enemyType = enemyType;
        this.aiStrategy = aiStrategy;
        this.name = "Enemy_" + enemyType;
        this.level = 1;

        configureByType(enemyType);

        System.out.println("[Enemy] Spawned " + enemyType + " at (" + x + ", " + y + ") with AI: " + aiStrategy.getAIName());
    }

    /**
     * Configure enemy stats based on type
     */
    private void configureByType(String type) {
        switch (type.toLowerCase()) {
            case "goblin":
                this.experienceReward = 50;
                this.goldReward = 25;
                this.attackDamage = 8;
                this.defense = 1;
                break;

            case "orc":
                this.experienceReward = 100;
                this.goldReward = 50;
                this.attackDamage = 15;
                this.defense = 3;
                break;

            case "skeleton":
                this.experienceReward = 75;
                this.goldReward = 30;
                this.attackDamage = 10;
                this.defense = 2;
                break;

            case "dragon":
                this.experienceReward = 500;
                this.goldReward = 200;
                this.attackDamage = 25;
                this.defense = 5;
                break;

            case "zombie":
                this.experienceReward = 40;
                this.goldReward = 15;
                this.attackDamage = 6;
                this.defense = 0;
                break;

            case "spider":
                this.experienceReward = 60;
                this.goldReward = 35;
                this.attackDamage = 12;
                this.defense = 1;
                break;

            default:
                this.experienceReward = 50;
                this.goldReward = 25;
                this.attackDamage = 10;
                this.defense = 1;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive) {
            return;
        }

        // Update movement
        updateMovement(deltaTime);
        // Update attack cooldown
        updateAttack(deltaTime);
        // AI executes decision each frame
    }

    /**
     * Set or swap AI strategy at runtime (Strategy Pattern)
     */
    public void setAIStrategy(AIStrategy newStrategy) {
        if (newStrategy != null) {
            this.aiStrategy = newStrategy;
            System.out.println("[Enemy] " + name + " AI changed to: " + aiStrategy.getAIName());
        }
    }

    /**
     * Get the current AI strategy
     */
    public AIStrategy getAIStrategy() {
        return aiStrategy;
    }

    /**
     * Make a decision using current AI
     */
    public int[] getNextMove(com.roguelike.core.entities.Player player, com.roguelike.core.dungeon.DungeonLevel level) {
        if (aiStrategy != null) {
            return aiStrategy.decideNextMove(this, player, level);
        }
        return new int[]{0, 0};
    }

    /**
     * Execute AI behavior
     */
    public void executeAI(com.roguelike.core.entities.Player player, com.roguelike.core.dungeon.DungeonLevel level, float deltaTime) {
        if (aiStrategy != null && isAlive) {
            aiStrategy.execute(this, player, level, deltaTime);
        }
    }

    /**
     * Create a copy of this enemy (useful for spawning same type)
     */
    public Enemy createCopy(int newX, int newY) {
        Enemy copy = new Enemy(newX, newY, this.enemyType, this.maxHealth, this.aiStrategy);
        return copy;
    }

    /**
     * Get enemy information as string (for debugging)
     */
    public String getInfoString() {
        return String.format(
            "Enemy: %s | Type: %s | Level: %d | HP: %.0f/%.0f | AI: %s",
            name, enemyType, level, health, maxHealth, aiStrategy.getAIName()
        );
    }

    // ==================== Getters ====================

    public String getEnemyType() {
        return enemyType;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        // Scale stats by level
        this.maxHealth *= (1 + (level * 0.1f));
        this.health = maxHealth;
        this.attackDamage = (int)(attackDamage * (1 + (level * 0.2f)));
        this.experienceReward = (int)(experienceReward * (1 + (level * 0.5f)));
        this.goldReward = (int)(goldReward * (1 + (level * 0.5f)));
    }
}
