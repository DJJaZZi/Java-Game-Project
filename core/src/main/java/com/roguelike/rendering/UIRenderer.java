package com.roguelike.rendering;

import com.roguelike.core.entities.Player;

/**
 * UIRenderer - Renders HUD/UI elements
 */
public class UIRenderer {
    /**
     * Render UI elements (health, inventory, stats, etc.)
     */
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, Player player) {
        if (player != null) {
            // In a real game, you'd render text here
            // For now, just logging
            // TODO: Render health, mana, inventory, minimap, etc.
        }
    }
}
