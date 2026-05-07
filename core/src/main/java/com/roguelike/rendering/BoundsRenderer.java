package com.roguelike.rendering;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roguelike.core.dungeon.LevelBounds;
import com.roguelike.core.dungeon.RoomType;

import java.util.List;

/**
 * BoundsRenderer — draws a coloured border around every room using the exact
 * pixel dimensions defined in LevelBounds.
 *
 * Colour coding matches the sketch:
 *   SPAWN    → teal  (#00C8C8)
 *   GOBLIN   → red   (#C83200)
 *   CORRIDOR → blue  (#3264C8)
 *   BASE     → green (#32C832)
 *   BOSS     → gold  (#C8A000)
 *
 * Call render() inside your ShapeRenderer.begin(Line) block.
 */
public class BoundsRenderer {

    /** Thickness of the border in pixels. */
    private static final float BORDER = 4f;

    /**
     * Draw borders for all rooms in the current level layout.
     *
     * @param sr    ShapeRenderer already begun in Line mode
     * @param rooms list of all LevelBounds for this level
     */
    public void render(ShapeRenderer sr, List<LevelBounds> rooms) {
        for (LevelBounds b : rooms) {
            setColor(sr, b.roomType);
            drawBorder(sr, b);
        }
    }

    // ── Internals ────────────────────────────────────────────────────────────

    private void setColor(ShapeRenderer sr, RoomType type) {
        switch (type) {
            case SPAWN:    sr.setColor(0.00f, 0.78f, 0.78f, 1f); break;
            case GOBLIN:   sr.setColor(0.78f, 0.20f, 0.00f, 1f); break;
            case CORRIDOR: sr.setColor(0.20f, 0.39f, 0.78f, 1f); break;
            case BASE:     sr.setColor(0.20f, 0.78f, 0.20f, 1f); break;
            case BOSS:     sr.setColor(0.78f, 0.63f, 0.00f, 1f); break;
            default:       sr.setColor(1f,    1f,    1f,    1f); break;
        }
    }

    /**
     * Draws BORDER-thick rectangle border by drawing four filled rectangles
     * (top, bottom, left, right strips) — works in both Line and Filled mode.
     */
    private void drawBorder(ShapeRenderer sr, LevelBounds b) {
        float x = b.x, y = b.y, w = b.width, h = b.height, t = BORDER;

        // Top strip
        sr.rect(x,         y + h - t, w, t);
        // Bottom strip
        sr.rect(x,         y,         w, t);
        // Left strip
        sr.rect(x,         y,         t, h);
        // Right strip
        sr.rect(x + w - t, y,         t, h);
    }
}
