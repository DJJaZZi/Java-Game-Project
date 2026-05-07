package com.roguelike.core.dungeon;

import com.roguelike.core.items.Item;

/**
 * ChestItem — pairs an Item with a pixel-space position inside a room.
 *
 * Chests are rendered as orange squares (TileType.CHEST) at (tileX, tileY).
 * When the player steps on or interacts with the tile, the item is added to
 * their inventory and the chest is marked as looted.
 */
public class ChestItem {

    /** Tile-grid coordinates of this chest inside the dungeon. */
    public final int tileX, tileY;

    /** The item inside this chest. */
    public final Item item;

    /** False until the player opens this chest. */
    public boolean looted = false;

    public ChestItem(int tileX, int tileY, Item item) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.item  = item;
    }

    @Override
    public String toString() {
        return String.format("Chest[%d,%d] %s (%s)",
            tileX, tileY, item.getName(), looted ? "looted" : "sealed");
    }
}
