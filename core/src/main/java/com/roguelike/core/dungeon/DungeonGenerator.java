package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.items.ItemRarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private final Random random;

    private static final int WIDTH  = WalkableMapBuilder.GRID_WIDTH;
    private static final int HEIGHT = WalkableMapBuilder.GRID_HEIGHT;

    private final EnemyFactory goblinFactory = new GoblinFactory();
    private final EnemyFactory orcFactory    = new OrcFactory();

    public DungeonGenerator() {
        this.random = new Random();
    }

    public DungeonLevel generate(int floorNumber) {
        System.out.println("[DungeonGenerator] Generating floor " + floorNumber);

        // 1. Строим тайловую сетку
        WalkableMapBuilder walkableBuilder = new WalkableMapBuilder();
        Tile[][] tiles = walkableBuilder.build();

        // 2. Строим список RoomLayout (для рендерера)
        FixedDungeonLayout layoutBuilder = new FixedDungeonLayout();
        List<RoomLayout> roomLayouts = layoutBuilder.build();

        // 3. Строим LevelBounds (для AI и блокировки коридоров)
        List<LevelBounds> levelBounds = LevelLayoutBuilder.build();

        // 4. Одна большая Room для совместимости
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(0, 0, WalkableMapBuilder.GRID_WIDTH, WalkableMapBuilder.GRID_HEIGHT));

        // 5. Спавним врагов + назначаем домашнюю комнату каждому
        List<Enemy> enemies = spawnEnemiesOnMap(floorNumber, tiles, levelBounds);

        // 6. Собираем уровень
        DungeonLevel level = new DungeonLevel(tiles, rooms, enemies, floorNumber);
        level.setRoomLayouts(roomLayouts);
        level.setLevelBounds(levelBounds); // ← сохраняем в уровне
        level.setExitRoom(rooms.get(0));

        System.out.println("[DungeonGenerator] Floor " + floorNumber + " ready. "
            + roomLayouts.size() + " rooms, " + enemies.size() + " enemies.");
        return level;
    }

    // ══════════════════════════════════════════════════════════════
    //  СПАВН ВРАГОВ
    // ══════════════════════════════════════════════════════════════

    private List<Enemy> spawnEnemiesOnMap(int floor, Tile[][] tiles, List<LevelBounds> levelBounds) {
        List<Enemy> enemies = new ArrayList<>();

        int[][] goblinSpawns = {
            {20, 6}, {22, 7},   // goblin room 1
            {45, 5}, {47, 7},   // goblin room 2
            {70, 6}, {72, 6},   // goblin room 3
            {110, 5}, {112, 7}, // goblin room 4
        };

        for (int[] pos : goblinSpawns) {
            Enemy e = spawnIfWalkable(goblinFactory, pos[0], pos[1], floor, tiles, enemies);
            if (e != null) assignHomeRoom(e, pos[0], pos[1], levelBounds);
        }

        // Орк (босс)
        Enemy orc = spawnIfWalkable(orcFactory, 158, 6, floor, tiles, enemies);
        if (orc != null) assignHomeRoom(orc, 158, 6, levelBounds);

        return enemies;
    }

    /**
     * Находит LevelBounds по тайловым координатам и назначает врагу.
     */
    private void assignHomeRoom(Enemy enemy, int tileX, int tileY, List<LevelBounds> levelBounds) {
        float px = tileX * 32f + 16f;
        float py = tileY * 32f + 16f;
        for (LevelBounds bounds : levelBounds) {
            if (bounds.contains(px, py)) {
                enemy.setHomeBounds(bounds);
                System.out.println("[DungeonGenerator] " + enemy.getName()
                    + " home → " + bounds.label);
                return;
            }
        }
        System.out.println("[DungeonGenerator] WARNING: " + enemy.getName()
            + " at (" + tileX + "," + tileY + ") has no home room!");
    }

    /**
     * Спавнит врага если тайл проходимый и свободный. Возвращает Enemy или null.
     */
    private Enemy spawnIfWalkable(EnemyFactory factory, int x, int y,
                                  int floor, Tile[][] tiles, List<Enemy> out) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return null;
        Tile tile = tiles[x][y];
        if (tile == null || tile.type != TileType.FLOOR || tile.occupant != null) return null;

        Enemy enemy = factory.createEnemy(x, y, floor);
        tile.setOccupant(enemy);
        out.add(enemy);
        return enemy;
    }

    // ══════════════════════════════════════════════════════════════
    //  Утилиты
    // ══════════════════════════════════════════════════════════════

    private ItemRarity getRandomRarity(int floor) {
        double roll            = random.nextDouble();
        double legendaryChance = 0.01 * floor;
        double rareChance      = 0.05 * floor;

        if (roll < legendaryChance)                    return ItemRarity.LEGENDARY;
        if (roll < legendaryChance + rareChance)       return ItemRarity.RARE;
        if (roll < legendaryChance + rareChance + 0.3) return ItemRarity.UNCOMMON;
        return ItemRarity.COMMON;
    }

    public static int getWidth()  { return WIDTH; }
    public static int getHeight() { return HEIGHT; }
}
