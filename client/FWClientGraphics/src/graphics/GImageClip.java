/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphics;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import FWUtils.SwitchSetting;

/**
 *
 * @author marlowe
 */
public class GImageClip {

    private Image image;
    private int imgPosX;
    private int imgPosY;
    private int width;
    private int height;

    private int xPos;
    private int yPos;

    private int numFrames = 1;
    //private int currentFrame = 0;
    private int frameWidth = 0;
    private int curFrame = 0;

    GAnimation animation = null;
    GAnimation flashAnimation = null;

    public SwitchSetting Enabled = new SwitchSetting(true);
    
    public GImageClip(Image image, int imgPosX, int imgPosY, int width, int height, int numFrames) {
        this(image, imgPosX, imgPosY, width, height);
        setNumFrames(numFrames);
    }

    public GImageClip(Image image, int imgPosX, int imgPosY, int width, int height) {
        this.image = image;
        this.imgPosX = imgPosX;
        this.imgPosY = imgPosY;
        this.width = this.frameWidth = width;
        this.height = height;
        numFrames = 1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public void setNumFrames(int numFrames) {
        //currentFrame = 0;
        this.numFrames = numFrames;
        frameWidth = width / numFrames;
    }

    public void setupAnimation(long frameDuration) {
        animation = new GAnimation(numFrames, frameDuration);
    }

    public void startAnimation(int numLoops) {
        if (animation != null)
            animation.startAnimation(numLoops);
    }

    public void stopAnimation() {
        if (animation != null)
            animation.stopAnimation();
    }

    public void flash(int iterations, long singleFlashDuration) {
        flashAnimation = new GAnimation(2, singleFlashDuration/2);
        flashAnimation.startAnimation(iterations);
    }

    public boolean isFlashing() {
        if (flashAnimation != null && flashAnimation.isAnimationPlaying() && flashAnimation.getCurrentFrame() == 1)
            return true;
                    
        return false;
    }


    public void draw(Graphics g) {
        draw(g, xPos, yPos);
    }

    public void draw(Graphics g, int x, int y) {
        draw(g, x, y, frameWidth, height);
    }

    public void draw(Graphics g, int x, int y, int overrideWidth, int overrideHeight) {
        draw(g, x, y, x, y, overrideWidth, overrideHeight);
    }

    public void draw(Graphics g, int x, int y, int overrideClipX, int overrideClipY, int overrideWidth, int overrideHeight) {
        if (animation != null && animation.isAnimationPlaying())
            setCurrentFrame(animation.getCurrentFrame());

        if (isFlashing() || Enabled.isOff())
            return;

        g.setClip(overrideClipX, overrideClipY, overrideWidth, overrideHeight);

        if (image != null) {
            g.drawImage(image, x - (curFrame * frameWidth) - imgPosX, y - imgPosY, Graphics.LEFT | Graphics.TOP);
        }
        else {
            g.setColor(255,0,0);
            g.drawRect(x, y, overrideWidth-1, overrideHeight-1);
        }
    }

    public void setCurrentFrame(int frameNo) {
        curFrame = frameNo % numFrames;
    }

    public int getCurrentFrame() {
        return curFrame;
    }

    public void setPosition(int newX, int newY) {
        xPos = newX;
        yPos = newY;
    }
   


}
