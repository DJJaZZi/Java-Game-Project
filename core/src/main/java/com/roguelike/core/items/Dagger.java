package com.roguelike.core.items;

/**
 * Dagger - Fast weapon
 */
public class Dagger extends Weapon {
    public Dagger() {
        super("Dagger", 8, ItemRarity.COMMON, 100);
        this.description = "A quick dagger. Damage: 8";
        this.critChance = 0.3f;  // Very high crit chance
    }
}
