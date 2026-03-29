package com.roguelike.core.systems;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;
import com.roguelike.core.dungeon.DungeonLevel;
import java.util.List;


//  AISystem - Orchestrates all enemy AI behaviors
//
//  Responsibilities:
//  - Update all enemy AI each frame
//  - Execute AI decisions (movement, attacks)
//  - Handle AI-related events
//  - Manage AI debugging/logging

public class AISystem {
    private boolean debugMode = true; // Set to false to reduce console spam
    private static final float AI_UPDATE_RATE = 0.1f; // Update AI every 0.1 seconds
    private float timeSinceLastUpdate = 0;

    /**
     * Constructor
     */
    public AISystem() {
        System.out.println("[AISystem] Initialized");
    }

    /**
     * Update all enemy AI each frame
     */
    public void update(List<Enemy> enemies, Player player, float deltaTime) {
        // Accumulate time
        timeSinceLastUpdate += deltaTime;

        // Only update AI at a fixed rate (not every frame for performance)
        if (timeSinceLastUpdate < AI_UPDATE_RATE) {
            return;
        }
        timeSinceLastUpdate = 0;

        // Update each enemy
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            // Execute AI behavior
            updateEnemyAI(enemy, player, enemy.getDungeonLevel());
        }
    }

    /**
     * Update a single enemy's AI
     */
    private void updateEnemyAI(Enemy enemy, Player player, DungeonLevel level) {
        if (level == null) {
            return;
        }

        try {
            // Execute AI strategy
            enemy.executeAI(player, level, AI_UPDATE_RATE);
        } catch (Exception e) {
            System.err.println("[AISystem] Error updating AI for " + enemy.getName());
            e.printStackTrace();
        }
    }

    /**
     * Get AI information for debugging
     */
    public String getEnemyAIInfo(Enemy enemy) {
        return enemy.getName() + " | AI: " + enemy.getAIStrategy().getAIName() +
            " | Pos: (" + enemy.getX() + ", " + enemy.getY() + ")";
    }

    /**
     * Print all enemy AI states (debugging)
     */
    public void debugPrintAllAI(List<Enemy> enemies) {
        if (!debugMode) return;

        System.out.println("=== ENEMY AI DEBUG ===");
        for (Enemy enemy : enemies) {
            System.out.println(getEnemyAIInfo(enemy));
        }
        System.out.println("=======================");
    }

    /**
     * Enable/disable debug mode
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    /**
     * Change AI for an enemy
     */
    public void changeEnemyAI(Enemy enemy, com.roguelike.core.ai.AIStrategy newAI) {
        System.out.println("[AISystem] Changing AI for " + enemy.getName() + " to " + newAI.getAIName());
        enemy.setAIStrategy(newAI);
    }

    /**
     * Get total distance all enemies are from player
     */
    public int getTotalEnemyDistance(List<Enemy> enemies, Player player) {
        int total = 0;
        for (Enemy enemy : enemies) {
            total += enemy.getDistance(player);
        }
        return total;
    }
}

// Helper class to store AI debug information
public class AIDebugInfo {
    public String enemyName;
    public String aiType;
    public int x, y;
    public int playerDistance;
    public boolean canAttack;

    public AIDebugInfo(Enemy enemy, Player player) {
        this.enemyName = enemy.getName();
        this.aiType = enemy.getAIStrategy().getAIName();
        this.x = enemy.getX();
        this.y = enemy.getY();
        this.playerDistance = enemy.getDistance(player);
        this.canAttack = enemy.canAttack();
    }

    @Override
    public String toString() {
        return String.format(
            "%s (%s) at [%d,%d] | Dist: %d | CanAttack: %s",
            enemyName, aiType, x, y, playerDistance, canAttack
        );
    }
}
