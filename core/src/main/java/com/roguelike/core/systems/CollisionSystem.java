package com.roguelike.core.systems;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.entities.Player;
import com.roguelike.core.entities.Enemy;

import java.util.ArrayList;
import java.util.List;


// CollisionSystem - Handles all collision detection and response
//
// Uses Observer Pattern:
// - Detects collisions
// - Notifies listeners of collision events
//
// Collision Types:
// - Player-Enemy collision (combat)
// - Player-Item collision (pickup)
// - Entity-Wall collision (blocking)

public class CollisionSystem {
    private List<CollisionListener> listeners;
    private boolean debugMode = false;

    // Constructor
    public CollisionSystem(){
        this.listeners = new ArrayList<>();
        System.out.println("[CollisionSystem] Initialized");
    }

    // Check all collisions in the level
    public void checkCollisions(Player player, DungeonLevel level) {
        // Check player-enemy collisions
        checkPlayerEnemyCollisions(player, level);

    }

    // Check collisions between player and all enemies
    private void checkPlayerEnemyCollisions(Player player, DungeonLevel level) {
        for (Enemy enemy : level.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }

            // Check if player and enemy are at same position
            if (player.getX() == enemy.getX() && player.getY() == enemy.getY()) {
                onCollision(player, enemy);
            }
        }
    }


    // Handle collision between two entities
    private void onCollision(Player player, Enemy enemy) {
        if (debugMode) {
            System.out.println("[CollisionSystem] Collision detected: " + player.getName() + " <-> " + enemy.getName());
        }

        // Notify all listeners
        for (CollisionListener listener : listeners) {
            listener.onPlayerEnemyCollision(player, enemy);
        }
    }


    // Register a collision listener
    public void addListener(CollisionListener listener) {
        listeners.add(listener);
    }


    // Remove a collision listener
    public void removeListener(CollisionListener listener) {
        listeners.remove(listener);
    }


    // Set debug mode
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
}


// CollisionListener interface - for Observer Pattern
// Implement this to react to collision events

