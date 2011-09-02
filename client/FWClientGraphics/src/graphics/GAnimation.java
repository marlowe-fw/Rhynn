/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphics;

/**
 *
 * @author marlowe
 */
public class GAnimation {

    private static long DEFAULT_FRAME_DURATION = 100;   // in ms

    private static int ANIMATION_STOPPED = 0;
    private static int ANIMATION_PAUSED = 1;
    private static int ANIMATION_PLAYING = 2;

    private int animationState = ANIMATION_STOPPED;


    private int numLoops = 0;
    private int numFrames = 1;

    private int curFrame = 0;
    private int curLoop = 0;
    private long frameDuration = DEFAULT_FRAME_DURATION;

    private long curFrameStartTime = 0;
    
    public GAnimation(int newNumFrames, long newFrameDuration) {
        numFrames = newNumFrames;
        frameDuration = newFrameDuration;
    }

    public void setFrameDuration(long newFrameDuration) {
        frameDuration = newFrameDuration;
    }

    public void startAnimation(int newNumLoops) {
        stopAnimation();
        if (numFrames == 1)
            return;

        numLoops = newNumLoops;
        curFrameStartTime = System.currentTimeMillis();
        animationState = ANIMATION_PLAYING;
    }

    public void stopAnimation() {
        numLoops = 0;
        curLoop = 0;
        curFrame = 0;
        curFrameStartTime = 0;
        animationState = ANIMATION_STOPPED;
    }

    public boolean isAnimationPlaying() {
        return animationState == ANIMATION_PLAYING;
    }

    public boolean isAnimationPaused() {
        return animationState == ANIMATION_PAUSED;
    }

    public boolean isAnimationStopped() {
        return animationState == ANIMATION_STOPPED;
    }

    public int getCurrentFrame() {
        if (isAnimationPlaying()) {
            // check next frame
            long curTime = System.currentTimeMillis();
            long elapsed = curTime - curFrameStartTime;
            if (elapsed > frameDuration) {
                curFrame++;
                if (curFrame >= numFrames) {
                    curFrame = 0;
                    curLoop++;
                    if (numLoops > 0 && curLoop >= numLoops) {
                        stopAnimation();
                    } else if(numLoops < 0) {
                        curLoop = 0;    // infinite, prevent integer overflow
                    }
                }
                curFrameStartTime = curTime;
            }
        }
        return curFrame;
    }


}

