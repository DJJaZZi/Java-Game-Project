package com.roguelike.core.items;

/**
 * ItemFactory - Creates items (Factory Pattern)
 */
public class ItemFactory {
    /**
     * Create random item of given rarity
     */
    public Item createRandomItem(ItemRarity rarity) {
        double roll = Math.random();

        switch (rarity) {
            case COMMON:
                if (roll < 0.33) {
                    return new HealthPotion();
                } else if (roll < 0.66) {
                    return new Sword();
                } else {
                    return new LeatherArmor();
                }

            case UNCOMMON:
                if (roll < 0.33) {
                    return new ManaPotion();
                } else if (roll < 0.66) {
                    return new Dagger();
                } else {
                    return new LeatherArmor();
                }

            case RARE:
                if (roll < 0.33) {
                    return new HealthPotion();
                } else if (roll < 0.66) {
                    return new Greatsword();
                } else {
                    return new IronPlate();
                }

            case LEGENDARY:
                if (roll < 0.5) {
                    return new Greatsword();
                } else {
                    return new MithrilPlate();
                }

            default:
                return new HealthPotion();
        }
    }

    /**
     * Create specific item by ID
     */
    public Item createItem(String itemId) {
        switch (itemId.toLowerCase()) {
            case "health_potion":
                return new HealthPotion();
            case "mana_potion":
                return new ManaPotion();
            case "iron_sword":
                return new Sword();
            case "greatsword":
                return new Greatsword();
            case "dagger":
                return new Dagger();
            case "leather_armor":
                return new LeatherArmor();
            case "iron_plate":
                return new IronPlate();
            case "mithril_plate":
                return new MithrilPlate();
            default:
                System.err.println("[ItemFactory] Unknown item: " + itemId);
                return null;
        }
    }

    /**
     * Create common item (weak)
     */
    public Item createCommonItem() {
        return createRandomItem(ItemRarity.COMMON);
    }

    /**
     * Create uncommon item (medium)
     */
    public Item createUncommonItem() {
        return createRandomItem(ItemRarity.UNCOMMON);
    }

    /**
     * Create rare item (strong)
     */
    public Item createRareItem() {
        return createRandomItem(ItemRarity.RARE);
    }

    /**
     * Create legendary item (very strong)
     */
    public Item createLegendaryItem() {
        return createRandomItem(ItemRarity.LEGENDARY);
    }
}
