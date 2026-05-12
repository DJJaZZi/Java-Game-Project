package com.roguelike.core.dungeon;

import java.util.List;

/**
 * Converts a list of RoomLayouts into a Tile[][] grid.
 * Floor tiles = inside any room. Wall tiles = everything else.
 */
public class TileMapBuilder {

    private static final int TILE_SIZE = 32;

    public Tile[][] build(List<RoomLayout> rooms, int gridWidth, int gridHeight) {
        Tile[][] tiles = new Tile[gridWidth][gridHeight];

        // Fill everything as FLOOR — the PNG handles visuals,
        // the tile grid just needs to allow movement everywhere
        for (int x = 0; x < gridWidth; x++)
            for (int y = 0; y < gridHeight; y++)
                tiles[x][y] = new Tile(TileType.FLOOR);

        return tiles;
    }

    // Calculate total grid width needed
    public int calcGridWidth(List<RoomLayout> rooms) {
        int maxX = 0;
        for (RoomLayout r : rooms) {
            maxX = Math.max(maxX, r.worldX + r.pixelWidth);
        }
        return (maxX / TILE_SIZE) + 2;
    }

    // Calculate total grid height needed
    public int calcGridHeight(List<RoomLayout> rooms) {
        int maxY = 0;
        for (RoomLayout r : rooms) {
            maxY = Math.max(maxY, Math.abs(r.worldY) + r.pixelHeight);
        }
        return (maxY / TILE_SIZE) + 4;
    }
}
