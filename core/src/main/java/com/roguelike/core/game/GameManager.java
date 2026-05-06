package com.roguelike.core.game;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.DungeonGenerator;
import com.roguelike.core.entities.Player;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.systems.CollisionSystem;
import com.roguelike.core.systems.CombatSystem;
import com.roguelike.core.systems.CombatListener;
import com.roguelike.core.systems.CollisionListener;
import com.roguelike.core.systems.AISystem;
import com.roguelike.input.PlayerAction;
import java.util.ArrayList;
import java.util.List;

public class GameManager implements CombatListener, CollisionListener {
    private static GameManager instance;

    private GameState currentState;
    private DungeonLevel currentLevel;
    private Player player;
    private int currentFloor;
    private int score;
    private boolean levelCompleted;

    private DungeonGenerator dungeonGenerator;
    private CollisionSystem collisionSystem;
    private CombatSystem combatSystem;
    private AISystem aiSystem;

    private List<GameStateListener> stateListeners;
    private static final int MAX_FLOORS = 10;

    private GameManager() {
        this.currentFloor = 1;
        this.score = 0;
        this.levelCompleted = false;
        this.stateListeners = new ArrayList<>();
        this.currentState = new GameOverState(); // safe default before init
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void init() {
        System.out.println("[GameManager] Initializing...");
        this.dungeonGenerator = new DungeonGenerator();
        this.collisionSystem = new CollisionSystem();
        this.combatSystem = new CombatSystem();
        this.aiSystem = new AISystem();
        this.combatSystem.addListener(this);
        this.collisionSystem.addListener(this);
        System.out.println("[GameManager] All systems initialized!");
    }

    public void newGame() {
        this.currentFloor = 1;
        this.score = 0;
        this.player = new Player(10, 10);
        generateNewLevel();
        setState(new PlayingState());
    }

    public void generateNewLevel() {
        System.out.println("[GameManager] Generating floor " + currentFloor + "...");
        this.currentLevel = dungeonGenerator.generate(currentFloor);
        this.levelCompleted = false;

        if (currentLevel != null && player != null) {
            // Fix: give each enemy a reference to the level
            for (Enemy enemy : currentLevel.getEnemies()) {
                enemy.setDungeonLevel(currentLevel);
            }
            currentLevel.placeEntity(player, player.getX(), player.getY());
        }
    }

    // Called by PlayingState.update()
    public void checkLevelCompletion() {
        if (currentLevel != null && currentLevel.getEnemies().isEmpty() && !levelCompleted) {
            levelCompleted = true;
            score += 500 + (currentFloor * 100);

            if (currentFloor >= MAX_FLOORS) {
                setState(new GameOverState());
            } else {
                setState(new LevelCompleteState());
            }
        }
    }

    public void update(float deltaTime) {
        currentState.update(this, deltaTime);
    }

    public void handleInput(PlayerAction action) {
        currentState.handleInput(this, action);
    }

    public void nextLevel() {
        if (currentFloor < MAX_FLOORS) {
            currentFloor++;
            generateNewLevel();
            setState(new PlayingState());
        } else {
            setState(new GameOverState());
        }
    }

    public void setState(GameState newState) {
        GameStateType oldType = currentState != null ? currentState.getType() : null;

        if (currentState != null) currentState.onExit(this);
        currentState = newState;
        currentState.onEnter(this);

        for (GameStateListener listener : stateListeners) {
            listener.onStateChanged(oldType, newState.getType());
        }
    }

    // Temporary bridge so GameStateListener still compiles unchanged
    private com.roguelike.core.game.GameState mapToLegacy(GameStateType t) {
        return null; // listeners will be updated in a later step
    }

    public void onPlayerDeath() {
        setState(new GameOverState());
    }

    public void handlePlayerMove(int dx, int dy) {
        if (player == null || currentLevel == null) return;
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        if (currentLevel.isWalkable(newX, newY)) {
            player.moveTo(newX, newY);
        }
    }

    public void addScore(int points) { this.score += points; }

    public void addStateListener(GameStateListener listener) { stateListeners.add(listener); }
    public void removeStateListener(GameStateListener listener) { stateListeners.remove(listener); }

    // ===== CombatListener =====
    @Override
    public void onPlayerEnemyCollision(Player player, Enemy enemy) {}

    @Override
    public void onHit(com.roguelike.core.entities.Entity a,
                      com.roguelike.core.entities.Entity d, float dmg) {}

    @Override
    public void onCriticalHit(com.roguelike.core.entities.Entity a,
                              com.roguelike.core.entities.Entity d, float dmg) {
        System.out.println("[GameManager] CRITICAL HIT!");
    }

    @Override
    public void onDodge(com.roguelike.core.entities.Entity a,
                        com.roguelike.core.entities.Entity d) {
        System.out.println("[GameManager] Dodged!");
    }

    @Override
    public void onDefenderDeath(com.roguelike.core.entities.Entity attacker,
                                com.roguelike.core.entities.Entity defender) {
        if (defender instanceof Enemy) {
            // Rewards given here only — removed from CombatSystem to fix double XP bug
            Enemy enemy = (Enemy) defender;
            if (player != null) {
                player.gainExperience(enemy.getExperienceReward());
                player.addGold(enemy.getGoldReward());
                addScore(100);
            }
        } else if (defender instanceof Player) {
            onPlayerDeath();
        }
    }

    // ===== Getters =====
    public GameStateType getGameStateType() { return currentState.getType(); }
    public GameState getCurrentGameState()  { return currentState; }
    public DungeonLevel getCurrentLevel()   { return currentLevel; }
    public Player getPlayer()              { return player; }
    public int getCurrentFloor()           { return currentFloor; }
    public int getScore()                  { return score; }
    public boolean isLevelCompleted()      { return levelCompleted; }
    public AISystem getAISystem()          { return aiSystem; }
    public CombatSystem getCombatSystem()  { return combatSystem; }
    public CollisionSystem getCollisionSystem() { return collisionSystem; }

    public void dispose() {
        System.out.println("[GameManager] Disposing...");
    }
}
