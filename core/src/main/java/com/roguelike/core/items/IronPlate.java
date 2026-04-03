package com.roguelike.core.items;

/**
 * IronPlate - Heavy armor
 */
public class IronPlate extends Armor {
    public IronPlate() {
        super("Iron Plate", 6, ItemRarity.RARE, 400);
        this.description = "Heavy iron plate mail. Defense: 6, Health: 30";
    }
}
