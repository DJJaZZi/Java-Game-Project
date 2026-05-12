package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private final Random random;

    // Grid dimensions — used only for bounds checking in spawnIfWalkable
    private static final int WIDTH  = 500; // large enough for the full fixed layout
    private static final int HEIGHT = 60;

    private final EnemyFactory goblinFactory = new GoblinFactory();
    private final EnemyFactory orcFactory    = new OrcFactory();

    public DungeonGenerator() {
        this.random = new Random();
    }

    // =========================================================================
    //  MAIN ENTRY POINT
    // =========================================================================

    public DungeonLevel generate(int floorNumber) {
        System.out.println("[DungeonGenerator] Generating fixed layout floor " + floorNumber);

        // 1. Build the fixed room sequence
        FixedDungeonLayout layoutBuilder = new FixedDungeonLayout();
        List<RoomLayout> roomLayouts = layoutBuilder.build();

        // 2. Convert pixel layout → tile grid
        TileMapBuilder tileBuilder = new TileMapBuilder();
        int gridW = tileBuilder.calcGridWidth(roomLayouts);
        int gridH = tileBuilder.calcGridHeight(roomLayouts);
        Tile[][] tiles = tileBuilder.build(roomLayouts, gridW, gridH);

        // 3. Convert RoomLayouts → Room objects (used for spawn positions + exit)
        List<Room> rooms = new ArrayList<>();
        for (RoomLayout rl : roomLayouts) {
            int rx = rl.worldX / 32;
            int ry = Math.max(0, rl.worldY / 32);
            int rw = rl.pixelWidth  / 32;
            int rh = rl.pixelHeight / 32;
            rooms.add(new Room(rx, ry, rw, rh));
        }

        // 4. Spawn enemies in the correct rooms
        List<Enemy> enemies = spawnEnemiesInLayout(floorNumber, roomLayouts, tiles, gridW, gridH);

        // 5. Build the level object
        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);
        level.setRoomLayouts(roomLayouts);                      // renderer needs this
        level.setExitRoom(rooms.get(rooms.size() - 1));        // last room = exit

        System.out.println("[DungeonGenerator] Floor " + floorNumber + " complete! "
            + rooms.size() + " rooms, " + enemies.size() + " enemies.");
        return level;
    }

    // =========================================================================
    //  ENEMY SPAWNING
    // =========================================================================

    private List<Enemy> spawnEnemiesInLayout(int floor,
                                             List<RoomLayout> layouts,
                                             Tile[][] tiles,
                                             int gridW, int gridH) {
        List<Enemy> enemies = new ArrayList<>();

        for (RoomLayout rl : layouts) {
            if (rl.type == RoomLayout.RoomImageType.GOBLIN) {
                // 2 goblins per goblin room
                int tileX = rl.worldX / 32;
                int tileY = Math.max(1, rl.worldY / 32);
                int tileW = rl.pixelWidth  / 32;
                int tileH = rl.pixelHeight / 32;

                for (int i = 0; i < 2; i++) {
                    int spawnX = tileX + 3 + random.nextInt(Math.max(1, tileW - 4));
                    int spawnY = tileY + 2 + random.nextInt(Math.max(1, tileH - 3));
                    spawnIfWalkable(goblinFactory, spawnX, spawnY,
                        floor, tiles, enemies, gridW, gridH);
                }

            } else if (rl.type == RoomLayout.RoomImageType.BOSS) {
                // 1 orc at the center of the boss room
                int spawnX = (rl.worldX / 32) + (rl.pixelWidth  / 32) / 2;
                int spawnY = Math.max(1, (rl.worldY / 32) + (rl.pixelHeight / 32) / 2);
                spawnIfWalkable(orcFactory, spawnX, spawnY,
                    floor, tiles, enemies, gridW, gridH);
            }
        }

        return enemies;
    }

    /**
     * Only spawns an enemy if the tile exists, is a FLOOR, and has no occupant.
     */
    private void spawnIfWalkable(EnemyFactory factory,
                                 int x, int y, int floor,
                                 Tile[][] tiles, List<Enemy> out,
                                 int gridW, int gridH) {
        if (x < 0 || x >= gridW || y < 0 || y >= gridH) return;

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
