package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Entity;
import com.roguelike.core.entities.Enemy;
import java.util.List;
import java.util.ArrayList;

/**
 * DungeonLevel - represents one complete floor of the dungeon
 * Contains all entities, tiles, and game logic for that level
 */
public class DungeonLevel {
    private Tile[][] tiles;
    private List<Room> rooms;
    private List<Enemy> enemies;
    private int floorNumber;
    private int tilesWide;
    private int tilesHigh;
    private Room exitRoom;  // Room with exit to next level

    /**
     * Constructor
     */
    public DungeonLevel(Tile[][] tiles, List<Room> rooms, List<Enemy> enemies, int floorNumber) {
        this.tiles = tiles;
        this.rooms = rooms;
        this.enemies = enemies;
        this.floorNumber = floorNumber;
        this.tilesWide = tiles.length;
        this.tilesHigh = tiles[0].length;

        System.out.println("[DungeonLevel] Floor " + floorNumber + " created: " +
            tilesWide + "x" + tilesHigh + " tiles, " +
            rooms.size() + " rooms, " + enemies.size() + " enemies");
    }

    /**
     * Update all entities in this level
     */
    public void update(float deltaTime) {
        // Update all enemies
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.update(deltaTime);
            }
        }

        // Remove dead enemies
        enemies.removeIf(e -> !e.isAlive());
    }

    /**
     * Check if a position is walkable
     */
    public boolean isWalkable(int x, int y) {
        // Bounds check
        if (x < 0 || x >= tilesWide || y < 0 || y >= tilesHigh) {
            return false;
        }

        Tile tile = tiles[x][y];
        return tile != null && tile.isWalkable();
    }

    /**
     * Check if a position blocks vision
     */
    public boolean blocksVision(int x, int y) {
        if (x < 0 || x >= tilesWide || y < 0 || y >= tilesHigh) {
            return true;
        }

        Tile tile = tiles[x][y];
        return tile != null && tile.blocksVision();
    }

    /**
     * Get tile at position
     */
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < tilesWide && y >= 0 && y < tilesHigh) {
            return tiles[x][y];
        }
        return null;
    }

    /**
     * Place entity at position
     */
    public void placeEntity(Entity entity, int x, int y) {
        if (isWalkable(x, y)) {
            // Remove from old position if it exists
            Tile oldTile = getTile(entity.getX(), entity.getY());
            if (oldTile != null && oldTile.occupant == entity) {
                oldTile.clearOccupant();
            }

            // Place at new position
            tiles[x][y].setOccupant(entity);
            entity.setPosition(x, y);
        } else {
            System.out.println("[DungeonLevel] Cannot place " + entity.getName() +
                " at (" + x + ", " + y + ") - tile not walkable");
        }
    }

    /**
     * Move entity from one position to another
     */
    public void moveEntity(Entity entity, int newX, int newY) {
        // Check bounds and walkable
        if (!isWalkable(newX, newY)) {
            return;
        }

        // Remove from old position
        Tile oldTile = getTile(entity.getX(), entity.getY());
        if (oldTile != null) {
            oldTile.clearOccupant();
        }

        // Place at new position
        placeEntity(entity, newX, newY);
    }

    /**
     * Get all entities at a position
     */
    public List<Entity> getEntitiesAt(int x, int y) {
        List<Entity> result = new ArrayList<>();

        Tile tile = getTile(x, y);
        if (tile != null && tile.occupant != null) {
            result.add(tile.occupant);
        }

        return result;
    }

    /**
     * Get all enemies in a radius around a point
     */
    public List<Enemy> getEnemiesInRadius(int centerX, int centerY, int radius) {
        List<Enemy> result = new ArrayList<>();

        for (Enemy enemy : enemies) {
            int dx = Math.abs(enemy.getX() - centerX);
            int dy = Math.abs(enemy.getY() - centerY);

            if (dx <= radius && dy <= radius) {
                result.add(enemy);
            }
        }

        return result;
    }

    /**
     * Find path between two points using BFS (Breadth-First Search)
     * Used for smart pathfinding
     */
    public List<int[]> findPath(int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();

        // Simple pathfinding: just move towards target
        // For a real implementation, use BFS/A* algorithm

        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);

        int currentX = startX;
        int currentY = startY;

        // Move until we reach the target or hit a wall
        while ((currentX != endX || currentY != endY) && path.size() < 50) {
            if (currentX != endX && isWalkable(currentX + dx, currentY)) {
                currentX += dx;
            } else if (currentY != endY && isWalkable(currentX, currentY + dy)) {
                currentY += dy;
            } else {
                // Try just moving in x direction
                if (currentX != endX && isWalkable(currentX + dx, currentY)) {
                    currentX += dx;
                }
                // Try just moving in y direction
                else if (currentY != endY && isWalkable(currentX, currentY + dy)) {
                    currentY += dy;
                }
                // Stuck
                else {
                    break;
                }
            }

            path.add(new int[]{currentX, currentY});
        }

        return path;
    }

    /**
     * Update fog of war (tiles visible to player)
     */
    public void updateFogOfWar(int playerX, int playerY, int visionRange) {
        // Reset exploration
        for (int x = 0; x < tilesWide; x++) {
            for (int y = 0; y < tilesHigh; y++) {
                Tile tile = tiles[x][y];
                if (tile != null && !tile.explored) {
                    tile.setExplored(false);
                }
            }
        }

        // Mark visible tiles
        for (int x = playerX - visionRange; x <= playerX + visionRange; x++) {
            for (int y = playerY - visionRange; y <= playerY + visionRange; y++) {
                if (x >= 0 && x < tilesWide && y >= 0 && y < tilesHigh) {
                    // Simple distance check (no line of sight)
                    int dx = Math.abs(x - playerX);
                    int dy = Math.abs(y - playerY);

                    if (dx * dx + dy * dy <= visionRange * visionRange) {
                        tiles[x][y].setExplored(true);
                    }
                }
            }
        }
    }

    /**
     * Get statistics about this level
     */
    public String getStatistics() {
        return String.format(
            "Floor %d: %dx%d tiles | %d rooms | %d enemies",
            floorNumber, tilesWide, tilesHigh, rooms.size(), enemies.size()
        );
    }

    // ==================== Getters ====================

    public Tile[][] getTiles() { return tiles; }
    public List<Room> getRooms() { return rooms; }
    public List<Enemy> getEnemies() { return enemies; }
    public int getFloorNumber() { return floorNumber; }
    public int getWidth() { return tilesWide; }
    public int getHeight() { return tilesHigh; }
    public Room getExitRoom() { return exitRoom; }
    public void setExitRoom(Room room) { this.exitRoom = room; }
}
