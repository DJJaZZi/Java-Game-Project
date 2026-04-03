package com.roguelike.core.dungeon;

/**
 * TileType enum - represents different tile types in the dungeon
 */
public enum TileType {
    WALL,      // Cannot pass through, solid
    FLOOR,     // Can walk on
    DOOR,      // Door between rooms
    CHEST      // Contains treasure
}
