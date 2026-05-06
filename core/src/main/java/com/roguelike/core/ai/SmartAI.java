package com.roguelike.core.ai;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

import java.util.List;

/**
 * SmartAI - Uses pathfinding to chase player
 */
public class SmartAI implements AIStrategy {
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
        List<int[]> path = level.findPath(enemy.getX(), enemy.getY(), player.getX(), player.getY());
        if (!path.isEmpty()) {
            int[] next = path.get(0);
            return new int[]{next[0] - enemy.getX(), next[1] - enemy.getY()};
        }
        return new int[]{0, 0};
    }

    @Override
    public String getAIName() {
        return "SmartAI";
    }
}
