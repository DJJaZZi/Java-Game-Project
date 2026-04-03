package com.roguelike.core.ai;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

/**
 * RangedAI - Keeps distance and attacks from range
 */
public class RangedAI implements AIStrategy {
    private static final int ATTACK_RANGE = 5;
    private static final int SAFE_DISTANCE = 3;
    private static final int DETECTION_RANGE = 12;

    @Override
    public void execute(Enemy enemy, Player player, DungeonLevel level, float deltaTime) {
        if (enemy == null || player == null) return;

        int distance = enemy.getDistance(player);

        if (distance <= DETECTION_RANGE) {
            if (distance <= ATTACK_RANGE && distance > SAFE_DISTANCE) {
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

        int distance = enemy.getDistance(player);

        if (distance < SAFE_DISTANCE) {
            int dx = Integer.compare(enemy.getX(), player.getX());
            int dy = Integer.compare(enemy.getY(), player.getY());

            if (level.isWalkable(enemy.getX() + dx, enemy.getY() + dy)) {
                return new int[]{dx, dy};
            }
        }

        if (distance > ATTACK_RANGE) {
            int dx = Integer.compare(player.getX(), enemy.getX());
            int dy = Integer.compare(player.getY(), enemy.getY());

            if (level.isWalkable(enemy.getX() + dx, enemy.getY() + dy)) {
                return new int[]{dx, dy};
            }
        }

        return new int[]{0, 0};
    }

    @Override
    public String getAIName() {
        return "RangedAI";
    }
}
