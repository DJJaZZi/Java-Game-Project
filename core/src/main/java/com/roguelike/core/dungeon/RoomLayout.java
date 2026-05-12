package com.roguelike.core.dungeon;

/**
 * Defines one room in the fixed dungeon layout.
 * Each room has a PNG image, world position, and tile bounds.
 */
public class RoomLayout {

    public enum RoomImageType {
        SPAWN,       // Spawnpoint(mirrored Base 224x256.png)
        BASE,        // Room 224x256.png
        GOBLIN,      // Basic Level 2 416x256.png
        CORRIDOR_1,  // Corridor-1 384x192.png
        CORRIDOR_2,  // Corridor-1 416x272.png
        BOSS         // Boss 656x384.png
    }

    public final RoomImageType type;
    public final int worldX;      // pixel position in world
    public final int worldY;
    public final int pixelWidth;
    public final int pixelHeight;
    public final String imagePath;
    public final boolean flipped;  // mirror horizontally (for spawn room)

    public RoomLayout(RoomImageType type, int worldX, int worldY,
                      int pixelWidth, int pixelHeight,
                      String imagePath, boolean flipped) {
        this.type        = type;
        this.worldX      = worldX;
        this.worldY      = worldY;
        this.pixelWidth  = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.imagePath   = imagePath;
        this.flipped     = flipped;
    }

    // Tile bounds (for collision / spawn logic)
    // tileSize = 32px assumed
    public int getTileX(int tileSize) { return worldX / tileSize; }
    public int getTileY(int tileSize) { return worldY / tileSize; }
    public int getTileWidth(int tileSize)  { return pixelWidth  / tileSize; }
    public int getTileHeight(int tileSize) { return pixelHeight / tileSize; }
}
