/*
 * gfxTextWindow.java
 *
 * Created on 20. Mai 2003, 17:16
 */


package graphics;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author  Jochai Papke (Jochai.Papke@AwareDreams.com)
 */
public class GTextWindow extends GWindow {

    /////////////////////
    //// MEMBER VARS ////
    /////////////////////
    
    //// LINE HANDLING ////
    /** Number of the line that is the first visible line in the window */
    protected int firstVisibleLineNo = 0;
    /** Number of the char that is the first visible char in the window */
    protected int firstVisibleCharNo = 0;

    public char[] text = null;
    
    //// TEXT PARAMETERS ////
    /** Maximum number of chars allowed for this TextWindow. */
    public int maxChars = 256;
    /** Defines, how many lines are visible in the window. */
    protected int noOfVisibleLines=0;
    /** Number of characters displayed on one line. */
    protected int noOfCharsPerLine=0;
    /** Number of chars int the current text. */
    public int textFill = 0;
    /** Number of chars in the last line. */
    int lastLineFill = 0;
    
    /** Number of total lines (taking into account line breaks). */
    public int noOfExistingLines=0;

    /** 
     * Whether this textwindow is a password window or not 
     * (will display asterix signs '*' if true). 
     */
    public boolean password = false;
    
    public boolean allowWrapInWords = false;
    
    //// DRAWING ////
    public boolean autoScrollExtents = false;
    protected GFont font = null;

    //BUTTON ONLY
    protected boolean activated=false;
    
    public boolean centerTextH = false;
    public boolean centerTextV = false;
    
    
    public Image buttonImage = null;
    
    public void prepareButtonImage() {
        buttonImage = null;
        int tempX = x, tempY = y;
        x=0;
        y=0;
        Image temp = Image.createImage(width, height);
        Graphics g = temp.getGraphics();
        GTools.drawWindow(g, this, true);
        buttonImage = temp;
        x=tempX;
        y=tempY;
    }
}
