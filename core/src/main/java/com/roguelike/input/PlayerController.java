package com.roguelike.input;

import com.roguelike.core.game.GameManager;

public class PlayerController {
    private Command moveUp, moveDown, moveLeft, moveRight;
    private Command attack, useItem;
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 150;

    public PlayerController(GameManager gameManager) {
        this.moveUp    = new MoveCommand(gameManager,  0,  1);
        this.moveDown  = new MoveCommand(gameManager,  0, -1);
        this.moveLeft  = new MoveCommand(gameManager, -1,  0);
        this.moveRight = new MoveCommand(gameManager,  1,  0);
        this.attack    = new AttackCommand(gameManager);
        this.useItem   = new UseItemCommand(gameManager);
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
