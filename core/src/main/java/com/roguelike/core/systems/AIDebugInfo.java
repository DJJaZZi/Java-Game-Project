package com.roguelike.core.systems;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

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
