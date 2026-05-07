package com.roguelike.core.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * LevelLayoutBuilder — places rooms at their world-space pixel positions,
 * producing a list of LevelBounds that exactly matches the level sketch.
 *
 * Layout (left → right, matching screenshots):
 *
 *   [Spawn 224x256] → [Corridor 384x192] → [Goblin1 412x256]
 *   → [Corridor 384x192] → [Base 224x256] → [Corridor 384x192]
 *   → [Goblin2 412x256] → [Corridor 384x192] → [Goblin3 412x256]
 *   → [Corridor 384x192] → [Goblin4 412x256] → [Corridor 384x192]
 *   → [Base 224x256] → [Boss 656x384]
 *
 * The Y origin of each room is centred on the corridor height so everything
 * lines up horizontally the way the sketch shows.
 *
 * Usage:
 *   List<LevelBounds> layout = LevelLayoutBuilder.build();
 */
public class LevelLayoutBuilder {

    /** Horizontal gap between rooms (in pixels). */
    private static final float GAP = 0f;

    /** Baseline Y — rooms are vertically centred on this line. */
    private static final float BASE_Y = 0f;

    public static List<LevelBounds> build() {
        List<LevelBounds> rooms = new ArrayList<>();
        float cursorX = 0f;

        // Helper: places a room left→right and returns it
        // Vertical centering: room sits at y = BASE_Y - (height - CORRIDOR_H) / 2
        // so corridors and taller rooms all share the same visual midline.

        // 1. Spawnpoint
        LevelBounds spawn = LevelBounds.spawnpoint(cursorX, centeredY(LevelBounds.SPAWN_H));
        rooms.add(spawn);
        cursorX += LevelBounds.SPAWN_W + GAP;

        // 2. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 3. Goblin Level 1
        rooms.add(LevelBounds.goblinRoom(cursorX, centeredY(LevelBounds.GOBLIN_H), 1));
        cursorX += LevelBounds.GOBLIN_W + GAP;

        // 4. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 5. Base (mid-point safe room)
        rooms.add(LevelBounds.base(cursorX, centeredY(LevelBounds.BASE_H)));
        cursorX += LevelBounds.BASE_W + GAP;

        // 6. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 7. Goblin Level 2
        rooms.add(LevelBounds.goblinRoom(cursorX, centeredY(LevelBounds.GOBLIN_H), 2));
        cursorX += LevelBounds.GOBLIN_W + GAP;

        // 8. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 9. Goblin Level 3
        rooms.add(LevelBounds.goblinRoom(cursorX, centeredY(LevelBounds.GOBLIN_H), 3));
        cursorX += LevelBounds.GOBLIN_W + GAP;

        // 10. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 11. Goblin Level 4
        rooms.add(LevelBounds.goblinRoom(cursorX, centeredY(LevelBounds.GOBLIN_H), 4));
        cursorX += LevelBounds.GOBLIN_W + GAP;

        // 12. Corridor
        rooms.add(LevelBounds.corridor(cursorX, centeredY(LevelBounds.CORRIDOR_H)));
        cursorX += LevelBounds.CORRIDOR_W + GAP;

        // 13. Base (second safe room before boss)
        rooms.add(LevelBounds.base(cursorX, centeredY(LevelBounds.BASE_H)));
        cursorX += LevelBounds.BASE_W + GAP;

        // 14. Boss Level 5
        rooms.add(LevelBounds.bossRoom(cursorX, centeredY(LevelBounds.BOSS_H)));

        return rooms;
    }

    /**
     * Returns the Y coordinate that vertically centres a room of [roomHeight]
     * on BASE_Y, assuming corridors (shortest room, 192px) set the midline.
     */
    private static float centeredY(float roomHeight) {
        float midline = BASE_Y + LevelBounds.CORRIDOR_H / 2f;
        return midline - roomHeight / 2f;
    }

    /**
     * Finds and returns the LevelBounds the player is currently standing in.
     * Returns null if the player is between rooms (shouldn't happen in a sealed layout).
     */
    public static LevelBounds findRoom(List<LevelBounds> rooms, float playerPixelX, float playerPixelY) {
        for (LevelBounds b : rooms) {
            if (b.contains(playerPixelX, playerPixelY)) return b;
        }
        return null;
    }
}
