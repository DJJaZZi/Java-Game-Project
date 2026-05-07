package com.roguelike.core.dungeon;

import com.badlogic.gdx.math.Rectangle;

/**
 * LevelBounds — stores the pixel-space rectangle for each room type.
 *
 * Pixel sizes come directly from the level sketches:
 *   Spawnpoint / Base  : 224 x 256
 *   Goblin Level 1-4   : 412 x 256
 *   Corridor           : 384 x 192
 *   Boss Level 5       : 656 x 384
 *
 * A LevelBounds object is attached to every RoomDefinition and is used by:
 *   - GameRenderer  : to know where to draw border/wall tiles
 *   - CameraSystem  : to clamp the camera inside the room
 *   - CollisionSystem : to push the player back if they walk out of bounds
 */
public class LevelBounds {

    // ── Named pixel dimensions from the sketch ──────────────────────────────
    public static final int SPAWN_W  = 224,  SPAWN_H  = 256;
    public static final int GOBLIN_W = 412,  GOBLIN_H = 256;
    public static final int CORRIDOR_W = 384, CORRIDOR_H = 192;
    public static final int BASE_W   = 224,  BASE_H   = 256;
    public static final int BOSS_W   = 656,  BOSS_H   = 384;

    // ── Fields ───────────────────────────────────────────────────────────────
    /** World-space position of the top-left corner of this room (in pixels). */
    public final float x, y;
    /** Pixel dimensions of this room. */
    public final float width, height;
    /** Human-readable label shown in the level sketch. */
    public final String label;
    /** Which type this room is — drives enemy spawning, upgrade UI, etc. */
    public final RoomType roomType;

    // ── Constructor ──────────────────────────────────────────────────────────
    public LevelBounds(float x, float y, float width, float height,
                       String label, RoomType roomType) {
        this.x        = x;
        this.y        = y;
        this.width    = width;
        this.height   = height;
        this.label    = label;
        this.roomType = roomType;
    }

    // ── Convenience constructors for each preset room type ───────────────────

    public static LevelBounds spawnpoint(float x, float y) {
        return new LevelBounds(x, y, SPAWN_W, SPAWN_H, "Spawnpoint (Base)", RoomType.SPAWN);
    }

    public static LevelBounds goblinRoom(float x, float y, int levelIndex) {
        return new LevelBounds(x, y, GOBLIN_W, GOBLIN_H,
            "Goblin Level " + levelIndex, RoomType.GOBLIN);
    }

    public static LevelBounds corridor(float x, float y) {
        return new LevelBounds(x, y, CORRIDOR_W, CORRIDOR_H, "Corridor", RoomType.CORRIDOR);
    }

    public static LevelBounds base(float x, float y) {
        return new LevelBounds(x, y, BASE_W, BASE_H, "Base", RoomType.BASE);
    }

    public static LevelBounds bossRoom(float x, float y) {
        return new LevelBounds(x, y, BOSS_W, BOSS_H, "Boss Level 5", RoomType.BOSS);
    }

    // ── Geometry helpers ─────────────────────────────────────────────────────

    /** Returns a libGDX Rectangle matching this room (useful for overlap tests). */
    public Rectangle toRectangle() {
        return new Rectangle(x, y, width, height);
    }

    /** True if the pixel point (px, py) is inside this room. */
    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    /**
     * Clamps a position so it stays inside the room's walkable area.
     * Adds an inset margin (= wall thickness) on every side.
     *
     * @param px        raw X in pixels
     * @param py        raw Y in pixels
     * @param margin    wall thickness in pixels (e.g. 16 for a one-tile border)
     * @return          {clampedX, clampedY}
     */
    public float[] clamp(float px, float py, float margin) {
        float minX = x + margin;
        float maxX = x + width  - margin;
        float minY = y + margin;
        float maxY = y + height - margin;
        return new float[]{
            Math.max(minX, Math.min(maxX, px)),
            Math.max(minY, Math.min(maxY, py))
        };
    }

    /** Center of the room — good default spawn position inside the room. */
    public float[] center() {
        return new float[]{ x + width / 2f, y + height / 2f };
    }

    @Override
    public String toString() {
        return String.format("%s [%.0f,%.0f  %.0fx%.0f]", label, x, y, width, height);
    }
}
