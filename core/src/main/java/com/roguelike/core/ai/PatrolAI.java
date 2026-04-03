package com.roguelike.core.ai;

import com.roguelike.core.dungeon.DungeonLevel;
import com.roguelike.core.entities.Enemy;
import com.roguelike.core.entities.Player;

/**
 * PatrolAI - Patrols fixed path
 */
public class PatrolAI implements AIStrategy {
    private int[] patrolPath;
    private int currentWaypoint = 0;
    private float pathWaitTime = 0;
    private static final float WAYPOINT_PAUSE = 1.0f;

    public PatrolAI(int[] patrolPath) {
        this.patrolPath = patrolPath;
        if (patrolPath == null || patrolPath.length < 2) {
            this.patrolPath = new int[]{0, 0, 2, 0, 2, 2, 0, 2};
        }
    }

    @Override
    public void execute(Enemy enemy, Player player, DungeonLevel level, float deltaTime) {
        if (pathWaitTime > 0) {
            pathWaitTime -= deltaTime;
            return;
        }

        int[] move = decideNextMove(enemy, player, level);
        if (move[0] != 0 || move[1] != 0) {
            enemy.moveTo(enemy.getX() + move[0], enemy.getY() + move[1]);
            pathWaitTime = WAYPOINT_PAUSE;
        }
    }

    @Override
    public int[] decideNextMove(Enemy enemy, Player player, DungeonLevel level) {
        if (patrolPath.length < 2) {
            return new int[]{0, 0};
        }

        int waypointIndex = currentWaypoint * 2;
        if (waypointIndex >= patrolPath.length) {
            waypointIndex = 0;
            currentWaypoint = 0;
        }

        int targetX = patrolPath[waypointIndex];
        int targetY = patrolPath[waypointIndex + 1];

        int dx = Integer.compare(targetX, enemy.getX());
        int dy = Integer.compare(targetY, enemy.getY());

        if (dx == 0 && dy == 0) {
            currentWaypoint++;
            if (currentWaypoint * 2 >= patrolPath.length) {
                currentWaypoint = 0;
            }
        }

        return new int[]{dx, dy};
    }

    @Override
    public String getAIName() {
        return "PatrolAI";
    }
}
