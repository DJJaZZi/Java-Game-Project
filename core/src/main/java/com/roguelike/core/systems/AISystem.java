package com.roguelike.core.systems;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;
import com.roguelike.core.entities.EntityState;

import java.util.List;

public class AISystem {

    private static final float AI_UPDATE_RATE = 0.5f; // seconds between AI ticks
    private float timeSinceLastUpdate = 0;

    public AISystem() {
        System.out.println("[AISystem] Initialized");
    }

    public void update(List<Enemy> enemies, Player player, float deltaTime) {
        timeSinceLastUpdate += deltaTime;
        if (timeSinceLastUpdate < AI_UPDATE_RATE) return;
        timeSinceLastUpdate = 0;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            updateEnemyAI(enemy, player, enemy.getDungeonLevel());
        }
    }

    private void updateEnemyAI(Enemy enemy, Player player, DungeonLevel level) {
        if (level == null || player == null) return;

        try {
            int[] move = enemy.getNextMove(player, level);
            int dx = move[0];
            int dy = move[1];

            if (dx == 0 && dy == 0) {
                // No move — check if we should attack
                if (enemy.getDistance(player) <= 1 && enemy.canAttack()) {
                    enemy.startAttack();
                }
                return;
            }

            int newX = enemy.getX() + dx;
            int newY = enemy.getY() + dy;

            // Check if target tile has the player — attack instead of move
            if (newX == player.getX() && newY == player.getY()) {
                if (enemy.canAttack()) {
                    enemy.startAttack();
                    // Damage is handled by CombatSystem elsewhere
                }
                return;
            }

            if (dx > 0) enemy.setFacingLeft(true);   // moving left → no flip
            if (dx < 0) enemy.setFacingLeft(false);    // moving right → flip

            // Actually move — updates BOTH entity coords AND tile grid
            if (level.isWalkable(newX, newY)) {
                level.moveEntity(enemy, newX, newY);
                enemy.setState(EntityState.MOVING);
                enemy.startMoving();

                // Face direction of movement
                if (dx < 0) enemy.setFacingLeft(true);
                if (dx > 0) enemy.setFacingLeft(false);
            }

        } catch (Exception e) {
            System.err.println("[AISystem] Error updating " + enemy.getName() + ": " + e.getMessage());
        }
    }

    public void setDebugMode(boolean enabled) {}
    public void changeEnemyAI(Enemy enemy, com.roguelike.core.ai.AIStrategy newAI) {
        enemy.setAIStrategy(newAI);
    }
}
