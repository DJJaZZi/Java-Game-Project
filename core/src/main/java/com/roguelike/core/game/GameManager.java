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

/**
 * GameManager (Singleton Pattern)
 * CORRECTED VERSION - All fixes applied
 */
public class GameManager implements CombatListener, CollisionListener {
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

    /**
     * Private constructor for Singleton Pattern
     */
    private GameManager() {
        this.gameState = GameState.MENU;
        this.previousState = GameState.MENU;
        this.currentFloor = 1;
        this.score = 0;
        this.levelCompleted = false;
        this.stateListeners = new ArrayList<>();
    }

    /**
     * Get singleton instance
     */
    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Initialize the game manager and all systems
     */
    public void init() {
        System.out.println("[GameManager] Initializing game systems...");

        // Initialize all systems
        this.dungeonGenerator = new DungeonGenerator();
        this.collisionSystem = new CollisionSystem();
        this.combatSystem = new CombatSystem();
        this.aiSystem = new AISystem();

        // Register listeners
        this.combatSystem.addListener(this);
        this.collisionSystem.addListener(this);

        System.out.println("[GameManager] All systems initialized!");
    }

    /**
     * Start a new game
     */
    public void newGame() {
        System.out.println("[GameManager] Starting new game...");

        this.currentFloor = 1;
        this.score = 0;
        this.player = new Player(10, 10); // Starting position

        // Generate first level
        generateNewLevel();

        // Update state
        setState(GameState.PLAYING);

        System.out.println("[GameManager] New game started! Floor: " + currentFloor);
    }

    /**
     * Generate the next dungeon level (Factory Pattern)
     */
    public void generateNewLevel() {
        System.out.println("[GameManager] Generating floor " + currentFloor + "...");

        if (dungeonGenerator == null) {
            System.err.println("[GameManager] ERROR: DungeonGenerator not initialized!");
            return;
        }

        this.currentLevel = dungeonGenerator.generate(currentFloor);
        this.levelCompleted = false;

        // Place player in the level
        if (currentLevel != null && player != null) {
            currentLevel.placeEntity(player, player.getX(), player.getY());
        }

        System.out.println("[GameManager] Floor " + currentFloor + " generated!");
        if (currentLevel != null) {
            System.out.println("[GameManager] Enemies spawned: " + currentLevel.getEnemies().size());
        }
    }

    /**
     * Update all game systems each frame
     */
    public void update(float deltaTime) {
        if (gameState != GameState.PLAYING) {
            return;
        }

        if (player == null || currentLevel == null) {
            System.err.println("[GameManager] ERROR: Player or Level is null!");
            return;
        }

        // Update player
        player.update(deltaTime);

        // Update AI (Strategy Pattern)
        if (aiSystem != null) {
            aiSystem.update(currentLevel.getEnemies(), player, deltaTime);
        }

        // Check collisions (Observer Pattern)
        if (collisionSystem != null) {
            collisionSystem.checkCollisions(player, currentLevel);
        }

        // Update all entities in level
        currentLevel.update(deltaTime);

        // Check win condition
        checkLevelCompletion();
    }

    /**
     * Check if the level is completed
     */
    private void checkLevelCompletion() {
        if (currentLevel != null && currentLevel.getEnemies().isEmpty() && !levelCompleted) {
            levelCompleted = true;
            onLevelComplete();
        }
    }

    /**
     * Called when a level is completed
     */
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

    /**
     * Go to next level
     */
    public void nextLevel() {
        if (currentFloor < MAX_FLOORS) {
            currentFloor++;
            generateNewLevel();
            setState(GameState.PLAYING);
        } else {
            setState(GameState.GAME_OVER);
        }
    }

    /**
     * Handle player input
     */
    public void handleInput(PlayerAction action) {
        if (action == null) return;

        if (gameState != GameState.PLAYING) {
            if (action == PlayerAction.PAUSE && gameState == GameState.PAUSE) {
                setState(GameState.PLAYING);
            }
            return;
        }

        if (player == null || currentLevel == null) return;

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
                if (combatSystem != null) {
                    combatSystem.handlePlayerAttack(player, currentLevel);
                }
                break;
            case USE_ITEM:
                if (player.getInventory() != null) {
                    player.getInventory().useCurrentItem();
                }
                break;
            case NEXT_ITEM:
                if (player.getInventory() != null) {
                    player.getInventory().switchToNextItem();
                }
                break;
            case PAUSE:
                setState(GameState.PAUSE);
                break;
            case INVENTORY:
                setState(GameState.INVENTORY_SCREEN);
                break;
            default:
                break;
        }
    }

    /**
     * Handle player movement
     */
    private void handlePlayerMove(int dx, int dy) {
        if (player == null || currentLevel == null) return;

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (currentLevel.isWalkable(newX, newY)) {
            player.moveTo(newX, newY);
            System.out.println("[GameManager] Player moved to: (" + newX + ", " + newY + ")");
        }
    }

    /**
     * Handle player death
     */
    public void onPlayerDeath() {
        System.out.println("[GameManager] *** GAME OVER! Player died! ***");
        System.out.println("[GameManager] Final Score: " + score);
        setState(GameState.GAME_OVER);
    }

    /**
     * Change game state
     */
    private void setState(GameState newState) {
        if (this.gameState != newState) {
            this.previousState = this.gameState;
            this.gameState = newState;

            System.out.println("[GameManager] State: " + previousState + " → " + newState);

            for (GameStateListener listener : stateListeners) {
                listener.onStateChanged(previousState, newState);
            }
        }
    }

    /**
     * Register listeners
     */
    public void addStateListener(GameStateListener listener) {
        stateListeners.add(listener);
    }

    public void removeStateListener(GameStateListener listener) {
        stateListeners.remove(listener);
    }

    /**
     * Cleanup
     */
    public void dispose() {
        System.out.println("[GameManager] Disposing...");
    }

    // ==================== Observer Implementation ====================

    @Override
    public void onPlayerEnemyCollision(Player player, Enemy enemy) {
        System.out.println("[GameManager] Collision: " + player.getName() + " hit " + enemy.getName());
    }

    @Override
    public void onHit(com.roguelike.core.entities.Entity attacker,
                      com.roguelike.core.entities.Entity defender, float damage) {
        // Handle hit
    }

    @Override
    public void onCriticalHit(com.roguelike.core.entities.Entity attacker,
                              com.roguelike.core.entities.Entity defender, float damage) {
        System.out.println("[GameManager] CRITICAL HIT!");
    }

    @Override
    public void onDodge(com.roguelike.core.entities.Entity attacker,
                        com.roguelike.core.entities.Entity defender) {
        System.out.println("[GameManager] Attack dodged!");
    }

    @Override
    public void onDefenderDeath(com.roguelike.core.entities.Entity attacker,
                                com.roguelike.core.entities.Entity defender) {
        if (defender instanceof Enemy) {
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

    // ==================== Getters ====================

    public GameState getGameState() { return gameState; }
    public DungeonLevel getCurrentLevel() { return currentLevel; }
    public Player getPlayer() { return player; }
    public int getCurrentFloor() { return currentFloor; }
    public int getScore() { return score; }

    public void addScore(int points) {
        this.score += points;
    }

    public boolean isLevelCompleted() { return levelCompleted; }

    public AISystem getAISystem() { return aiSystem; }
    public CombatSystem getCombatSystem() { return combatSystem; }
    public CollisionSystem getCollisionSystem() { return collisionSystem; }
}
