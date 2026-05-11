package com.roguelike.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * EntityAnimator - manages all animations for one entity.
 * Tracks current state and switches animations automatically.
 */
public class EntityAnimator {

    public enum AnimState {
        IDLE, RUN, ATTACK, DEAD
    }

    private Map<AnimState, SpriteAnimation> animations = new HashMap<>();
    private AnimState currentState = AnimState.IDLE;
    private AnimState previousState = null;
    private boolean facingLeft = false;

    public void addAnimation(AnimState state, SpriteAnimation anim) {
        animations.put(state, anim);
    }

    public void update(float deltaTime) {
        SpriteAnimation current = animations.get(currentState);
        if (current != null) {
            current.update(deltaTime);
            // If non-looping anim (attack) finishes, go back to idle
            if (current.isFinished() && currentState == AnimState.ATTACK) {
                setState(AnimState.IDLE);
            }
        }
    }

    public void setState(AnimState newState) {
        if (newState == currentState) return;
        previousState = currentState;
        currentState = newState;
        SpriteAnimation anim = animations.get(newState);
        if (anim != null) anim.reset();
    }

    public TextureRegion getCurrentFrame() {
        SpriteAnimation anim = animations.get(currentState);
        if (anim == null) {
            // fallback to idle
            anim = animations.get(AnimState.IDLE);
        }
        if (anim == null) return null;

        TextureRegion frame = anim.getCurrentFrame();
        if (frame == null) return null;

        // Flip horizontally if facing left
        if (facingLeft && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!facingLeft && frame.isFlipX()) {
            frame.flip(true, false);
        }
        return frame;
    }

    public void setFacingLeft(boolean left) { this.facingLeft = left; }
    public AnimState getCurrentState() { return currentState; }
}
