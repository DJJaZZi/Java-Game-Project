package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roguelike.core.entities.Player;

public class UIRenderer {

    private final BitmapFont  font;
    private final GlyphLayout layout;

    private static final float PAD     = 10f;
    private static final float PANEL_W = 180f;
    private static final float PANEL_H = 90f;
    private static final float LINE_H  = 22f;
    private static final float BAR_W   = 150f;
    private static final float BAR_H   = 10f;

    public UIRenderer() {
        this.font   = new BitmapFont();
        this.layout = new GlyphLayout();
        font.setColor(Color.WHITE);
    }

    /**
     * Вызывается из GameRenderer.renderHUD().
     * ShapeRenderer и SpriteBatch НЕ начаты — мы управляем ими сами.
     */
    public void render(SpriteBatch batch, ShapeRenderer sr, Player player) {
        if (player == null) return;

        float screenH = Gdx.graphics.getHeight();
        float panelX  = PAD;
        float panelY  = screenH - PAD - PANEL_H;

        // ── 1. Фон + рамка ────────────────────────────────────────────────────
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Тёмный фон
        sr.setColor(0.05f, 0.05f, 0.08f, 0.82f);
        sr.rect(panelX, panelY, PANEL_W, PANEL_H);

        // Золотая рамка (4 полосы)
        float t = 2f;
        sr.setColor(0.55f, 0.45f, 0.20f, 1f);
        sr.rect(panelX,              panelY,               PANEL_W, t);
        sr.rect(panelX,              panelY + PANEL_H - t, PANEL_W, t);
        sr.rect(panelX,              panelY,               t,       PANEL_H);
        sr.rect(panelX + PANEL_W - t, panelY,             t,       PANEL_H);

        // HP бар — фон
        float barX = panelX + PAD;
        float barY = panelY + PAD;
        float hpPct = Math.max(0f, player.getHealth() / player.getMaxHealth());

        sr.setColor(0.40f, 0.05f, 0.05f, 1f);
        sr.rect(barX, barY, BAR_W, BAR_H);

        // HP бар — заполнение (зелёный → жёлтый → красный)
        float[] c = hpColor(hpPct);
        sr.setColor(c[0], c[1], c[2], 1f);
        sr.rect(barX, barY, BAR_W * hpPct, BAR_H);

        sr.end();

        // ── 2. Текст ──────────────────────────────────────────────────────────
        batch.begin();

        float tx = panelX + PAD;
        float cy = panelY + PANEL_H - PAD;

        // Заголовок
        font.setColor(0.55f, 0.45f, 0.20f, 1f);
        font.draw(batch, "HERO STATS", tx, cy);
        cy -= LINE_H;

        // Уровень
        font.setColor(Color.WHITE);
        font.draw(batch, "LVL  " + player.getLevel(), tx, cy);
        cy -= LINE_H;

        // HP числа (цвет совпадает с баром)
        font.setColor(c[0], c[1], c[2], 1f);
        font.draw(batch, "HP   " + (int) player.getHealth()
            + " / " + (int) player.getMaxHealth(), tx, cy);

        batch.end();
    }

    private float[] hpColor(float pct) {
        if (pct > 0.5f) {
            float t = (pct - 0.5f) * 2f;
            return new float[]{ 0.9f - 0.8f * t, 0.9f, 0.1f };
        } else {
            float t = pct * 2f;
            return new float[]{ 0.9f, 0.1f + 0.8f * t, 0.1f };
        }
    }

    public void dispose() {
        font.dispose();
    }
}
