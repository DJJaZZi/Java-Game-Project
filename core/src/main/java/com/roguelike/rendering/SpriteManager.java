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
        // idle frames
        cut("player_idle",    heroTexture,  2,  2, 30, 32);   // idle 4 - clearest
        cut("player_idle_1",  heroTexture, 34, 33, 24, 35);
        cut("player_idle_2",  heroTexture, 99, 72, 27, 34);
        cut("player_idle_3",  heroTexture, 68, 73, 29, 33);
        // run frames
        cut("player_run",     heroTexture,  2, 36, 30, 35);
        cut("player_run_1",   heroTexture,  2, 36, 30, 35);
        cut("player_run_2",   heroTexture, 37, 70, 29, 37);
        cut("player_run_3",   heroTexture, 39,109, 34, 35);
        cut("player_run_4",   heroTexture, 83,145, 34, 36);
        // attack frames
        cut("player_attack",  heroTexture, 68, 36, 26, 35);
        cut("player_attack_1",heroTexture, 68, 36, 26, 35);
        cut("player_attack_2",heroTexture,  2, 73, 33, 36);

        // ── GOBLIN (goblin.png — 512x64) ────────────────────────────────────
        // idle frames
        cut("goblin_idle",    goblinTexture, 73,  2, 23, 28);
        cut("goblin_idle_1",  goblinTexture, 73,  2, 23, 28);
        cut("goblin_idle_2",  goblinTexture,173,  2, 23, 28);
        cut("goblin_idle_3",  goblinTexture,248,  2, 23, 29);
        // run frames
        cut("goblin_run",     goblinTexture, 47, 33, 25, 29);
        cut("goblin_run_1",   goblinTexture, 47, 33, 25, 29);
        cut("goblin_run_2",   goblinTexture,254, 34, 23, 28);
        cut("goblin_run_3",   goblinTexture,198,  2, 23, 28);
        // attack frames
        cut("goblin_attack",  goblinTexture, 41,  2, 30, 29);
        cut("goblin_attack_1",goblinTexture, 41,  2, 30, 29);
        cut("goblin_attack_2",goblinTexture,  2, 33, 43, 29);
        // death frames
        cut("goblin_dead",    goblinTexture,197, 32, 28, 30);
        cut("goblin_dead_1",  goblinTexture,197, 32, 28, 30);

        // ── ORC (orc.png — 1024x128) ────────────────────────────────────────
        // idle frames
        cut("orc_idle",    orcTexture,  67,  2, 63, 61);
        cut("orc_idle_1",  orcTexture,  67,  2, 63, 61);
        cut("orc_idle_2",  orcTexture,   2,  2, 63, 61);
        cut("orc_idle_3",  orcTexture,  86, 65, 63, 61);
        cut("orc_idle_4",  orcTexture, 274,  2, 63, 62);
        // run frames
        cut("orc_run",     orcTexture, 339,  2, 48, 62);
        cut("orc_run_1",   orcTexture, 339,  2, 48, 62);
        cut("orc_run_2",   orcTexture, 467, 63, 48, 63);
        cut("orc_run_3",   orcTexture, 567, 62, 48, 64);
        // attack frames
        cut("orc_attack",  orcTexture,   2, 65, 82, 61);
        cut("orc_attack_1",orcTexture,   2, 65, 82, 61);
        cut("orc_attack_2",orcTexture, 667, 66, 37, 60);
        // death frames
        cut("orc_dead",    orcTexture, 439,  2, 39, 59);
        cut("orc_dead_1",  orcTexture, 439,  2, 39, 59);

        System.out.println("[SpriteManager] Defined " + regions.size() + " sprite regions.");
    }

    /**
     * Build a fully configured EntityAnimator for the given entity type.
     */
    public EntityAnimator buildAnimator(String entityType) {
        EntityAnimator animator = new EntityAnimator();

        switch (entityType.toLowerCase()) {
            case "player":
                animator.addAnimation(EntityAnimator.AnimState.IDLE,
                    new SpriteAnimation(list(
                        regions.get("player_idle_1"),
                        regions.get("player_idle_2"),
                        regions.get("player_idle_3"),
                        regions.get("player_idle")
                    ), 0.15f, true));

                animator.addAnimation(EntityAnimator.AnimState.RUN,
                    new SpriteAnimation(list(
                        regions.get("player_run_1"),
                        regions.get("player_run_2"),
                        regions.get("player_run_3"),
                        regions.get("player_run_4")
                    ), 0.1f, true));

                animator.addAnimation(EntityAnimator.AnimState.ATTACK,
                    new SpriteAnimation(list(
                        regions.get("player_attack_1"),
                        regions.get("player_attack_2")
                    ), 0.08f, false));
                break;

            case "goblin":
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
