package com.roguelike.core.items;

/**
 * Sword - Common weapon
 */
public class Sword extends Weapon {
    public Sword() {
        super("Iron Sword", 15, ItemRarity.COMMON, 200);
        this.description = "A reliable iron sword. Damage: 15";
    }
}
