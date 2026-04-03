package com.roguelike.core.items;

/**
 * Weapon - Base class for weapons
 */
public class Weapon extends Item {
    protected int damage;
    protected float critChance;

    public Weapon(String name, int damage, ItemRarity rarity, int value) {
        super(name, ItemType.WEAPON, rarity, value, false);
        this.damage = damage;
        this.critChance = 0.1f;  // 10% crit
    }

    @Override
    public void use() {
        System.out.println("[Item] " + name + " equipped! Damage: " + damage);
    }

    public int getDamage() {
        return damage;
    }

    public float getCritChance() {
        return critChance;
    }
}

