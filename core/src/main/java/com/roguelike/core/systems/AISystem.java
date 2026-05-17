package com.roguelike.core.systems;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.EntityState;
import com.roguelike.core.entities.Player;

import java.util.List;

public class AISystem {

    private static final float AI_UPDATE_RATE = 0.5f;
    private float timeSinceLastUpdate = 0;

    // ← ИЗМЕНЕНО: теперь принимает CombatSystem чтобы наносить урон
    private final CombatSystem combatSystem;

    public AISystem(CombatSystem combatSystem) {
        this.combatSystem = combatSystem;
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
            // ── GUARD 1: враг реагирует только если игрок в его комнате ──────
            if (!enemy.isPlayerInMyRoom(player)) return;

            int[] move = enemy.getNextMove(player, level);
            int dx = move[0];
            int dy = move[1];

            int newX = enemy.getX() + dx;
            int newY = enemy.getY() + dy;

            // Враг рядом с игроком → атакует (наносит реальный урон)
            if (newX == player.getX() && newY == player.getY()) {
                if (enemy.canAttack()) {
                    combatSystem.attack(enemy, player); // ← реальный урон
                }
                return;
            }

            // Стоит на месте, но рядом с игроком → тоже атакует
            if (dx == 0 && dy == 0) {
                if (enemy.getDistance(player) <= 1 && enemy.canAttack()) {
                    combatSystem.attack(enemy, player);
                }
                return;
            }

            // ── GUARD 2: враг не выходит из своей комнаты ────────────────────
            if (!enemy.isInsideHome(newX, newY)) return;

            // Движение
            if (level.isWalkable(newX, newY)) {
                level.moveEntity(enemy, newX, newY);
                enemy.setState(EntityState.MOVING);
                enemy.startMoving();

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
