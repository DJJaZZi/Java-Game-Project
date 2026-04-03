package com.roguelike.core.items;

/**
 * Greatsword - Powerful weapon
 */
public class Greatsword extends Weapon {
    public Greatsword() {
        super("Greatsword", 25, ItemRarity.RARE, 500);
        this.description = "A massive two-handed sword. Damage: 25";
        this.critChance = 0.2f;  // Higher crit chance
    }
}
