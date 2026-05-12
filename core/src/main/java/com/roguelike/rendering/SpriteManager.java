package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * SpriteManager - Loads PNGs directly and cuts regions manually.
 * Bypasses the .atlas format entirely — works with any LibGDX version.
 */
public class SpriteManager {

    private Texture heroTexture;
    private Texture goblinTexture;
    private Texture orcTexture;

    private final Map<String, TextureRegion> regions = new HashMap<>();

    public SpriteManager() {
        load();
    }

    private void load() {
        try {
            System.out.println("[SpriteManager] CWD: " + new java.io.File(".").getAbsolutePath());
            System.out.println("[SpriteManager] hero exists: "   + Gdx.files.internal("textures/player/hero.png").exists());
            System.out.println("[SpriteManager] goblin exists: " + Gdx.files.internal("textures/enemies/goblin/goblin.png").exists());
            System.out.println("[SpriteManager] orc exists: "    + Gdx.files.internal("textures/enemies/orc/orc.png").exists());

            heroTexture   = new Texture(Gdx.files.internal("textures/player/hero.png"));
            goblinTexture = new Texture(Gdx.files.internal("textures/enemies/goblin/goblin.png"));
            orcTexture    = new Texture(Gdx.files.internal("textures/enemies/orc/orc.png"));

            System.out.println("[SpriteManager] Textures loaded successfully.");
            System.out.println("  hero:   " + heroTexture.getWidth() + "x" + heroTexture.getHeight());
            System.out.println("  goblin: " + goblinTexture.getWidth() + "x" + goblinTexture.getHeight());
            System.out.println("  orc:    " + orcTexture.getWidth() + "x" + orcTexture.getHeight());

            defineRegions();

        } catch (Exception e) {
            System.err.println("[SpriteManager] ERROR loading textures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Define all sprite regions from pixel coordinates.
     * Data taken directly from your .atlas files: x, y, width, height
     * NOTE: LibGDX origin is bottom-left, but TextureRegion uses top-left (image coords).
     * The atlas file gives top-left coords already.
     */
    private void defineRegions() {
        // ── PLAYER (hero.png — 128x256) ─────────────────────────────────────
        // IDLE — 7 frames
        cut("player_idle_1", heroTexture, 34,  33, 24, 35);
        cut("player_idle_2", heroTexture, 99,  72, 27, 34);
        cut("player_idle_3", heroTexture, 68,  73, 29, 33);
        cut("player_idle_4", heroTexture,  2,   2, 30, 32);
        cut("player_idle_5", heroTexture, 96,  38, 28, 32);
        cut("player_idle_6", heroTexture, 96,   3, 26, 33);
        cut("player_idle_7", heroTexture,104, 186, 22, 34);

        // RUN — 8 frames
        cut("player_run_1", heroTexture,  2,  36, 30, 35);
        cut("player_run_2", heroTexture, 37,  70, 29, 37);
        cut("player_run_3", heroTexture, 39, 109, 34, 35);
        cut("player_run_4", heroTexture, 83, 145, 34, 36);
        cut("player_run_5", heroTexture, 75, 108, 32, 35);
        cut("player_run_6", heroTexture, 58, 183, 44, 37);
        cut("player_run_7", heroTexture, 42, 146, 39, 35);
        cut("player_run_8", heroTexture,  2, 148, 38, 36);

        // ATTACK — 6 frames
        cut("player_attack_1", heroTexture, 68,  36, 26, 35);
        cut("player_attack_2", heroTexture,  2,  73, 33, 36);
        cut("player_attack_3", heroTexture,  2, 111, 35, 35);
        cut("player_attack_4", heroTexture,  2, 220, 54, 34);
        cut("player_attack_5", heroTexture,  2, 186, 49, 32);
        cut("player_attack_6", heroTexture, 58, 222, 51, 32);

        // ── GOBLIN (goblin.png — 512x64) ────────────────────────────────────

// IDLE — 19 frames (most share same bounds, that's normal for sprite sheets)
        cut("goblin_idle_1",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_2",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_3",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_4",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_5",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_6",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_7",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_8",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_9",  goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_10", goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_11", goblinTexture,  73,  2, 23, 28);
        cut("goblin_idle_12", goblinTexture, 173,  2, 23, 28);
        cut("goblin_idle_13", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_14", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_15", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_16", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_17", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_18", goblinTexture, 248,  2, 23, 29);
        cut("goblin_idle_19", goblinTexture, 173,  2, 23, 28);

// RUN — 8 frames
        cut("goblin_run_1", goblinTexture,  47, 33, 25, 29);
        cut("goblin_run_2", goblinTexture, 254, 34, 23, 28);
        cut("goblin_run_3", goblinTexture, 198,  2, 23, 28);
        cut("goblin_run_4", goblinTexture, 123,  2, 23, 28);
        cut("goblin_run_5", goblinTexture, 227, 33, 25, 29);
        cut("goblin_run_6", goblinTexture, 148,  2, 23, 28);
        cut("goblin_run_7", goblinTexture,  98,  2, 23, 28);
        cut("goblin_run_8", goblinTexture, 223,  2, 23, 28);

// ATTACK — 3 frames
        cut("goblin_attack_1", goblinTexture,  41,  2, 30, 29);
        cut("goblin_attack_2", goblinTexture,   2, 33, 43, 29);
        cut("goblin_attack_3", goblinTexture,   2,  2, 37, 29);

// DEATH — 19 frames
        cut("goblin_dead_1",  goblinTexture, 197, 32, 28, 30);
        cut("goblin_dead_2",  goblinTexture, 197, 32, 28, 30);
        cut("goblin_dead_3",  goblinTexture,  74, 32, 29, 30);
        cut("goblin_dead_4",  goblinTexture,  74, 32, 29, 30);
        cut("goblin_dead_5",  goblinTexture, 167, 32, 28, 30);
        cut("goblin_dead_6",  goblinTexture, 167, 32, 28, 30);
        cut("goblin_dead_7",  goblinTexture, 105, 32, 29, 30);
        cut("goblin_dead_8",  goblinTexture, 105, 32, 29, 30);
        cut("goblin_dead_9",  goblinTexture, 136, 32, 29, 30);
        cut("goblin_dead_10", goblinTexture, 136, 32, 29, 30);
        cut("goblin_dead_11", goblinTexture, 279, 36, 32, 12);
        cut("goblin_dead_12", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_13", goblinTexture, 313, 50, 32, 12);
        cut("goblin_dead_14", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_15", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_16", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_17", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_18", goblinTexture, 279, 50, 32, 12);
        cut("goblin_dead_19", goblinTexture, 279, 50, 32, 12);

        // ── ORC (orc.png — 1024x128) ────────────────────────────────────────
        // IDLE — 5 frames
        cut("orc_idle_1", orcTexture,  67,  2, 63, 61);
        cut("orc_idle_2", orcTexture,   2,  2, 63, 61);
        cut("orc_idle_3", orcTexture,  86, 65, 63, 61);
        cut("orc_idle_4", orcTexture, 274,  2, 63, 62);
        cut("orc_idle_5", orcTexture, 209,  2, 63, 62);

        // RUN — 6 frames
        cut("orc_run_1", orcTexture, 339,  2, 48, 62);
        cut("orc_run_2", orcTexture, 467, 63, 48, 63);
        cut("orc_run_3", orcTexture, 567, 62, 48, 64);
        cut("orc_run_4", orcTexture, 517, 63, 48, 63);
        cut("orc_run_5", orcTexture, 617, 62, 48, 64);
        cut("orc_run_6", orcTexture, 389,  9, 48, 65);

        // ATTACK — 4 frames
        cut("orc_attack_1", orcTexture,   2, 65, 82, 61);
        cut("orc_attack_2", orcTexture, 667, 66, 37, 60);
        cut("orc_attack_3", orcTexture, 209, 66, 67, 60);
        cut("orc_attack_4", orcTexture, 278, 66, 61, 60);

        // DEAD — 4 frames
        cut("orc_dead_1", orcTexture, 439,  2, 39, 59);
        cut("orc_dead_2", orcTexture, 480,  7, 39, 54);
        cut("orc_dead_3", orcTexture, 413, 76, 52, 50);
        cut("orc_dead_4", orcTexture, 341, 76, 70, 50);

        // HURT — 2 frames
        cut("orc_hurt_1", orcTexture, 151, 65, 56, 61);
        cut("orc_hurt_2", orcTexture, 132,  2, 56, 61);

        System.out.println("[SpriteManager] Defined " + regions.size() + " sprite regions.");
    }

    /**
     * Build a fully configured EntityAnimator for the given entity type.
     */
    public EntityAnimator buildAnimator(String entityType) {
        EntityAnimator animator = new EntityAnimator();

        switch (entityType.toLowerCase()) {
            case "player":
                animator.setDefaultFacingLeft(false);
                animator.addAnimation(EntityAnimator.AnimState.IDLE,
                    new SpriteAnimation(list(
                        regions.get("player_idle_1"),
                        regions.get("player_idle_2"),
                        regions.get("player_idle_3"),
                        regions.get("player_idle_4"),
                        regions.get("player_idle_5"),
                        regions.get("player_idle_6"),
                        regions.get("player_idle_7")
                    ), 0.15f, true));

                animator.addAnimation(EntityAnimator.AnimState.RUN,
                    new SpriteAnimation(list(
                        regions.get("player_run_1"),
                        regions.get("player_run_2"),
                        regions.get("player_run_3"),
                        regions.get("player_run_4"),
                        regions.get("player_run_5"),
                        regions.get("player_run_6"),
                        regions.get("player_run_7"),
                        regions.get("player_run_8")
                    ), 0.08f, true));

                animator.addAnimation(EntityAnimator.AnimState.ATTACK,
                    new SpriteAnimation(list(
                        regions.get("player_attack_1"),
                        regions.get("player_attack_2"),
                        regions.get("player_attack_3"),
                        regions.get("player_attack_4"),
                        regions.get("player_attack_5"),
                        regions.get("player_attack_6")
                    ), 0.06f, false));
                break;

            case "goblin":
                animator.setDefaultFacingLeft(true);
                animator.addAnimation(EntityAnimator.AnimState.IDLE,
                    new SpriteAnimation(list(
                        regions.get("goblin_idle_1"),
                        regions.get("goblin_idle_2"),
                        regions.get("goblin_idle_3")
                    ), 0.15f, true));

                animator.addAnimation(EntityAnimator.AnimState.RUN,
                    new SpriteAnimation(list(
                        regions.get("goblin_run_1"),
                        regions.get("goblin_run_2"),
                        regions.get("goblin_run_3")
                    ), 0.1f, true));

                animator.addAnimation(EntityAnimator.AnimState.ATTACK,
                    new SpriteAnimation(list(
                        regions.get("goblin_attack_1"),
                        regions.get("goblin_attack_2")
                    ), 0.08f, false));

                animator.addAnimation(EntityAnimator.AnimState.DEAD,
                    new SpriteAnimation(list(
                        regions.get("goblin_dead_1")
                    ), 0.2f, false));
                break;

            case "orc":
                animator.setDefaultFacingLeft(true);
                animator.addAnimation(EntityAnimator.AnimState.IDLE,
                    new SpriteAnimation(list(
                        regions.get("orc_idle_1"),
                        regions.get("orc_idle_2"),
                        regions.get("orc_idle_3"),
                        regions.get("orc_idle_4")
                    ), 0.18f, true));

                animator.addAnimation(EntityAnimator.AnimState.RUN,
                    new SpriteAnimation(list(
                        regions.get("orc_run_1"),
                        regions.get("orc_run_2"),
                        regions.get("orc_run_3")
                    ), 0.1f, true));

                animator.addAnimation(EntityAnimator.AnimState.ATTACK,
                    new SpriteAnimation(list(
                        regions.get("orc_attack_1"),
                        regions.get("orc_attack_2")
                    ), 0.1f, false));

                animator.addAnimation(EntityAnimator.AnimState.DEAD,
                    new SpriteAnimation(list(
                        regions.get("orc_dead_1")
                    ), 0.2f, false));
                break;
        }

        return animator;
    }


    // Helper to build a list and skip nulls safely
    private java.util.List<com.badlogic.gdx.graphics.g2d.TextureRegion> list(
        com.badlogic.gdx.graphics.g2d.TextureRegion... frames) {
        java.util.List<com.badlogic.gdx.graphics.g2d.TextureRegion> result = new java.util.ArrayList<>();
        for (com.badlogic.gdx.graphics.g2d.TextureRegion f : frames) {
            if (f != null) result.add(f);
        }
        return result;
    }


    /**
     * Cut a region from a texture using top-left pixel coordinates.
     */
    private void cut(String key, Texture tex, int x, int y, int w, int h) {
        // LibGDX TextureRegion(texture, x, y, width, height) uses top-left origin — matches atlas data
        regions.put(key, new TextureRegion(tex, x, y, w, h));
    }

    /**
     * Get a sprite region. Usage:
     *   getFrame("player", "idle")  → player_idle
     *   getFrame("goblin", "run")   → goblin_run
     *   getFrame("orc", "attack")   → orc_attack
     */
    public TextureRegion getFrame(String entityType, String state) {
        String key = entityType.toLowerCase() + "_" + state.toLowerCase();
        TextureRegion region = regions.get(key);

        if (region == null) {
            // Try fallback to idle
            region = regions.get(entityType.toLowerCase() + "_idle");
        }
        if (region == null) {
            System.err.println("[SpriteManager] No region found for: " + key);
        }
        return region;
    }

    public void dispose() {
        if (heroTexture   != null) heroTexture.dispose();
        if (goblinTexture != null) goblinTexture.dispose();
        if (orcTexture    != null) orcTexture.dispose();
    }
}
