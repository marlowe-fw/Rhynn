/*
 * GInputWindow.java
 *
 * Created on 28. Mai 2003, 17:18
 */

package graphics;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author  Jochai Papke (Jochai.Papke@AwareDreams.com)
 */
public class GInputWindow extends GTextWindow {

    /////////////////////
    //// MEMBER VARS ////
    /////////////////////
    
    /** Current column pos of the cursor. */
    int cursorPos=0;

    /** Line No the cursor is in */
    int cursorLineNo=0;

    /** Color of the cursor */
    int cursorColor=0xA0A0A0;

    /** Last key that was pressed (one out of KEY_0 - KEY_9 or KEY_POUND or KEY_STAR). */
    int lastKey=-1;

    /** Key repeat count */
    int keyRepeat=0;

    /** Flag to indicate whether the cursor should move next frame. */
    boolean cursorMove=false;
    
    /** Flag that determines whether the keymapping of this window is shown or not. */
    boolean showKeyMapping=true;
    
    /** Position to use for the key map area in reltaion to the window position. */
    public int keyMapPosition=GTools.POSITION_TOP;
    
    /** Background to display for the key mapping. */
    int keyMapBackground = 0x000099;
    
    /** Color to use for the currently selected char in the keymap area. */
    int keyMapForeground = 0xCC9900;
    
    /** Additional y spacing for the keymap area. */
    int keyMapYExtraSpace = 4;
    
    /** Additional char spacing for the chars in the keymap area. */
    int keyMapCharGap = 1;
    
    /** Last time that a char was put into the GInputWinodw. */
    protected static long inputLastKeypress = 0;
    
    /** Last time the cursor flashed. */
    protected static long lastCursorFlash = 0;
    
    /** Determines whether the cursor should be drawn or not. */
    protected boolean flashCursor = true;
    
    /** Show cursor as hollow block or as line. */
    protected boolean cursorBlock = false;
    
    /** Show cursor if applicable or not at all. */
    protected boolean drawCursor = true;
    
    /** Whether input is numeric or not */
    public boolean numeric = false;
    
    /** Specific information shown when this window is empty. */
    public char[] emptyInfo = null;
    
}