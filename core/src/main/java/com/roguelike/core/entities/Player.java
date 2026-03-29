package com.roguelike.core.entities;

import com.roguelike.core.items.Inventory;

/**
 * Player class - extends Entity
 * Represents the player character with:
 * - Inventory system (Composite Pattern)
 * - Experience and leveling
 * - Gold/currency
 * - Special abilities
 */
public class Player extends Entity {
    private Inventory inventory;
    private int level;
    private int experience;
    private int experienceForNextLevel;
    private int gold;
    private int maxMana;
    private int currentMana;

    // Statistics
    private int strength;      // Affects damage
    private int dexterity;     // Affects dodge/critical
    private int constitution; // Affects max health
    private int intelligence; // Affects magic

    /**
     * Constructor - initialize player with starting stats
     */
    public Player(int startX, int startY) {
        super(startX, startY, 100); // 100 base health
        this.name = "Player";

        // Initialize stats
        this.level = 1;
        this.experience = 0;
        this.experienceForNextLevel = 100;
        this.gold = 0;
        this.maxMana = 50;
        this.currentMana = maxMana;

        // Base attributes
        this.strength = 10;
        this.dexterity = 10;
        this.constitution = 10;
        this.intelligence = 10;

        // Recalculate derived stats
        this.attackDamage = strength + 5; // Base damage 15 at start
        this.defense = 2;

        // Create inventory (20 slots)
        this.inventory = new Inventory(20);

        System.out.println("[Player] Created new player at (" + startX + ", " + startY + ")");
        System.out.println("[Player] Starting stats: HP=" + maxHealth + ", Damage=" + attackDamage);
    }

    @Override
    public void update(float deltaTime) {
        // Update movement animation
        updateMovement(deltaTime);

        // Update attack cooldown
        updateAttack(deltaTime);

        // Regenerate mana over time
        if (currentMana < maxMana) {
            currentMana = Math.min(currentMana + (int)(5 * deltaTime), maxMana);
        }

        // Handle state-specific logic
        switch (state) {
            case IDLE:
                // Could add idle animation here
                break;
            case MOVING:
                // Movement is handled in updateMovement()
                break;
            case ATTACKING:
                // Attack animation handling
                break;
            case DEAD:
                // Player death handling
                break;
        }
    }

    /**
     * Pick up an item (uses Inventory - Composite Pattern)
     */
    public void pickUpItem(com.roguelike.core.items.Item item) {
        if (inventory.addItem(item)) {
            System.out.println("[Player] Picked up: " + item.getName());
        } else {
            System.out.println("[Player] Inventory full! Cannot pick up: " + item.getName());
        }
    }

    /**
     * Drop an item from inventory
     */
    public com.roguelike.core.items.Item dropItem(int index) {
        return inventory.dropItem(index);
    }

    /**
     * Use an item from inventory
     */
    public void useInventoryItem(int index) {
        com.roguelike.core.items.Item item = inventory.getItems().get(index);
        if (item != null) {
            useItem(item);
        }
    }

    /**
     * Use a specific item
     */
    public void useItem(com.roguelike.core.items.Item item) {
        System.out.println("[Player] Using item: " + item.getName());

        switch (item.getType()) {
            case POTION:
                // Health potion
                heal(50);
                break;
            case WEAPON:
                // Equip weapon
                this.attackDamage = ((com.roguelike.core.items.Sword) item).getDamage() + strength;
                System.out.println("[Player] Equipped weapon! New damage: " + attackDamage);
                break;
            case ARMOR:
                // Equip armor
                this.defense += 5;
                System.out.println("[Player] Equipped armor! New defense: " + defense);
                break;
            default:
                break;
        }
    }

    /**
     * Cast a spell (uses mana)
     */
    public boolean castSpell(int manaCost) {
        if (currentMana >= manaCost) {
            currentMana -= manaCost;
            System.out.println("[Player] Cast spell! Mana remaining: " + currentMana);
            return true;
        } else {
            System.out.println("[Player] Not enough mana! Need: " + manaCost + ", Have: " + currentMana);
            return false;
        }
    }

    /**
     * Gain experience and check for level up
     */
    public void gainExperience(int amount) {
        this.experience += amount;
        System.out.println("[Player] Gained " + amount + " XP! Total: " + experience + "/" + experienceForNextLevel);

        // Check for level up
        while (experience >= experienceForNextLevel) {
            levelUp();
        }
    }

    /**
     * Level up the player
     */
    private void levelUp() {
        this.level++;

        // Increase stats
        this.maxHealth += 20;
        this.health = maxHealth;
        this.maxMana += 10;
        this.currentMana = maxMana;
        this.strength += 2;
        this.constitution += 2;

        // Recalculate derived stats
        this.attackDamage = strength + 5;

        // Calculate next level requirement
        this.experience -= experienceForNextLevel;
        this.experienceForNextLevel = (int)(experienceForNextLevel * 1.2); // 20% more XP needed each level

        System.out.println("[Player] *** LEVEL UP! Now level " + level + " ***");
        System.out.println("[Player] New stats: HP=" + maxHealth + ", Mana=" + maxMana + ", Damage=" + attackDamage);
    }

    /**
     * Add gold to player
     */
    public void addGold(int amount) {
        this.gold += amount;
        System.out.println("[Player] Found " + amount + " gold! Total: " + gold);
    }

    /**
     * Spend gold
     */
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Get player statistics as string (for display)
     */
    public String getStatsString() {
        return String.format(
            "Level: %d | HP: %.0f/%.0f | Mana: %d/%d | Gold: %d | XP: %d/%d",
            level, health, maxHealth, currentMana, maxMana, gold, experience, experienceForNextLevel
        );
    }

    /**
     * Get inventory contents as string
     */
    public String getInventoryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY ===\n");
        if (inventory.getSize() == 0) {
            sb.append("Empty\n");
        } else {
            int index = 0;
            for (com.roguelike.core.items.Item item : inventory.getItems()) {
                sb.append(index).append(". ").append(item.getName()).append("\n");
                index++;
            }
        }
        sb.append("Capacity: ").append(inventory.getSize()).append("/").append(inventory.getCapacity()).append("\n");
        return sb.toString();
    }

    // ==================== Getters and Setters ====================

    public Inventory getInventory() { return inventory; }

    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getExperienceForNextLevel() { return experienceForNextLevel; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getMaxMana() { return maxMana; }
    public int getCurrentMana() { return currentMana; }
    public void setCurrentMana(int mana) { this.currentMana = Math.min(mana, maxMana); }

    public int getStrength() { return strength; }
    public int getDexterity() { return dexterity; }
    public int getConstitution() { return constitution; }
    public int getIntelligence() { return intelligence; }
}
