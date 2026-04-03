package com.roguelike.core.systems;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

public interface CollisionListener {
    // Called when player collides with enemy
    void onPlayerEnemyCollision(Player player, Enemy enemy);
}
