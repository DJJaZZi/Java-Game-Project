package com.roguelike.core.entities;

import com.badlogic.gdx.math.Vector2;

/**
 * Entity - Abstract base class for all game entities
 * Defines common properties and behavior for Player, Enemy, etc.
 *
 * This class implements:
 * - Position and movement
 * - Health and damage
 * - State machine (Idle, Moving, Attacking, Dead)
 * - Basic update logic
 */
public abstract class Entity {
    protected int x, y;              // Grid position
    protected float health;
    protected float maxHealth;
    protected float defense;         // Damage reduction
    protected EntityState state;
    protected boolean isAlive;
    protected String name;

    // Movement
    protected float moveSpeed;       // Tiles per second
    protected float moveProgress;    // 0-1 for animation
    protected int targetX, targetY;  // Target position when moving
    protected boolean isMoving;

    // Combat
    protected float attackCooldown;
    protected float attackCooldownMax;
    protected int attackDamage;

    /**
     * Constructor for Entity
     */
    public Entity(int x, int y, float maxHealth) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.state = EntityState.IDLE;
        this.isAlive = true;
        this.isMoving = false;
        this.moveSpeed = 5.0f; // Tiles per second
        this.moveProgress = 0;
        this.attackDamage = 10;
        this.defense = 0;
        this.attackCooldown = 0;
        this.attackCooldownMax = 0.5f; // Half second between attacks
    }

    /**
     * Update entity each frame - override in subclasses
     */
    public abstract void update(float deltaTime);

    /**
     * Move entity towards target grid position
     */
    public void moveTo(int newX, int newY) {
        if (!isMoving && (newX != x || newY != y)) {
            this.targetX = newX;
            this.targetY = newY;
            this.isMoving = true;
            this.moveProgress = 0;
            this.state = EntityState.MOVING;

            System.out.println("[Entity] " + name + " moving to (" + newX + ", " + newY + ")");
        }
    }

    /**
     * Update movement animation
     */
    protected void updateMovement(float deltaTime) {
        if (!isMoving) {
            return;
        }

        // Calculate distance to move this frame
        float distance = moveSpeed * deltaTime;
        moveProgress += distance;

        // Check if movement is complete (simplified - in real game would use pathfinding)
        if (moveProgress >= 1.0f) {
            x = targetX;
            y = targetY;
            isMoving = false;
            moveProgress = 0;
            state = EntityState.IDLE;
        }
    }

    /**
     * Simple move (1 tile in direction)
     */
    public void move(int dx, int dy) {
        moveTo(x + dx, y + dy);
    }

    /**
     * Take damage
     */
    public void takeDamage(float damage) {
        // Apply defense reduction
        float actualDamage = Math.max(1, damage - defense);
        health -= actualDamage;

        System.out.println("[Entity] " + name + " took " + actualDamage + " damage! Health: " + health + "/" + maxHealth);

        if (health <= 0) {
            health = 0;
            isAlive = false;
            state = EntityState.DEAD;
            System.out.println("[Entity] " + name + " died!");
        }
    }

    /**
     * Heal entity
     */
    public void heal(float amount) {
        float oldHealth = health;
        health = Math.min(health + amount, maxHealth);

        if (oldHealth < health) {
            System.out.println("[Entity] " + name + " healed for " + (health - oldHealth) + " HP!");
        }
    }

    /**
     * Check if entity can attack (cooldown finished)
     */
    public boolean canAttack() {
        return attackCooldown <= 0;
    }

    /**
     * Start attack - cooldown begins
     */
    public void startAttack() {
        attackCooldown = attackCooldownMax;
        state = EntityState.ATTACKING;
        System.out.println("[Entity] " + name + " attacking!");
    }

    /**
     * Update attack cooldown
     */
    protected void updateAttack(float deltaTime) {
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
            if (attackCooldown <= 0) {
                state = EntityState.IDLE;
            }
        }
    }

    /**
     * Get distance to another entity
     */
    public int getDistance(Entity other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y); // Manhattan distance
    }

    /**
     * Get health percentage (0-100)
     */
    public float getHealthPercent() {
        return (health / maxHealth) * 100;
    }

    /**
     * Check if entity is at full health
     */
    public boolean isFullHealth() {
        return health >= maxHealth;
    }

    // ==================== Getters and Setters ====================

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }

    public EntityState getState() { return state; }
    public void setState(EntityState newState) { this.state = newState; }

    public boolean isAlive() { return isAlive; }
    public boolean isDead() { return !isAlive; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getDefense() { return defense; }
    public void setDefense(float defense) { this.defense = defense; }

    public int getAttackDamage() { return attackDamage; }
    public void setAttackDamage(int damage) { this.attackDamage = damage; }

    public boolean isMoving() { return isMoving; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }

    public float getMoveProgress() { return moveProgress; }
}
