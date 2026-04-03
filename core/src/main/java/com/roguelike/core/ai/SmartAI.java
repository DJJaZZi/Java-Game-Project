package com.roguelike.core.ai;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

/**
 * SmartAI - Uses pathfinding to chase player
 */
class SmartAI implements AIStrategy {
    private static final int DETECTION_RANGE = 15;
    private static final int ATTACK_RANGE = 1;
    private static final int PATHFINDING_UPDATE_RATE = 0;

    private float timeSincePathUpdate = 0;
    private int[] currentPath;
    private int currentPathStep = 0;

    @Override
    public void execute(Enemy enemy, Player player, DungeonLevel level, float deltaTime) {
        if (enemy == null || player == null) return;

        int distance = enemy.getDistance(player);

        if (distance <= DETECTION_RANGE) {
            if (distance <= ATTACK_RANGE) {
                if (enemy.canAttack()) {
                    enemy.startAttack();
                }
            } else {
                int[] move = decideNextMove(enemy, player, level);
                if (move[0] != 0 || move[1] != 0) {
                    enemy.moveTo(enemy.getX() + move[0], enemy.getY() + move[1]);
                }
            }
        }
    }

    @Override
    public int[] decideNextMove(Enemy enemy, Player player, DungeonLevel level) {
        if (enemy == null || player == null || level == null) {
            return new int[]{0, 0};
        }

        int dx = Integer.compare(player.getX(), enemy.getX());
        int dy = Integer.compare(player.getY(), enemy.getY());

        if (level.isWalkable(enemy.getX() + dx, enemy.getY() + dy)) {
            return new int[]{dx, dy};
        }

        if (dx != 0 && level.isWalkable(enemy.getX() + dx, enemy.getY())) {
            return new int[]{dx, 0};
        }
        if (dy != 0 && level.isWalkable(enemy.getX(), enemy.getY() + dy)) {
            return new int[]{0, dy};
        }

        return new int[]{0, 0};
    }

    @Override
    public String getAIName() {
        return "SmartAI";
    }
}
