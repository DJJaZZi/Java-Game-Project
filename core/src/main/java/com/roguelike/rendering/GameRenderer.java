package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.dungeon.Tile;
import com.roguelike.core.entities.Entity;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;
import com.roguelike.core.game.GameManager;
import com.roguelike.core.game.GameStateType;

import java.util.HashMap;
import java.util.Map;

public class GameRenderer {

    private final SpriteBatch        batch;
    private final ShapeRenderer      shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteManager      spriteManager;
    private final UIRenderer         uiRenderer;
    private final TileRenderer       tileRenderer;
    private final OverlayRenderer    overlayRenderer;
    private final WalkableDebugRenderer walkableDebugRenderer;

    private final Map<String, EntityAnimator> animatorCache = new HashMap<>();

    private static final float TILE_SIZE   = 32f;
    private static final float ZOOM_FACTOR = 0.45f;

    public GameRenderer() {
        batch           = new SpriteBatch();
        shapeRenderer   = new ShapeRenderer();
        camera          = new OrthographicCamera();
        spriteManager   = new SpriteManager();
        uiRenderer      = new UIRenderer();
        tileRenderer    = new TileRenderer();
        overlayRenderer = new OverlayRenderer();
        walkableDebugRenderer = new WalkableDebugRenderer();
    }

    // ── Главный метод ─────────────────────────────────────────────────────────
    public void render(GameManager gameManager) {
        updateCamera(gameManager);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        DungeonLevel level = gameManager.getCurrentLevel();
        GameStateType state = gameManager.getGameStateType();

        // 1. Подземелье
        if (level != null) renderDungeon(level);

        // 2. Существа + HP (во время игры и паузы)
        if (level != null && (state == GameStateType.PLAYING || state == GameStateType.PAUSE)) {
            renderEntities(gameManager);
        }

        // 3. HUD (только во время игры)
        if (state == GameStateType.PLAYING) {
            renderHUD(gameManager);
        }

        // 4. Оверлеи (Game Over / Level Complete / Pause)
        overlayRenderer.render(gameManager);

        walkableDebugRenderer.render(camera.combined);
    }

    public void toggleDebug() {
        walkableDebugRenderer.toggle();
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false, width * ZOOM_FACTOR, height * ZOOM_FACTOR);
        camera.update();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        spriteManager.dispose();
        tileRenderer.dispose();
        uiRenderer.dispose();
        overlayRenderer.dispose();
        walkableDebugRenderer.dispose();
    }

    // ── Подземелье ────────────────────────────────────────────────────────────
    private void renderDungeon(DungeonLevel level) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (tileRenderer.isLoaded()) {
            tileRenderer.render(batch);
        } else {
            batch.end();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderTilesFallback(level);
            shapeRenderer.end();
            return;
        }
        batch.end();
    }

    private void renderTilesFallback(DungeonLevel level) {
        Tile[][] tiles = level.getTiles();
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                Tile tile = tiles[x][y];
                if (tile == null) continue;
                switch (tile.type) {
                    case WALL:  drawTile(x, y, 0.15f, 0.15f, 0.15f); break;
                    case FLOOR: drawTile(x, y, 0.50f, 0.45f, 0.40f); break;
                    case DOOR:  drawTile(x, y, 0.80f, 0.60f, 0.20f); break;
                    case CHEST: drawTile(x, y, 1.00f, 0.80f, 0.20f); break;
                    default:    drawTile(x, y, 0.30f, 0.30f, 0.30f); break;
                }
            }
        }
    }

    private void drawTile(int x, int y, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // ── Существа ──────────────────────────────────────────────────────────────
    private void renderEntities(GameManager gm) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Player player = gm.getPlayer();
        if (player != null) {
            EntityAnimator anim = getOrCreate("player", "player");
            syncAnim(anim, player);
            anim.update(Gdx.graphics.getDeltaTime());
            drawAnimated(player, anim);
        }

        for (Enemy enemy : gm.getCurrentLevel().getEnemies()) {
            String key = "enemy_" + System.identityHashCode(enemy);
            EntityAnimator anim = getOrCreate(key, enemy.getEnemyType().toLowerCase());
            syncAnim(anim, enemy);
            anim.update(Gdx.graphics.getDeltaTime());
            drawAnimated(enemy, anim);
        }

        batch.end();

        // HP полоски
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (player != null && player.isAlive()) drawHpBar(player);
        for (Enemy e : gm.getCurrentLevel().getEnemies()) {
            if (e.isAlive()) drawHpBar(e);
        }
        shapeRenderer.end();
    }

    private void drawAnimated(Entity entity, EntityAnimator animator) {
        TextureRegion frame = animator.getCurrentFrame();
        if (frame == null) return;
        float wx    = entity.getX() * TILE_SIZE;
        float wy    = entity.getY() * TILE_SIZE;
        float scale = TILE_SIZE / (float) Math.max(frame.getRegionWidth(), frame.getRegionHeight());
        float dw    = frame.getRegionWidth()  * scale;
        float dh    = frame.getRegionHeight() * scale;
        batch.draw(frame, wx + (TILE_SIZE - dw) / 2f, wy + (TILE_SIZE - dh) / 2f, dw, dh);
    }

    private void drawHpBar(Entity entity) {
        float wx   = entity.getX() * TILE_SIZE;
        float wy   = entity.getY() * TILE_SIZE + TILE_SIZE + 2f;
        float pct  = entity.getHealthPercent() / 100f;
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(wx, wy, TILE_SIZE, 4f);
        shapeRenderer.setColor(0.1f, 0.9f, 0.1f, 1f);
        shapeRenderer.rect(wx, wy, TILE_SIZE * pct, 4f);
    }

    // ── HUD ───────────────────────────────────────────────────────────────────
    private void renderHUD(GameManager gm) {
        Matrix4 ui = getUIMatrix();
        shapeRenderer.setProjectionMatrix(ui);
        batch.setProjectionMatrix(ui);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        uiRenderer.render(batch, shapeRenderer, gm.getPlayer());
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // ── Анимация ──────────────────────────────────────────────────────────────
    private EntityAnimator getOrCreate(String key, String type) {
        if (!animatorCache.containsKey(key))
            animatorCache.put(key, spriteManager.buildAnimator(type));
        return animatorCache.get(key);
    }

    private void syncAnim(EntityAnimator anim, Entity entity) {
        anim.setFacingLeft(entity.isFacingLeft());
        if (!entity.isAlive()) { anim.forceState(EntityAnimator.AnimState.DEAD); return; }
        switch (entity.getState()) {
            case MOVING:    anim.setState(EntityAnimator.AnimState.RUN);    break;
            case ATTACKING: anim.setState(EntityAnimator.AnimState.ATTACK); break;
            case DEAD:      anim.forceState(EntityAnimator.AnimState.DEAD); break;
            default:        anim.setState(EntityAnimator.AnimState.IDLE);   break;
        }
    }

    // ── Камера ────────────────────────────────────────────────────────────────
    private void updateCamera(GameManager gm) {
        Player player = gm.getPlayer();
        if (player != null) {
            float tx = player.getX() * TILE_SIZE + TILE_SIZE / 2f;
            float ty = player.getY() * TILE_SIZE + TILE_SIZE / 2f;
            camera.position.x += (tx - camera.position.x) * 0.15f;
            camera.position.y += (ty - camera.position.y) * 0.15f;
            camera.update();
        }
    }

    private Matrix4 getUIMatrix() {
        Matrix4 m = new Matrix4();
        m.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return m;
    }
}
