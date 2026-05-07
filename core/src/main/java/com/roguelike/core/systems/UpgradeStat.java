package com.roguelike.core.systems;

/**
 * UpgradeStat — every characteristic the player can improve at a Base room.
 *
 * Each constant carries:
 *   displayName  : shown in the upgrade UI
 *   description  : tooltip text
 *   baseCost     : upgrade points required for level 1 → 2
 *   costScaling  : multiplier applied each level (e.g. 1.5 → 1→2 costs 10, 2→3 costs 15…)
 *   maxLevel     : hard cap on how many times this can be upgraded
 */
public enum UpgradeStat {

    MAX_HEALTH   ("Max Health",    "Increases maximum HP by 25",       10, 1.5f, 10),
    ATTACK       ("Attack Power",  "Increases attack damage by 5",     10, 1.5f, 10),
    DEFENSE      ("Defense",       "Reduces incoming damage by 2",     10, 1.5f, 8),
    MAX_MANA     ("Max Mana",      "Increases maximum mana by 20",     8,  1.4f, 10),
    MOVE_SPEED   ("Move Speed",    "Reduces movement delay by 10ms",   12, 1.3f, 5),
    CRIT_CHANCE  ("Crit Chance",   "Increases crit chance by 5%",      15, 1.6f, 6),
    ATTACK_SPEED ("Attack Speed",  "Reduces attack cooldown by 0.05s", 15, 1.6f, 6);

    // ── Fields ────────────────────────────────────────────────────────────────
    public final String displayName;
    public final String description;
    public final int    baseCost;
    public final float  costScaling;
    public final int    maxLevel;

    UpgradeStat(String displayName, String description,
                int baseCost, float costScaling, int maxLevel) {
        this.displayName  = displayName;
        this.description  = description;
        this.baseCost     = baseCost;
        this.costScaling  = costScaling;
        this.maxLevel     = maxLevel;
    }

    /** Cost to go from currentLevel → currentLevel+1. */
    public int costForLevel(int currentLevel) {
        return Math.round(baseCost * (float) Math.pow(costScaling, currentLevel));
    }
}
