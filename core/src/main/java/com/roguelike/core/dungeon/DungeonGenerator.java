package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private final Random random;

    // Grid dimensions — used only for bounds checking in spawnIfWalkable
    private static final int WIDTH  = WalkableMapBuilder.GRID_WIDTH;   // 166
    private static final int HEIGHT = WalkableMapBuilder.GRID_HEIGHT;  // 10


    private final EnemyFactory goblinFactory = new GoblinFactory();
    private final EnemyFactory orcFactory    = new OrcFactory();

    public DungeonGenerator() {
        this.random = new Random();
    }

    // =========================================================================
    //  MAIN ENTRY POINT
    // =========================================================================

    public DungeonLevel generate(int floorNumber) {
        System.out.println("[DungeonGenerator] Generating floor " + floorNumber);

        // 1. Build collision tile grid from relevels.png pixel data
        WalkableMapBuilder walkableBuilder = new WalkableMapBuilder();
        Tile[][] tiles = walkableBuilder.build();

        // 2. Build the RoomLayout list — renderer needs this to draw the PNG rooms
        FixedDungeonLayout layoutBuilder = new FixedDungeonLayout();
        List<RoomLayout> roomLayouts = layoutBuilder.build();

        // 3. One room covering the whole walkable map
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(0, 0, WalkableMapBuilder.GRID_WIDTH, WalkableMapBuilder.GRID_HEIGHT));

        // 4. Spawn enemies
        List<Enemy> enemies = spawnEnemiesOnMap(floorNumber, tiles);

        // 5. Build level — MUST call setRoomLayouts so TileRenderer can draw the PNGs
        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);
        level.setRoomLayouts(roomLayouts);  // ← this is what makes rooms visible
        level.setExitRoom(rooms.get(0));

        System.out.println("[DungeonGenerator] Floor " + floorNumber + " ready. "
            + roomLayouts.size() + " rooms, " + enemies.size() + " enemies.");
        return level;
    }

    // =========================================================================
    //  ENEMY SPAWNING
    // =========================================================================

    private List<Enemy> spawnEnemiesOnMap(int floor, Tile[][] tiles) {
        List<Enemy> enemies = new ArrayList<>();

        // Goblin positions — spread across the map (adjust X positions to match your rooms)
        int[][] goblinSpawns = {
            {20, 6}, {22, 7},   // goblin room 1
            {45, 5}, {47, 7},   // goblin room 2
            {70, 6}, {72, 6},   // goblin room 3
            {110, 5}, {112, 7}, // goblin room 4
        };

        for (int[] pos : goblinSpawns) {
            spawnIfWalkable(goblinFactory, pos[0], pos[1], floor, tiles, enemies);
        }

        // Orc boss — far right (boss room)
        spawnIfWalkable(orcFactory, 158, 6, floor, tiles, enemies);

        return enemies;
    }

    /**
     * Only spawns an enemy if the tile exists, is a FLOOR, and has no occupant.
     */
    private void spawnIfWalkable(EnemyFactory factory, int x, int y,
                                 int floor, Tile[][] tiles, List<Enemy> out) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;
        Tile tile = tiles[x][y];
        if (tile == null || tile.type != TileType.FLOOR || tile.occupant != null) return;

        Enemy enemy = factory.createEnemy(x, y, floor);
        tile.setOccupant(enemy);
        out.add(enemy);
    }

    // =========================================================================
    //  UTILITIES
    // =========================================================================

    private ItemRarity getRandomRarity(int floor) {
        double roll            = random.nextDouble();
        double legendaryChance = 0.01 * floor;
        double rareChance      = 0.05 * floor;

        if (roll < legendaryChance)                        return ItemRarity.LEGENDARY;
        if (roll < legendaryChance + rareChance)           return ItemRarity.RARE;
        if (roll < legendaryChance + rareChance + 0.3)     return ItemRarity.UNCOMMON;
        return ItemRarity.COMMON;
    }

    public static int getWidth()  { return WIDTH; }
    public static int getHeight() { return HEIGHT; }
}
