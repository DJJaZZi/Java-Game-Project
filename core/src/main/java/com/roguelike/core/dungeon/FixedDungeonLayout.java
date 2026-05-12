package com.roguelike.core.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds the fixed room sequence described in the design doc.
 * Rooms are placed left→right. Each room's worldX = previous room's end.
 */
public class FixedDungeonLayout {

    private static final int TILE_SIZE = 32;
    private static final Random random = new Random();

    // Image paths
    private static final String SPAWN_IMG    = "textures/maps/rooms/Spawnpoint(mirrored Base 224x256.png";
    private static final String BASE_IMG     = "textures/maps/rooms/Room 224x256.png";
    private static final String GOBLIN_IMG   = "textures/maps/rooms/Basic Level 2 416x256.png";
    private static final String CORRIDOR_1   = "textures/maps/rooms/Corridor-1 384x192.png";
    private static final String CORRIDOR_2   = "textures/maps/rooms/Corridor-1 416x272.png";
    private static final String BOSS_IMG     = "textures/maps/rooms/Boss 656x384.png";

    // Room pixel sizes
    private static final int SPAWN_W = 224,  SPAWN_H  = 256;
    private static final int BASE_W  = 224,  BASE_H   = 256;
    private static final int GOBLIN_W= 416,  GOBLIN_H = 256;
    private static final int COR1_W  = 384,  COR1_H   = 192;
    private static final int COR2_W  = 416,  COR2_H   = 272;
    private static final int BOSS_W  = 656,  BOSS_H   = 384;

    public List<RoomLayout> build() {
        List<RoomLayout> rooms = new ArrayList<>();
        int cursorX = 0;
        // All rooms vertically centered around Y=0
        // worldY centers each room on a common horizontal axis

        // 1. Spawn room
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.SPAWN,
            cursorX, centerY(SPAWN_H), SPAWN_W, SPAWN_H, SPAWN_IMG, true);

        // 2. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 3. Goblin room 1
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.GOBLIN,
            cursorX, centerY(GOBLIN_H), GOBLIN_W, GOBLIN_H, GOBLIN_IMG, false);

        // 4. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 5. Base room
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.BASE,
            cursorX, centerY(BASE_H), BASE_W, BASE_H, BASE_IMG, false);

        // 6. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 7. Goblin room 2
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.GOBLIN,
            cursorX, centerY(GOBLIN_H), GOBLIN_W, GOBLIN_H, GOBLIN_IMG, false);

        // 8. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 9. Goblin room 3
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.GOBLIN,
            cursorX, centerY(GOBLIN_H), GOBLIN_W, GOBLIN_H, GOBLIN_IMG, false);

        // 10. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 11. Base room
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.BASE,
            cursorX, centerY(BASE_H), BASE_W, BASE_H, BASE_IMG, false);

        // 12. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 13. Goblin room 4
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.GOBLIN,
            cursorX, centerY(GOBLIN_H), GOBLIN_W, GOBLIN_H, GOBLIN_IMG, false);

        // 14. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 15. Base room
        cursorX = addRoom(rooms, RoomLayout.RoomImageType.BASE,
            cursorX, centerY(BASE_H), BASE_W, BASE_H, BASE_IMG, false);

        // 16. Corridor
        cursorX = addCorridor(rooms, cursorX);

        // 17. Boss room
        addRoom(rooms, RoomLayout.RoomImageType.BOSS,
            cursorX, centerY(BOSS_H), BOSS_W, BOSS_H, BOSS_IMG, false);

        return rooms;
    }

    // Adds a room, returns new cursorX (end of this room)
    private int addRoom(List<RoomLayout> rooms, RoomLayout.RoomImageType type,
                        int x, int y, int w, int h, String img, boolean flipped) {
        rooms.add(new RoomLayout(type, x, y, w, h, img, flipped));
        return x + w;
    }

    // Picks a random corridor, adds it, returns new cursorX
    private int addCorridor(List<RoomLayout> rooms, int x) {
        boolean useCor1 = random.nextBoolean();
        if (useCor1) {
            return addRoom(rooms, RoomLayout.RoomImageType.CORRIDOR_1,
                x, centerY(COR1_H), COR1_W, COR1_H, CORRIDOR_1, false);
        } else {
            return addRoom(rooms, RoomLayout.RoomImageType.CORRIDOR_2,
                x, centerY(COR2_H), COR2_W, COR2_H, CORRIDOR_2, false);
        }
    }

    // Center room vertically around a common axis (y=128 baseline)
    private int centerY(int roomHeight) {
        return 128 - roomHeight / 2;
    }
}
