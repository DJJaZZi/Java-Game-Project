package com.roguelike.core.systems;

import com.roguelike.core.dungeon.ChestItem;
import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.LevelBounds;
import com.roguelike.core.dungeon.RoomType;
import com.roguelike.core.dungeon.Tile;
import com.roguelike.core.dungeon.TileType;
import com.roguelike.core.entities.Player;
import com.roguelike.core.items.Dagger;
import com.roguelike.core.items.HealthPotion;
import com.roguelike.core.items.IronPlate;
import com.roguelike.core.items.Item;
import com.roguelike.core.items.ItemFactory;
import com.roguelike.core.items.ItemRarity;
import com.roguelike.core.items.ManaPotion;
import com.roguelike.core.items.Sword;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ChestSystem — responsible for:
 *  1. Placing chests in goblin rooms (at design-time, when the level is built).
 *  2. Checking if the player steps on a CHEST tile each frame.
 *  3. Opening the chest: adding the item to the player's inventory and
 *     replacing the tile with a FLOOR tile so it can't be looted twice.
 *
 * HOW TO USE
 * ──────────
 * // In GameManager.generateNewLevel():
 *   chestSystem = new ChestSystem();
 *   chestSystem.placeChestsForLayout(currentLevel, layoutRooms, currentFloor);
 *
 * // In PlayingState.update() or CollisionSystem:
 *   chestSystem.checkChestInteraction(player, currentLevel);
 */
public class ChestSystem {

    private final List<ChestItem> chests = new ArrayList<>();
    private final ItemFactory itemFactory = new ItemFactory();
    private final Random random = new Random();

    // ── Placement ─────────────────────────────────────────────────────────────

    /**
     * Places chests in every GOBLIN room according to the sketch.
     * Each goblin room gets 1-2 chests at fixed relative positions.
     *
     * @param level      the current DungeonLevel (tile grid)
     * @param rooms      all LevelBounds for this level (from LevelLayoutBuilder)
     * @param floor      current floor number — higher floor = better loot
     */
    public void placeChestsForLayout(DungeonLevel level,
                                     List<LevelBounds> rooms,
                                     int floor) {
        chests.clear();

        for (LevelBounds room : rooms) {
            if (room.roomType != RoomType.GOBLIN) continue;

            // Convert room pixel position → tile coordinates.
            // Assumes tile size = 32 px (must match GameRenderer.tileSize).
            int tileSize = 32;
            int originTileX = (int)(room.x / tileSize);
            int originTileY = (int)(room.y / tileSize);
            int roomTilesW  = (int)(room.width  / tileSize);
            int roomTilesH  = (int)(room.height / tileSize);

            // Place 1-2 chests per goblin room, away from walls (2-tile margin)
            int chestCount = 1 + random.nextInt(2); // 1 or 2 chests
            for (int i = 0; i < chestCount; i++) {
                // Distribute chests evenly across the room width
                int relX = 2 + (i + 1) * (roomTilesW / (chestCount + 1));
                int relY = roomTilesH / 2; // vertically centred

                int tileX = originTileX + relX;
                int tileY = originTileY + relY;

                Item loot = rollLoot(floor);
                placeChest(level, tileX, tileY, loot);
            }
        }

        System.out.println("[ChestSystem] Placed " + chests.size() + " chests on floor " + floor);
    }

    /**
     * Manually place a chest at a specific tile with a specific item.
     * Useful for scripted/fixed placements.
     */
    public void placeChest(DungeonLevel level, int tileX, int tileY, Item item) {
        Tile tile = level.getTile(tileX, tileY);
        if (tile == null || tile.type != TileType.FLOOR) {
            System.out.println("[ChestSystem] Cannot place chest at (" + tileX + "," + tileY
                + ") — tile is not a floor tile.");
            return;
        }

        tile.type = TileType.CHEST; // Rendered as orange square by GameRenderer
        ChestItem chest = new ChestItem(tileX, tileY, item);
        chests.add(chest);
        System.out.println("[ChestSystem] Placed chest: " + chest);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    /**
     * Call every frame (or every update tick).
     * If the player is standing on a CHEST tile, open it.
     */
    public void checkChestInteraction(Player player, DungeonLevel level) {
        int px = player.getX();
        int py = player.getY();

        Tile tile = level.getTile(px, py);
        if (tile == null || tile.type != TileType.CHEST) return;

        // Find the matching ChestItem
        for (ChestItem chest : chests) {
            if (chest.tileX == px && chest.tileY == py && !chest.looted) {
                openChest(chest, player, level);
                return;
            }
        }
    }

    /** Opens a chest: gives item to player, converts tile to FLOOR. */
    private void openChest(ChestItem chest, Player player, DungeonLevel level) {
        chest.looted = true;

        Tile tile = level.getTile(chest.tileX, chest.tileY);
        if (tile != null) tile.type = TileType.FLOOR;

        player.pickUpItem(chest.item);
        System.out.println("[ChestSystem] " + player.getName()
            + " opened a chest and found: " + chest.item.getInfoString());
    }

    // ── Loot table ────────────────────────────────────────────────────────────

    /**
     * Returns a random item based on the current floor.
     * Higher floor = greater chance of rare/uncommon items.
     */
    private Item rollLoot(int floor) {
        double roll = random.nextDouble();

        // Boss-room quality on floor 5+
        if (floor >= 5) {
            if (roll < 0.3)  return new IronPlate();
            if (roll < 0.6)  return new Sword();
            return new HealthPotion();
        }

        // Mid-game floors 3-4
        if (floor >= 3) {
            if (roll < 0.25) return new Sword();
            if (roll < 0.45) return new IronPlate();
            if (roll < 0.70) return new HealthPotion();
            return new ManaPotion();
        }

        // Early floors 1-2
        if (roll < 0.40) return new HealthPotion();
        if (roll < 0.65) return new ManaPotion();
        if (roll < 0.85) return new Dagger();
        return new Sword();
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public List<ChestItem> getChests() { return chests; }

    public int getLootedCount() {
        int n = 0;
        for (ChestItem c : chests) if (c.looted) n++;
        return n;
    }
}
