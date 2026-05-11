package com.roguelike.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

/**
 * SpriteAnimation - cycles through a list of frames at a given speed.
 */
public class SpriteAnimation {
    private List<TextureRegion> frames;
    private float frameDuration;  // seconds per frame
    private float stateTime = 0f;
    private boolean looping;

    public SpriteAnimation(List<TextureRegion> frames, float frameDuration, boolean looping) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.looping = looping;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public TextureRegion getCurrentFrame() {
        if (frames.isEmpty()) return null;
        int totalFrames = frames.size();
        int index;
        float totalDuration = frameDuration * totalFrames;

        if (looping) {
            index = (int)((stateTime % totalDuration) / frameDuration);
        } else {
            index = Math.min((int)(stateTime / frameDuration), totalFrames - 1);
        }
        return frames.get(index);
    }

    public boolean isFinished() {
        return !looping && stateTime >= frameDuration * frames.size();
    }

    public void reset() {
        stateTime = 0f;
    }
}
