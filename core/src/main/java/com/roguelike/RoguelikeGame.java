package com.roguelike;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.roguelike.core.game.GameManager;
import com.roguelike.core.game.GameState;
import com.roguelike.input.InputHandler;
import com.roguelike.input.PlayerController;
import com.roguelike.rendering.GameRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RoguelikeGame extends ApplicationAdapter {
    private GameManager gameManager;
    private GameRenderer gameRenderer;
    private InputHandler inputHandler;
    private PlayerController playerController;

    private float accumulator = 0f;
    private static final float TIMESTEP = 1f/60f;

    @Override
    public void create() {
        System.out.println("=== Creating RoguelikeGame ===");
        gameManager = GameManager.getInstance();
        gameManager.init();

        renderer = new GameRenderer();
        playerController = new PlayerController(gameManager);
        inputHandler = new InputHandler(gameManager, playerController);

        gameManager.newGame();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        accumulator += deltaTime;

        inputHandler.handleInput();

        while (accumulator >= TIMESTEP) {
            gameManager.update(TIMESTEP);
            accumulator -= TIMESTEP;
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(gameManager);
    }

    @Override
    public void dispose() {
        gameManager.dispose();
        renderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }
}
