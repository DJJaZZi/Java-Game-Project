package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roguelike.core.game.GameManager;
import com.roguelike.core.game.GameStateType;

public class OverlayRenderer {

    private final BitmapFont  titleFont;
    private final BitmapFont  bodyFont;
    private final GlyphLayout layout;
    private final ShapeRenderer sr;
    private final SpriteBatch   batch;

    public OverlayRenderer() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.5f);

        layout = new GlyphLayout();
        sr     = new ShapeRenderer();
        batch  = new SpriteBatch();
    }

    /** Вызывается из GameRenderer каждый кадр. */
    public void render(GameManager gm) {
        switch (gm.getGameStateType()) {
            case GAME_OVER:      renderGameOver(gm);      break;
            case LEVEL_COMPLETE: renderLevelComplete(gm); break;
            case PAUSE:          renderPause();            break;
            default: break;
        }
    }

    // ── Game Over ─────────────────────────────────────────────────────────────
    private void renderGameOver(GameManager gm) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        drawBg(0.35f, 0.02f, 0.02f, 0.88f);

        batch.begin();
        titleFont.setColor(Color.RED);
        drawCentered(titleFont, "GAME OVER", sw, sh * 0.65f);

        bodyFont.setColor(Color.WHITE);
        drawCentered(bodyFont, "Score: " + gm.getScore(), sw, sh * 0.50f);
        drawCentered(bodyFont, "Floor reached: " + gm.getCurrentFloor(), sw, sh * 0.42f);

        bodyFont.setColor(Color.YELLOW);
        drawCentered(bodyFont, "Press  R  to Restart", sw, sh * 0.28f);
        batch.end();
    }

    // ── Level Complete ────────────────────────────────────────────────────────
    private void renderLevelComplete(GameManager gm) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        drawBg(0.02f, 0.30f, 0.05f, 0.85f);

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

    // ── Pause ─────────────────────────────────────────────────────────────────
    private void renderPause() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        drawBg(0f, 0f, 0f, 0.65f);

        batch.begin();
        titleFont.setColor(Color.WHITE);
        drawCentered(titleFont, "PAUSED", sw, sh * 0.60f);

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(bodyFont, "Press  P  to Resume", sw, sh * 0.45f);
        drawCentered(bodyFont, "Press  R  to Restart", sw, sh * 0.37f);
        batch.end();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void drawBg(float r, float g, float b, float a) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(r, g, b, a);
        sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawCentered(BitmapFont font, String text, float screenW, float y) {
        layout.setText(font, text);
        font.draw(batch, text, (screenW - layout.width) / 2f, y);
    }

    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
        sr.dispose();
        batch.dispose();
    }
}
