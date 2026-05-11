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

        TextureRegion region = null;

        // Match by exact names per entity type
        if (entityType.equals("player")) {
            // hero atlas uses: "idle 1", "run 1", "attack 1"
            region = atlas.findRegion(state + " 1");
        } else if (entityType.equals("goblin")) {
            // goblin atlas uses: "goblin_idle_left 1", "goblin_run 1", "goblin_attack 1"
            if (state.equals("idle"))   region = atlas.findRegion("goblin_idle_left 1");
            else if (state.equals("run"))    region = atlas.findRegion("goblin_run 1");
            else if (state.equals("attack")) region = atlas.findRegion("goblin_attack 1");
            else if (state.equals("dead"))   region = atlas.findRegion("goblin_death 1");
            if (region == null) region = atlas.findRegion("goblin_idle_left 1"); // fallback
        } else if (entityType.equals("orc")) {
            // orc atlas uses: "Idle 1", "Run 1", "Attack 1", "Dead 1"  (capital first letter)
            if (state.equals("idle"))   region = atlas.findRegion("Idle 1");
            else if (state.equals("run"))    region = atlas.findRegion("Run 1");
            else if (state.equals("attack")) region = atlas.findRegion("Attack 1");
            else if (state.equals("dead"))   region = atlas.findRegion("Dead 1");
            if (region == null) region = atlas.findRegion("Idle 1"); // fallback
        }

        // Last resort: first region in atlas
        if (region == null && !atlas.getRegions().isEmpty()) {
            region = atlas.getRegions().first();
        }

        if (region != null) cache.put(key, region);
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
