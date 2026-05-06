package com.roguelike.core.dungeon;

import com.roguelike.core.ai.AggressiveAI;
import com.roguelike.core.entities.Enemy;

public class GoblinFactory implements EnemyFactory {
    @Override
    public Enemy createEnemy(int x, int y, int floor) {
        float health = 20 + (floor * 5f);
        Enemy enemy = new Enemy(x, y, "goblin", health, new AggressiveAI());
        enemy.setLevel(floor);
        return enemy;
    }
}
