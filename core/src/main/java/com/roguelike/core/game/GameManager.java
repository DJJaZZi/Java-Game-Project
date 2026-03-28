package com.roguelike.core.game;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.DungeonGenerator;
import com.roguelike.core.entities.Player;
import com.roguelike.core.systems.CollisionSystem;
import com.roguelike.core.systems.CombatSystem;
import com.roguelike.core.systems.AISystem;
import com.roguelike.core.input.PlayerAction;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    private GameState gameState;
    private GameState previousState;
    private DungeonLevel currentLevel;
    private Player player;
    private int currentFloor;
    private int score;
    private boolean levelCompleted;

    // Game Systems
    private DungeonGenerator dungeonGenerator;
    private CollisionSystem collisionSystem;
    private CombatSystem combatSystem;
    private AISystem aiSystem;

    // Event listeners
    private List<GameStateListener> stateListeners;

    // Configuration
    private static final int MAX_FLOORS = 10;

    private GameManager() {
        this.gameState = GameState.MENU;
        this.previousState = GameState.MENU;
        this.currentFloor = 1;
        this.score = 0;
        this.levelCompleted = false;
        this.stateListeners = new ArrayList<>();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void init() {
        System.out.println("[GameManager] Initializing game systems...");

        this.dungeonGenerator = new DungeonGenerator();
        this.collisionSystem = new CollisionSystem();
        this.combatSystem = new CombatSystem();
        this.aiSystem = new AISystem();

        System.out.println("[GameManager] All systems initialized!");
    }


    public void newGame() {
        System.out.println("[GameManager] Starting new game...");

        this.currentFloor = 1;
        this.score = 0;
        this.player = new Player(10, 10);

        generateNewLevel();

        setState(GameState.PLAYING);

        System.out.println("[GameManager] New game started! Floor: " + currentFloor);
    }

    public void generateNewLevel() {
        System.out.println("[GameManager] Generating floor " + currentFloor + "...");

        this.currentLevel = dungeonGenerator.generate(currentFloor);
        this.levelCompleted = false;

        currentLevel.placeEntity(player, player.getX(), player.getY());

        System.out.println("[GameManager] Floor " + currentFloor + " generated!");
        System.out.println("[GameManager] Enemies spawned: " + currentLevel.getEnemies().size());
    }


    public void update(float deltaTime) {
        if (gameState != GameState.PLAYING) {
            return;
        }

        player.update(deltaTime);

        aiSystem.update(currentLevel.getEnemies(), player, deltaTime);

        collisionSystem.checkCollisions(player, currentLevel);

        currentLevel.update(deltaTime);

        checkLevelCompletion();
    }

    private void checkLevelCompletion() {
        if (currentLevel.getEnemies().isEmpty() && !levelCompleted) {
            levelCompleted = true;
            onLevelComplete();
        }
    }

    private void onLevelComplete() {
        score += 500 + (currentFloor * 100);

        if (currentFloor >= MAX_FLOORS) {
            setState(GameState.GAME_OVER);
            System.out.println("[GameManager] *** GAME WON! Final Score: " + score + " ***");
        } else {
            setState(GameState.LEVEL_COMPLETE);
            System.out.println("[GameManager] Level completed! Score: " + score);
        }
    }

    public void nextLevel() {
        if (currentFloor < MAX_FLOORS) {
            currentFloor++;
            generateNewLevel();
            setState(GameState.PLAYING);
        } else {
            setState(GameState.GAME_OVER);
        }
    }

    public void handleInput(PlayerAction action) {
        if (gameState != GameState.PLAYING) {
            if (action == PlayerAction.PAUSE && gameState == GameState.PAUSE) {
                setState(GameState.PLAYING);
            }
            return;
        }

        switch (action) {
            case MOVE_UP:
                handlePlayerMove(0, 1);
                break;
            case MOVE_DOWN:
                handlePlayerMove(0, -1);
                break;
            case MOVE_LEFT:
                handlePlayerMove(-1, 0);
                break;
            case MOVE_RIGHT:
                handlePlayerMove(1, 0);
                break;
            case ATTACK:
                combatSystem.handlePlayerAttack(player, currentLevel);
                break;
            case USE_ITEM:
                player.getInventory().useCurrentItem();
                break;
            case NEXT_ITEM:
                player.getInventory().switchToNextItem();
                break;
            case PAUSE:
                setState(GameState.PAUSE);
                break;
            case INVENTORY:
                setState(GameState.INVENTORY_SCREEN);
                break;
        }
    }

    private void handlePlayerMove(int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;


        if (currentLevel.isWalkable(newX, newY)) {
            player.move(dx, dy);
            System.out.println("[GameManager] Player moved to: (" + newX + ", " + newY + ")");
        } else {
            System.out.println("[GameManager] Can't move there - blocked!");
        }
    }

    public void onPlayerDeath() {
        System.out.println("[GameManager] *** GAME OVER! Player died! ***");
        System.out.println("[GameManager] Final Score: " + score);
        setState(GameState.GAME_OVER);
    }

    private void setState(GameState newState) {
        if (this.gameState != newState) {
            this.previousState = this.gameState;
            this.gameState = newState;

            System.out.println("[GameManager] State changed: " + previousState + " -> " + newState);

            for (GameStateListener listener : stateListeners) {
                listener.onStateChanged(previousState, newState);
            }
        }
    }

    public void addStateListener(GameStateListener listener) {
        stateListeners.add(listener);
    }

    public void removeStateListener(GameStateListener listener) {
        stateListeners.remove(listener);
    }

    public void dispose() {
        System.out.println("[GameManager] Disposing resources...");
    }

    // ==================== Getters and Setters ====================

    public GameState getGameState() {
        return gameState;
    }

    public DungeonLevel getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
        System.out.println("[GameManager] Score increased by " + points + "! Total: " + score);
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public AISystem getAISystem() {
        return aiSystem;
    }

    public CombatSystem getCombatSystem() {
        return combatSystem;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }
}

interface GameStateListener {
    void onStateChanged(GameState oldState, GameState newState);
}
