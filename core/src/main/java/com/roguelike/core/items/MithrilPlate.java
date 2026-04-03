package com.roguelike.core.items;

/**
 * MithrilPlate - Legendary armor
 */
public class MithrilPlate extends Armor {
    public MithrilPlate() {
        super("Mithril Plate", 10, ItemRarity.LEGENDARY, 1000);
        this.description = "Mythical mithril armor. Defense: 10, Health: 50";
    }
}
