package com.roguelike.rendering;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.roguelike.core.dungeon.RoomType;
import com.roguelike.core.systems.UpgradeStat;
import com.roguelike.core.systems.UpgradeSystem;

/**
 * UpgradeUI — renders the upgrade shop panel when the player is inside a
 * BASE room.
 *
 * In a real game this would use a proper GUI library (Scene2D, etc.).
 * For now it uses BitmapFont so it works without any extra dependencies.
 *
 * Key bindings shown in the panel (handled by InputHandler, not here):
 *   1-7  : upgrade the stat at that list position
 *   ESC  : close the panel
 *
 * INTEGRATION
 * ───────────
 * // In GameRenderer.render():
 *   if (currentRoom != null && currentRoom.roomType == RoomType.BASE) {
 *       batch.begin();
 *       upgradeUI.render(batch, upgradeSystem, 20, screenHeight - 20);
 *       batch.end();
 *   }
 */
public class UpgradeUI {

    private final BitmapFont font;
    private static final float LINE_H = 22f;

    public UpgradeUI() {
        // Default libGDX font — replace with your own BitmapFont if desired
        this.font = new BitmapFont();
    }

    /**
     * Draws the upgrade panel.
     *
     * @param batch          SpriteBatch already begun
     * @param system         the live UpgradeSystem instance
     * @param x              left edge of the panel (screen pixels)
     * @param y              top edge of the panel (screen pixels)
     */
    public void render(SpriteBatch batch, UpgradeSystem system, float x, float y) {
        float cy = y;

        font.draw(batch, "=== UPGRADE SHOP ===", x, cy);  cy -= LINE_H;
        font.draw(batch, "Points: " + system.getPoints(), x, cy);  cy -= LINE_H * 1.5f;

        UpgradeStat[] stats = UpgradeStat.values();
        for (int i = 0; i < stats.length; i++) {
            UpgradeStat stat = stats[i];
            int  lvl      = system.getLevel(stat);
            int  cost     = system.getCost(stat);
            boolean maxed = system.isMaxed(stat);
            boolean afford = system.canAfford(stat);

            String suffix;
            if (maxed)        suffix = "  [MAX]";
            else if (!afford) suffix = "  [" + cost + " pts — need more]";
            else              suffix = "  [" + cost + " pts — press " + (i + 1) + "]";

            String line = String.format("%d. %-14s  Lv %d/%d%s",
                i + 1, stat.displayName, lvl, stat.maxLevel, suffix);

            // Colour: green if affordable, grey if maxed, white otherwise
            if (maxed)        font.setColor(0.4f, 0.4f, 0.4f, 1f);
            else if (afford)  font.setColor(0.2f, 1.0f, 0.2f, 1f);
            else              font.setColor(1f,   1f,   1f,   1f);

            font.draw(batch, line, x, cy);
            cy -= LINE_H;
        }

        font.setColor(1f, 1f, 1f, 1f); // reset
        cy -= LINE_H * 0.5f;
        font.draw(batch, stat -> "Press ESC to leave shop", x, cy);
    }

    public void dispose() {
        font.dispose();
    }
}
