package com.roguelike.core.entities;

import com.roguelike.core.ai.AIStrategy;
import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.LevelBounds;

public class Enemy extends Entity {

    private AIStrategy  aiStrategy;
    private int         experienceReward;
    private int         goldReward;
    private String      enemyType;
    private int         level;
    private DungeonLevel dungeonLevel;
    protected int       xpReward = 20;

    // Домашняя комната — враг не выходит за её пределы
    private LevelBounds homeBounds;

    public Enemy(int x, int y, String enemyType, float health, AIStrategy aiStrategy) {
        super(x, y, health);
        this.enemyType  = enemyType;
        this.aiStrategy = aiStrategy;
        this.name       = "Enemy_" + enemyType;
        this.level      = 1;
        configureByType(enemyType);
        System.out.println("[Enemy] Spawned " + enemyType
            + " at (" + x + ", " + y + ") AI: " + aiStrategy.getAIName());
    }

    // ── Конфигурация по типу ──────────────────────────────────────────────────
    private void configureByType(String type) {
        switch (type.toLowerCase()) {
            case "goblin":
                this.experienceReward = 50;  this.goldReward = 25;
                this.attackDamage = 8;       this.defense = 1; break;
            case "orc":
                this.experienceReward = 100; this.goldReward = 50;
                this.attackDamage = 15;      this.defense = 3; break;
            case "skeleton":
                this.experienceReward = 75;  this.goldReward = 30;
                this.attackDamage = 10;      this.defense = 2; break;
            case "dragon":
                this.experienceReward = 500; this.goldReward = 200;
                this.attackDamage = 25;      this.defense = 5; break;
            case "zombie":
                this.experienceReward = 40;  this.goldReward = 15;
                this.attackDamage = 6;       this.defense = 0; break;
            case "spider":
                this.experienceReward = 60;  this.goldReward = 35;
                this.attackDamage = 12;      this.defense = 1; break;
            default:
                this.experienceReward = 50;  this.goldReward = 25;
                this.attackDamage = 10;      this.defense = 1;
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // AI выполняется через AISystem
    }

    // ── Методы домашней комнаты ───────────────────────────────────────────────

    /** True если тайл (tileX, tileY) внутри домашней комнаты врага. */
    public boolean isInsideHome(int tileX, int tileY) {
        if (homeBounds == null) return true;
        float px = tileX * 32f + 16f;
        float py = tileY * 32f + 16f;
        return homeBounds.contains(px, py);
    }

    /** True если игрок находится в той же комнате что и враг. */
    public boolean isPlayerInMyRoom(Player player) {
        if (homeBounds == null) return true;
        float px = player.getX() * 32f + 16f;
        float py = player.getY() * 32f + 16f;
        return homeBounds.contains(px, py);
    }

    // ── AI методы ─────────────────────────────────────────────────────────────
    public void setAIStrategy(AIStrategy s) {
        if (s != null) { this.aiStrategy = s; }
    }
    public AIStrategy getAIStrategy() { return aiStrategy; }

    public int[] getNextMove(Player player, DungeonLevel level) {
        return aiStrategy != null ? aiStrategy.decideNextMove(this, player, level) : new int[]{0, 0};
    }

    public void executeAI(Player player, DungeonLevel level, float deltaTime) {
        if (aiStrategy != null && isAlive) aiStrategy.execute(this, player, level, deltaTime);
    }

    public Enemy createCopy(int newX, int newY) {
        return new Enemy(newX, newY, enemyType, maxHealth, aiStrategy);
    }

    public String getInfoString() {
        return String.format("Enemy: %s | Type: %s | Level: %d | HP: %.0f/%.0f | AI: %s",
            name, enemyType, level, health, maxHealth, aiStrategy.getAIName());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public int          getXpReward()                       { return xpReward; }
    public DungeonLevel getDungeonLevel()                   { return dungeonLevel; }
    public void         setDungeonLevel(DungeonLevel l)     { this.dungeonLevel = l; }
    public String       getEnemyType()                      { return enemyType; }
    public int          getExperienceReward()               { return experienceReward; }
    public int          getGoldReward()                     { return goldReward; }
    public int          getLevel()                          { return level; }
    public LevelBounds  getHomeBounds()                     { return homeBounds; }
    public void         setHomeBounds(LevelBounds bounds)   { this.homeBounds = bounds; }

    public void setLevel(int level) {
        this.level = level;
        this.maxHealth        *= (1 + level * 0.1f);
        this.health            = maxHealth;
        this.attackDamage      = (int)(attackDamage * (1 + level * 0.2f));
        this.experienceReward  = (int)(experienceReward * (1 + level * 0.5f));
        this.goldReward        = (int)(goldReward * (1 + level * 0.5f));
    }
}
