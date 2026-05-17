package com.roguelike.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.roguelike.core.entities.Player;

/**
 * UIRenderer — рисует HUD в углу экрана.
 *
 * Отображает:
 *   ❤ HP : текущее / максимальное  +  полоска HP
 *   ⚔ LVL: уровень игрока
 *
 * Координаты — экранные (левый нижний угол = 0,0 в LibGDX).
 * Панель расположена в левом верхнем углу.
 */
public class UIRenderer {

    // ── Шрифт ────────────────────────────────────────────────────────────────
    private final BitmapFont font;
    private final GlyphLayout layout;

    // ── Константы панели ─────────────────────────────────────────────────────
    private static final float PAD         = 10f;  // отступ от края экрана
    private static final float PANEL_X     = PAD;
    private static final float PANEL_W     = 180f;
    private static final float PANEL_H     = 90f;
    private static final float LINE_H      = 22f;
    private static final float BAR_W       = 150f;
    private static final float BAR_H       = 10f;

    // ── Цвета ────────────────────────────────────────────────────────────────
    // Фон панели — тёмный полупрозрачный
    private static final float BG_R = 0.05f, BG_G = 0.05f, BG_B = 0.08f, BG_A = 0.80f;
    // Рамка
    private static final float BORDER_R = 0.55f, BORDER_G = 0.45f, BORDER_B = 0.20f;
    // HP бар — фон (тёмно-красный) и заполнение (зелёный → жёлтый → красный)
    private static final float BAR_BG_R = 0.40f, BAR_BG_G = 0.05f, BAR_BG_B = 0.05f;

    public UIRenderer() {
        this.font   = new BitmapFont();           // стандартный шрифт LibGDX
        this.layout = new GlyphLayout();
        font.setColor(Color.WHITE);
    }

    /**
     * Вызывается из GameRenderer каждый кадр.
     *
     * @param batch         SpriteBatch (НЕ начат — мы сами управляем begin/end)
     * @param shapeRenderer ShapeRenderer (НЕ начат)
     * @param player        текущий игрок
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Player player) {
        if (player == null) return;

        float screenH = Gdx.graphics.getHeight();
        float panelY  = screenH - PAD - PANEL_H;   // верхний левый угол панели

        // ── 1. Фон и рамка (ShapeRenderer) ───────────────────────────────────
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Тёмный фон
        shapeRenderer.setColor(BG_R, BG_G, BG_B, BG_A);
        shapeRenderer.rect(PANEL_X, panelY, PANEL_W, PANEL_H);

        // Рамка (4 полосы по краям)
        shapeRenderer.setColor(BORDER_R, BORDER_G, BORDER_B, 1f);
        float t = 2f; // толщина рамки
        shapeRenderer.rect(PANEL_X,              panelY,               PANEL_W, t);       // низ
        shapeRenderer.rect(PANEL_X,              panelY + PANEL_H - t, PANEL_W, t);       // верх
        shapeRenderer.rect(PANEL_X,              panelY,               t,       PANEL_H); // лево
        shapeRenderer.rect(PANEL_X + PANEL_W - t, panelY,              t,       PANEL_H); // право

        // ── HP бар ───────────────────────────────────────────────────────────
        float barX = PANEL_X + PAD;
        float barY = panelY + PAD;
        float hpPct = Math.max(0f, player.getHealth() / player.getMaxHealth());

        // Фон бара
        shapeRenderer.setColor(BAR_BG_R, BAR_BG_G, BAR_BG_B, 1f);
        shapeRenderer.rect(barX, barY, BAR_W, BAR_H);

        // Заполнение: зелёный при полном HP → жёлтый → красный при низком
        float[] barColor = getHpColor(hpPct);
        shapeRenderer.setColor(barColor[0], barColor[1], barColor[2], 1f);
        shapeRenderer.rect(barX, barY, BAR_W * hpPct, BAR_H);

        shapeRenderer.end();

        // ── 2. Текст (SpriteBatch) ────────────────────────────────────────────
        batch.begin();

        float textX = PANEL_X + PAD;
        float cy    = panelY + PANEL_H - PAD;  // начинаем сверху панели, идём вниз

        // Заголовок
        font.setColor(BORDER_R, BORDER_G, BORDER_B, 1f);
        font.draw(batch, "HERO STATS", textX, cy);
        cy -= LINE_H;

        // LVL
        font.setColor(Color.WHITE);
        font.draw(batch, "LVL  " + player.getLevel(), textX, cy);
        cy -= LINE_H;

        // HP числа
        String hpText = "HP   " + (int) player.getHealth() + " / " + (int) player.getMaxHealth();
        font.setColor(getHpColor(hpPct)[0], getHpColor(hpPct)[1], getHpColor(hpPct)[2], 1f);
        font.draw(batch, hpText, textX, cy);
        // (сам бар уже нарисован ShapeRenderer ниже)

        batch.end();
    }

    /**
     * Возвращает RGB цвет бара HP:
     *   100% → зелёный  (0.1, 0.9, 0.1)
     *    50% → жёлтый   (0.9, 0.9, 0.1)
     *     0% → красный  (0.9, 0.1, 0.1)
     */
    private float[] getHpColor(float pct) {
        if (pct > 0.5f) {
            // зелёный → жёлтый
            float t = (pct - 0.5f) * 2f;   // 1.0 при 100%, 0.0 при 50%
            return new float[]{ 0.9f - 0.8f * t, 0.9f, 0.1f };
        } else {
            // жёлтый → красный
            float t = pct * 2f;             // 1.0 при 50%, 0.0 при 0%
            return new float[]{ 0.9f, 0.1f + 0.8f * t, 0.1f };
        }
    }

    public void dispose() {
        font.dispose();
    }
}
