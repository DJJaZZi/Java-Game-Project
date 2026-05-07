package com.roguelike.core.dungeon;

/**
 * RoomType — classifies each room in the level layout.
 *
 * Used by:
 *   - LevelBounds  : to attach the correct pixel size
 *   - UpgradeSystem: to know when the player is in a BASE room
 *   - EnemyFactory : to spawn the right enemies per room type
 *   - ChestSystem  : to decide which loot table to use
 */
public enum RoomType {
    /** Starting room — no enemies, player spawns here. */
    SPAWN,
    /** Combat room with goblins and possible chests. */
    GOBLIN,
    /** Narrow connector — may contain patrol enemies or traps. */
    CORRIDOR,
    /** Safe room between combat sections — upgrade shop lives here. */
    BASE,
    /** Final arena — boss fight, no chests until boss is dead. */
    BOSS
}
