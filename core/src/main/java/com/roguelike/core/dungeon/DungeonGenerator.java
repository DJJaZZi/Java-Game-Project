package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemFactory;
import com.roguelike.core.items.Item;
import com.roguelike.core.ai.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DungeonGenerator - Factory Pattern implementation
 * Procedurally generates dungeon levels with rooms, enemies, and items
 *
 * Features:
 * - Room generation with random placement
 * - Corridor creation between rooms
 * - Enemy spawning based on floor difficulty
 * - Loot distribution
 * - Difficulty scaling
 */
public class DungeonGenerator {
    private Random random;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private static final int ROOM_COUNT_BASE = 5;
    private static final int MIN_ROOM_SIZE = 6;
    private static final int MAX_ROOM_SIZE = 14;

    public DungeonGenerator() {
        this.random = new Random();
    }

    /**
     * Generate a complete dungeon level
     */
    public DungeonLevel generate(int floorNumber) {
        System.out.println("[DungeonGenerator] Generating floor " + floorNumber + "...");

        // 1. Create base tilemap (all walls)
        Tile[][] tiles = generateTiles();

        // 2. Generate rooms
        List<Room> rooms = generateRooms();

        // 3. Carve rooms into tilemap
        carveRooms(tiles, rooms);

        // 4. Connect rooms with corridors
        connectRooms(tiles, rooms);

        // 5. Spawn enemies based on floor difficulty
        List<Enemy> enemies = spawnEnemies(floorNumber, rooms);

        // 6. Create the level
        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);

        // 7. Set exit room (usually the last room)
        if (!rooms.isEmpty()) {
            level.setExitRoom(rooms.get(rooms.size() - 1));
        }

        System.out.println("[DungeonGenerator] Floor complete!");
        return level;
    }

    /**
     * Generate base tilemap (all walls)
     */
    private Tile[][] generateTiles() {
        Tile[][] tiles = new Tile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = new Tile(TileType.WALL);
            }
        }

        return tiles;
    }

    /**
     * Generate random rooms
     */
    private List<Room> generateRooms() {
        List<Room> rooms = new ArrayList<>();

        // Determine number of rooms based on difficulty
        int roomCount = ROOM_COUNT_BASE;

        // Try to place rooms with no overlap
        int attempts = 0;
        int maxAttempts = 100;

        while (rooms.size() < roomCount && attempts < maxAttempts) {
            // Random room dimensions
            int width = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE);
            int height = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE);

            // Random position (with padding)
            int x = 2 + random.nextInt(WIDTH - width - 4);
            int y = 2 + random.nextInt(HEIGHT - height - 4);

            Room newRoom = new Room(x, y, width, height);

            // Check for overlap with existing rooms
            boolean overlaps = false;
            for (Room existing : rooms) {
                if (roomsOverlap(newRoom, existing)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                rooms.add(newRoom);
            }

            attempts++;
        }

        System.out.println("[DungeonGenerator] Generated " + rooms.size() + " rooms");
        return rooms;
    }

    /**
     * Check if two rooms overlap with padding
     */
    private boolean roomsOverlap(Room r1, Room r2) {
        int padding = 2;

        return r1.x < r2.x + r2.width + padding &&
            r1.x + r1.width + padding > r2.x &&
            r1.y < r2.y + r2.height + padding &&
            r1.y + r1.height + padding > r2.y;
    }

    /**
     * Carve rooms into the tilemap
     */
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

        System.out.println("[DungeonGenerator] Carved " + rooms.size() + " rooms");
    }

    /**
     * Connect rooms with corridors
     */
    private void connectRooms(Tile[][] tiles, List<Room> rooms) {
        if (rooms.size() < 2) return;

        // Sort rooms by x position for consistent connection
        rooms.sort((r1, r2) -> Integer.compare(r1.x, r2.x));

        // Connect each room to the next
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room.createCorridor(rooms.get(i), rooms.get(i + 1), tiles);
        }

        System.out.println("[DungeonGenerator] Connected " + (rooms.size() - 1) + " corridors");
    }

    /**
     * Spawn enemies based on floor difficulty (Factory Pattern + Strategy Pattern)
     */
    private List<Enemy> spawnEnemies(int floor, List<Room> rooms) {
        List<Enemy> enemies = new ArrayList<>();

        // Calculate difficulty: more enemies on deeper floors
        int baseEnemyCount = 2 + (floor - 1);  // 2, 3, 4, etc.
        int enemiesPerRoom = Math.min(baseEnemyCount, 5);

        // Spawn enemies in random rooms
        for (Room room : rooms) {
            // Random number of enemies in this room
            int roomEnemyCount = 1 + random.nextInt(Math.min(enemiesPerRoom, 3));

            for (int i = 0; i < roomEnemyCount; i++) {
                // Random position in room
                int[] pos = room.getRandomPosition();
                int x = pos[0];
                int y = pos[1];

                // Create enemy based on floor
                Enemy enemy = createEnemyForFloor(floor, x, y);
                if (enemy != null) {
                    enemies.add(enemy);
                }
            }
        }

        System.out.println("[DungeonGenerator] Spawned " + enemies.size() + " enemies for floor " + floor);
        return enemies;
    }

    /**
     * Factory method: Create appropriate enemy for floor level
     */
    private Enemy createEnemyForFloor(int floor, int x, int y) {
        String[] enemyTypes = {"goblin", "zombie", "skeleton", "orc", "spider"};
        String selectedType = enemyTypes[(floor - 1) % enemyTypes.length];

        // Select AI based on floor difficulty
        AIStrategy ai;
        float health;

        switch (floor) {
            case 1:
                // Easy: Patrol AI
                health = 20;
                ai = new PatrolAI(new int[]{0, 0, 2, 0, 2, 2, 0, 2});
                break;

            case 2:
            case 3:
                // Medium: Aggressive AI
                health = 35 + (floor - 2) * 10;
                ai = new AggressiveAI();
                break;

            case 4:
            case 5:
                // Hard: Ranged AI
                health = 50 + (floor - 4) * 15;
                ai = new RangedAI();
                break;

            default:
                // Very Hard: Smart AI with pathfinding
                health = 75 + (floor - 6) * 25;
                ai = new SmartAI();
        }

        Enemy enemy = new Enemy(x, y, selectedType, health, ai);
        enemy.setLevel(Math.min(floor, 10));  // Cap at level 10

        return enemy;
    }

    /**
     * Get AI strategy based on difficulty
     */
    private AIStrategy getAIForDifficulty(int floor) {
        if (floor <= 2) {
            return new PatrolAI(new int[]{});
        } else if (floor <= 4) {
            return new AggressiveAI();
        } else if (floor <= 7) {
            return new RangedAI();
        } else {
            return new SmartAI();
        }
    }

    /**
     * Distribute loot in level
     */
    private void distributeLoot(DungeonLevel level, int floor) {
        ItemFactory itemFactory = new ItemFactory();

        // Place loot in rooms (simplified)
        List<Room> rooms = level.getRooms();
        if (rooms.isEmpty()) return;

        // Place 1-2 items per room
        for (Room room : rooms) {
            if (random.nextDouble() < 0.6) {  // 60% chance
                int[] pos = room.getRandomPosition();

                // Create random item
                Item item = itemFactory.createRandomItem(
                    getRandomRarity(floor)
                );

                // Place item (in real game, add to level items list)
                // For now, just don't spawn items since we haven't implemented item entities
            }
        }
    }

    /**
     * Get rarity based on floor
     */
    private com.roguelike.core.items.ItemRarity getRandomRarity(int floor) {
        double roll = random.nextDouble();

        // Higher floors have better loot
        double legendaryChance = 0.01 * floor;
        double rareChance = 0.05 * floor;

        if (roll < legendaryChance) {
            return com.roguelike.core.items.ItemRarity.LEGENDARY;
        } else if (roll < legendaryChance + rareChance) {
            return com.roguelike.core.items.ItemRarity.RARE;
        } else if (roll < legendaryChance + rareChance + 0.3) {
            return com.roguelike.core.items.ItemRarity.UNCOMMON;
        } else {
            return com.roguelike.core.items.ItemRarity.COMMON;
        }
    }

    /**
     * Get width of dungeons
     */
    public static int getWidth() {
        return WIDTH;
    }

    /**
     * Get height of dungeons
     */
    public static int getHeight() {
        return HEIGHT;
    }
}
