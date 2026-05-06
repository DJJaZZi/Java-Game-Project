package com.roguelike.input;

import com.roguelike.core.game.GameManager;

public class PlayerController {
    private Command moveUp, moveDown, moveLeft, moveRight;
    private Command attack, useItem;
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 150;

    public PlayerController(GameManager gameManager) {
        this.moveUp    = () -> gameManager.handleInput(PlayerAction.MOVE_UP);
        this.moveDown  = () -> gameManager.handleInput(PlayerAction.MOVE_DOWN);
        this.moveLeft  = () -> gameManager.handleInput(PlayerAction.MOVE_LEFT);
        this.moveRight = () -> gameManager.handleInput(PlayerAction.MOVE_RIGHT);
        this.attack    = () -> gameManager.handleInput(PlayerAction.ATTACK);
        this.useItem   = () -> gameManager.handleInput(PlayerAction.USE_ITEM);
    }

    public void openInventory() {
        gameManager.handleInput(PlayerAction.INVENTORY);
    }

    public void moveUp()    { if (canMove()) { moveUp.execute();    updateTime(); } }
    public void moveDown()  { if (canMove()) { moveDown.execute();  updateTime(); } }
    public void moveLeft()  { if (canMove()) { moveLeft.execute();  updateTime(); } }
    public void moveRight() { if (canMove()) { moveRight.execute(); updateTime(); } }
    public void attack()    { attack.execute(); }
    public void useItem()   { useItem.execute(); }

    public void pause()        { /* handled by GameManager state */ }
    public void toggleMenu()   { System.out.println("Menu toggled!"); }
    public void previousItem() { /* handled via useItem chain */ }
    public void nextItem()     { /* handled via useItem chain */ }

    private boolean canMove() {
        return System.currentTimeMillis() - lastMoveTime >= MOVE_DELAY;
    }

    private void updateTime() {
        lastMoveTime = System.currentTimeMillis();
    }
}
