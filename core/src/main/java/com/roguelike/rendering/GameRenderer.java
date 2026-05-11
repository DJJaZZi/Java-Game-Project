package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public void render(GameManager gameManager) {
        updateCamera(gameManager);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Draw dungeon tiles ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (gameManager.getCurrentLevel() != null) {
            renderDungeon(gameManager.getCurrentLevel());
        }
        shapeRenderer.end();

        // --- Draw entities (sprites + health bars) ---
        if (gameManager.getCurrentLevel() != null) {
            renderEntities(gameManager);
        }

        // --- Draw UI overlay ---
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
     * Render all entities using sprites.
     * SpriteBatch must be used separately from ShapeRenderer.
     */
    private void renderEntities(GameManager gameManager) {
        // End shape rendering before starting batch
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Render player
        Player player = gameManager.getPlayer();
        if (player != null) {
            renderEntitySprite(player, "player", "idle");
        }

        // Render enemies
        if (gameManager.getCurrentLevel() != null) {
            for (Enemy enemy : gameManager.getCurrentLevel().getEnemies()) {
                if (enemy.isAlive()) {
                    String type = enemy.getEnemyType().toLowerCase(); // "goblin" or "orc"
                    renderEntitySprite(enemy, type, "idle");
                }
            }
        }

        batch.end();

        // Resume shape rendering for health bars
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        // Draw health bars (shape renderer)
        if (gameManager.getPlayer() != null) {
            drawHealthBar(gameManager.getPlayer());
        }
        if (gameManager.getCurrentLevel() != null) {
            for (Enemy enemy : gameManager.getCurrentLevel().getEnemies()) {
                if (enemy.isAlive()) drawHealthBar(enemy);
            }
        }

        shapeRenderer.end();

        // Re-open for anything after (the outer render() calls shapeRenderer.end() — remove that call)
    }

    /**
     * Draw a single tile
     */
    private void drawTile(int x, int y, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
    }

    private void renderEntitySprite(com.roguelike.core.entities.Entity entity,
                                    String type, String state) {
        TextureRegion frame = spriteManager.getFrame(type, state);

        if (frame == null) {
            // Absolute fallback — colored square so entity is always visible
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
            if (type.equals("player"))      shapeRenderer.setColor(0.2f, 0.5f, 1f, 1f);
            else if (type.equals("goblin")) shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
            else                            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
            shapeRenderer.rect(entity.getX() * tileSize, entity.getY() * tileSize, tileSize, tileSize);
            shapeRenderer.end();
            return;
        }

        float worldX = entity.getX() * tileSize;
        float worldY = entity.getY() * tileSize;

        // Scale to fit inside one tile, centered
        float spriteW = frame.getRegionWidth();
        float spriteH = frame.getRegionHeight();
        float scale   = tileSize / Math.max(spriteW, spriteH);
        float drawW   = spriteW * scale;
        float drawH   = spriteH * scale;
        float offX    = (tileSize - drawW) / 2f;
        float offY    = (tileSize - drawH) / 2f;

        batch.draw(frame, worldX + offX, worldY + offY, drawW, drawH);
    }

    /**
     * Health bar drawn with ShapeRenderer (call inside shapeRenderer.begin block).
     */
    private void drawHealthBar(com.roguelike.core.entities.Entity entity) {
        float worldX = entity.getX() * tileSize;
        float worldY = entity.getY() * tileSize + tileSize + 2f;

        float barW = tileSize;
        float barH = 4f;
        float hp   = entity.getHealthPercent() / 100f;

        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(worldX, worldY, barW, barH);
        shapeRenderer.setColor(0.1f, 0.9f, 0.1f, 1);
        shapeRenderer.rect(worldX, worldY, barW * hp, barH);
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
        camera.setToOrtho(false, width, height);
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

