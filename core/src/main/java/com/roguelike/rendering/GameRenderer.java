package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    // ── Рендереры ─────────────────────────────────────────────────────────────
    private final SpriteBatch        batch;
    private final ShapeRenderer      shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteManager      spriteManager;
    private final UIRenderer         uiRenderer;
    private final TileRenderer       tileRenderer;
    private final OverlayRenderer    overlayRenderer; // ← НОВЫЙ

    // ── Кэш аниматоров ───────────────────────────────────────────────────────
    private final Map<String, EntityAnimator> animatorCache = new HashMap<>();

    // ── Константы ─────────────────────────────────────────────────────────────
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
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ГЛАВНЫЙ МЕТОД — каждый кадр
    // ═════════════════════════════════════════════════════════════════════════

    public void render(GameManager gameManager) {

        // ── Клавиши управления оверлеями (R = restart, P = pause/resume) ──────
        handleGlobalInput(gameManager);

        updateCamera(gameManager);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        DungeonLevel level = gameManager.getCurrentLevel();

        // 1. Подземелье (всегда рисуем фон)
        if (level != null) renderDungeon(level);

        // 2. Существа + полоски HP (только в PLAYING и PAUSE)
        GameStateType state = gameManager.getGameStateType();
        if (level != null && (state == GameStateType.PLAYING || state == GameStateType.PAUSE)) {
            renderEntities(gameManager);
        }

        // 3. HUD (HP + Level) — только пока играем
        if (state == GameStateType.PLAYING) {
            renderHUD(gameManager);
        }

        // 4. Оверлеи (Game Over / Level Complete / Pause)
        overlayRenderer.render(gameManager);
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
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ГЛОБАЛЬНЫЙ ВВОД (R = рестарт, P = пауза)
    // ═════════════════════════════════════════════════════════════════════════

    private boolean rWasDown = false;

    private void handleGlobalInput(GameManager gm) {
        // R — рестарт в любом состоянии
        boolean rDown = Gdx.input.isKeyPressed(Input.Keys.R);
        if (rDown && !rWasDown) {
            gm.newGame();
        }
        rWasDown = rDown;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ПОДЗЕМЕЛЬЕ
    // ═════════════════════════════════════════════════════════════════════════

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
                    case WALL:  drawTileShape(x, y, 0.15f, 0.15f, 0.15f); break;
                    case FLOOR: drawTileShape(x, y, 0.50f, 0.45f, 0.40f); break;
                    case DOOR:  drawTileShape(x, y, 0.80f, 0.60f, 0.20f); break;
                    case TRAP:  drawTileShape(x, y, 1.00f, 0.20f, 0.20f); break;
                    case CHEST: drawTileShape(x, y, 1.00f, 0.80f, 0.20f); break;
                    default:    drawTileShape(x, y, 0.30f, 0.30f, 0.30f); break;
                }
            }
        }
    }

    private void drawTileShape(int x, int y, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  СУЩЕСТВА
    // ═════════════════════════════════════════════════════════════════════════

    private void renderEntities(GameManager gameManager) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Player player = gameManager.getPlayer();
        if (player != null) {
            EntityAnimator anim = getOrCreateAnimator("player", "player");
            syncAnimatorToEntity(anim, player);
            anim.update(Gdx.graphics.getDeltaTime());
            drawAnimated(player, anim);
        }

        for (Enemy enemy : gameManager.getCurrentLevel().getEnemies()) {
            String animKey = "enemy_" + System.identityHashCode(enemy);
            EntityAnimator anim = getOrCreateAnimator(animKey, enemy.getEnemyType().toLowerCase());
            syncAnimatorToEntity(anim, enemy);
            anim.update(Gdx.graphics.getDeltaTime());
            drawAnimated(enemy, anim);
        }

        batch.end();

        // Полоски HP
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (player != null && player.isAlive()) drawHealthBar(player);
        for (Enemy e : gameManager.getCurrentLevel().getEnemies()) {
            if (e.isAlive()) drawHealthBar(e);
        }

        shapeRenderer.end();
    }

    private void drawAnimated(Entity entity, EntityAnimator animator) {
        TextureRegion frame = animator.getCurrentFrame();
        if (frame == null) return;

        float worldX = entity.getX() * TILE_SIZE;
        float worldY = entity.getY() * TILE_SIZE;
        float scale  = TILE_SIZE / (float) Math.max(frame.getRegionWidth(), frame.getRegionHeight());
        float drawW  = frame.getRegionWidth()  * scale;
        float drawH  = frame.getRegionHeight() * scale;
        float offX   = (TILE_SIZE - drawW) / 2f;
        float offY   = (TILE_SIZE - drawH) / 2f;

        batch.draw(frame, worldX + offX, worldY + offY, drawW, drawH);
    }

    private void drawHealthBar(Entity entity) {
        float worldX = entity.getX() * TILE_SIZE;
        float worldY = entity.getY() * TILE_SIZE + TILE_SIZE + 2f;
        float barW   = TILE_SIZE;
        float barH   = 4f;
        float hpPct  = entity.getHealthPercent() / 100f;

        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(worldX, worldY, barW, barH);
        shapeRenderer.setColor(0.1f, 0.9f, 0.1f, 1f);
        shapeRenderer.rect(worldX, worldY, barW * hpPct, barH);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HUD
    // ═════════════════════════════════════════════════════════════════════════

    private void renderHUD(GameManager gameManager) {
        Matrix4 uiMatrix = getUIMatrix();
        shapeRenderer.setProjectionMatrix(uiMatrix);
        batch.setProjectionMatrix(uiMatrix);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        uiRenderer.render(batch, shapeRenderer, gameManager.getPlayer());

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  АНИМАЦИЯ
    // ═════════════════════════════════════════════════════════════════════════

    private EntityAnimator getOrCreateAnimator(String key, String entityType) {
        if (!animatorCache.containsKey(key)) {
            animatorCache.put(key, spriteManager.buildAnimator(entityType));
        }
        return animatorCache.get(key);
    }

    private void syncAnimatorToEntity(EntityAnimator animator, Entity entity) {
        animator.setFacingLeft(entity.isFacingLeft());

        if (!entity.isAlive()) {
            animator.forceState(EntityAnimator.AnimState.DEAD);
            return;
        }

        switch (entity.getState()) {
            case MOVING:    animator.setState(EntityAnimator.AnimState.RUN);    break;
            case ATTACKING: animator.setState(EntityAnimator.AnimState.ATTACK); break;
            case DEAD:      animator.forceState(EntityAnimator.AnimState.DEAD); break;
            default:        animator.setState(EntityAnimator.AnimState.IDLE);   break;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  КАМЕРА
    // ═════════════════════════════════════════════════════════════════════════

    private void updateCamera(GameManager gameManager) {
        Player player = gameManager.getPlayer();
        if (player != null) {
            float targetX = player.getX() * TILE_SIZE + TILE_SIZE / 2f;
            float targetY = player.getY() * TILE_SIZE + TILE_SIZE / 2f;
            camera.position.x += (targetX - camera.position.x) * 0.15f;
            camera.position.y += (targetY - camera.position.y) * 0.15f;
            camera.update();
        }
    }

    private Matrix4 getUIMatrix() {
        Matrix4 m = new Matrix4();
        m.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return m;
    }
}
