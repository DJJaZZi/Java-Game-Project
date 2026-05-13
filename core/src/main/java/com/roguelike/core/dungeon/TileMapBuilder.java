// TileMapBuilder.java  — COMPLETE UPDATED FILE

package com.roguelike.core.dungeon;

import java.util.List;

public class TileMapBuilder {

    private static final int TILE_SIZE = 32;

    public Tile[][] build(List<RoomLayout> rooms, int gridWidth, int gridHeight) {
        Tile[][] tiles = new Tile[gridWidth][gridHeight];

        // 1. Fill everything with walls
        for (int x = 0; x < gridWidth; x++)
            for (int y = 0; y < gridHeight; y++)
                tiles[x][y] = new Tile(TileType.WALL);

        // 2. Carve rooms — border tiles stay WALL, interior becomes FLOOR
        for (RoomLayout room : rooms) {
            int tileX = room.worldX / TILE_SIZE;
            int tileY = room.worldY / TILE_SIZE;
            int tileW = room.pixelWidth  / TILE_SIZE;
            int tileH = room.pixelHeight / TILE_SIZE;
            int margin = getWallMargin(room.type);

            for (int x = tileX; x < tileX + tileW && x < gridWidth; x++) {
                for (int y = tileY; y < tileY + tileH && y < gridHeight; y++) {
                    if (y < 0) continue;

                    boolean onBorderX = (x < tileX + margin) || (x >= tileX + tileW - margin);
                    boolean onBorderY = (y < tileY + margin) || (y >= tileY + tileH - margin);

                    tiles[x][y] = new Tile(onBorderX || onBorderY ? TileType.WALL : TileType.FLOOR);
                }
            }
        }

        // 3. Punch open doorways between every adjacent pair of rooms  ← STEP 2 GOES HERE
        punchDoorways(rooms, tiles, gridWidth, gridHeight);

        return tiles;   // ← return is AFTER punchDoorways, not before
    }

    // ── called from build() above ────────────────────────────────────────────

    private void punchDoorways(List<RoomLayout> rooms, Tile[][] tiles,
                               int gridW, int gridH) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            RoomLayout current = rooms.get(i);
            RoomLayout next    = rooms.get(i + 1);

            int boundaryTileX = (current.worldX + current.pixelWidth) / TILE_SIZE;

            // Open right wall of current room
            int cTileY = current.worldY / TILE_SIZE;
            int cTileH = current.pixelHeight / TILE_SIZE;
            openColumn(tiles, boundaryTileX - 1, cTileY, cTileH, gridH);

            // Open left wall of next room
            int nTileY = next.worldY / TILE_SIZE;
            int nTileH = next.pixelHeight / TILE_SIZE;
            openColumn(tiles, boundaryTileX, nTileY, nTileH, gridH);
        }
    }

    private void openColumn(Tile[][] tiles, int tileX, int tileY,
                            int tileH, int gridH) {
        int margin = 1;
        for (int y = tileY + margin; y < tileY + tileH - margin; y++) {
            if (y >= 0 && y < gridH && tileX >= 0 && tileX < tiles.length) {
                tiles[tileX][y] = new Tile(TileType.FLOOR);
            }
        }
    }

    private int getWallMargin(RoomLayout.RoomImageType type) {
        return 1; // 1 tile = 32px — matches the blue wall thickness in your PNG
    }

    // ── unchanged helpers ────────────────────────────────────────────────────

    public int calcGridWidth(List<RoomLayout> rooms) {
        int maxX = 0;
        for (RoomLayout r : rooms)
            maxX = Math.max(maxX, r.worldX + r.pixelWidth);
        return (maxX / TILE_SIZE) + 2;
    }

    public int calcGridHeight(List<RoomLayout> rooms) {
        int maxY = 0;
        for (RoomLayout r : rooms)
            maxY = Math.max(maxY, Math.abs(r.worldY) + r.pixelHeight);
        return (maxY / TILE_SIZE) + 4;
    }
}
