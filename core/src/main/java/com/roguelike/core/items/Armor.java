package com.roguelike.core.items;

/**
 * Armor - Defense equipment
 */
public class Armor extends Item {
    protected int defense;
    protected int healthBonus;

    public Armor(String name, int defense, ItemRarity rarity, int value) {
        super(name, ItemType.ARMOR, rarity, value, false);
        this.defense = defense;
        this.healthBonus = defense * 5;  // Extra health from armor
    }

    @Override
    public void use() {
        System.out.println("[Item] " + name + " equipped! Defense: " + defense);
    }

    public int getDefense() {
        return defense;
    }

    public int getHealthBonus() {
        return healthBonus;
    }
}
