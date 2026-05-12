package com.roguelike.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.roguelike.core.entities.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * EntityAnimator - manages all animations for one entity.
 * Tracks current state and switches animations automatically.
 */
public class EntityAnimator {

    public enum AnimState {
        IDLE, RUN, ATTACK, DEAD, HURT
    }

    private final Map<AnimState, SpriteAnimation> animations = new HashMap<>();
    private AnimState currentState = AnimState.IDLE;
    private boolean facingLeft = false;

    public void addAnimation(AnimState state, SpriteAnimation anim) {
        animations.put(state, anim);
    }

    /**
     * Call every frame with deltaTime from Gdx.graphics.getDeltaTime()
     */
    public void update(float deltaTime) {
        SpriteAnimation current = animations.get(currentState);
        if (current == null) return;

        current.update(deltaTime);

        // Non-looping animations (attack, hurt) return to idle when done
        if (current.isFinished()) {
            if (currentState == AnimState.ATTACK || currentState == AnimState.HURT) {
                setState(AnimState.IDLE);
            }
        }
    }

    /**
     * Switch to a new animation state.
     * Resets the new animation so it always plays from frame 1.
     * Does nothing if already in that state (prevents restart mid-animation).
     */
    public void setState(AnimState newState) {
        if (newState == currentState) return;

        // Don't interrupt attack or hurt mid-play
        if ((currentState == AnimState.ATTACK || currentState == AnimState.HURT)
            && !isCurrentFinished()) {
            return;
        }

        // Dead is permanent — never leave it
        if (currentState == AnimState.DEAD) return;

        currentState = newState;
        SpriteAnimation anim = animations.get(newState);
        if (anim != null) anim.reset();
    }

    private void syncAnimatorToEntity(EntityAnimator animator, Entity entity) {
        // Sync facing direction
        animator.setFacingLeft(entity.isFacingLeft());

        if (!entity.isAlive()) {
            animator.forceState(EntityAnimator.AnimState.DEAD);
            return;
        }

        switch (entity.getState()) {
            case MOVING:
                animator.setState(EntityAnimator.AnimState.RUN);
                break;
            case ATTACKING:
                animator.setState(EntityAnimator.AnimState.ATTACK);
                break;
            case DEAD:
                animator.forceState(EntityAnimator.AnimState.DEAD);
                break;
            default:
                animator.setState(EntityAnimator.AnimState.IDLE);
                break;
        }
    }

    /**
     * Force a state change regardless of current state.
     * Use this for DEAD and HURT only.
     */
    public void forceState(AnimState newState) {
        if (newState == currentState) return;
        currentState = newState;
        SpriteAnimation anim = animations.get(newState);
        if (anim != null) anim.reset();
    }

    /**
     * Returns the current frame to draw, flipped if facing left.
     */
    public TextureRegion getCurrentFrame() {
        SpriteAnimation anim = animations.get(currentState);

        // Fallback to idle if current state has no animation
        if (anim == null) {
            anim = animations.get(AnimState.IDLE);
        }
        if (anim == null) return null;

        TextureRegion frame = anim.getCurrentFrame();
        if (frame == null) return null;

        // Apply horizontal flip for direction
        if (facingLeft && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!facingLeft && frame.isFlipX()) {
            frame.flip(true, false);
        }

        return frame;
    }

    private boolean isCurrentFinished() {
        SpriteAnimation anim = animations.get(currentState);
        return anim != null && anim.isFinished();
    }

    // ── Getters / Setters ────────────────────────────────────────────

    public void setFacingLeft(boolean left) {
        this.facingLeft = left;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public AnimState getCurrentState() {
        return currentState;
    }
}
