package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private final Random random;

    // Grid dimensions — used only for bounds checking in spawnIfWalkable
    private static final int WIDTH  = 169; // 5392 / 32 = 168.5 → 169
    private static final int HEIGHT = 14;  // 416  / 32 = 13    → 14

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

        // Tile grid — all floor, PNG handles visuals
        Tile[][] tiles = new Tile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                tiles[x][y] = new Tile(TileType.FLOOR);

        // One big room covering the whole map
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(0, 0, WIDTH, HEIGHT));

        // Spawn enemies across the map
        List<Enemy> enemies = spawnEnemiesOnMap(floorNumber, tiles);

        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);
        level.setExitRoom(rooms.get(0));
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
