package com.roguelike.core.ai;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;
import com.roguelike.core.dungeon.DungeonLevel;

/**
 * AIStrategy interface - Strategy Pattern
 */
public interface AIStrategy {
    void execute(Enemy enemy, Player player, DungeonLevel level, float deltaTime);
    int[] decideNextMove(Enemy enemy, Player player, DungeonLevel level);
    String getAIName();
}

