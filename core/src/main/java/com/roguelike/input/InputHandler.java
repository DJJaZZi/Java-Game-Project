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
    private boolean[] wasPressed = new boolean[256];

    public InputHandler(GameManager gameManager, PlayerController playerController) {
        this.gameManager = gameManager;
        this.playerController = playerController;
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)   ||
            Gdx.input.isKeyPressed(Input.Keys.W))     playerController.moveUp();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)  ||
            Gdx.input.isKeyPressed(Input.Keys.S))     playerController.moveDown();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)  ||
            Gdx.input.isKeyPressed(Input.Keys.A))     playerController.moveLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Input.Keys.D))     playerController.moveRight();

        if (justPressed(Input.Keys.SPACE))  playerController.attack();
        if (justPressed(Input.Keys.E))      playerController.useItem();
        if (justPressed(Input.Keys.P))      playerController.pause();
        if (justPressed(Input.Keys.R))      playerController.nextItem();
    }

    private boolean justPressed(int key) {
        boolean down = Gdx.input.isKeyPressed(key);
        boolean triggered = down && !wasPressed[key];
        wasPressed[key] = down;
        return triggered;
    }
}
