package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.roguelike.core.game.GameManager;
import com.roguelike.core.entities.Player;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.Tile;

/**
 * GameRenderer - Main rendering system
 * Handles all drawing to the screen
 */
public class GameRenderer {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private SpriteManager spriteManager;
    private UIRenderer uiRenderer;
    private float tileSize = 32f; // Size of each tile in pixels

    public GameRenderer() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        this.spriteManager = new SpriteManager();
        this.uiRenderer = new UIRenderer();
    }

    /**
     * Main render method called each frame
     */
    public void render(GameManager gameManager) {
        // Update camera
        updateCamera(gameManager);

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render game world
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Render dungeon
        if (gameManager.getCurrentLevel() != null) {
            renderDungeon(gameManager.getCurrentLevel());
            renderEntities(gameManager);
        }

        batch.end();

        // Render UI (on top, fixed screen space)
        batch.setProjectionMatrix(getUIProjectionMatrix());
        batch.begin();
        uiRenderer.render(batch, gameManager.getPlayer());
        batch.end();
    }

    /**
     * Render the dungeon tilemap
     */
    private void renderDungeon(DungeonLevel level) {
        Tile[][] tiles = level.getTiles();

        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                Tile tile = tiles[x][y];

                // Draw different colors based on tile type
                switch (tile.type) {
                    case WALL:
                        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
                        drawTile(x, y, 0.3f, 0.3f, 0.3f); // Dark gray
                        break;
                    case FLOOR:
                        shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 1);
                        drawTile(x, y, 0.6f, 0.6f, 0.6f); // Light gray
                        break;
                    case DOOR:
                        drawTile(x, y, 0.8f, 0.6f, 0.2f); // Gold
                        break;
                    case TRAP:
                        drawTile(x, y, 1f, 0.2f, 0.2f); // Red
                        break;
                    case CHEST:
                        drawTile(x, y, 1f, 0.8f, 0.2f); // Orange
                        break;
                }
            }
        }
    }

    /**
     * Render all entities (player and enemies)
     */
    private void renderEntities(GameManager gameManager) {
        // Render player
        if (gameManager.getPlayer() != null) {
            renderEntity(gameManager.getPlayer(), 0.2f, 0.8f, 1f); // Blue
        }

        // Render enemies
        if (gameManager.getCurrentLevel() != null) {
            for (Enemy enemy : gameManager.getCurrentLevel().getEnemies()) {
                if (enemy.isAlive()) {
                    renderEntity(enemy, 1f, 0.2f, 0.2f); // Red
                }
            }
        }
    }

    /**
     * Draw a single tile
     */
    private void drawTile(int x, int y, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
    }

    /**
     * Render an entity (player or enemy)
     */
    private void renderEntity(com.roguelike.core.entities.Entity entity,
                              float r, float g, float b) {
        float x = entity.getX() * tileSize + tileSize / 4;
        float y = entity.getY() * tileSize + tileSize / 4;
        float size = tileSize / 2;

        // Draw entity as a circle
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.circle(x, y, size / 2);

        // Draw health bar above entity
        drawHealthBar(x, y, entity);
    }

    /**
     * Draw health bar above entity
     */
    private void drawHealthBar(float x, float y, com.roguelike.core.entities.Entity entity) {
        float healthPercent = entity.getHealthPercent() / 100f;
        float barWidth = tileSize / 2;
        float barHeight = 4f;
        float barY = y + tileSize / 2;

        // Background (red)
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(x - barWidth / 2, barY, barWidth, barHeight);

        // Health (green)
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(x - barWidth / 2, barY, barWidth * healthPercent, barHeight);
    }

    /**
     * Update camera to follow player
     */
    private void updateCamera(GameManager gameManager) {
        Player player = gameManager.getPlayer();
        if (player != null) {
            camera.position.x = player.getX() * tileSize + tileSize / 2;
            camera.position.y = player.getY() * tileSize + tileSize / 2;
            camera.update();
        }
    }

    /**
     * Get projection matrix for UI (fixed screen space)
     */
    private Matrix4 getUIProjectionMatrix() {
        Matrix4 uiMatrix = new Matrix4();
        uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return uiMatrix;
    }

    /**
     * Resize camera when window resizes
     */
    public void resize(int width, int height) {
        camera.viewportWidth = width / tileSize;
        camera.viewportHeight = height / tileSize;
        camera.update();
    }

    /**
     * Cleanup
     */
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        spriteManager.dispose();
    }
}

