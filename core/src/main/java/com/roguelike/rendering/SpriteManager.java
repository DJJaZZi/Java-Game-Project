// core/src/main/java/com/roguelike/rendering/SpriteManager.java
package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * SpriteManager - Loads and caches all sprite atlases.
 * Single source of truth for textures.
 */
public class SpriteManager {

    private TextureAtlas heroAtlas;
    private TextureAtlas goblinAtlas;
    private TextureAtlas orcAtlas;

    // Cache: "goblin_idle" -> first matching TextureRegion
    private final Map<String, TextureRegion> cache = new HashMap<>();

    public SpriteManager() {
        load();
    }

    private void load() {
        try {
            heroAtlas   = new TextureAtlas(Gdx.files.internal("textures/player/hero.atlas"));
            goblinAtlas = new TextureAtlas(Gdx.files.internal("textures/enemies/goblin/goblin.atlas"));
            orcAtlas    = new TextureAtlas(Gdx.files.internal("textures/enemies/orc/orc.atlas"));
            System.out.println("[SpriteManager] All atlases loaded.");
        } catch (Exception e) {
            System.err.println("[SpriteManager] Failed to load atlas: " + e.getMessage());
        }
    }

    /**
     * Get a frame for an entity type + state.
     * Examples: getFrame("player","idle"), getFrame("goblin","run"), getFrame("orc","Attack")
     * Falls back to first available region if not found.
     */
    public TextureRegion getFrame(String entityType, String state) {
        String key = entityType + "_" + state;
        if (cache.containsKey(key)) return cache.get(key);

        TextureAtlas atlas = atlasFor(entityType);
        if (atlas == null) return null;

        // Try exact name first, then case-insensitive prefix search
        TextureAtlas.AtlasRegion region = atlas.findRegion(state + " 1");
        if (region == null) region = atlas.findRegion(state + "_idle_left 1");
        if (region == null) {
            // Find any region whose name starts with state (case-insensitive)
            for (TextureAtlas.AtlasRegion r : atlas.getRegions()) {
                if (r.name.toLowerCase().startsWith(state.toLowerCase())) {
                    region = r;
                    break;
                }
            }
        }
        if (region == null && !atlas.getRegions().isEmpty()) {
            region = atlas.getRegions().first(); // absolute fallback
        }

        if (region != null) {
            cache.put(key, region);
        }
        return region;
    }

    private TextureAtlas atlasFor(String entityType) {
        switch (entityType.toLowerCase()) {
            case "player": return heroAtlas;
            case "goblin": return goblinAtlas;
            case "orc":    return orcAtlas;
            default:       return null;
        }
    }

    public void dispose() {
        if (heroAtlas   != null) heroAtlas.dispose();
        if (goblinAtlas != null) goblinAtlas.dispose();
        if (orcAtlas    != null) orcAtlas.dispose();
    }
}
