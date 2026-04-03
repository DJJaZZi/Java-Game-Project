package com.roguelike.core.dungeon;

/**
 * Room - represents a rectangular area in the dungeon
 * Used for room-based dungeon generation
 */
public class Room {
    public int x, y;           // Top-left corner
    public int width, height;  // Room dimensions
    public boolean hasExit;    // Does this room have exit to next level?

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hasExit = false;
    }

    /**
     * Get center of room (useful for spawning)
     */
    public int[] getCenter() {
        return new int[]{
            x + width / 2,
            y + height / 2
        };
    }

    /**
     * Check if point is in this room
     */
    public boolean containsPoint(int px, int py) {
        return px >= x && px < x + width &&
            py >= y && py < y + height;
    }

    /**
     * Get a random position in this room
     */
    public int[] getRandomPosition() {
        int randX = x + (int) (Math.random() * width);
        int randY = y + (int) (Math.random() * height);
        return new int[]{randX, randY};
    }

    /**
     * Create corridor between two rooms
     */
    public static void createCorridor(Room r1, Room r2, Tile[][] tiles) {
        int[] center1 = r1.getCenter();
        int[] center2 = r2.getCenter();

        // Horizontal then vertical corridor
        int x = center1[0];
        int y = center1[1];

        // Move horizontally
        while (x != center2[0]) {
            if (x < center2[0]) x++;
            else x--;

            if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
                tiles[x][y] = new Tile(TileType.FLOOR);
            }
        }

        // Move vertically
        while (y != center2[1]) {
            if (y < center2[1]) y++;
            else y--;

            if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
                tiles[x][y] = new Tile(TileType.FLOOR);
            }
        }
    }
}
