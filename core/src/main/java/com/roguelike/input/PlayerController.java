package com.roguelike.input;

import com.roguelike.core.game.GameManager;

/**
 * PlayerController - Translates input into player actions
 * <p>
 * Responsibility:
 * - Execute player commands
 * - Validate actions
 * - Update game state through GameManager
 */
public class PlayerController {
    private GameManager gameManager;
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 150; // Milliseconds between moves (turn-based feel)

    public PlayerController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // ============ MOVEMENT COMMANDS ============

    /**
     * Move player up
     */
    public void moveUp() {
        if (canExecuteAction()) {
            gameManager.handleInput(PlayerAction.MOVE_UP);
            lastMoveTime = System.currentTimeMillis();
        }
    }

    /**
     * Move player down
     */
    public void moveDown() {
        if (canExecuteAction()) {
            gameManager.handleInput(PlayerAction.MOVE_DOWN);
            lastMoveTime = System.currentTimeMillis();
        }
    }

    /**
     * Move player left
     */
    public void moveLeft() {
        if (canExecuteAction()) {
            gameManager.handleInput(PlayerAction.MOVE_LEFT);
            lastMoveTime = System.currentTimeMillis();
        }
    }

    /**
     * Move player right
     */
    public void moveRight() {
        if (canExecuteAction()) {
            gameManager.handleInput(PlayerAction.MOVE_RIGHT);
            lastMoveTime = System.currentTimeMillis();
        }
    }

    // ============ ACTION COMMANDS ============

    /**
     * Attack adjacent enemy
     */
    public void attack() {
        gameManager.handleInput(PlayerAction.ATTACK);
    }

    /**
     * Use current item from inventory
     */
    public void useItem() {
        gameManager.handleInput(PlayerAction.USE_ITEM);
    }

    /**
     * Pause the game
     */
    public void pause() {
        gameManager.handleInput(PlayerAction.PAUSE);
    }

    /**
     * Toggle menu
     */
    public void toggleMenu() {
        // Menu toggle logic
        System.out.println("Menu toggled!");
    }

    // ============ INVENTORY COMMANDS ============

    /**
     * Switch to previous item
     */
    public void previousItem() {
        if (gameManager.getPlayer() != null) {
            gameManager.getPlayer().getInventory().switchToNextItem();
        }
    }

    /**
     * Switch to next item
     */
    public void nextItem() {
        if (gameManager.getPlayer() != null) {
            gameManager.getPlayer().getInventory().switchToNextItem();
        }
    }

    // ============ HELPER METHODS ============

    /**
     * Check if enough time has passed to execute next action
     * (Turn-based movement delay)
     */
    private boolean canExecuteAction() {
        long timeSinceLastMove = System.currentTimeMillis() - lastMoveTime;
        return timeSinceLastMove >= MOVE_DELAY;
    }
}
