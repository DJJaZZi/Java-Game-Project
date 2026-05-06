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
        // dx/dy need to map to the correct PlayerAction
        if      (dx ==  1) gameManager.handleInput(PlayerAction.MOVE_RIGHT);
        else if (dx == -1) gameManager.handleInput(PlayerAction.MOVE_LEFT);
        else if (dy ==  1) gameManager.handleInput(PlayerAction.MOVE_UP);
        else if (dy == -1) gameManager.handleInput(PlayerAction.MOVE_DOWN);
    }
}
