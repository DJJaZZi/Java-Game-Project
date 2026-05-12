package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.roguelike.core.dungeon.RoomLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TileRenderer - draws room PNG images at their world positions.
 * Each PNG is drawn as one big image — no tile slicing needed.
 */
public class TileRenderer {

    private final Map<String, Texture> textureCache = new HashMap<>();

    public void render(SpriteBatch batch, List<RoomLayout> rooms) {
        for (RoomLayout room : rooms) {
            Texture tex = getTexture(room.imagePath);
            if (tex == null) continue;

            if (room.flipped) {
                // Draw mirrored (spawn room)
                TextureRegion region = new TextureRegion(tex);
                region.flip(true, false);
                batch.draw(region,
                    room.worldX, room.worldY,
                    room.pixelWidth, room.pixelHeight);
            } else {
                batch.draw(tex,
                    room.worldX, room.worldY,
                    room.pixelWidth, room.pixelHeight);
            }
        }
    }

    private Texture getTexture(String path) {
        if (textureCache.containsKey(path)) return textureCache.get(path);
        try {
            if (!Gdx.files.internal(path).exists()) {
                System.err.println("[TileRenderer] File not found: " + path);
                return null;
            }
            Texture tex = new Texture(Gdx.files.internal(path));
            textureCache.put(path, tex);
            System.out.println("[TileRenderer] Loaded: " + path);
            return tex;
        } catch (Exception e) {
            System.err.println("[TileRenderer] Error loading: " + path + " — " + e.getMessage());
            return null;
        }
    }

    public void dispose() {
        for (Texture t : textureCache.values()) t.dispose();
        textureCache.clear();
    }
}
