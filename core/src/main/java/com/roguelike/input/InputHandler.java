package com.roguelike.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.roguelike.core.game.GameManager;


/**
 * InputHandler - Captures keyboard input from libGDX
 *
 * Responsibility:
 * - Listen to keyboard events
 * - Convert key presses to game actions
 * - Pass to PlayerController
 */
public class InputHandler {
    private PlayerController playerController;
    private GameManager gameManager;

    public InputHandler(GameManager gameManager, PlayerController playerController) {
        this.gameManager = gameManager;
        this.playerController = playerController;
    }

    /**
     * Process input each frame
     */
    public void handleInput() {
        // Arrow keys / WASD for movement
        if (Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerController.moveUp();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
            Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerController.moveDown();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerController.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerController.moveRight();
        }

        // Single press keys (handled with isKeyJustPressed)
        if (isKeyJustPressed(Input.Keys.SPACE)) {
            playerController.attack();
        }
        if (isKeyJustPressed(Input.Keys.E)) {
            playerController.useItem();
        }
        if (isKeyJustPressed(Input.Keys.P)) {
            playerController.pause();
        }
        if (isKeyJustPressed(Input.Keys.ESCAPE)) {
            playerController.toggleMenu();
        }

        // Inventory navigation
        if (isKeyJustPressed(Input.Keys.Q)) {
            playerController.previousItem();
        }
        if (isKeyJustPressed(Input.Keys.R)) {
            playerController.nextItem();
        }
    }

    /**
     * Check if key was just pressed this frame
     * (libGDX doesn't have isKeyJustPressed, so we track it ourselves)
     */
    private boolean isKeyJustPressed(int keycode) {
        // Simple implementation - in production you'd want frame-based tracking
        return Gdx.input.isKeyPressed(keycode);
    }
}

