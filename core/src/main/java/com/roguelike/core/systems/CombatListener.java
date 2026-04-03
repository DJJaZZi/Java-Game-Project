package com.roguelike.core.systems;

/**
 * CombatListener interface - for Observer Pattern
 * Implement to react to combat events
 */
public interface CombatListener {
    /**
     * Called when a hit lands
     */
    void onHit(com.roguelike.core.entities.Entity attacker,
               com.roguelike.core.entities.Entity defender, float damage);

    /**
     * Called on critical hit
     */
    void onCriticalHit(com.roguelike.core.entities.Entity attacker,
                       com.roguelike.core.entities.Entity defender, float damage);

    /**
     * Called when attack is dodged
     */
    void onDodge(com.roguelike.core.entities.Entity attacker,
                 com.roguelike.core.entities.Entity defender);

    /**
     * Called when entity dies
     */
    void onDefenderDeath(com.roguelike.core.entities.Entity attacker,
                         com.roguelike.core.entities.Entity defender);
}
