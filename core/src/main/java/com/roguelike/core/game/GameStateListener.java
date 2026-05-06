package com.roguelike.core.game;

public interface GameStateListener {
    void onStateChanged(GameStateType oldState, GameStateType newState);
}
