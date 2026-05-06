package com.roguelike.core.dungeon;

import com.roguelike.core.ai.SmartAI;
import com.roguelike.core.entities.Enemy;

public class OrcFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(int x, int y, int floor) {
        float health = 80 + (floor * 15f);
        Enemy enemy = new Enemy(x, y, "orc", health, new SmartAI());
        enemy.setLevel(floor);
        return enemy;
    }
}
