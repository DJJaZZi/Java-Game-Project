package com.roguelike.input;

import com.roguelike.core.game.GameManager;

public class UseItemCommand implements Command {
    private GameManager gameManager;

    public UseItemCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void execute() {
        if (gameManager.getPlayer() != null &&
            gameManager.getPlayer().getInventory() != null) {
            gameManager.getPlayer().getInventory().useCurrentItem();
        }
    }
}
