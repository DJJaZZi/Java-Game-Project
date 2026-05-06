package com.roguelike.core.game;

import com.roguelike.input.PlayerAction;

public class PlayingState implements GameState {

    @Override
    public void onEnter(GameManager gm) {
        System.out.println("[PlayingState] Entered playing state");
    }

    @Override
    public void update(GameManager gm, float deltaTime) {
        if (gm.getPlayer() == null || gm.getCurrentLevel() == null) return;

        gm.getPlayer().update(deltaTime);

        if (gm.getAISystem() != null) {
            gm.getAISystem().update(
                gm.getCurrentLevel().getEnemies(),
                gm.getPlayer(),
                deltaTime
            );
        }

        if (gm.getCollisionSystem() != null) {
            gm.getCollisionSystem().checkCollisions(
                gm.getPlayer(),
                gm.getCurrentLevel()
            );
        }

        gm.getCurrentLevel().update(deltaTime);
        gm.checkLevelCompletion();
    }

    @Override
    public void handleInput(GameManager gm, PlayerAction action) {
        if (action == null) return;

        switch (action) {
            case MOVE_UP:    gm.handlePlayerMove(0,  1);  break;
            case MOVE_DOWN:  gm.handlePlayerMove(0, -1);  break;
            case MOVE_LEFT:  gm.handlePlayerMove(-1, 0);  break;
            case MOVE_RIGHT: gm.handlePlayerMove(1,  0);  break;
            case ATTACK:
                if (gm.getCombatSystem() != null) {
                    gm.getCombatSystem().handlePlayerAttack(
                        gm.getPlayer(), gm.getCurrentLevel()
                    );
                }
                break;
            case USE_ITEM:
                if (gm.getPlayer().getInventory() != null) {
                    gm.getPlayer().getInventory().useCurrentItem();
                }
                break;
            case NEXT_ITEM:
                if (gm.getPlayer().getInventory() != null) {
                    gm.getPlayer().getInventory().switchToNextItem();
                }
                break;
            case PAUSE:
                gm.setState(new PausedState());
                break;
            default:
                break;
            case INVENTORY:
                // Future: transition to inventory screen state
                System.out.println("[PlayingState] Inventory opened");
                break;
        }
    }

    @Override
    public void onExit(GameManager gm) {
        System.out.println("[PlayingState] Exiting playing state");
    }

    @Override
    public GameStateType getType() {
        return GameStateType.PLAYING;
    }
}
