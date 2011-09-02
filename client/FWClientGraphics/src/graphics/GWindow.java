/*
 * GWindow.java
 *
 * Created on 20. Mai 2003, 10:27
 */


package graphics;

import javax.microedition.lcdui.Graphics;


/**
 *
 * @author  Jochai Papke (Jochai.Papke@AwareDreams.com)
 */
public class GWindow {
    
    /////////////////////
    //// MEMBER VARS ////
    /////////////////////

    public int type;
    
    //// BORDER + INNER OFFSET ////
    /** Size of the border. */
    public int borderSize=1;
    /** Offset between border and content of the window. */
    public int innerOffset=1;
    
    
    //// COLOR / APPEARANCE ////

    /** Color to use for the border if the window is not selected. */
    public int borderColor = 0xCCCCCC;
    /** Color to use for the border if the window is selected. */
    public int activeBorderColor = 0xFFFFFF;
    /** Color to use for the background of the window. */
    public int backColor;
    /** Color to use for the background of the window. */
    public int activeBackColor;
    

    //// WINDOW STATUS AND ORDER ////
    
    /** Indicates whether this window is selected. */
    public boolean selected = false;

    /** Index of this window. */
    public int index;

    protected boolean useParentColors = false;    

    public int x, y, width, height;
    public int xOffset, yOffset;
    public boolean visible = true;
    
    public boolean acceptInput = true;
    
    public boolean selectable = true;
    
    //SPIN BUTTON ONLY
    public GWindow partnerWindow = null;

    public int nextKey = -200;
    public int prevKey = -200;
    public int nextAltKey = -200;
    public int prevAltKey = -200;
    
    public static int activateKey2 = -200;
    
    public GWindow nextWindow  = null;
    public GWindow prevWindow =  null;
    public GWindow nextAltWindow = null;
    public GWindow prevAltWindow = null;
}
