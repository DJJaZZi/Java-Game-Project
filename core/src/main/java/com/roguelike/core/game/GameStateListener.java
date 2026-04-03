package com.roguelike.core.game;

public interface GameStateListener {
    void onStateChanged(GameState oldState, GameState newState);
}
