package com.roguelike.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Camera - Manages viewport and camera movement
 */
public class Camera {
    private OrthographicCamera camera;
    private float targetX, targetY;
    private float lerpSpeed = 0.1f;

    public Camera(int width, int height) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, width, height);
    }

    /**
     * Follow target entity
     */
    public void followEntity(com.roguelike.core.entities.Entity entity) {
        targetX = entity.getX();
        targetY = entity.getY();
    }

    /**
     * Update camera position smoothly
     */
    public void update() {
        camera.position.x += (targetX - camera.position.x) * lerpSpeed;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
