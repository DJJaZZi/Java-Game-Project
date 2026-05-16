// core/src/main/java/com/roguelike/rendering/WalkableDebugRenderer.java
package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.roguelike.core.dungeon.WalkableMapBuilder;

/**
 * Debug overlay — draws the walkable/wall grid on top of the map PNG.
 *
 * GREEN  (semi-transparent) = walkable FLOOR tile
 * RED    (semi-transparent) = blocked  WALL  tile
 *
 * Toggle from GameRenderer with F1. Zero cost when disabled.
 */
public class WalkableDebugRenderer {

    private static final int   TILE_SIZE = WalkableMapBuilder.TILE_SIZE;   // 32
    private static final int   COLS      = WalkableMapBuilder.GRID_WIDTH;  // 166
    private static final int   ROWS      = WalkableMapBuilder.GRID_HEIGHT; // 10

    // Fill colours  (r, g, b, alpha)
    private static final float[] FLOOR_COLOR = { 0.0f, 1.0f, 0.2f, 0.25f }; // green
    private static final float[] WALL_COLOR  = { 1.0f, 0.1f, 0.1f, 0.35f }; // red

    // Border colour drawn around every tile for a crisp grid
    private static final float[] BORDER_COLOR = { 0f, 0f, 0f, 0.55f };

    private final ShapeRenderer sr;
    private boolean enabled = false;

    public WalkableDebugRenderer() {
        sr = new ShapeRenderer();
    }

    // ── public API ──────────────────────────────────────────────────────────

    /** Toggle overlay on/off. */
    public void toggle() {
        enabled = !enabled;
        System.out.println("[WalkableDebug] overlay " + (enabled ? "ON" : "OFF"));
    }

    public boolean isEnabled() { return enabled; }

    /**
     * Call once per frame AFTER the map PNG is drawn.
     * @param cameraMatrix  pass camera.combined from GameRenderer
     */
    public void render(Matrix4 cameraMatrix) {
        if (!enabled) return;

        // Enable alpha blending so the fills are translucent
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(cameraMatrix);

        // ── Pass 1: filled rectangles ────────────────────────────────────
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                boolean walkable = WalkableMapBuilder.isWalkable(x, y);
                float[] c = walkable ? FLOOR_COLOR : WALL_COLOR;
                sr.setColor(c[0], c[1], c[2], c[3]);
                sr.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        sr.end();

        // ── Pass 2: grid border lines ────────────────────────────────────
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(BORDER_COLOR[0], BORDER_COLOR[1], BORDER_COLOR[2], BORDER_COLOR[3]);
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                sr.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        sr.dispose();
    }
}
