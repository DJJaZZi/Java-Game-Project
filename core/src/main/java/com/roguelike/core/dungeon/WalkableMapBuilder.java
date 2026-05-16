package com.roguelike.core.dungeon;

/**
 * WalkableMapBuilder
 * Pixel-perfect collision grid from relevels.atlas + relevels.png.
 * Each boolean = one 32x32 tile. WALKABLE[y][x], y=0 is TOP of map.
 *
 * Room layout (left to right):
 *   1. Spawnpoint 160x192           tileX=  0  worldX=0px  (160x192px)
 *   2. Corridor 448x80 1            tileX=  5  worldX=160px  (448x80px)
 *   3. Goblin level 1               tileX= 19  worldX=608px  (352x192px)
 *   4. Corridor 480x160 2           tileX= 30  worldX=960px  (480x160px)
 *   5. Base 160x192 1               tileX= 45  worldX=1440px  (160x192px)
 *   6. Corridor 448x80 3            tileX= 50  worldX=1600px  (448x80px)
 *   7. Goblin level 2               tileX= 64  worldX=2048px  (352x192px)
 *   8. Corridor 448x82 4            tileX= 75  worldX=2400px  (448x82px)
 *   9. Goblin level 3               tileX= 89  worldX=2848px  (352x192px)
 *  10. Corridor 480x160 5           tileX=100  worldX=3200px  (480x160px)
 *  11. Goblin level 4               tileX=115  worldX=3680px  (352x192px)
 *  12. Corridor 480x160 6           tileX=126  worldX=4032px  (480x160px)
 *  13. Base 160x192 2               tileX=141  worldX=4512px  (224x192px)
 *  14. Boss level                   tileX=148  worldX=4736px  (592x320px)
 *
 *  Total grid : 166 x 10 tiles  (5312 x 320 px)
 *  Floor tiles: 689
 */
public class WalkableMapBuilder {

    public static final int TILE_SIZE   = 32;
    public static final int GRID_WIDTH  = 169;
    public static final int GRID_HEIGHT = 14;

    // Room tileX positions (1-based index matches the sequence above)
    public static final int ROOM_01_TILE_X =   0; // Spawnpoint 160x192
    public static final int ROOM_02_TILE_X =   5; // Corridor 448x80 1
    public static final int ROOM_03_TILE_X =  19; // Goblin level 1
    public static final int ROOM_04_TILE_X =  30; // Corridor 480x160 2
    public static final int ROOM_05_TILE_X =  45; // Base 160x192 1
    public static final int ROOM_06_TILE_X =  50; // Corridor 448x80 3
    public static final int ROOM_07_TILE_X =  64; // Goblin level 2
    public static final int ROOM_08_TILE_X =  75; // Corridor 448x82 4
    public static final int ROOM_09_TILE_X =  89; // Goblin level 3
    public static final int ROOM_10_TILE_X = 100; // Corridor 480x160 5
    public static final int ROOM_11_TILE_X = 115; // Goblin level 4
    public static final int ROOM_12_TILE_X = 126; // Corridor 480x160 6
    public static final int ROOM_13_TILE_X = 141; // Base 160x192 2
    public static final int ROOM_14_TILE_X = 148; // Boss level

    // WALKABLE[y][x] — true = FLOOR, false = WALL
    private static final boolean[][] WALKABLE = {

        /* Y= 0 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },

        /* Y= 1 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },

        /* Y= 2 */ { false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },

        /* Y= 3 */ { false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 4 */ { false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 5 */ { false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 6 */ { false, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 7 */ { false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 8 */ { false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y= 9 */ { false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y=10 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, true, true, true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y=11 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y=12 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false },

        /* Y=13 */ { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },

    };
    /**
     * Builds the full Tile[][] collision grid.
     * LibGDX Y=0 is BOTTOM, image Y=0 is TOP — Y is flipped here.
     */
    public Tile[][] build() {
        Tile[][] tiles = new Tile[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                int imgY = GRID_HEIGHT - 1 - y;
                boolean walkable = WALKABLE[imgY][x];
                tiles[x][y] = new Tile(walkable ? TileType.FLOOR : TileType.WALL);
            }
        }
        return tiles;
    }

    /** Bounds-safe walkability check using tile coordinates. */
    public static boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= GRID_WIDTH)  return false;
        if (tileY < 0 || tileY >= GRID_HEIGHT) return false;
        int imgY = GRID_HEIGHT - 1 - tileY;
        return WALKABLE[imgY][tileX];
    }

    /** World-pixel X of a room by 1-based index. Useful for spawning. */
    public static int getRoomWorldX(int roomIndex) {
        int[] tileXs = { 0, 5, 19, 30, 45, 50, 64, 75, 89, 100, 115, 126, 141, 148 };
        int i = Math.max(0, Math.min(roomIndex - 1, tileXs.length - 1));
        return tileXs[i] * TILE_SIZE;
    }
}
