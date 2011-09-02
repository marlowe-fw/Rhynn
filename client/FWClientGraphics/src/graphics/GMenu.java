/*
 * gfxMenu.java
 *
 * Created on 26. Mai 2003, 17:30
 */

package graphics;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Jochai Papke (Jochai.Papke@AwareDreams.com)
 */
public class GMenu extends GWindow {

    /////////////////////
    //// MEMBER VARS ////
    /////////////////////

    
    /** Array of windows that are part of the menu container. */
    public GWindow[] items=null;

    /** Index of the window item which is currently selected,
     *  do not allow public access, to ensure all tasks are processed which
     *  are required when setting the index!
     */
    protected int selectedIndex = 0;
    
    protected GTextWindow captionWindow = null;

    public GFont font=null;
    
    int xMin, yMin, xMax, yMax;
}