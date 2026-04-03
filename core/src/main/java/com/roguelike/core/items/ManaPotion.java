package com.roguelike.core.items;

/**
 * ManaPo tion - Restores mana
 */
public class ManaPotion extends Item {
    private int manaAmount;

    public ManaPotion() {
        super("Mana Potion", ItemType.POTION, ItemRarity.COMMON, 40, true);
        this.manaAmount = 50;
        this.description = "Restores 50 Mana";
    }

    @Override
    public void use() {
        System.out.println("[Item] Mana potion used! Restores " + manaAmount + " Mana");
    }

    public int getManaAmount() {
        return manaAmount;
    }
}
