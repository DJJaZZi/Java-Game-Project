package com.roguelike.core.dungeon;

import com.roguelike.core.entities.Entity;
import com.roguelike.core.entities.Enemy;

import java.util.ArrayList;
import java.util.List;

public class DungeonLevel {

    private Tile[][]       tiles;
    private List<Room>     rooms;
    private List<Enemy>    enemies;
    private int            floorNumber;
    private int            tilesWide;
    private int            tilesHigh;
    private Room           exitRoom;
    private List<RoomLayout> roomLayouts  = new ArrayList<>();
    private List<LevelBounds> levelBounds = new ArrayList<>(); // ← НОВОЕ

    public DungeonLevel(Tile[][] tiles, List<Room> rooms, List<Enemy> enemies, int floorNumber) {
        this.tiles       = tiles;
        this.rooms       = rooms;
        this.enemies     = enemies;
        this.floorNumber = floorNumber;
        this.tilesWide   = tiles.length;
        this.tilesHigh   = tiles[0].length;
        System.out.println("[DungeonLevel] Floor " + floorNumber + " created: "
            + tilesWide + "x" + tilesHigh + " tiles, "
            + rooms.size() + " rooms, " + enemies.size() + " enemies");
    }

    public void update(float deltaTime) {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) enemy.update(deltaTime);
        }
        enemies.removeIf(e -> !e.isAlive());
    }

    // ── Новые методы для комнат ───────────────────────────────────────────────

    /**
     * Возвращает LevelBounds комнаты в которой находится тайл (tileX, tileY).
     * Null если тайл вне всех комнат (коридор или за пределами).
     */
    public LevelBounds getBoundsAt(int tileX, int tileY) {
        float px = tileX * 32f + 16f;
        float py = tileY * 32f + 16f;
        for (LevelBounds b : levelBounds) {
            if (b.contains(px, py)) return b;
        }
        return null;
    }

    /**
     * True если все враги с homeBounds == room мертвы (комната очищена).
     */
    public boolean isRoomCleared(LevelBounds room) {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.getHomeBounds() == room) return false;
        }
        return true;
    }

    // ── Стандартные методы ────────────────────────────────────────────────────

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= tilesWide || y < 0 || y >= tilesHigh) return false;
        Tile tile = tiles[x][y];
        return tile != null && tile.isPassable();
    }

    public boolean isFloor(int x, int y) {
        if (x < 0 || x >= tilesWide || y < 0 || y >= tilesHigh) return false;
        Tile tile = tiles[x][y];
        return tile != null && tile.isWalkable();
    }

    public Entity getOccupantAt(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null ? tile.occupant : null;
    }

    public boolean blocksVision(int x, int y) {
        if (x < 0 || x >= tilesWide || y < 0 || y >= tilesHigh) return true;
        Tile tile = tiles[x][y];
        return tile != null && tile.blocksVision();
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < tilesWide && y >= 0 && y < tilesHigh) return tiles[x][y];
        return null;
    }

    public void placeEntity(Entity entity, int x, int y) {
        if (isWalkable(x, y)) {
            Tile oldTile = getTile(entity.getX(), entity.getY());
            if (oldTile != null && oldTile.occupant == entity) oldTile.clearOccupant();
            tiles[x][y].setOccupant(entity);
            entity.setPosition(x, y);
        }
    }

    public void moveEntity(Entity entity, int newX, int newY) {
        if (!isFloor(newX, newY)) return;
        Tile oldTile = getTile(entity.getX(), entity.getY());
        if (oldTile != null) oldTile.setOccupant(null);
        entity.setPosition(newX, newY);
        Tile newTile = getTile(newX, newY);
        if (newTile != null) newTile.setOccupant(entity);
    }

    public List<Entity> getEntitiesAt(int x, int y) {
        List<Entity> result = new ArrayList<>();
        Tile tile = getTile(x, y);
        if (tile != null && tile.occupant != null) result.add(tile.occupant);
        return result;
    }

    public List<Enemy> getEnemiesInRadius(int cx, int cy, int radius) {
        List<Enemy> result = new ArrayList<>();
        for (Enemy e : enemies) {
            if (Math.abs(e.getX() - cx) <= radius && Math.abs(e.getY() - cy) <= radius)
                result.add(e);
        }
        return result;
    }

    public List<int[]> findPath(int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);
        int cx = startX, cy = startY;
        while ((cx != endX || cy != endY) && path.size() < 50) {
            if (cx != endX && isFloor(cx + dx, cy))      cx += dx;
            else if (cy != endY && isFloor(cx, cy + dy)) cy += dy;
            else break;
            path.add(new int[]{cx, cy});
        }
        return path;
    }

    public void updateFogOfWar(int playerX, int playerY, int visionRange) {
        for (int x = playerX - visionRange; x <= playerX + visionRange; x++) {
            for (int y = playerY - visionRange; y <= playerY + visionRange; y++) {
                if (x >= 0 && x < tilesWide && y >= 0 && y < tilesHigh) {
                    int ddx = Math.abs(x - playerX), ddy = Math.abs(y - playerY);
                    if (ddx * ddx + ddy * ddy <= visionRange * visionRange)
                        tiles[x][y].setExplored(true);
                }
            }
        }
    }

    public String getStatistics() {
        return String.format("Floor %d: %dx%d tiles | %d rooms | %d enemies",
            floorNumber, tilesWide, tilesHigh, rooms.size(), enemies.size());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public Tile[][]       getTiles()                            { return tiles; }
    public List<Room>     getRooms()                            { return rooms; }
    public List<Enemy>    getEnemies()                          { return enemies; }
    public int            getFloorNumber()                      { return floorNumber; }
    public int            getWidth()                            { return tilesWide; }
    public int            getHeight()                           { return tilesHigh; }
    public Room           getExitRoom()                         { return exitRoom; }
    public void           setExitRoom(Room room)                { this.exitRoom = room; }
    public List<RoomLayout>  getRoomLayouts()                   { return roomLayouts; }
    public void           setRoomLayouts(List<RoomLayout> l)    { this.roomLayouts = l; }
    public List<LevelBounds> getLevelBounds()                   { return levelBounds; }
    public void           setLevelBounds(List<LevelBounds> b)   { this.levelBounds = b; }
}
