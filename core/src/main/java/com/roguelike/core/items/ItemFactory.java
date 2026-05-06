package com.roguelike.core.items;

public class ItemFactory {

    public Item createRandomItem(ItemRarity rarity) {
        double roll = Math.random();
        switch (rarity) {
            case COMMON:
                return roll < 0.5 ? new HealthPotion() : new Sword();
            case RARE:
                return roll < 0.5 ? new HealthPotion() : new LeatherArmor();
            default:
                return new HealthPotion();
        }
    }

    public Item createItem(String itemId) {
        switch (itemId.toLowerCase()) {
            case "health_potion":  return new HealthPotion();
            case "sword":          return new Sword();
            case "leather_armor":  return new LeatherArmor();
            default:
                System.err.println("[ItemFactory] Unknown item: " + itemId);
                return null;
        }
    }
}
