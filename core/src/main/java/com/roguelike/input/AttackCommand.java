package com.roguelike.input;

import com.roguelike.core.game.GameManager;

public class AttackCommand implements Command {
    private GameManager gameManager;

    public AttackCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void execute() {
        // Routes through current state — PausedState will silently ignore it
        gameManager.handleInput(PlayerAction.ATTACK);
    }
}
