package com.roguelike.input;

import com.roguelike.core.game.GameManager;

public class MoveCommand implements Command {
    private GameManager gameManager;
    private int dx, dy;

    public MoveCommand(GameManager gameManager, int dx, int dy) {
        this.gameManager = gameManager;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        gameManager.handlePlayerMove(dx, dy);
    }
}
