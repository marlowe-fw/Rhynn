package rhynn;




import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class PlayfieldCell {
    public static final int function_none = 0x0;
	public static final int function_blocked = 0x1;
	public static final int function_peaceful = 0x2;
	public static final int function_reserved1 = 0x4;
	public static final int function_reserved2 = 0x8;

    public static final int trigger_none = 0x0;
	public static final int trigger_default = 0x1;

    public static final int defaultWidth = 24;
    public static final int defaultHeight = 24;

    private int tilesetIndex = 0;
    private int tileIndex = 0;
    private int functionValue = 0;
    private int triggerValue = 0;

    private Image backgroundImage = null;
    private int imgClipX = 0;
    private int imgClipY = 0;

    public PlayfieldCell() {
    }

    public void setGraphicsInfo(Image img, int imgClipX, int imgClipY) {
        backgroundImage = img;
        this.imgClipX = imgClipX;
        this.imgClipY = imgClipY;
    }

    public void drawToRect(Graphics g, int x, int y, int screenX, int screenY, int rectWidth, int rectHeight) {
        int curWidth = defaultWidth;
        int curHeight = defaultHeight;
        int xDraw = screenX + x;
        int yDraw = screenY + y;

        // adjust the clipping region if part of the cell is out of the screen bounds
        if (x < 0) {
            curWidth += x;
            x = 0;
        } else if (x + defaultWidth > rectWidth) {
            curWidth = rectWidth - x;
        }
        if (y < 0) {
            curHeight += y;
            y = 0;
        } else if (y + defaultHeight > rectHeight) {
            curHeight = rectHeight - y;
        }

        g.setClip(screenX + x, screenY + y, curWidth, curHeight);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, xDraw-imgClipX, yDraw-imgClipY, Graphics.TOP | Graphics.LEFT);
        } else {
            g.setColor(0,0,128);
            g.fillRect(x, y, curWidth, curHeight);
        }
    }

    public void setBaseValues(int functionValue, int triggerValue, int tilesetIndex, int tileIndex) {
        this.tilesetIndex = tilesetIndex;
        this.tileIndex = tileIndex;
        this.functionValue = functionValue;
        this.triggerValue = triggerValue;
    }

    public void addFunction(int newFunction) {
        this.functionValue |= newFunction;
    }

    public int getTilesetIndex() {return tilesetIndex;}
    public int getTileIndex() {return tileIndex;}

    public boolean hasFunction(int checkFunctionValue) {
        return (functionValue & checkFunctionValue) == checkFunctionValue;
    }

    public boolean hasTrigger(int checkTriggerValue) {
        return (triggerValue & checkTriggerValue) == checkTriggerValue;
    }

    /*
    public void setTileInfo(int data) {}
    public void setFunction(int data) {}
    public void setTrigger(int data) {}
     */

}
