package com.roguelike.core.game;

import com.roguelike.input.PlayerAction;

public class GameOverState implements GameState {

    @Override
    public void onEnter(GameManager gm) {
        System.out.println("[GameOverState] Game over. Final score: " + gm.getScore());
    }

    @Override
    public void update(GameManager gm, float deltaTime) {
        // Nothing updates on game over
    }

    @Override
    public void handleInput(GameManager gm, PlayerAction action) {
        // Could add "press any key to restart" here later
    }

    @Override
    public void onExit(GameManager gm) {}

    @Override
    public GameStateType getType() {
        return GameStateType.GAME_OVER;
    }
}
