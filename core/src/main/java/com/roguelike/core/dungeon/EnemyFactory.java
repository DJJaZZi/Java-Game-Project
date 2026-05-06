package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;

public interface EnemyFactory {
    Enemy createEnemy(int x, int y, int floor);
}
