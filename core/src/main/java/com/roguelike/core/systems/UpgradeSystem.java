package com.roguelike.core.systems;

import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

import java.util.EnumMap;
import java.util.Map;

/**
 * UpgradeSystem — tracks upgrade points earned from killing goblins and
 * applies stat boosts to the Player when they upgrade at a Base room.
 *
 * POINT ECONOMY
 * ─────────────
 * Goblin killed   → +1  upgrade point
 * Orc killed      → +3  upgrade points
 * Boss killed     → +10 upgrade points
 *
 * Each upgrade costs a scaling number of points (see UpgradeStat.costForLevel).
 * Upgrade levels are stored per stat and persist across floors.
 *
 * HOW TO USE
 * ──────────
 * // In GameManager.init():
 *   upgradeSystem = new UpgradeSystem();
 *   combatSystem.addListener(upgradeSystem.makeCombatListener(player));
 *
 * // In PlayingState (when player is in a BASE room and presses upgrade key):
 *   upgradeSystem.tryUpgrade(UpgradeStat.ATTACK, player);
 *
 * // In UIRenderer:
 *   upgradeSystem.getPoints()          → current spendable points
 *   upgradeSystem.getLevel(stat)       → current level of a stat
 *   upgradeSystem.getCost(stat)        → cost to upgrade stat next level
 */
public class UpgradeSystem {

    // ── State ─────────────────────────────────────────────────────────────────

    private int upgradePoints = 0;
    private int totalPointsEarned = 0;

    /** Current upgrade level for each stat (0 = not upgraded yet). */
    private final Map<UpgradeStat, Integer> levels = new EnumMap<>(UpgradeStat.class);

    // Point rewards per enemy type
    private static final int POINTS_GOBLIN = 1;
    private static final int POINTS_ORC    = 3;
    private static final int POINTS_BOSS   = 10;
    private static final int POINTS_DEFAULT = 1;

    // ── Constructor ───────────────────────────────────────────────────────────

    public UpgradeSystem() {
        for (UpgradeStat stat : UpgradeStat.values()) {
            levels.put(stat, 0);
        }
        System.out.println("[UpgradeSystem] Initialized — all stats at level 0");
    }

    // ── Point earning ─────────────────────────────────────────────────────────

    /**
     * Call this when an enemy is killed (from GameManager.onDefenderDeath).
     * Awards upgrade points based on the enemy type.
     */
    public void onEnemyKilled(Enemy enemy) {
        int reward = pointsFor(enemy.getEnemyType());
        upgradePoints   += reward;
        totalPointsEarned += reward;
        System.out.printf("[UpgradeSystem] +%d point(s) for killing %s — total: %d%n",
            reward, enemy.getEnemyType(), upgradePoints);
    }

    private int pointsFor(String enemyType) {
        if (enemyType == null) return POINTS_DEFAULT;
        switch (enemyType.toLowerCase()) {
            case "goblin": return POINTS_GOBLIN;
            case "orc":    return POINTS_ORC;
            case "dragon": return POINTS_BOSS;
            default:       return POINTS_DEFAULT;
        }
    }

    // ── Upgrading ─────────────────────────────────────────────────────────────

    /**
     * Attempts to upgrade [stat] by one level, deducting points from the player.
     *
     * @param stat   the characteristic to improve
     * @param player the player whose stats will be modified
     * @return true if the upgrade succeeded, false if not enough points or at max level
     */
    public boolean tryUpgrade(UpgradeStat stat, Player player) {
        int currentLevel = levels.get(stat);

        if (currentLevel >= stat.maxLevel) {
            System.out.println("[UpgradeSystem] " + stat.displayName + " is already at max level!");
            return false;
        }

        int cost = stat.costForLevel(currentLevel);
        if (upgradePoints < cost) {
            System.out.printf("[UpgradeSystem] Not enough points for %s (need %d, have %d)%n",
                stat.displayName, cost, upgradePoints);
            return false;
        }

        upgradePoints -= cost;
        levels.put(stat, currentLevel + 1);
        applyUpgrade(stat, player);

        System.out.printf("[UpgradeSystem] %s upgraded to level %d (cost %d, remaining %d)%n",
            stat.displayName, currentLevel + 1, cost, upgradePoints);
        return true;
    }

    /**
     * Applies the actual stat change to the Player.
     * Called automatically by tryUpgrade() — you do not need to call this directly.
     */
    private void applyUpgrade(UpgradeStat stat, Player player) {
        switch (stat) {
            case MAX_HEALTH:
                // Extend max health by 25 and top up current health
                player.setMaxHealth(player.getMaxHealth() + 25);
                player.heal(25);
                break;

            case ATTACK:
                // Increase raw attack damage by 5
                player.setAttackDamage(player.getAttackDamage() + 5);
                break;

            case DEFENSE:
                // Increase damage reduction by 2
                player.setDefense(player.getDefense() + 2);
                break;

            case MAX_MANA:
                // Extend max mana by 20 and refill
                player.setCurrentMana(player.getCurrentMana() + 20);
                // Note: Player needs a setMaxMana() — add it if missing
                break;

            case MOVE_SPEED:
                // Reduce PlayerController.MOVE_DELAY — achieved via a shared config
                // For now store it in the system; PlayerController reads it
                moveSpeedBonusMs += 10;
                break;

            case CRIT_CHANCE:
                // Stored here; CombatSystem reads getCritBonus()
                critChanceBonus += 0.05f;
                break;

            case ATTACK_SPEED:
                // Stored here; applied to Entity.attackCooldownMax via GameManager
                attackSpeedBonus += 0.05f;
                break;
        }
    }

    // Bonus values read by other systems ─────────────────────────────────────
    private long  moveSpeedBonusMs  = 0;   // read by PlayerController
    private float critChanceBonus   = 0f;  // read by CombatSystem
    private float attackSpeedBonus  = 0f;  // read by CombatSystem / Entity

    // ── Status helpers ────────────────────────────────────────────────────────

    public int  getPoints()                 { return upgradePoints; }
    public int  getTotalPointsEarned()      { return totalPointsEarned; }
    public int  getLevel(UpgradeStat stat)  { return levels.get(stat); }
    public int  getCost(UpgradeStat stat)   { return stat.costForLevel(levels.get(stat)); }
    public boolean canAfford(UpgradeStat s) { return upgradePoints >= getCost(s); }
    public boolean isMaxed(UpgradeStat s)   { return levels.get(s) >= s.maxLevel; }

    public long  getMoveSpeedBonusMs()  { return moveSpeedBonusMs; }
    public float getCritChanceBonus()   { return critChanceBonus; }
    public float getAttackSpeedBonus()  { return attackSpeedBonus; }

    /** Full status string — useful for debug or simple console UI. */
    public String getStatusString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== UPGRADE SHOP ===\n");
        sb.append(String.format("Points: %d\n\n", upgradePoints));
        for (UpgradeStat stat : UpgradeStat.values()) {
            int lvl  = levels.get(stat);
            int cost = stat.costForLevel(lvl);
            String suffix = isMaxed(stat) ? " (MAX)" : String.format(" [Cost: %d pts]", cost);
            sb.append(String.format("%-14s  Lvl %d/%d%s\n",
                stat.displayName, lvl, stat.maxLevel, suffix));
        }
        return sb.toString();
    }
}
