package com.roguelike.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.roguelike.core.game.GameManager;
import com.roguelike.rendering.GameRenderer;

public class InputHandler {

    private final PlayerController playerController;
    private final GameManager      gameManager;
    private final GameRenderer     gameRenderer;        // ← NEW
    private final boolean[]        wasPressed = new boolean[256];

    public InputHandler(GameManager gameManager,
                        PlayerController playerController,
                        GameRenderer gameRenderer) {    // ← NEW param
        this.gameManager      = gameManager;
        this.playerController = playerController;
        this.gameRenderer     = gameRenderer;
    }

    public void handleInput() {
        // Movement
        if (Gdx.input.isKeyPressed(Input.Keys.UP)    ||
            Gdx.input.isKeyPressed(Input.Keys.W))      playerController.moveUp();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)  ||
            Gdx.input.isKeyPressed(Input.Keys.S))      playerController.moveDown();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)  ||
            Gdx.input.isKeyPressed(Input.Keys.A))      playerController.moveLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Input.Keys.D))      playerController.moveRight();

        // Actions
        if (justPressed(Input.Keys.SPACE))  playerController.attack();
        if (justPressed(Input.Keys.E))      playerController.useItem();
        if (justPressed(Input.Keys.P))      playerController.pause();
        if (justPressed(Input.Keys.R))      playerController.nextItem();

        // Debug toggle
        if (justPressed(Input.Keys.F1))     gameRenderer.toggleWalkableDebug(); // ← NEW
    }

    private boolean justPressed(int key) {
        boolean down = Gdx.input.isKeyPressed(key);
        boolean triggered = down && !wasPressed[key];
        wasPressed[key] = down;
        return triggered;
    }
}
