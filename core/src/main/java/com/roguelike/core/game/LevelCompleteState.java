package com.roguelike.core.game;

import com.roguelike.input.PlayerAction;

public class LevelCompleteState implements GameState {

    @Override
    public void onEnter(GameManager gm) {
        System.out.println("[LevelCompleteState] Level complete! Score: " + gm.getScore());
    }

    @Override
    public void update(GameManager gm, float deltaTime) {}

    @Override
    public void handleInput(GameManager gm, PlayerAction action) {
        if (action == PlayerAction.ATTACK || action == PlayerAction.MOVE_UP
            || action == PlayerAction.MOVE_DOWN /* etc */) {
            gm.nextLevel();
        }
    }

    @Override
    public void onExit(GameManager gm) {}

    @Override
    public GameStateType getType() {
        return GameStateType.LEVEL_COMPLETE;
    }
}
