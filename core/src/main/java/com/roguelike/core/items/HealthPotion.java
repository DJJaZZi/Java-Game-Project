package com.roguelike.core.items;

/**
 * HealthPotion - Restores health
 */
public class HealthPotion extends Item {
    private int healAmount;

    public HealthPotion() {
        super("Health Potion", ItemType.POTION, ItemRarity.COMMON, 50, true);
        this.healAmount = 50;
        this.description = "Restores 50 HP";
    }

    @Override
    public void use() {
        System.out.println("[Item] Health potion used! Restores " + healAmount + " HP");
    }

    public int getHealAmount() {
        return healAmount;
    }
}
