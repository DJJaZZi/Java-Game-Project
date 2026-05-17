package com.roguelike.core.game;

import com.roguelike.core.dungeon.DungeonGenerator;
import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.LevelBounds;
import com.roguelike.core.dungeon.RoomType;
import com.roguelike.core.dungeon.Tile;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Entity;
import com.roguelike.core.entities.EntityState;
import com.roguelike.core.entities.Player;
import com.roguelike.core.input.PlayerAction;
import com.roguelike.core.systems.AISystem;
import com.roguelike.core.systems.CollisionListener;
import com.roguelike.core.systems.CollisionSystem;
import com.roguelike.core.systems.CombatListener;
import com.roguelike.core.systems.CombatSystem;

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
    private CollisionSystem  collisionSystem;
    private CombatSystem     combatSystem;
    private AISystem         aiSystem;

    private List<GameStateListener> stateListeners;
    private static final int MAX_FLOORS = 10;

    private GameManager() {
        this.currentFloor  = 1;
        this.score         = 0;
        this.levelCompleted = false;
        this.stateListeners = new ArrayList<>();
        this.currentState  = new GameOverState();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    public void init() {
        System.out.println("[GameManager] Initializing...");
        this.dungeonGenerator = new DungeonGenerator();
        this.collisionSystem  = new CollisionSystem();
        this.combatSystem     = new CombatSystem();
        this.aiSystem         = new AISystem(combatSystem); // ← передаём combatSystem
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
        this.currentLevel    = dungeonGenerator.generate(currentFloor);
        this.levelCompleted  = false;

        if (currentLevel != null && player != null) {
            int spawnX = 4;
            int spawnY = 6;

            Tile spawnTile = currentLevel.getTile(spawnX, spawnY);
            if (spawnTile != null) {
                spawnTile.setOccupant(null);
                player.setPosition(spawnX, spawnY);
                spawnTile.setOccupant(player);
                System.out.println("[GameManager] Player spawned at (" + spawnX + "," + spawnY + ")");
            }

            for (Enemy enemy : currentLevel.getEnemies()) {
                enemy.setDungeonLevel(currentLevel);
            }
        }
    }

    public void checkLevelCompletion() {
        if (currentLevel != null && currentLevel.getEnemies().isEmpty() && !levelCompleted) {
            levelCompleted = true;
            score += 500 + (currentFloor * 100);

            if (currentFloor >= MAX_FLOORS) setState(new GameOverState());
            else setState(new LevelCompleteState());
        }
    }

    public void update(float deltaTime) {
        currentState.update(this, deltaTime);
    }

    public void handleInput(PlayerAction action) {
        currentState.handleInput(this, action);
    }

    // ══════════════════════════════════════════════════════════════
    //  ДВИЖЕНИЕ ИГРОКА + БЛОКИРОВКА КОРИДОРА
    // ══════════════════════════════════════════════════════════════

    public void handlePlayerMove(int dx, int dy) {
        if (player == null || currentLevel == null) return;

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (!currentLevel.isFloor(newX, newY)) return;

        // Атака при столкновении с врагом
        Entity occupant = currentLevel.getOccupantAt(newX, newY);
        if (occupant instanceof Enemy) {
            Enemy enemy = (Enemy) occupant;
            if (enemy.isAlive()) {
                player.startAttack();
                combatSystem.attack(player, enemy);
                checkDefenderDeath(enemy);
                return;
            }
        }

        // ── БЛОКИРОВКА КОРИДОРА ─────────────────────────────────────────────
        LevelBounds currentRoom = currentLevel.getBoundsAt(player.getX(), player.getY());
        LevelBounds targetRoom  = currentLevel.getBoundsAt(newX, newY);

        if (currentRoom != null && currentRoom != targetRoom) {
            // Игрок пытается выйти из GOBLIN комнаты — проверяем, все ли мертвы
            if (currentRoom.roomType == RoomType.GOBLIN
                && !currentLevel.isRoomCleared(currentRoom)) {
                System.out.println("[GameManager] ⚔ Победи всех врагов в комнате!");
                return; // ← блокируем движение
            }
        }

        // ── Обычное движение ───────────────────────────────────────────────
        player.setState(EntityState.MOVING);
        currentLevel.moveEntity(player, newX, newY);

        if (dx < 0) player.setFacingLeft(true);
        if (dx > 0) player.setFacingLeft(false);

        player.startMoving();
    }

    private void checkDefenderDeath(Enemy enemy) {
        if (!enemy.isAlive()) {
            Tile tile = currentLevel.getTile(enemy.getX(), enemy.getY());
            if (tile != null) tile.clearOccupant();
            player.gainExperience(enemy.getXpReward());
            score += enemy.getXpReward() * 10;
            System.out.println("[GameManager] " + enemy.getName() + " defeated! +" + enemy.getXpReward() + " XP");
        }
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

    public void onPlayerDeath() {
        setState(new GameOverState());
    }

    public void addScore(int points) { this.score += points; }

    public void addStateListener(GameStateListener listener)    { stateListeners.add(listener); }
    public void removeStateListener(GameStateListener listener) { stateListeners.remove(listener); }

    // ══════════════════════════════════════════════════════════════
    //  CombatListener
    // ══════════════════════════════════════════════════════════════

    @Override
    public void onPlayerEnemyCollision(Player player, Enemy enemy) {}

    @Override
    public void onHit(Entity a, Entity d, float dmg) {}

    @Override
    public void onCriticalHit(Entity a, Entity d, float dmg) {
        System.out.println("[GameManager] CRITICAL HIT!");
    }

    @Override
    public void onDodge(Entity a, Entity d) {
        System.out.println("[GameManager] Dodged!");
    }

    @Override
    public void onDefenderDeath(Entity attacker, Entity defender) {
        if (defender instanceof Enemy) {
            Enemy enemy = (Enemy) defender;
            if (player != null) {
                player.gainExperience(enemy.getExperienceReward());
                player.addGold(enemy.getGoldReward());
                addScore(100);
                System.out.println("[GameManager] " + enemy.getName() + " defeated! +"
                    + enemy.getExperienceReward() + " XP, +" + enemy.getGoldReward() + " gold");
            }
        } else if (defender instanceof Player) {
            System.out.println("[GameManager] Player has died! Game Over.");
            onPlayerDeath();
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  Getters
    // ══════════════════════════════════════════════════════════════

    public GameStateType getGameStateType()          { return currentState.getType(); }
    public GameState getCurrentGameState()           { return currentState; }
    public DungeonLevel getCurrentLevel()            { return currentLevel; }
    public Player getPlayer()                        { return player; }
    public int getCurrentFloor()                     { return currentFloor; }
    public int getScore()                            { return score; }
    public boolean isLevelCompleted()                { return levelCompleted; }
    public AISystem getAISystem()                    { return aiSystem; }
    public CombatSystem getCombatSystem()            { return combatSystem; }
    public CollisionSystem getCollisionSystem()      { return collisionSystem; }

    public void dispose() {
        System.out.println("[GameManager] Disposing...");
    }
}
