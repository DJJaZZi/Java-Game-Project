package com.roguelike.core.items;

/**
 * Item - Abstract base class for all items
 */
public abstract class Item {
    protected String name;
    protected String description;
    protected ItemType type;
    protected ItemRarity rarity;
    protected int value;
    protected boolean consumable;

    public Item(String name, ItemType type, ItemRarity rarity, int value, boolean consumable) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.value = value;
        this.consumable = consumable;
        this.description = "";
    }

    /**
     * Use this item
     */
    public abstract void use();

    /**
     * Get item info string
     */
    public String getInfoString() {
        return String.format("%s [%s] - Value: %d gold", name, rarity, value);
    }

    // Getters
    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public int getValue() {
        return value;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public String getDescription() {
        return description;
    }
}
