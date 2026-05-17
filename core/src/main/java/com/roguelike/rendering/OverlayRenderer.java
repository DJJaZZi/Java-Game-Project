package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roguelike.core.game.GameStateType;
import com.roguelike.core.game.GameManager;

/**
 * OverlayRenderer — рисует полноэкранные оверлеи:
 *   GAME_OVER      → красный экран  "GAME OVER"   + счёт + [R] restart
 *   LEVEL_COMPLETE → зелёный экран  "LEVEL CLEAR!" + этаж + [любая кнопка]
 *   PAUSE          → тёмный оверлей "PAUSED"       + [P] resume
 *
 * Координаты — экранные пиксели (LibGDX: Y=0 внизу).
 */
public class OverlayRenderer {

    private final BitmapFont   titleFont;
    private final BitmapFont   bodyFont;
    private final GlyphLayout  layout;
    private final ShapeRenderer sr;
    private final SpriteBatch   batch;

    public OverlayRenderer() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);   // крупный заголовок

        bodyFont  = new BitmapFont();
        bodyFont.getData().setScale(1.5f);  // обычный текст

        layout = new GlyphLayout();
        sr     = new ShapeRenderer();
        batch  = new SpriteBatch();
    }

    /**
     * Вызывается из GameRenderer каждый кадр.
     * Рисует оверлей только если состояние не PLAYING.
     */
    public void render(GameManager gm) {
        GameStateType state = gm.getGameStateType();

        switch (state) {
            case GAME_OVER:      renderGameOver(gm);      break;
            case LEVEL_COMPLETE: renderLevelComplete(gm); break;
            case PAUSE:          renderPause();            break;
            default:             break; // PLAYING — ничего не рисуем
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  GAME OVER
    // ══════════════════════════════════════════════════════════════

    private void renderGameOver(GameManager gm) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Тёмно-красный полупрозрачный фон
        drawBackground(0.35f, 0.02f, 0.02f, 0.88f);

        batch.begin();

        // Заголовок
        titleFont.setColor(Color.RED);
        drawCentered(titleFont, "GAME OVER", sw, sh * 0.65f);

        // Счёт
        bodyFont.setColor(Color.WHITE);
        drawCentered(bodyFont, "Score: " + gm.getScore(), sw, sh * 0.50f);
        drawCentered(bodyFont, "Floor reached: " + gm.getCurrentFloor(), sw, sh * 0.42f);

        // Подсказка
        bodyFont.setColor(Color.YELLOW);
        drawCentered(bodyFont, "Press  R  to Restart", sw, sh * 0.28f);

        batch.end();
    }

    // ══════════════════════════════════════════════════════════════
    //  LEVEL COMPLETE
    // ══════════════════════════════════════════════════════════════

    private void renderLevelComplete(GameManager gm) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Тёмно-зелёный полупрозрачный фон
        drawBackground(0.02f, 0.30f, 0.05f, 0.85f);

        batch.begin();

        titleFont.setColor(Color.GREEN);
        drawCentered(titleFont, "LEVEL CLEAR!", sw, sh * 0.65f);

        bodyFont.setColor(Color.WHITE);
        drawCentered(bodyFont, "Floor " + gm.getCurrentFloor() + " completed!", sw, sh * 0.50f);
        drawCentered(bodyFont, "Score: " + gm.getScore(), sw, sh * 0.42f);

        bodyFont.setColor(Color.YELLOW);
        drawCentered(bodyFont, "Press any key to continue...", sw, sh * 0.28f);

        batch.end();
    }

    // ══════════════════════════════════════════════════════════════
    //  PAUSE
    // ══════════════════════════════════════════════════════════════

    private void renderPause() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Тёмный полупрозрачный фон
        drawBackground(0.0f, 0.0f, 0.0f, 0.65f);

        batch.begin();

        titleFont.setColor(Color.WHITE);
        drawCentered(titleFont, "PAUSED", sw, sh * 0.60f);

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(bodyFont, "Press  P  to Resume", sw, sh * 0.45f);
        drawCentered(bodyFont, "Press  R  to Restart", sw, sh * 0.37f);

        batch.end();
    }

    // ══════════════════════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════════════════════

    /** Рисует полупрозрачный прямоугольник на весь экран */
    private void drawBackground(float r, float g, float b, float a) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(r, g, b, a);
        sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Рисует текст по центру экрана по X, на заданной высоте Y */
    private void drawCentered(BitmapFont font, String text, float screenW, float y) {
        layout.setText(font, text);
        float x = (screenW - layout.width) / 2f;
        font.draw(batch, text, x, y);
    }

    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
        sr.dispose();
        batch.dispose();
    }
}
