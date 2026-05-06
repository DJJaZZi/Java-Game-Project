package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemFactory;
import com.roguelike.core.items.Item;
import com.roguelike.core.items.ItemRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {
    private Random random;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private static final int ROOM_COUNT_BASE = 5;
    private static final int MIN_ROOM_SIZE = 6;
    private static final int MAX_ROOM_SIZE = 14;

    private final EnemyFactory goblinFactory = new GoblinFactory();
    private final EnemyFactory orcFactory    = new OrcFactory();

    public DungeonGenerator() {
        this.random = new Random();
    }

    public DungeonLevel generate(int floorNumber) {
        System.out.println("[DungeonGenerator] Generating floor " + floorNumber + "...");

        Tile[][] tiles = generateTiles();
        List<Room> rooms = generateRooms();
        carveRooms(tiles, rooms);
        connectRooms(tiles, rooms);
        List<Enemy> enemies = spawnEnemies(floorNumber, rooms, tiles);

        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);

        if (!rooms.isEmpty()) {
            level.setExitRoom(rooms.get(rooms.size() - 1));
        }

        System.out.println("[DungeonGenerator] Floor complete!");
        return level;
    }

    private Tile[][] generateTiles() {
        Tile[][] tiles = new Tile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(TileType.WALL);
            }
        }
        return tiles;
    }

    private List<Room> generateRooms() {
        List<Room> rooms = new ArrayList<>();
        int attempts = 0;

        while (rooms.size() < ROOM_COUNT_BASE && attempts < 100) {
            int width  = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE);
            int height = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE);
            int x = 2 + random.nextInt(WIDTH  - width  - 4);
            int y = 2 + random.nextInt(HEIGHT - height - 4);

            Room newRoom = new Room(x, y, width, height);
            boolean overlaps = false;

            for (Room existing : rooms) {
                if (roomsOverlap(newRoom, existing)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) rooms.add(newRoom);
            attempts++;
        }

        System.out.println("[DungeonGenerator] Generated " + rooms.size() + " rooms");
        return rooms;
    }

    private boolean roomsOverlap(Room r1, Room r2) {
        int padding = 2;
        return r1.x < r2.x + r2.width  + padding &&
            r1.x + r1.width  + padding > r2.x &&
            r1.y < r2.y + r2.height + padding &&
            r1.y + r1.height + padding > r2.y;
    }

    private void carveRooms(Tile[][] tiles, List<Room> rooms) {
        for (Room room : rooms) {
            for (int x = room.x; x < room.x + room.width; x++) {
                for (int y = room.y; y < room.y + room.height; y++) {
                    if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                        tiles[x][y] = new Tile(TileType.FLOOR);
                    }
                }
            }
        }
    }

    private void connectRooms(Tile[][] tiles, List<Room> rooms) {
        if (rooms.size() < 2) return;
        rooms.sort((r1, r2) -> Integer.compare(r1.x, r2.x));
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room.createCorridor(rooms.get(i), rooms.get(i + 1), tiles);
        }
    }

    private List<Enemy> spawnEnemies(int floor, List<Room> rooms, Tile[][] tiles) {
        List<Enemy> enemies = new ArrayList<>();

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            boolean isBossRoom = (i == rooms.size() - 1);

            if (isBossRoom) {
                // Last room always gets one orc boss, spawned at room center
                int[] pos = room.getCenter();
                spawnIfWalkable(orcFactory, pos[0], pos[1], floor, tiles, enemies);
            } else {
                // Every other room gets 1-2 goblins
                int count = 1 + random.nextInt(2);
                for (int j = 0; j < count; j++) {
                    int[] pos = room.getRandomPosition();
                    spawnIfWalkable(goblinFactory, pos[0], pos[1], floor, tiles, enemies);
                }
            }
        }

        System.out.println("[DungeonGenerator] Spawned " + enemies.size() +
            " enemies for floor " + floor);
        return enemies;
    }

    /**
     * Only spawns an enemy if the tile is a floor tile with no existing occupant.
     * Fixes the tile-stacking bug from before.
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

    private ItemRarity getRandomRarity(int floor) {
        double roll = random.nextDouble();
        double legendaryChance = 0.01 * floor;
        double rareChance      = 0.05 * floor;

        if (roll < legendaryChance)              return ItemRarity.LEGENDARY;
        if (roll < legendaryChance + rareChance) return ItemRarity.RARE;
        if (roll < legendaryChance + rareChance + 0.3) return ItemRarity.UNCOMMON;
        return ItemRarity.COMMON;
    }

    public static int getWidth()  { return WIDTH; }
    public static int getHeight() { return HEIGHT; }
}
