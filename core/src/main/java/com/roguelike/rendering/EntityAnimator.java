package com.roguelike.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

public class EntityAnimator {

    public enum AnimState {
        IDLE, RUN, ATTACK, DEAD, HURT
    }

    private final Map<AnimState, SpriteAnimation> animations = new HashMap<>();
    private AnimState currentState = AnimState.IDLE;
    private boolean facingLeft = false;
    private boolean defaultFacingLeft = false; // ← tracks which way sprite faces in PNG

    public void addAnimation(AnimState state, SpriteAnimation anim) {
        animations.put(state, anim);
    }

    public void update(float deltaTime) {
        SpriteAnimation current = animations.get(currentState);
        if (current == null) return;

        current.update(deltaTime);

        if (current.isFinished()) {
            if (currentState == AnimState.ATTACK || currentState == AnimState.HURT) {
                setState(AnimState.IDLE);
            }
        }
    }

    public void setState(AnimState newState) {
        if (newState == currentState) return;
        if ((currentState == AnimState.ATTACK || currentState == AnimState.HURT)
            && !isCurrentFinished()) return;
        if (currentState == AnimState.DEAD) return;

        currentState = newState;
        SpriteAnimation anim = animations.get(newState);
        if (anim != null) anim.reset();
    }

    public void forceState(AnimState newState) {
        if (newState == currentState) return;
        currentState = newState;
        SpriteAnimation anim = animations.get(newState);
        if (anim != null) anim.reset();
    }

    public TextureRegion getCurrentFrame() {
        SpriteAnimation anim = animations.get(currentState);
        if (anim == null) anim = animations.get(AnimState.IDLE);
        if (anim == null) return null;

        TextureRegion frame = anim.getCurrentFrame();
        if (frame == null) return null;

        // XOR: flip only when current direction differs from sprite's default direction
        boolean shouldFlip = (facingLeft != defaultFacingLeft);

        TextureRegion copy = new TextureRegion(frame);
        copy.flip(shouldFlip, false);
        return copy;
    }

    private boolean isCurrentFinished() {
        SpriteAnimation anim = animations.get(currentState);
        return anim != null && anim.isFinished();
    }

    // ── Setters ──────────────────────────────────────────────────────

    /** Called every frame from syncAnimatorToEntity */
    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    /** Set once when building animator — which way does the PNG face? */
    public void setDefaultFacingLeft(boolean defaultFacingLeft) {
        this.defaultFacingLeft = defaultFacingLeft;
    }

    public boolean isFacingLeft() { return facingLeft; }
    public AnimState getCurrentState() { return currentState; }
}
