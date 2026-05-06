package com.roguelike.core.game;

import com.roguelike.input.PlayerAction;

public class PausedState implements GameState {

    @Override
    public void onEnter(GameManager gm) {
        System.out.println("[PausedState] Game paused");
    }

    @Override
    public void update(GameManager gm, float deltaTime) {
        // Nothing updates while paused
    }

    @Override
    public void handleInput(GameManager gm, PlayerAction action) {
        if (action == PlayerAction.PAUSE) {
            gm.setState(new PlayingState());
        }
    }

    @Override
    public void onExit(GameManager gm) {
        System.out.println("[PausedState] Game resumed");
    }

    @Override
    public GameStateType getType() {
        return GameStateType.PAUSE;
    }
}
