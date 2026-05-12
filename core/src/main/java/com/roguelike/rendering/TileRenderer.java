package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TileRenderer {

    private Texture mapTexture;

    // Exact pixel dimensions of your PNG
    private static final float MAP_WIDTH  = 5392f;
    private static final float MAP_HEIGHT = 416f;
    private static final String MAP_PATH  = "textures/maps/rooms/full_level 5392x416.png";

    public TileRenderer() {
        try {
            if (Gdx.files.internal(MAP_PATH).exists()) {
                mapTexture = new Texture(Gdx.files.internal(MAP_PATH));
                System.out.println("[TileRenderer] Map loaded: " + MAP_WIDTH + "x" + MAP_HEIGHT);
            } else {
                System.err.println("[TileRenderer] Map not found: " + MAP_PATH);
            }
        } catch (Exception e) {
            System.err.println("[TileRenderer] Error loading map: " + e.getMessage());
        }
    }

    /**
     * Draw the full map PNG at world position (0, 0).
     * Call this inside SpriteBatch.begin/end.
     */
    public void render(SpriteBatch batch) {
        if (mapTexture == null) return;
        batch.draw(mapTexture, 0, 0, MAP_WIDTH, MAP_HEIGHT);
    }

    public boolean isLoaded() {
        return mapTexture != null;
    }

    public void dispose() {
        if (mapTexture != null) mapTexture.dispose();
    }
}
