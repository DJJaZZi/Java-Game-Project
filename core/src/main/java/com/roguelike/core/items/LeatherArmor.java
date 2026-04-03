package com.roguelike.core.items;

/**
 * LeatherArmor - Basic armor
 */
public class LeatherArmor extends Armor {
    public LeatherArmor() {
        super("Leather Armor", 3, ItemRarity.COMMON, 150);
        this.description = "Light leather armor. Defense: 3, Health: 15";
    }
}
