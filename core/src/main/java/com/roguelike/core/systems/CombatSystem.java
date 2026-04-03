package com.roguelike.core.systems;

import com.roguelike.core.entities.Player;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.dungeon.DungeonLevel;
import java.util.ArrayList;
import java.util.List;

/**
 * CombatSystem - Handles all combat mechanics
 *
 * Features:
 * - Player vs Enemy combat
 * - Damage calculation with stats
 * - Combat events (hit, miss, critical, etc.)
 * - Command Pattern for recording actions
 */
public class CombatSystem {
    private List<CombatListener> listeners;
    private boolean debugMode = true;

    // Combat statistics
    private static final float CRITICAL_CHANCE = 0.2f;      // 20% crit chance
    private static final float CRITICAL_MULTIPLIER = 1.5f;  // 50% more damage on crit
    private static final float DODGE_CHANCE = 0.1f;         // 10% dodge chance

    /**
     * Constructor
     */
    public CombatSystem() {
        this.listeners = new ArrayList<>();
        System.out.println("[CombatSystem] Initialized");
    }

    /**
     * Handle player attacking an enemy
     */
    public void handlePlayerAttack(Player player, DungeonLevel level) {
        // Find adjacent enemies
        List<Enemy> adjacentEnemies = getAdjacentEnemies(player, level);

        if (adjacentEnemies.isEmpty()) {
            if (debugMode) {
                System.out.println("[CombatSystem] No enemy to attack!");
            }
            return;
        }

        // Attack first adjacent enemy (can be improved to select target)
        Enemy target = adjacentEnemies.get(0);
        attack(player, target);
    }

    /**
     * Attack another entity
     */
    public void attack(com.roguelike.core.entities.Entity attacker,
                       com.roguelike.core.entities.Entity defender) {
        // Check if attacker can attack (cooldown)
        if (!attacker.canAttack()) {
            if (debugMode) {
                System.out.println("[CombatSystem] " + attacker.getName() + " is on cooldown!");
            }
            return;
        }

        // Check for dodge
        if (Math.random() < DODGE_CHANCE) {
            onDodge(attacker, defender);
            return;
        }

        // Calculate damage
        float damage = calculateDamage(attacker, defender);

        // Check for critical hit
        boolean isCritical = Math.random() < CRITICAL_CHANCE;
        if (isCritical) {
            damage *= CRITICAL_MULTIPLIER;
        }

        // Deal damage
        defender.takeDamage(damage);

        // Start attack cooldown
        attacker.startAttack();

        // Notify listeners
        for (CombatListener listener : listeners) {
            if (isCritical) {
                listener.onCriticalHit(attacker, defender, damage);
            } else {
                listener.onHit(attacker, defender, damage);
            }
        }

        if (debugMode) {
            String critText = isCritical ? " (CRITICAL!)" : "";
            System.out.println("[CombatSystem] " + attacker.getName() + " hits " +
                defender.getName() + " for " + damage + " damage" + critText);
        }

        // Check if defender is dead
        if (defender.isDead()) {
            onDefenderDeath(attacker, defender);
        }
    }

    /**
     * Calculate damage based on attacker stats and defender defense
     */
    private float calculateDamage(com.roguelike.core.entities.Entity attacker,
                                  com.roguelike.core.entities.Entity defender) {
        float baseDamage = attacker.getAttackDamage();
        float defenseReduction = defender.getDefense();

        // Damage formula: Base damage reduced by defense
        float actualDamage = Math.max(1, baseDamage - defenseReduction);

        // Add some randomness (±20%)
        float variance = (float)Math.random() * 0.4f - 0.2f; // -0.2 to +0.2
        actualDamage = actualDamage * (1 + variance);

        return actualDamage;
    }

    /**
     * Called when an attack is dodged
     */
    private void onDodge(com.roguelike.core.entities.Entity attacker,
                         com.roguelike.core.entities.Entity defender) {
        // Defender dodges the attack
        defender.startAttack(); // Start cooldown anyway

        // Notify listeners
        for (CombatListener listener : listeners) {
            listener.onDodge(attacker, defender);
        }

        if (debugMode) {
            System.out.println("[CombatSystem] " + defender.getName() + " dodged " +
                attacker.getName() + "'s attack!");
        }
    }

    /**
     * Called when a defender dies
     */
    private void onDefenderDeath(com.roguelike.core.entities.Entity attacker,
                                 com.roguelike.core.entities.Entity defender) {
        if (debugMode) {
            System.out.println("[CombatSystem] " + defender.getName() + " has been defeated!");
        }

        // Notify listeners
        for (CombatListener listener : listeners) {
            listener.onDefenderDeath(attacker, defender);
        }

        // Handle special logic if attacker is player
        if (attacker instanceof Player && defender instanceof Enemy) {
            Player player = (Player) attacker;
            Enemy enemy = (Enemy) defender;

            // Give rewards to player
            player.gainExperience(enemy.getExperienceReward());
            player.addGold(enemy.getGoldReward());
        }
    }

    /**
     * Get all enemies adjacent to an entity
     */
    private List<Enemy> getAdjacentEnemies(Player player, DungeonLevel level) {
        List<Enemy> adjacent = new ArrayList<>();

        // Check all 8 directions
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int checkX = player.getX() + dx;
                int checkY = player.getY() + dy;

                // Check if any enemy is at this position
                for (Enemy enemy : level.getEnemies()) {
                    if (enemy.isAlive() && enemy.getX() == checkX && enemy.getY() == checkY) {
                        adjacent.add(enemy);
                    }
                }
            }
        }

        return adjacent;
    }

    /**
     * Register a combat listener
     */
    public void addListener(CombatListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a combat listener
     */
    public void removeListener(CombatListener listener) {
        listeners.remove(listener);
    }

    /**
     * Set debug mode
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
}

