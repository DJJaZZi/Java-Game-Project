package com.roguelike.core.game;

import com.roguelike.input.PlayerAction;

public interface GameState {
    void onEnter(GameManager gameManager);
    void update(GameManager gameManager, float deltaTime);
    void handleInput(GameManager gameManager, PlayerAction action);
    void onExit(GameManager gameManager);
    GameStateType getType();
}
