package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Entity;

/**
 * Tile - represents a single grid cell in the dungeon
 */
public class Tile {
    public TileType type;
    public Entity occupant;      // Entity standing on this tile
    public boolean explored;     // Fog of war tracking

    public Tile(TileType type) {
        this.type = type;
        this.occupant = null;
        this.explored = false;
    }

    /**
     * Check if this tile can be walked on
     */
    public boolean isWalkable() {
        // A floor tile is walkable even if occupied — combat handles the occupant case
        return type == TileType.FLOOR || type == TileType.DOOR;
    }

    public boolean isPassable() {
        // True only if walkable AND no occupant blocking the path
        return isWalkable() && occupant == null;
    }
        /**
         * Check if this tile blocks vision
         */
    public boolean blocksVision() {
        return type == TileType.WALL;
    }

    /**
     * Set entity on this tile
     */
    public void setOccupant(Entity entity) {
        this.occupant = entity;
    }

    /**
     * Remove entity from this tile
     */
    public void clearOccupant() {
        this.occupant = null;
    }

    /**
     * Mark as explored (for fog of war)
     */
    public void setExplored(boolean explored) {
        this.explored = explored;
    }
}
