package com.roguelike.core.game;

/*
 * ═══════════════════════════════════════════════════════════════════════════
 * GAMEMANAGER — INTEGRATION PATCH
 *
 * This file shows ONLY the changes/additions needed in your existing
 * GameManager.java.  Copy each section into the correct place.
 * ═══════════════════════════════════════════════════════════════════════════
 */

import com.roguelike.core.dungeon.LevelBounds;
import com.roguelike.core.dungeon.LevelLayoutBuilder;
import com.roguelike.core.dungeon.RoomType;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;
import com.roguelike.core.systems.ChestSystem;
import com.roguelike.core.systems.UpgradeStat;
import com.roguelike.core.systems.UpgradeSystem;

import java.util.List;

/**
 * Paste these additions into the real GameManager class.
 */
@SuppressWarnings("unused")
public class GameManagerPatch {

    // ── 1. NEW FIELDS — add alongside existing fields ─────────────────────────

    private List<LevelBounds>  layoutRooms;   // pixel-accurate room rectangles
    private LevelBounds        currentRoom;   // the room the player is currently in
    private ChestSystem        chestSystem;
    private UpgradeSystem      upgradeSystem;

    // ── 2. init() — add after existing system initialisation ─────────────────

    void init_additions() {
        // ChestSystem and UpgradeSystem do not depend on anything else
        // so they can be created here.
        chestSystem   = new ChestSystem();
        upgradeSystem = new UpgradeSystem();
        System.out.println("[GameManager] ChestSystem + UpgradeSystem ready");
    }

    // ── 3. generateNewLevel() — replace the existing method body with this ───

    void generateNewLevel_replacement(int currentFloor,
                                      com.roguelike.core.dungeon.DungeonGenerator dungeonGenerator,
                                      Player player) {
        System.out.println("[GameManager] Generating floor " + currentFloor + "...");

        // ① Build the tile-grid dungeon (unchanged)
        com.roguelike.core.dungeon.DungeonLevel currentLevel =
            dungeonGenerator.generate(currentFloor);

        // ② Build pixel-space room layout from the sketch dimensions
        layoutRooms = LevelLayoutBuilder.build();

        // ③ Spawn player at the centre of the SPAWN room (not hardcoded 10,10!)
        LevelBounds spawnRoom = layoutRooms.get(0); // always the first room
        float[] spawnCenter   = spawnRoom.center();
        int spawnTileX = (int)(spawnCenter[0] / 32); // 32 = tile size
        int spawnTileY = (int)(spawnCenter[1] / 32);

        // Find the nearest walkable tile to that position
        if (currentLevel.isWalkable(spawnTileX, spawnTileY)) {
            player.setPosition(spawnTileX, spawnTileY);
            currentLevel.placeEntity(player, spawnTileX, spawnTileY);
        } else {
            // Fallback: use first room center from the tile-grid generator
            int[] center = currentLevel.getRooms().get(0).getCenter();
            player.setPosition(center[0], center[1]);
            currentLevel.placeEntity(player, center[0], center[1]);
        }

        // ④ Give every enemy a reference to the level (unchanged)
        for (Enemy enemy : currentLevel.getEnemies()) {
            enemy.setDungeonLevel(currentLevel);
        }

        // ⑤ Place chests inside goblin rooms
        chestSystem.placeChestsForLayout(currentLevel, layoutRooms, currentFloor);

        System.out.println("[GameManager] Floor " + currentFloor + " ready");
    }

    // ── 4. update() additions — call these inside your existing update() ──────

    void update_additions(float deltaTime,
                          Player player,
                          com.roguelike.core.dungeon.DungeonLevel currentLevel) {
        // Track which room the player is currently in (pixel-space lookup)
        if (layoutRooms != null && player != null) {
            float pixelX = player.getX() * 32f;
            float pixelY = player.getY() * 32f;
            LevelBounds detected = LevelLayoutBuilder.findRoom(layoutRooms, pixelX, pixelY);
            if (detected != currentRoom) {
                currentRoom = detected;
                if (currentRoom != null) {
                    System.out.println("[GameManager] Player entered: " + currentRoom.label);
                }
            }
        }

        // Check chest interactions every frame
        if (chestSystem != null && player != null && currentLevel != null) {
            chestSystem.checkChestInteraction(player, currentLevel);
        }
    }

    // ── 5. onDefenderDeath() — add upgradeSystem call alongside existing XP ──

    void onDefenderDeath_addition(Enemy enemy) {
        // ADD this line right after player.gainExperience(...):
        if (upgradeSystem != null) {
            upgradeSystem.onEnemyKilled(enemy);
        }
    }

    // ── 6. New method: handleUpgradeInput() — call from PlayingState ──────────

    /**
     * Try to purchase an upgrade. Call this when the player presses 1-7 while
     * inside a BASE room.
     *
     * @param statIndex 0-based index into UpgradeStat.values()
     */
    boolean handleUpgradeInput(int statIndex, Player player) {
        if (currentRoom == null || currentRoom.roomType != RoomType.BASE) {
            System.out.println("[GameManager] Upgrades only available in BASE rooms!");
            return false;
        }
        UpgradeStat[] stats = UpgradeStat.values();
        if (statIndex < 0 || statIndex >= stats.length) return false;
        return upgradeSystem.tryUpgrade(stats[statIndex], player);
    }

    // ── 7. New getters — add to the existing Getters section ─────────────────

    List<LevelBounds> getLayoutRooms()    { return layoutRooms; }
    LevelBounds       getCurrentRoom()    { return currentRoom; }
    ChestSystem       getChestSystem()    { return chestSystem; }
    UpgradeSystem     getUpgradeSystem()  { return upgradeSystem; }

    boolean isInBaseRoom() {
        return currentRoom != null && currentRoom.roomType == RoomType.BASE;
    }
}
