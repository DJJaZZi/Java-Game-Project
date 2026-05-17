package com.roguelike.core.entities;

/**
 * Entity — абстрактный базовый класс для всех игровых существ.
 *
 * Содержит:
 * - Позиция и движение по тайловой сетке
 * - HP, урон, защита
 * - State machine: IDLE / MOVING / ATTACKING / DEAD
 * - Cooldown атаки
 */
public abstract class Entity {

    // ── Позиция ───────────────────────────────────────────────────────────────
    protected int x, y;
    protected int targetX, targetY;

    // ── Здоровье ──────────────────────────────────────────────────────────────
    protected float health;
    protected float maxHealth;
    protected float defense;

    // ── Состояние ─────────────────────────────────────────────────────────────
    protected EntityState state;
    protected boolean isAlive;
    protected String name;

    // ── Движение ──────────────────────────────────────────────────────────────
    protected float moveSpeed;      // тайлов в секунду
    protected float moveProgress;   // 0..1 для анимации
    protected boolean isMoving;
    protected boolean facingLeft = false;

    // ── Бой ───────────────────────────────────────────────────────────────────
    protected float attackCooldown;
    protected float attackCooldownMax;
    protected int   attackDamage;

    // ── Конструктор ───────────────────────────────────────────────────────────

    public Entity(int x, int y, float maxHealth) {
        this.x         = x;
        this.y         = y;
        this.targetX   = x;
        this.targetY   = y;
        this.maxHealth = maxHealth;
        this.health    = maxHealth;
        this.state     = EntityState.IDLE;
        this.isAlive   = true;
        this.isMoving  = false;
        this.moveSpeed = 5.0f;
        this.moveProgress    = 0f;
        this.attackDamage    = 10;
        this.defense         = 0f;
        this.attackCooldown  = 0f;
        this.attackCooldownMax = 0.5f;
    }

    // ══════════════════════════════════════════════════════════════
    //  UPDATE — Template Method
    // ══════════════════════════════════════════════════════════════

    /**
     * Вызывается каждый кадр. Подклассы переопределяют onUpdate().
     */
    public void update(float deltaTime) {
        if (!isAlive) return;
        updateMovement(deltaTime);
        updateAttack(deltaTime);
        onUpdate(deltaTime);
    }

    /** Hook — подкласс кладёт сюда свою логику. */
    protected abstract void onUpdate(float deltaTime);

    // ══════════════════════════════════════════════════════════════
    //  ДВИЖЕНИЕ
    // ══════════════════════════════════════════════════════════════

    /**
     * Начать движение к тайлу (newX, newY).
     * Физическое перемещение выполняет DungeonLevel.moveEntity().
     */
    public void moveTo(int newX, int newY) {
        if (!isMoving && (newX != x || newY != y)) {
            this.targetX     = newX;
            this.targetY     = newY;
            this.isMoving    = true;
            this.moveProgress = 0f;
            this.state       = EntityState.MOVING;
        }
    }

    /**
     * Вызывается после физического перемещения (GameManager/AISystem).
     * Включает MOVING-состояние с коротким таймером для анимации.
     */
    public void startMoving() {
        this.state       = EntityState.MOVING;
        this.moveProgress = 0f;
        this.isMoving    = true;
    }

    /** Шаг на dx/dy тайлов. */
    public void move(int dx, int dy) {
        moveTo(x + dx, y + dy);
    }

    /** Обновляет прогресс анимации движения. */
    protected void updateMovement(float deltaTime) {
        if (!isMoving) return;

        moveProgress += moveSpeed * deltaTime;

        if (moveProgress >= 1.0f) {
            isMoving     = false;
            moveProgress = 0f;
            if (state == EntityState.MOVING) {
                state = EntityState.IDLE;
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  БОЙ
    // ══════════════════════════════════════════════════════════════

    /**
     * Получить урон. Защита уже учтена в CombatSystem — здесь чистый вычет.
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
            state   = EntityState.DEAD;
        }
    }

    /** Восстановить HP (не выше maxHealth). */
    public void heal(float amount) {
        float before = health;
        health = Math.min(health + amount, maxHealth);
        if (health > before) {
            System.out.println("[Entity] " + name + " healed for " + (health - before) + " HP!");
        }
    }

    /** True если cooldown прошёл и можно атаковать. */
    public boolean canAttack() {
        return attackCooldown <= 0;
    }

    /** Начать атаку — включает cooldown и переводит в ATTACKING. */
    public void startAttack() {
        attackCooldown = attackCooldownMax;
        state          = EntityState.ATTACKING;
        System.out.println("[Entity] " + name + " attacking!");
    }

    /** Обновляет таймер cooldown-а; возвращает в IDLE когда истёк. */
    protected void updateAttack(float deltaTime) {
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
            if (attackCooldown <= 0) {
                attackCooldown = 0;
                if (state == EntityState.ATTACKING) {
                    state = EntityState.IDLE;
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ВСПОМОГАТЕЛЬНЫЕ
    // ══════════════════════════════════════════════════════════════

    /** Манхэттенское расстояние до другой сущности. */
    public int getDistance(Entity other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    /** HP в процентах (0–100). */
    public float getHealthPercent() {
        return (health / maxHealth) * 100f;
    }

    /** True если HP == maxHP. */
    public boolean isFullHealth() {
        return health >= maxHealth;
    }

    // ══════════════════════════════════════════════════════════════
    //  GETTERS / SETTERS
    // ══════════════════════════════════════════════════════════════

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x       = x;
        this.y       = y;
        this.targetX = x;
        this.targetY = y;
    }

    public float getHealth()              { return health; }
    public float getMaxHealth()           { return maxHealth; }
    public void  setMaxHealth(float v)    { this.maxHealth = v; }

    public EntityState getState()             { return state; }
    public void        setState(EntityState s){ this.state = s; }

    public boolean isAlive() { return isAlive; }
    public boolean isDead()  { return !isAlive; }

    public String getName()         { return name; }
    public void   setName(String n) { this.name = n; }

    public float getDefense()          { return defense; }
    public void  setDefense(float d)   { this.defense = d; }

    public int  getAttackDamage()       { return attackDamage; }
    public void setAttackDamage(int d)  { this.attackDamage = d; }

    public boolean isMoving()      { return isMoving; }
    public int     getTargetX()    { return targetX; }
    public int     getTargetY()    { return targetY; }
    public float   getMoveProgress(){ return moveProgress; }

    /** Направление спрайта: true = смотрит влево. */
    public boolean isFacingLeft()          { return facingLeft; }
    public void    setFacingLeft(boolean v){ this.facingLeft = v; }
}
