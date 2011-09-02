/*
 * gfxTools.java
 *
 * Created on 4. Juni 2003, 14:19
 */
 
package graphics;

import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 *
 * @author  Jochai Papke (Jochai.Papke@AwareDreams.com)
 */
public class GTools {
    
    //////////////////////////
    //// STATIC CONSTANTS ////
    //////////////////////////
    
    /** Transparent color */
    public static final int TRANSPARENT = -1;
    
    //// WINDOW TYPES AVAILABLE /////
    
    public static final int             WINDOW = 0;
    public static final int             TEXTWINDOW = 1;
    public static final int             INPUTWINDOW = 2;
    public static final int             IMAGEWINDOW = 3;
    public static final int             BUTTON = 4;
    public static final int             MENU = 5;
    public static final int             LIST = 6;    
    public static final int             SPINBUTTON = 7;
    
    public static final int             YES = 1;
    public static final int             NO = 2;
    public static final int             OK = 1;    
    
    public static final int             POSITION_TOP = 0;
    public static final int             POSITION_RIGHT = 1;
    public static final int             POSITION_BOTTOM = 2;
    public static final int             POSITION_LEFT = 3;
    
    /////////////////////
    //// STATIC VARS ////
    /////////////////////
    
    //// Font and Input ////
    
    /** The key mapping table to use for gfxInputTextWindow objects. */
    public static char[][] inputKeyTable = null;
    
    
    //// RESTORING GFX SETTINGS /////
    
    /** Color that should be restored on the gfx context after drawing. */
    private static int prevColor = 0x000000;
    /** Clipping values that should be restored after drawing. */
    private static int prevClipX = 0;
    private static int prevClipY = 0;
    private static int prevClipWidth = 0;
    private static int prevClipHeight = 0;

    private static int displayX = 0;
    private static int displayY = 0; 
    private static int displayWidth = 96;
    private static int displayHeight = 128;
    
    private static int saveBorderColor = 0x000000; 
    private static int saveActiveBorderColor = 0x000000;
    private static int saveBackColor = 0x000000;

    private static int defaultBorderColor = 0xCCCCCC; 
    private static int defaultActiveBorderColor = 0xFFFFFF;
    private static int defaultBackColor = 0x000000;
    private static int defaultActiveBackColor = 0x999999;
    
    
    //// HELPERS ////
    
    //private static int i, j, k, l, m, n, 
    private static int d, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, m1, e, w1, h1, s;
    private static GWindow wndTmp = null;
    private static char[] tmpChars = null;
    
    private static long curTime = 0;
    
    private static GInputWindow tmpInputWindow = null;
    private static char[] inputInfo = "* = DEL  0 = SPACE".toCharArray();
    
    /////////////////////////
    //// STATIC METHODS /////
    /////////////////////////

    
    public static void inputWindowEnableKeymappingDisplay(GInputWindow wnd, int yExtraSpace, int charGap, int backgroundColor, int selectionColor, boolean showOnTop) {
        wnd.showKeyMapping = true;
        wnd.keyMapYExtraSpace = yExtraSpace;
        wnd.keyMapCharGap = charGap;
        wnd.keyMapBackground = backgroundColor;
        wnd.keyMapForeground = selectionColor;
        if (showOnTop) {
            wnd.keyMapPosition = POSITION_TOP;
        } else {
            wnd.keyMapPosition = POSITION_BOTTOM;
        }
    }
    
    public static void inputWindowShowCurrentKeyMapping(Graphics g, GInputWindow wnd, boolean showInfoOnly, char[] alternateMsg) {
        if (g==null || wnd == null || inputKeyTable == null) {
            return;
        }

        
        d5 = (wnd.keyMapYExtraSpace/2);
        d6 = wnd.font.charHeight + wnd.keyMapYExtraSpace;   //keymap height
        
        if (!showInfoOnly) {    //show keymapping
            d6+=2;
            d5+=1;
            d = wnd.x;

            if (wnd.keyMapPosition == GTools.POSITION_BOTTOM) {
                d1 = wnd.y + wnd.height;
            } else {
                d1 = wnd.y - d6;
            }
            d2 = wnd.width;
            
            //set the clipping
            g.setClip(d, d1, d2, d6);
            //draw background
            g.setColor(wnd.keyMapBackground);
            g.fillRect(d, d1, d2, d6);
            

            if (wnd.lastKey==Canvas.KEY_STAR || !(wnd.lastKey>=48 && wnd.lastKey <=57 || wnd.lastKey==Canvas.KEY_POUND) ) {
                return;
            }
            if (wnd.lastKey!=Canvas.KEY_POUND) {
                d12 = wnd.lastKey - 48;
            } else {
                d12 = 10;    //KEY_POUND
            }
            d8 = wnd.font.charWidth + wnd.keyMapCharGap;
            d9 = inputKeyTable[d12].length;
            d10 = wnd.keyRepeat % d9;   //current selected key in the current key mapping range

            
 
            //get first char that should be displayed and length of key mapping area
            if (d8 * d9 > d2) {   // d8*d9: total length of all chars in t he keymap area
                d11 = d10 + 2 - ((d2-4) / d8); //get first char that is displayed
                if (d11 < 0) {                       // displayWidth/d8 : no of chars that can be displayed
                    d11 = 0;
                }
            } else {
                d11 = 0;
            }
            
            //draw surrent char selection            
            d7 = (wnd.font.charWidth + wnd.keyMapCharGap);
            d4 = ((d10 - d11) * d7) + 2;
            g.setClip(d + d4, d1, wnd.font.charWidth, d6);
            g.setColor(wnd.keyMapForeground);
            g.fillRect(d + d4, d1, wnd.font.charWidth, d6);
            //draw all chars, starting with the char at pos d11
            wnd.font.drawString(g, inputKeyTable[d12], d + 2, d1 + d5, d2-4, d6, d11, wnd.keyMapCharGap);
            d6-=2;
            d5-=1;
        }   
        
        
        //show info
        d = displayX;
        d1 = displayY;
        d2 = displayWidth;

        //set the clipping
        g.setClip(d, d1, d2, d6);
        //draw background
        g.setColor(wnd.keyMapBackground);
        g.fillRect(d, d1, d2, d6);

        //draw all chars, starting with the char at pos d11
        if (alternateMsg!=null) {
            wnd.font.drawString(g, alternateMsg, d + 2, d1 + d5, d2, d6);
        } else {
            wnd.font.drawString(g, inputInfo, d + 2, d1 + d5, d2, d6);
        }
    }
    
    public static void drawArrow(Graphics g, int direction, int x, int y, int size, int color) {
        saveGraphicsSettings(g);

        g.setColor(color);       
        switch (direction) {
            case 0: // up
                d8 = (2*size)-1;    //x-extend
                d9 = size;          //y-extend
                d4 = x-(d8/2);      //x-startPlay
                d5 = y;             //y-startPlay
                g.setClip(d4, d5-d9+1, d8, d9);
                for(d11=0; d11<size; d11++) {
                    g.drawLine(d4+d11, d5-d11, d4+d8-d11-1, d5-d11);
                }
                break;
            case 1: // right
                d8 = size;          //x-extend
                d9 = (2*size)-1;    //y-extend
                d4 = x;             //x-startPlay
                d5 = y-(d9/2);      //y-startPlay
                g.setClip(d4, d5, d8, d9);
                for(d11=0; d11<size; d11++) {
                    g.drawLine(d4+d11, d5+d11, d4+d11, d5+d9-d11-1);
                }
                break;
            case 2: // down
                d8 = (2*size)-1;    //x-extend
                d9 = size;          //y-extend
                d4 = x-(d8/2);      //x-startPlay
                d5 = y;             //y-startPlay
                g.setClip(d4, d5, d8, d9);
                for(d11=0; d11<size; d11++) {
                    g.drawLine(d4+d11, d5+d11, d4+d8-d11-1, d5+d11);
                }
                break;
            default: // left
                d8 = size;          //x-extend
                d9 = (2*size)-1;    //y-extend
                d4 = x;        //x-startPlay
                d5 = y-(d9/2);      //y-startPlay
                g.setClip(d4-d8+1, d5, d8, d9);
                for(d11=0; d11<size; d11++) {
                    g.drawLine(d4-d11, d5+d11, d4-d11, d5+d9-d11-1);
                }
                break;
                /*
            default:    //default is down
                d8 = (2*size)-1;    //x-extend
                d9 = size;          //y-extend
                d4 = x-(d8/2);      //x-startPlay
                d5 = y;             //y-startPlay
                g.setClip(d4, d5, d8, d9);
                for(d11=0; d11<size; d11++) {
                    g.drawLine(d4+d11, d5+d11, d4+d8-d11-1, d5+d11);
                }
                break;*/
                
        }
        
        restoreGraphicsSettings(g);
    }
    
    
    
    public static void setDisplayDimensions(int x, int y, int width, int height) {
        GTools.displayX = x;
        GTools.displayY = y;
        GTools.displayWidth = width;
        GTools.displayHeight = height;
    }
    
    
    /** Save gfx settings. */
    public static void saveGraphicsSettings(Graphics g) {
        //save previous gfx settings
        prevColor = g.getColor();
        prevClipX = g.getClipX();
        prevClipY = g.getClipY();
        prevClipWidth = g.getClipWidth();
        prevClipHeight = g.getClipHeight();
    }
    
    /** Restore gfx settings. */
    public static void restoreGraphicsSettings(Graphics g) {
        //restore previous settings
        g.setColor(prevColor);
        g.setClip(prevClipX, prevClipY, prevClipWidth, prevClipHeight);
    }
    
    /** Check whether the button was activated since the status was checked for the last time, 
     *  do not allow public access to member activeted, so the activated flag is set to false 
     *  once wasActivated() is called!
     */
    public static boolean buttonWasActivated(GTextWindow button) {
        if (button.activated) {
            button.activated = false;
            return true;
        }
        else {
            return false;
        }
    }
    
    public static boolean listWasActivated(GList list) {
        if (list.activated) {
            list.activated = false;
            return true;
        }
        else {
            return false;
        }
    }
    

     public static void setDefaultWindowColors(int borderColor, int activeBorderColor, int backColor, int activeBackColor) {
        defaultBorderColor = borderColor; 
        defaultActiveBorderColor = activeBorderColor;
        defaultBackColor = backColor;
        defaultActiveBackColor = activeBackColor;
    }
    
    
    
    /**
     * Find out how the text should be formatted according to 
     * the window size and font.
     */
    public static void textWindowCalculateTextSettings(GTextWindow wnd, GFont font) {
        if (font!=null) {
            //get number of chars per line
            wnd.noOfCharsPerLine = (wnd.width - (2*wnd.borderSize) - (2*wnd.innerOffset)) / font.charWidth;
            //get number of visible lines
            wnd.noOfVisibleLines = (wnd.height - (2*wnd.borderSize) - (2*wnd.innerOffset) + 1) / font.charHeight;
        }
    }

    public static void listCalculateEntrySettings(GList wnd, GFont font) {
        if (font!=null) {
            wnd.totalGapY = wnd.entryGapY;
            if (wnd.useIcons && wnd.font.charHeight + wnd.totalGapY < wnd.iconHeight) {
                wnd.totalGapY = wnd.iconHeight - wnd.font.charHeight;
            }
            //get number of chars per line
            wnd.noOfCharsPerLine = (wnd.width - (2*wnd.borderSize) - (2*wnd.innerOffset) - wnd.xSpace) / font.charWidth;
            //get number of visible entries
            wnd.noOfVisibleEntries = (wnd.height - (2*wnd.borderSize) - (2*wnd.innerOffset)) / (font.charHeight+wnd.totalGapY);
        }
        for (int i=0; i<wnd.entries.size(); i++) {
            GListEntry gse = (GListEntry)wnd.entries.elementAt(i);
            if (gse!= null) {
                if (gse.entry.length() > wnd.noOfCharsPerLine) {
                    gse.entry = gse.entry.substring(0, wnd.noOfCharsPerLine-2);
                    gse.entry += "..";
                }
            }
        }
        listSetSelectedIndex(wnd, wnd.selectedEntry);        
    }
    
    

    public static GWindow windowCreate(int x, int y, int width, int height) {
        GWindow wnd = new GWindow();
        wnd.type = WINDOW;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
    }
    
    public static void windowSetNextPrevKeys(GWindow wnd, int nextKey, int prevKey, int nextAltKey, int prevAltKey) {
        wnd.nextKey  = nextKey;
        wnd.nextAltKey  = nextAltKey;
        wnd.prevKey  = prevKey;
        wnd.prevAltKey  = prevAltKey;
    }
    
    public static GTextWindow textWindowCreate(int x, int y, int width, int height, String text, int maxChars, GFont font, boolean autoScrollExtents) {
        GTextWindow wnd = new GTextWindow();
        wnd.font = font;
        wnd.type = TEXTWINDOW;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        wnd.maxChars = maxChars;
        wnd.autoScrollExtents = autoScrollExtents;
        textWindowCalculateTextSettings(wnd, font);
        wnd.text = new char[maxChars];
        for (int i=0; i<maxChars; i++) {
            wnd.text[i]=' ';
        }
        textWindowAddText(wnd, text);
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
    }

    public static GImageWindow imageWindowCreate(int x, int y, int width, int height, int clipX, int clipY, Image image) {
        GImageWindow wnd = new GImageWindow();
        wnd.type = IMAGEWINDOW;
        wnd.x = x;
        wnd.y = y;
        wnd.image = image;
        if (height == 0 && image != null) {
            height = image.getHeight();
        }
        if (width == 0 && image != null) {
            width = image.getWidth();
        }
        wnd.width = width;
        wnd.height = height;
        wnd.clipOffsetX = clipX;
        wnd.clipOffsetY = clipY;
        windowSetColors(wnd, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT);

        wnd.borderSize = 0;
        wnd.innerOffset = 0;
        wnd.selectable = false;
        return wnd;
    }

    public static GList listCreate(int x, int y, int width, int height, int entryGapY, int xSpace, int maxEntries, GFont font) {
        GList wnd = new GList();
        wnd.type = LIST;
        wnd.font = font;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        //wnd.maxEntries = maxEntries;
        wnd.entryGapY = entryGapY;
        wnd.totalGapY = entryGapY;
        wnd.selectedEntry = 0;    
        wnd.firstVisibleEntry = 0;
        wnd.xSpace = xSpace;
        listCalculateEntrySettings(wnd, font);
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
    }    

    public static void listSetIconDimensions(GList list, int iconWidth, int iconHeight) {
        if (list.useIcons) {
            listDiscardIcons(list);
        }
        list.useIcons = true;
        /*
        list.entriesIcons = new int[list.maxEntries];
        list.useIcons = true;
        list.iconImages = new Image[noOfIcons];
        list.iconCoordinates = new int [noOfIcons][2];
        */
        list.iconWidth = iconWidth;
        list.iconHeight = iconHeight;
        list.xSpace += iconWidth;
        listCalculateEntrySettings(list, list.font);
    }
    
    public static void listDiscardIcons(GList list) {
        list.useIcons = false;
        list.xSpace -= list.iconWidth;
        list.totalGapY = list.entryGapY;
        listCalculateEntrySettings(list, list.font);
    }
    
    public static void listSetDimensions(GList wnd, int width, int height, int entryGapY, int xSpace) {
        wnd.width = width;
        wnd.height = height;
        wnd.entryGapY = entryGapY;
        wnd.totalGapY = entryGapY;
        wnd.xSpace = xSpace;
        listCalculateEntrySettings(wnd, wnd.font);
    }
    
    /** Make sure all list entries fit into the menu extends. */
    public static void listEnsureContainAll(GList list, boolean minimumExtendsY) {
        if (list==null || list.font==null)
            return;
        
        int h = list.entries.size() * (list.totalGapY + list.font.charHeight) + 2*(list.borderSize + list.innerOffset);
        if (h > list.height || (h < list.height && minimumExtendsY)) {
            list.height = h;
            listCalculateEntrySettings(list, list.font);
        }
    }
    
    public static void listSetIconForEntry(GList list, int entryIndex, GImageClip icon) {
        if (!list.useIcons || !(entryIndex < list.entries.size()))
            return;
        GListEntry gle = (GListEntry)list.entries.elementAt(entryIndex);
        gle.setIcon(icon);
    }

    
    public static boolean listAppendEntry(GList list, char[] entry, Object entryData) {
        return listAppendEntry(list, new String(entry), entryData);
    }

    public static boolean listAppendEntry(GList list, String entry, Object entryData, GImageClip gic) {
        if (entry.length() > list.noOfCharsPerLine) {
            entry = entry.substring(0, list.noOfCharsPerLine-2);
            entry += "..";
        }
        GListEntry newEntry = new GListEntry(entry, entryData, gic);
        list.entries.addElement(newEntry);
        return true;        
    }

    public static boolean listAppendEntry(GList list, String entry, Object entryData) {
        return listAppendEntry(list, entry, entryData, null);
    }
    
    public static boolean listInsertEntry(GList list, char[] entry, Object entryData, int pos) {
        return listInsertEntry(list, new String(entry), entryData, pos);
    }
        
    public static boolean listInsertEntry(GList list, String entry, Object entryData, int pos) {
        /*if (list.entries.size() == list.maxEntries)
            return false;*/

        if (entry.length() > list.noOfCharsPerLine) {
            entry = entry.substring(0, list.noOfCharsPerLine-2);
            entry += "..";
        }

        GListEntry newEntry = new GListEntry(entry, entryData);
        list.entries.insertElementAt(newEntry, pos);
        return true;
    }

    public static void listRemoveAllEntries(GList list) {
        list.entries.removeAllElements();
        list.selectedEntry = 0;
        list.firstVisibleEntry = 0;
    }
    
    public synchronized static void listRemoveEntry(GList list, int pos) {
        if (list.entries.size()==0 || !(pos >=0 && pos < list.entries.size())) {
            return;
        }
        
        list.entries.removeElementAt(pos);

        if (list.selectedEntry >= list.entries.size()) {
            if (list.entries.size() > 0) {
                listSetSelectedIndex(list, list.entries.size()-1);
            } else {
                listSetSelectedIndex(list, 0);
            }
        }
    }

    public static void listSetSelectedIndex(GList list, int index) {
        if (list.entries.size()==0) {
            list.selectedEntry = 0;
            return;
        }
        
        if (index >= list.entries.size()) {
            index = list.entries.size()-1;
        } else if (index < 0) {
            index = 0;
        }
        //check if scrolling is needed
        while (index < list.firstVisibleEntry) {
            list.firstVisibleEntry--;
        }
        while (list.firstVisibleEntry + list.noOfVisibleEntries-1 < index) {
            list.firstVisibleEntry++;
        }
        list.selectedEntry = index;
    }

    public static int listGetSelectedIndex(GList list) {
        if (list.entries.size() > 0) {
            return list.selectedEntry;
        } else {
            return -1;
        }
    }
    
    
    public static void listSelectNext(GList list) {
        if (list.selectedEntry+1 < list.entries.size()) {
            int j = (list.noOfVisibleEntries-1);
            //ensure visible
            int k = list.selectedEntry+1;
            while (list.firstVisibleEntry + j < k) {
                list.firstVisibleEntry++;
            }
            list.selectedEntry++;
        } else if (list.cycleWrap) {
            // select first
            list.firstVisibleEntry = 0;
            list.selectedEntry = 0;
        }

    }
    
    public static void listSelectPrev(GList list) {
        if (list.selectedEntry-1 >= 0) {
            int j = (list.noOfVisibleEntries-1);
            list.selectedEntry--;
            //ensure visible
            while (list.firstVisibleEntry > list.selectedEntry) {
                list.firstVisibleEntry--;
            }
        } else if (list.cycleWrap) {
            // select last
            int j = (list.noOfVisibleEntries-1);
            int k = list.entries.size() - 1;
            if (k > 0) {
                // ensure visible
                list.firstVisibleEntry = k - j;
                if (list.firstVisibleEntry < 0) list.firstVisibleEntry = 0;
                list.selectedEntry = k;
            }
        }
    }
    
    
    public static Object listGetData(GList list) {
        if (list.entries.size() > 0) {
            GListEntry gle = (GListEntry)(list.entries.elementAt(list.selectedEntry));
            if (gle!=null) {
                return gle.entryData;
            }
        }
        return null;
    }

    
    public static char[] listGetEntry(GList list) {
        String s = listGetEntryString(list);
        if (s!=null) {
            return s.toCharArray();
        }
        return null;
    }

    public static String listGetEntryString(GList list) {
        return listGetEntryStringAt(list, list.selectedEntry);
    }
    
    
    public static Object listGetDataAt(GList list, int index) {
        if (list!=null && list.entries !=null && index>=0 && index < list.entries.size()) {
            GListEntry gle = (GListEntry)(list.entries.elementAt(index));
            if (gle!=null) {
                return gle.entryData;
            }
        }
        return null;
    }


    public static char[] listGetEntryAt(GList list, int index) {
        String s = listGetEntryStringAt(list, index);
        if (s!=null) {
            return s.toCharArray();
        }
        return null;
    }

    public static String listGetEntryStringAt(GList list, int index) {
        if (list.entries!=null && list.entries.size() > 0) {
            GListEntry gle = (GListEntry)(list.entries.elementAt(index));
            if (gle != null) {
                return gle.entry;
            }
        }
        return null;
    }
    
    
    public static void listSetDataAt(GList list, Object obj, int index) {
        if (list!=null && list.entries !=null && index>=0 && index < list.entries.size()) {
            GListEntry gle = (GListEntry)(list.entries.elementAt(index));
            if (gle!=null) {
                gle.entryData = obj;
            }

        }
    }

    public static void listSetEntryAt(GList list, char[] entry, int index) {
        if (list!=null && list.entries !=null && index>=0 && index < list.entries.size()) {
            GListEntry existingEntry = (GListEntry)(list.entries.elementAt(index));

            if (existingEntry!=null) {
                String entryString = new String(entry);
                if (entryString.length() > list.noOfCharsPerLine) {
                    entryString = entryString.substring(0, list.noOfCharsPerLine-2);
                    entryString += "..";
                }
                existingEntry.entry = entryString;
            }
        }
    }
    

    public static void textWindowSetMaxChars(GTextWindow wnd, int maxChars) {
        char[] oldText = wnd.text;
        if (wnd.type != INPUTWINDOW) {
            textWindowRemoveText(wnd);
        } else {
            inputWindowRemoveText((GInputWindow)wnd);
        }
        wnd.maxChars = maxChars;
        wnd.text = new char[maxChars];
        for (int i=0; i<maxChars; i++) {
            if (i<oldText.length) {
                wnd.text[i]=oldText[i];
            } else {
                wnd.text[i]=' ';
            }
        }
        
    }
    
    
    /** Creates a new instance of gfxInputWindow */
    public static GInputWindow inputWindowCreate(int x, int y, int width, int height, int maxChars, GFont font, int cursorColor) {
        GInputWindow wnd = new GInputWindow();
        wnd.type = INPUTWINDOW;
        wnd.font = font;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        wnd.maxChars = maxChars;
        wnd.autoScrollExtents = false;
        textWindowCalculateTextSettings(wnd, font);        
        wnd.text = new char[maxChars];
        for (int i=0; i<maxChars; i++) {
            wnd.text[i]=' ';
        }
        wnd.cursorColor = cursorColor;
        wnd.noOfExistingLines = 1;
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
    }
    
   


    /** Create a new button. */
    public static GTextWindow buttonCreate(int x, int y, String text, GFont font, boolean useParentColors) {
        GTextWindow wnd = new GTextWindow();
        wnd.type = BUTTON;
        wnd.font = font;
        wnd.useParentColors = useParentColors;
        wnd.x = x;
        wnd.y = y;
        buttonSetText(wnd, text, false);
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        //wnd.activateKey2 = -6; // SOFTKEY1 for nokia UI 60 phones (using fullcanvas)
        return wnd;
    }

    
    public static GTextWindow labelCreate(int x, int y, String text, GFont font, boolean transparent) {
        GTextWindow wnd = new GTextWindow();
        wnd.type = BUTTON;
        wnd.font = font;
        wnd.useParentColors = false;
        wnd.x = x;
        wnd.y = y;
        wnd.innerOffset = 0;
        wnd.borderSize = 0;
        wnd.selectable = false;
        wnd.acceptInput = false;
        buttonSetText(wnd, text, false);
        if (transparent) {
            windowSetColors(wnd, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT);
        } else {
            windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        }
        return wnd;
    }
    
    public static void labelSetText(GTextWindow wnd, String text, boolean keepXEnd) {
        buttonSetText(wnd, text, keepXEnd);
    }
    
    public static GWindow spinButtonCreate(int x, int y, int width, int height, GWindow partnerWindow) {
        GWindow wnd = new GWindow();
        wnd.type = SPINBUTTON;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        wnd.partnerWindow = partnerWindow;
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
        
    }
    

    public static void buttonSetText(GTextWindow wnd, String text, boolean keepXEnd) {
        wnd.type = BUTTON;
        int l = wnd.width;
        int lines = 1;
        int c1 = 0; int c2 = 0;
        if (text==null) {
            text = "-";
        }
        for (int i=0; i<text.length(); i++) {
            c1++;
            if (text.charAt(i)=='\n') {
                if (c1>c2) {
                    c2 = c1;
                }
                lines++;
                c1 = 0;
            }
        }
        if (c1>c2) {
            c2=c1;
        }
        wnd.width = (c2*wnd.font.charWidth)+(2*wnd.borderSize)+(2*wnd.innerOffset);
        if (wnd.width > displayWidth) {
            wnd.width = displayWidth;
        }
        wnd.height = lines*wnd.font.charHeight+(2*wnd.borderSize)+(2*wnd.innerOffset);
        
        textWindowCalculateTextSettings(wnd, wnd.font);
        wnd.maxChars = text.length();
        wnd.text=null;
        System.gc();
        wnd.text = new char[wnd.maxChars];
        for (int i=0; i<wnd.maxChars; i++) {
            wnd.text[i]=' ';
        }
        wnd.autoScrollExtents = false;
        wnd.textFill = 0;
        wnd.lastLineFill = 0;
        wnd.firstVisibleLineNo=0;
        wnd.firstVisibleCharNo=0;
        wnd.noOfExistingLines = 0;

        textWindowAddText(wnd, text);
        
        if (wnd.noOfExistingLines > lines) {
            wnd.height += (wnd.noOfExistingLines-lines) * wnd.font.charHeight;
        }

        textWindowCalculateTextSettings(wnd, wnd.font);
        
        
        
        if (keepXEnd && wnd.width != l) {
            l = wnd.width - l;
            wnd.x -= l;
        }
    }

    /**
     * Get the minimum x-extend of a button, text settings 
     * must have been calculated before.
     */
    public static int buttonGetMinimumWidth(GTextWindow button) {
        return ((button.noOfCharsPerLine * button.font.charWidth) + 2 * (button.innerOffset + button.borderSize));
    }
    
    
    
    /** Create a new menu. */
    public static GMenu menuCreate(int x, int y, int width, int height, int noOfItems) {
        GMenu wnd = new GMenu();
        wnd.type = GTools.MENU;
        wnd.x = x;
        wnd.y = y;
        wnd.width = width;
        wnd.height = height;
        wnd.borderSize = 0;
        wnd.innerOffset = 0;
        wnd.items = new GWindow[noOfItems];
        for (int i=0; i<wnd.items.length; i++) {
            wnd.items[i] = null;
        }
        windowSetNextPrevKeys(wnd, Canvas.DOWN, Canvas.UP, Canvas.RIGHT, Canvas.LEFT);
        windowSetColors(wnd, defaultBorderColor, defaultActiveBorderColor, defaultBackColor, defaultActiveBackColor);
        return wnd;
    }


    
    
    public static void menuRemoveAllItems(GMenu menu) {
        if (menu.items!=null) {
            for (int i=0; i<menu.items.length; i++) {
                menu.items[i] = null;
            }
        }
        menu.selectedIndex = -1;
    }
    
    public static void menuSetMaxItems(GMenu menu, int maxItems) {
        if (maxItems <= 0) {
            menuRemoveAllItems(menu);
        }
        else {
            if (menu.items!=null) {
                //transfer existing items
                GWindow[] tmpItems = new GWindow[maxItems];
                int k;
                if (menu.items.length > maxItems) {
                    k = maxItems;
                } else {
                    k = menu.items.length;
                }
                System.arraycopy(menu.items, 0, tmpItems, 0, k);
                menu.items = null;
                menu.items = tmpItems;
                //ensure valid selection
                if (menuGetSelected(menu) >= maxItems) {
                    menuSetSelected(menu, 0);
                }
            }
        }
    }

    
    
    /** Place the items on the menu, using the menu position as the virtual origin for each menu item. */
    public static void menuPlaceItems(GMenu menu) {
        if (menu.items==null)
            return;

        int w = menu.x + menu.borderSize + menu.innerOffset;
        int h = menu.y + menu.borderSize + menu.innerOffset;
        int k = 0;
        
        if (menu.captionWindow!=null) {
            //restore original pos
            menu.captionWindow.x -= menu.captionWindow.xOffset;
            menu.captionWindow.y -= menu.captionWindow.yOffset;
            k = menu.captionWindow.y + menu.captionWindow.height + 1;   //get additional y offset for other items
            //set new offset
            menu.captionWindow.xOffset = w;
            menu.captionWindow.yOffset = h;
            //set new pos
            menu.captionWindow.x += w;
            menu.captionWindow.y += h;
            h += k; //add the caption offset
        }
        
        for (int i=0; i<menu.items.length; i++) {
            if (menu.items[i]!=null) {
                //restore original pos
                menu.items[i].x -= menu.items[i].xOffset;
                menu.items[i].y -= menu.items[i].yOffset;
                //set new offset
                menu.items[i].xOffset = w;
                menu.items[i].yOffset = h;
                //set new pos
                menu.items[i].x += w;
                menu.items[i].y += h;
                
                if (menu.items[i].y < menu.items[i].yOffset) {
                    menu.items[i].y = menu.items[i].yOffset;
                }
            }
        }
    }


    /** Make sure all items fit into the menu extends. */
    public static void menuEnsureContainAll(GMenu menu, boolean minimumExtendsX, boolean minimumExtendsY) {
        int w = 0; int h = 0; int j = 0; int k = 0;
        int n = 0;
        if (menu.captionWindow != null) {
            n = menu.captionWindow.y - menu.captionWindow.yOffset + menu.captionWindow.height + 1;
        }
        
        for (int i=0; i<menu.items.length; i++) {
            if (menu.items[i] != null) {
                j = menu.items[i].x - menu.items[i].xOffset + menu.items[i].width;
                k = menu.items[i].y - menu.items[i].yOffset + n + menu.items[i].height;
                if (j > w) w = j;
                if (k > h) h = k;
            }
        }

        w += 2*(menu.innerOffset + menu.borderSize);
        h += 2*(menu.innerOffset + menu.borderSize);
        if (w > menu.width || minimumExtendsX) {
            menu.width = w;
        }
        if (h > menu.height || minimumExtendsY) {
            menu.height = h;
        }
    }
    
    
    /*
    public static void menuUnPlaceItems(GMenu m) {
        if (m.captionWindow!=null) {
            k = menu.captionWindow.y + menu.captionWindow.height + 1;
            m.captionWindow.x -= m.x + m.borderSize + m.innerOffset;
            m.captionWindow.y -= m.y + m.borderSize + m.innerOffset;
        }

        for (i=0; i<m.items.length; i++) {
            if (m.items[i]!=null) {
                m.items[i].x -= m.x + m.borderSize + m.innerOffset;
                m.items[i].y -= m.y + m.borderSize + m.innerOffset - k;
            }
        }
    }*/

    /** Set the selected index to the item in the array, perform all actions necessary. */
    public static void menuSetSelected(GMenu menu, int index) {
        if (menu.items==null)
            return;
        int k = menu.selectedIndex;
        
        if (menu.selectedIndex>=0 && menu.selectedIndex < menu.items.length && menu.items[menu.selectedIndex]!=null) {
            menu.items[menu.selectedIndex].selected=false;  //unselect current item in any case
        }
        if (index>=0 && index < menu.items.length) {
            menu.selectedIndex=index;
            if (menu.items[menu.selectedIndex]!=null && menu.items[menu.selectedIndex].selectable) {
                menu.items[menu.selectedIndex].selected=true;
            } else { 
                if (!(index < k) && !(index == menu.items.length-1 && k==0) || (k == menu.items.length-1 && index==0)) {
                    //find mext item that is not null and select it
                    for (int i=1; i<menu.items.length; i++) {
                        wndTmp = menu.items[(menu.selectedIndex + i)%menu.items.length];
                        if (wndTmp!=null && wndTmp.selectable) {
                            wndTmp.selected = true;
                            menu.selectedIndex=(menu.selectedIndex + i)%menu.items.length;
                            return;
                        }
                    }
                } else {
                    //find prev item that is not null and select it
                    int j = index;
                    for (int i=1; i<menu.items.length; i++) {
                        j--;
                        if (j<0)
                            j = menu.items.length-1;
                        wndTmp = menu.items[j];
                        if (wndTmp!=null && wndTmp.selectable) {
                            wndTmp.selected = true;
                            menu.selectedIndex=j;
                            return;
                        }
                    }
                }
            }
        } else if (index == -1) {
            menu.selectedIndex = -1;
        } else {
            menuSetSelected(menu, 0);   //invalid index
        }
    }
    
    
    /** Get the selected index, do not allow public access to slectedIndex to make sure required steps 
     *  are performed when the index is set.
     */
    public static int menuGetSelected(GMenu menu) {
        return menu.selectedIndex;
    }
    
    
    public static void mbOKCustomize(GMenu m, String caption, String OKString, GFont font, boolean autoCenterXY) {
        if (OKString==null) 
            OKString = "OK";
        
        if (m.items[0]==null) {
            m.items[0] = buttonCreate(0, 0, OKString, font, true);
        }
        if (m.items[0].width > m.xMax-m.xMin-6) {
            //set only visible chars for button, width will be adapted
            buttonSetText((GTextWindow)m.items[0], OKString.substring(0, (m.xMax-m.xMin-6)/font.charWidth), false);
        } else {
            buttonSetText((GTextWindow)m.items[0], OKString, false);        
        }
        
        //add the caption, thereby determining the msgbox dimensions
        mbSetCaption(m, caption, font);
        if (autoCenterXY) {
            windowCenterXY(m, m.xMin, m.yMin, m.xMax, m.yMax);
        }
        
        menuSetSelected(m, 0);
    }
    
    public static GMenu mbOKCreate(int xMin, int xMax, int yMin, int yMax, 
                                   int borderColor, int activeBorderColor, int backColor, int activeBackColor, 
                                   String caption, GFont font, String OKString, boolean autoCenterXY)
    {
        if (font==null)
            return null;
        
        //create the message box     
        GMenu m = menuCreate(xMin, yMin, 10, 10, 1);
        windowSetBorder(m, 1, 2);
        windowSetColors(m, borderColor, activeBorderColor, backColor, activeBackColor);
        m.xMin = xMin;
        m.yMin = yMin;
        m.xMax = xMax;
        m.yMax = yMax;
        
        //mbOKCustomize(m, caption, OKString, font, autoCenterXY);
        
        //create button
        
        if (OKString==null) 
            OKString = "OK";
        m.items[0] = buttonCreate(0, 0, OKString, font, true);

        
        if (m.items[0].width > xMax-xMin-6) {
            //set only visible chars for button, width will be adapted
            buttonSetText((GTextWindow)m.items[0], OKString.substring(0, (xMax-xMin-6)/font.charWidth), false);
        }


        //add the caption, thereby determining the msgbox dimensions
        mbSetCaption(m, caption, font);
        
        menuSetSelected(m, 0);
        if (autoCenterXY) {
            windowCenterXY(m, xMin, yMin, xMax, yMax);
        }
        
        return m;
    }
    
    
    public static void mbSetCaption(GMenu menu, String caption, GFont font) {
        int numLines=1; int buttonLength=0; int captionLength=0;
        int i, j, k, l, m;
        
        //max length of the msgbox
        l=(menu.xMax-menu.xMin)-6;
        //max height of the msgbox
        m=(menu.yMax-menu.yMin)-6;
        
        //get x-extends of buttons in pixels
        for (i=0; i<menu.items.length; i++) {
            if (menu.items[i]!=null) {
                if (buttonLength > 0)
                    buttonLength += 2;
                buttonLength += menu.items[i].width;
            }
        }
        
        ///////////////////////
        //WIDTH
        ///////////////////////
        //get num chars of longest line in caption
        j=0;
        k=font.charWidth;
        for (i=0;i<caption.length();i++) {
            j++;
            k+=font.charWidth;
            if (caption.charAt(i)=='\n' || i==caption.length()-1 || k > l) {
                if (j > captionLength) {
                    captionLength = j;
                }
                j=0;
                k=font.charWidth;
                if (! (i==caption.length()-1 && caption.charAt(i)!='\n'))
                    numLines++;
            }
        }
        if (captionLength==0)
            captionLength = caption.length();
        
        //get x-extends of caption in pixels
        captionLength = (captionLength*font.charWidth);
        //determine width of the msgbox
        if (captionLength > buttonLength) {
            l = captionLength + 6;
        } else {
            l = buttonLength + 6;
        }
        ///////////////////////
        //HEIGHT
        ///////////////////////
        
        //decrease num of visible lines if too many lines
        k = ((numLines+1) * font.charHeight) + 6 + 8;
        
        do {
            k = ((numLines+1) * font.charHeight) + 6 + 8;
            if (k>m && numLines > 1) {
                k-=font.charHeight;
                numLines--;
            }
        } while (k > m && numLines > 2);

        //set msgbox dimensions
        menu.width = l;
        menu.height = k;

        //set the caption
        if (menu.captionWindow==null) {
            menu.captionWindow = textWindowCreate(0, 0, captionLength, numLines*font.charHeight, caption, caption.length(), font, false);
            windowSetColors(menu.captionWindow, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT);
            windowSetBorder(menu.captionWindow, 0, 0);
        } else {
            menu.captionWindow.width = captionLength;
            menu.captionWindow.height = numLines*font.charHeight;
            menu.captionWindow.maxChars = captionLength;
            menu.captionWindow.text = null;
            System.gc();
            menu.captionWindow.text = new char[captionLength];
            textWindowCalculateTextSettings(menu.captionWindow, font);
            textWindowSetText(menu.captionWindow, caption);
        }
        windowCenterX(menu.captionWindow, 0, menu.width-6);
        menu.captionWindow.xOffset = 0;
        menu.captionWindow.yOffset = 0;
        menu.captionWindow.y = 0;

        //set yPos and xPos of all items
        j = ((menu.width-6)/2)-(buttonLength/2);
        for (i=0; i<menu.items.length; i++) {
            if (menu.items[i]!=null) {
                menu.items[i].xOffset = 0;
                menu.items[i].yOffset = 0;
                menu.items[i].y = 2;
                menu.items[i].x = j;
                j+=menu.items[i].width + 2;
            }
        }
        menuPlaceItems(menu);
    }
    
    
    
    
    public static GMenu mbYesNoCreate(int xMin, int xMax, int yMin, int yMax, 
                                      int borderColor, int activeBorderColor, int backColor, int activeBackColor, 
                                      String caption, GFont font, String yesString, String noString, boolean autoCenterXY) {

        if (font==null || yesString==null || noString==null)
            return null;

        //create the message box     
        GMenu m = menuCreate(xMin, yMin, 10, 10, 2);
        windowSetBorder(m, 1, 2);
        windowSetColors(m, borderColor, activeBorderColor, backColor, activeBackColor);
        m.xMin = xMin;
        m.yMin = yMin;
        m.xMax = xMax;
        m.yMax = yMax;
        
        //mbOKCustomize(m, caption, OKString, font, autoCenterXY);
        
        //create buttons
        
        if (yesString==null) 
            yesString = "Yes";
        m.items[0] = buttonCreate(0, 0, yesString, font, true);

        if (noString==null) 
            noString = "No";
        m.items[1] = buttonCreate(0, 0, noString, font, true);
        
        
        //set only visible chars for buttons, width will be adapted
        int r = ((xMax-xMin-6)/font.charWidth);
        int s = noString.length() + yesString.length();
        boolean flip = false;
        if (s>r) {
            int a=yesString.length(); int b=noString.length();
            while ((a>1 || b>1) && a+b > r) {
                if (a>1 && flip) {
                    a--;
                } else if (b>1) {
                    b--;
                }
                flip = !flip;
            }
            buttonSetText((GTextWindow)m.items[0], yesString.substring(0, a), false);
            buttonSetText((GTextWindow)m.items[1], noString.substring(0, b), false);
        }
        



        //add the caption, thereby determining the msgbox dimensions
        mbSetCaption(m, caption, font);
        
        menuSetSelected(m, 0);
        if (autoCenterXY) {
            windowCenterXY(m, xMin, yMin, xMax, yMax);
        }
        
        return m;        
        
/*        
        
        int numLines=1;
        int xPos, yPos, width, height;
        int j=0;
        int buttonTextLength=0;
        int captionLength=0;
        
        GTextWindow buttonYes=null;
        GTextWindow buttonNo=null;
        
        //get num chars of longest line
        for (i=0;i<caption.length();i++) {
            if (caption.charAt(i)=='\n') {
                if (j>captionLength)
                    captionLength = j;
                j=0;
                numLines++;
            } else {
                j++;
            }
        }
        
        if (yesString!=null)
            buttonTextLength = yesString.length();
        if (noString!=null)
            buttonTextLength += noString.length();
        
        width = 6 + (font.charWidth * captionLength);
        height = 21 + (font.charHeight * numLines) + font.charHeight;
        
        //dimensions must be at least enough to contain the buttons
        if (width < (21 + (buttonTextLength * font.charWidth))) {
            width = (21 + (buttonTextLength * font.charWidth));
        }
        
        if (height > displayHeight) {
            height = displayHeight;
        }
        
        //get position
        xPos = displayX + (displayWidth/2) - (width/2);
        yPos = displayY + (displayHeight/2) - (height/2);

        //create the message box     
        GMenu m = menuCreate(xPos, yPos, width, height, 2);
        windowSetBorder(m, 1, 2);
        windowSetColors(m, borderColor, activeBorderColor, backColor, activeBackColor);

        menuSetCaption(m, caption, font, 3);        
        
        //add buttons
        yPos = height - 6 - font.charHeight - 7;
        //determine width and height of the MsgBox
        if (yesString!=null) {
            buttonYes = buttonCreate(0, 0, yesString, font, true);            
            if (buttonNo!=null) {
                xPos = (width-6)/2 - (buttonYes.width + buttonNo.width + 3) /2;
            } else {
                xPos = (width-6)/2 - (buttonYes.width)/2;            
            }
            buttonYes.x = xPos;
            buttonYes.y = yPos;
            menuSetItem(m, buttonYes, 0);            
        }
        if (noString!=null) {
            buttonNo = buttonCreate(0, 0, noString, font, true);            
            if (buttonYes!=null) {
                xPos = buttonYes.x + buttonYes.width + 3;   
            } else {
               xPos = (width-6)/2 - (buttonNo.width)/2; 
            }
            buttonNo.x = xPos;
            buttonNo.y = yPos;
            menuSetItem(m, buttonNo, 1);            
        }
        m.setSelected(0);
        
        return m;
 */
    }
    

    public static int mbOKStatus(GMenu m) {
        if( (GTextWindow)m.items[0]!=null &&  buttonWasActivated((GTextWindow)m.items[0])  ){
            return OK;
        }
        return 0;
    }

    
    public static int mbYesNoStatus(GMenu m) {
        if( (GTextWindow)m.items[0]!=null &&  buttonWasActivated((GTextWindow)m.items[0])  ){
            return YES;
        }
        if((GTextWindow)m.items[1]!=null &&  buttonWasActivated((GTextWindow)m.items[1])  ){
            return NO;
        }
        return -1;
    }
    
        
    public static void menuSetItem(GMenu menu, GWindow newItem, int index) {
        if (menu.items==null)
            return;
        int k=0;
        if (index < menu.items.length && index >= 0) {
            menu.items[index] = newItem;
            //know own index
            if (newItem!=null) {
                menu.items[index].index = index;
                if (menu.captionWindow!=null) {
                    k = menu.captionWindow.y - menu.captionWindow.yOffset + menu.captionWindow.height + 1;
                }
                int j = menu.borderSize + menu.innerOffset;
                //restore original pos
                newItem.x -= newItem.xOffset;
                newItem.y -= newItem.yOffset;
                //save new offset
                newItem.xOffset = menu.x + j;
                newItem.yOffset = menu.y + j + k;
                //set new pos
                newItem.x += newItem.xOffset; 
                newItem.y += newItem.yOffset;
            }   
        }
        if (index >= 0 && index==menuGetSelected(menu)) {
            menuSetSelected(menu, index);
        }
    }
 

    public static void menuSetCaptionOneLine(GMenu menu, String caption, GFont font, int yOffset) {
        int x,y,width,height;
        
        int l = font.charWidth * caption.length();
        int m = font.charHeight + 2;
        
        if (l > menu.width) {
            int n = ( menu.width - (2*menu.innerOffset) - (2*menu.borderSize) ) / font.charWidth;
            caption = caption.substring(0, n-2) + "..";
        }
        
        int i = ( (menu.width- 2*(menu.borderSize+menu.innerOffset))/2 ) - l/2; //+ menu.x - menu.innerOffset - menu.borderSize;
        int j = yOffset; // + menu.y;
        
        if (menu.captionWindow==null) {
            menu.captionWindow = textWindowCreate(i, j, l, m, caption, caption.length(), font, true);
            windowSetColors(menu.captionWindow, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT);
        } else {
            textWindowRemoveText(menu.captionWindow);
            menu.captionWindow.maxChars = caption.length();
            menu.captionWindow.text = null;
            System.gc();
            menu.captionWindow.text = new char[caption.length()];
            menu.captionWindow.width = l;
            menu.captionWindow.height = m;
            windowSetPosition(menu.captionWindow, i, j);
        }
        windowSetBorder(menu.captionWindow, 0, 0);
        textWindowSetText(menu.captionWindow, caption);
        menu.captionWindow.xOffset = 0;
        menu.captionWindow.yOffset = 0;
        menuPlaceItems(menu);
    }

    public static int menuButtonStatus(GMenu buttonList, int gameAction, int keyCode) {
        if (handleMenuInput(buttonList, gameAction, keyCode)) {
            for (int i=0; i<buttonList.items.length; i++) {
                if( (GTextWindow)buttonList.items[i]!=null &&  buttonWasActivated((GTextWindow)buttonList.items[i])  ) {
                    return i;
                }
            }
        }
        return -1;
    }    
    
    public static GMenu buttonListCreate(int x, int y, int borderSize, int innerOffset, int yGap, int noOfButtons, GFont font) {
        //int j = (noOfButtons * (font.charHeight + 4)) + ((noOfButtons-1) * yGap) + 2*(innerOffset+borderSize);
        int j = 2*(innerOffset+borderSize);
        GMenu m = GTools.menuCreate(x, y, font.charWidth, j, noOfButtons);
        m.width = 2*(borderSize+innerOffset);
        m.height = 2*(borderSize+innerOffset);
        GTools.windowSetBorder(m, borderSize, innerOffset);
        m.font = font;
        m.yOffset = yGap;
        return m;
    }
    
    
    public static void buttonListSetButton(GMenu buttonList, String buttonText, int index, boolean keepXEnd, boolean keepYEnd) {
        if (buttonList==null || buttonList.items ==null || buttonList.font==null || index >= buttonList.items.length || index < 0)
            return;
        
        boolean changed = false;
        boolean placeNew = false;
        
        int heightUnit = (buttonList.font.charHeight + buttonList.yOffset);
        int buttonsBefore = 0;
        
        for (int b=0; b<index; b++) {
            if (buttonList.items[b]!=null && buttonList.items[b].visible) {
                buttonsBefore++;
            }
        }
        
        int j = (buttonsBefore * heightUnit);
        //j += ;
        //if (buttonsBefore > 0) {
            
        //}
        
        GTextWindow btn = null;
        if (buttonList.items[index]==null) {
            btn = buttonCreate(0, j, buttonText, buttonList.font, false);
            windowSetBorder(btn, 0, buttonList.yOffset/2);
            //btn.height += buttonList.yOffset;
            /*
            if (index>0) {
                btn.y -= buttonList.yOffset/2;
            }
             */
            menuSetItem(buttonList, btn, index);
            changed = true;
        } else {
            btn = (GTextWindow)(buttonList.items[index]);
            if (buttonText!=null) { // do not change the button text if null
                buttonSetText(btn, buttonText, false);  // size will be changed accordingly
            } else {
                btn.width = buttonGetMinimumWidth(btn);
            }
            btn.y = buttonList.y + j + buttonList.borderSize + buttonList.innerOffset;  // set the button y position relative to the buttonlist origin
            
            if (btn.visible == false) { // button was not visible
                btn.visible = true;
                btn.selectable = true;
                btn.acceptInput = true;
                changed = true;
            }
        }
        // set the button
        //menuSetItem(buttonList, btn, index);

        // HEIGHT CHANGES (IF NECESSARY)
        if (changed) {
            int addY = heightUnit;
            // change the buttonlist height itself
            buttonList.height += addY;
            // change the y position of buttons lower than the one that was set
            for (int z=index+1; z<buttonList.items.length; z++) {
                if (buttonList.items[z]!=null) {
                    buttonList.items[z].y +=addY;
                }
            }
            if (keepYEnd) {
                buttonList.y -= addY;
                placeNew = true;
            }
        }
        
        // WIDTH CHANGES (IF NECESSARY)
        
        // get current inner width of the buttonlist
        j = buttonList.width - (2*(buttonList.borderSize+buttonList.innerOffset));
        if (j < btn.width) {    // button width exceeds buttonlist width
            // adapt menu and button widths
            buttonList.width = btn.width + (2*(buttonList.borderSize+buttonList.innerOffset));
            for (int i=0; i<buttonList.items.length; i++) {
                if (buttonList.items[i]!=null && buttonList.items[i].visible) {
                    buttonList.items[i].width = btn.width;
                }
            }
            if (keepXEnd) { // make sure the End is kept if necessary
                buttonList.x -= btn.width - j;
                placeNew = true;
            }
        } else if (j > btn.width) { // button width is smaller than current buttonlist width
            /*
            // check for smallest size
            // get the smallest possible width for the buttonList
            int smallestWidth = 1;
            int tempWidth = 0;
            for (int z=0; z<buttonList.items.length; z++) {
                if (buttonList.items[z]!=null && buttonList.items[z].visible) {
                    tempWidth = buttonGetMinimumWidth((GTextWindow)buttonList.items[z]);
                    if (smallestWidth < tempWidth) {
                        smallestWidth = tempWidth;
                    }
                }
            }
            tempWidth = smallestWidth +  2 * (buttonList.innerOffset + buttonList.borderSize);
            if (tempWidth < buttonList.width) { // width can only shrink
                if (keepXEnd) {
                    // set new x Position to keep the xEnd
                    buttonList.x += buttonList.width - tempWidth;
                    placeNew = true;
                }
                buttonList.width = tempWidth;   // set the new, smaller width
                for (int z=0; z<buttonList.items.length; z++) {
                    if (buttonList.items[z]!=null) {
                        if (buttonGetMinimumWidth((GTextWindow)buttonList.items[z]) < smallestWidth) {
                            buttonList.items[z].width = smallestWidth;
                        }
                    }
                }
            }*/
            btn.width = j;
        } 
        
        if (placeNew) {
            menuPlaceItems(buttonList);
        }
    }
    

    public static void buttonListUnsetButton(GMenu buttonList, int index, boolean keepXEnd, boolean keepYEnd) {
       if (buttonList==null || buttonList.items ==null || buttonList.font==null || index >= buttonList.items.length || index < 0)
            return;
        
       boolean placeNew = false;
       
       if (buttonList.items[index]!=null) {
            if (buttonList.items[index].visible) {  // disabling has new effects
                
                // WIDTH CHANGES
                
                // get the smallest possible width for the buttonList
                int smallestWidth = 1;
                int tempWidth = 0;
                for (int z=0; z<buttonList.items.length; z++) {
                    if (buttonList.items[z]!=null && buttonList.items[z].visible && z!=index) {
                        tempWidth = buttonGetMinimumWidth((GTextWindow)buttonList.items[z]);
                        if (smallestWidth < tempWidth) {
                            smallestWidth = tempWidth;
                        }
                    }
                }
                tempWidth = smallestWidth +  2 * (buttonList.innerOffset + buttonList.borderSize);
                if (tempWidth < buttonList.width) { // width can only shrink
                    if (keepXEnd) {
                        // set new x Position to keep the xEnd
                        buttonList.x += buttonList.width - tempWidth;
                        placeNew = true;
                    }
                    buttonList.width = tempWidth;   // set the new, smaller width
                    for (int z=0; z<buttonList.items.length; z++) {
                        if (buttonList.items[z]!=null && buttonList.visible && z!=index) {
                            buttonList.items[z].width = smallestWidth;
                        }
                    }
                }
                
                // HEIGHT CHANGES
                int subY = 0;
                subY = buttonList.font.charHeight + buttonList.yOffset;

                // $->
                
                buttonList.height -= subY;
                for (int z=index+1; z<buttonList.items.length; z++) {
                    if (buttonList.items[z]!=null) {
                        buttonList.items[z].y -= subY;
                        if (buttonList.items[z].yOffset > buttonList.items[z].y) {
                            buttonList.items[z].y = buttonList.items[z].yOffset;
                        }
                    }
                }
                if (keepYEnd) {
                    buttonList.y += subY;
                    placeNew = true;
                }

                // disable the button
                buttonList.items[index].selectable = false;
                buttonList.items[index].acceptInput = false;
                buttonList.items[index].visible = false;

                if (placeNew) {
                    menuPlaceItems(buttonList);
                }
            }
       }
    }
     
    
    public static void buttonListUnsetAll(GMenu buttonList, boolean keepXEnd, boolean keepYEnd) {
        if (buttonList!=null && buttonList.items!=null) {
            for (int i=0; i<buttonList.items.length; i++) {
                buttonListUnsetButton(buttonList, i, keepXEnd, keepYEnd);
            }
        }

            /*
                if (buttonList.items[i]!=null) {
                    // disable the button
                    buttonList.items[i].selectable = false;
                    buttonList.items[i].acceptInput = false;
                    buttonList.items[i].visible = false;
                }
            }
        }
        int newWidth = 2 * (buttonList.innerOffset + buttonList.borderSize) + 1;
        int newHeight = 2 * (buttonList.innerOffset + buttonList.borderSize) + 1;
        
        if (keepXEnd) {
            buttonList.x += buttonList.width - newWidth;
        }
        if (keepYEnd) {
            buttonList.y += buttonList.height - newHeight;
        }
        
        buttonList.width = newWidth;
        buttonList.height = newHeight;
        if (keepXEnd || keepYEnd) {
            menuPlaceItems(buttonList);
        
        }
             */
             
    }
       
  
    
    
    public static void windowSetPosition(GWindow wnd, int x, int y) {
        wnd.x = x;
        wnd.y = y;
        if (wnd.type==MENU) {
            menuPlaceItems((GMenu)wnd);
        }
    }
    
    public static void windowSetBorder(GWindow wnd, int borderSize, int innerOffset) {
        //borderSize
        if (borderSize < 0) {
            wnd.borderSize = 0;
        } else if (borderSize > wnd.width/2 || borderSize > wnd.width/2) {
            if (wnd.width > wnd.width)
                wnd.borderSize = wnd.width/2;
            else
                wnd.borderSize = wnd.width/2;
        } else {
            wnd.borderSize = borderSize;
        }
        //innerOffset
        if (innerOffset < 0) {
            wnd.innerOffset = 0;
        } else if (innerOffset > wnd.width-(2*wnd.borderSize) || innerOffset > wnd.width-(2*wnd.borderSize)) {
            if (wnd.width > wnd.width)
                wnd.innerOffset = wnd.width-(2*wnd.borderSize);
            else
                wnd.innerOffset = wnd.width-(2*wnd.borderSize);
        }
        else {
            wnd.innerOffset = innerOffset;
        }
        
        //special treatment for GTextWindow / GInputWindow
        if (wnd.type == TEXTWINDOW || wnd.type == INPUTWINDOW) {
            int first = ((GTextWindow)wnd).firstVisibleCharNo;
            textWindowCalculateTextSettings(((GTextWindow)wnd), ((GTextWindow)wnd).font);
            char[] text = textWindowGetText((GTextWindow)wnd);
            if (text!=null) {
                textWindowSetText((GTextWindow)wnd, text);
                textWindowEnsureCharIsVisible(((GTextWindow)wnd), first);
            }
        } else if (wnd.type == BUTTON) {
            wnd.width = ( ((GTextWindow)wnd).noOfCharsPerLine * ((GTextWindow)wnd).font.charWidth ) + (2*wnd.borderSize) + (2*wnd.innerOffset);
            wnd.height = ((GTextWindow)wnd).font.charHeight + (2*wnd.borderSize) + (2*wnd.innerOffset);
            textWindowCalculateTextSettings(((GTextWindow)wnd), ((GTextWindow)wnd).font);
        } else if (wnd.type == MENU) {
            menuPlaceItems((GMenu)wnd);
        } else if (wnd.type == LIST) {
            listCalculateEntrySettings((GList)wnd, ((GList)wnd).font);
            listSetSelectedIndex((GList)wnd, ((GList)wnd).selectedEntry);
        }

    }

    public static void windowSetColors(GWindow wnd, int borderColor, int activeBorderColor, int backColor, int activeBackColor) {
        wnd.borderColor = borderColor;
        wnd.activeBorderColor = activeBorderColor;
        wnd.backColor = backColor;
        wnd.activeBackColor = activeBackColor;
    }

   
    public static void windowCenterX(GWindow wnd, int displayStartX, int displayWidth) {
        /*if (wnd.type==MENU) {
            menuUnPlaceItems((GMenu)wnd);
        }*/
        wnd.x = displayStartX + (displayWidth/2) - (wnd.width/2);
        if (wnd.type==MENU) {
            menuPlaceItems((GMenu)wnd);
        }
    }
    
    public static void windowCenterY(GWindow wnd, int displayStartY, int displayHeight) {
        /*if (wnd.type==MENU) {
            menuUnPlaceItems((GMenu)wnd);
        }*/
        wnd.y = displayStartY + (displayHeight/2) - (wnd.height/2);
        if (wnd.type==MENU) {
            menuPlaceItems((GMenu)wnd);
        }
    }
    
    public static void windowCenterXY(GWindow wnd, int displayStartX, int displayStartY, int displayWidth, int displayHeight) {
        /*if (wnd.type==MENU) {
            menuUnPlaceItems((GMenu)wnd);
        }*/
        wnd.x = displayStartX + ((displayWidth - wnd.width)/2);
        wnd.y = displayStartY + ((displayHeight - wnd.height)/2);
        if (wnd.type==MENU) {
            menuPlaceItems((GMenu)wnd);
        }
    }

    /*
    public static void windowCenterOnWindowX(GWindow parentWindow, GWindow childWindow) {
        windowCenterX(childWindow, parentWindow.x, parentWindow.width);
    }

    public static void windowCenterOnWindowX(GWindow parentWindow, GWindow childWindow) {
        windowCenterY(childWindow, parentWindow.y, parentWindow.width);
    }

    public static void windowCenterOnWindowXY(GWindow parentWindow, GWindow childWindow) {
        windowCenterXY(childWindow, parentWindow.x, parentWindow.y, parentWindow.width, parentWindow.height);
    }
    */
    
    public static void saveWindowColors(GWindow wnd) {
        saveBorderColor = wnd.borderColor;
        saveActiveBorderColor = wnd.activeBorderColor;
        saveBackColor = wnd.backColor;
    }
    
    public static void restoreWindowColors(GWindow wnd) {
        wnd.borderColor = saveBorderColor;
        wnd.activeBorderColor = saveActiveBorderColor;
        wnd.backColor = saveBackColor;
    }
    
    
    
    
    /** Create the default key mapping table used for GInputWindow objects. */
    public static void createDefaultKeyTable() {
        //new keytable
        inputKeyTable = new char[11][]; //index 10: KEY_POUND
        
        //set char capacity for the keys
        inputKeyTable[0] = new char[9];
        inputKeyTable[1] = new char[2];
        inputKeyTable[2] = new char[5];

        int k = 4;
        for (int i=3; i<10; i++) {
            inputKeyTable[i] = new char[k];
            if (i==5) k++;
        }

        inputKeyTable[10] = new char[12];
        
        //assign the char mapping        
        inputKeyTable[1][0] = ' ';
        inputKeyTable[1][1] = '1';
        
        inputKeyTable[2][0] = 'a';
        inputKeyTable[2][1] = 'b';
        inputKeyTable[2][2] = 'c';
        inputKeyTable[2][3] = '2';
        inputKeyTable[2][4] = 'ä';
        
        inputKeyTable[3][0] = 'd';
        inputKeyTable[3][1] = 'e';
        inputKeyTable[3][2] = 'f';
        inputKeyTable[3][3] = '3';
        
        inputKeyTable[4][0] = 'g';
        inputKeyTable[4][1] = 'h';
        inputKeyTable[4][2] = 'i';
        inputKeyTable[4][3] = '4';
        
        inputKeyTable[5][0] = 'j';
        inputKeyTable[5][1] = 'k';
        inputKeyTable[5][2] = 'l';
        inputKeyTable[5][3] = '5';
        
        inputKeyTable[6][0] = 'm';
        inputKeyTable[6][1] = 'n';
        inputKeyTable[6][2] = 'o';
        inputKeyTable[6][3] = '6';
        inputKeyTable[6][4] = 'ö';
        
        inputKeyTable[7][0] = 'p';
        inputKeyTable[7][1] = 'q';
        inputKeyTable[7][2] = 'r';
        inputKeyTable[7][3] = 's';
        inputKeyTable[7][4] = '7';
        
        inputKeyTable[8][0] = 't';
        inputKeyTable[8][1] = 'u';
        inputKeyTable[8][2] = 'v';
        inputKeyTable[8][3] = '8';
        inputKeyTable[8][4] = 'ü';
        
        inputKeyTable[9][0] = 'w';
        inputKeyTable[9][1] = 'x';
        inputKeyTable[9][2] = 'y';
        inputKeyTable[9][3] = 'z';
        inputKeyTable[9][4] = '9';
        
        inputKeyTable[0][0] = ' ';
        inputKeyTable[0][1] = '0';  
        inputKeyTable[0][2] = '?';
        inputKeyTable[0][3] = '!';
        inputKeyTable[0][4] = '.';
        inputKeyTable[0][5] = ',';
        inputKeyTable[0][6] = '-';
        inputKeyTable[0][7] = '_';
        inputKeyTable[0][8] = '\'';
        
        
        //KEY_POUND
        inputKeyTable[10][0] = ':';
        inputKeyTable[10][1] = ';';
        inputKeyTable[10][2] = '(';
        inputKeyTable[10][3] = ')';
        inputKeyTable[10][4] = '+';
        inputKeyTable[10][5] = '*';
        inputKeyTable[10][6] = '/';
        inputKeyTable[10][7] = '\\';
        inputKeyTable[10][8] = '#';
        inputKeyTable[10][9] = '@';
        inputKeyTable[10][10] = '[';
        inputKeyTable[10][11] = ']';
        
    }
    
    
    
    
    
    //// INPUT HANDLING ////
    
    /**
     * Handle any input for a window.
     */
    public static boolean handleInput(GWindow wnd, int gameAction, int keyCode) {
        if (!wnd.acceptInput)
            return false;

        switch (wnd.type) {
            case WINDOW:
                return false;
            case TEXTWINDOW:
                return handleTextWindowInput((GTextWindow)wnd, gameAction, keyCode);
            case INPUTWINDOW:
                return handleInputWindowInput((GInputWindow)wnd, gameAction, keyCode);
            case BUTTON:
                return handleButtonWindowInput((GTextWindow)wnd, gameAction, keyCode);
            case MENU:
                return handleMenuInput((GMenu)wnd, gameAction, keyCode);
            case LIST:
                return handleListInput((GList)wnd, gameAction, keyCode);
            case SPINBUTTON:
                return handleSpinButtonInput((GMenu)wnd, gameAction, keyCode);
            default:
                return false;
        }
    }

    /** Handle input for a menu window */
    public static boolean handleMenuInput(GMenu menu, int gameAction, int keyCode) {
        int selected = menuGetSelected(menu);
        if (selected==-1 || menu.items==null)
            return false;

        boolean handled=false;
        wndTmp = menu.items[selected];
        if (wndTmp != null) {
            
            //handle input for selected item
            if (gameAction == wndTmp.nextKey) {
                if (wndTmp.nextWindow!=null && wndTmp.nextWindow.selectable && wndTmp.nextWindow.index <= menu.items.length) {
                    s = wndTmp.nextWindow.index;
                    if (menu.items[s]!=null && menu.items[s].equals(wndTmp.nextWindow)) {
                        menuSetSelected(menu, s);
                        handled = true;
                    }
                }
            } else if (gameAction == wndTmp.nextAltKey) {
                if (wndTmp.nextAltWindow!=null && wndTmp.nextAltWindow.selectable && wndTmp.nextAltWindow.index <= menu.items.length) {
                    s = wndTmp.nextAltWindow.index;
                    if (menu.items[s]!=null && menu.items[s].equals(wndTmp.nextAltWindow)) {
                        menuSetSelected(menu, s);
                        handled = true;
                    }
                }
            } else if (gameAction == wndTmp.prevKey) {
                if (wndTmp.prevWindow!=null && wndTmp.prevWindow.selectable && wndTmp.prevWindow.index <= menu.items.length) {
                    s = wndTmp.prevWindow.index;
                    if (menu.items[s]!=null && menu.items[s].equals(wndTmp.prevWindow)) {
                        menuSetSelected(menu, s);
                        handled = true;
                    }
                }
            } else if (gameAction == wndTmp.prevAltKey) {
                if (wndTmp.prevAltWindow!=null && wndTmp.prevAltWindow.selectable && wndTmp.prevAltWindow.index <= menu.items.length) {
                    s = wndTmp.prevAltWindow.index;
                    if (menu.items[s]!=null && menu.items[s].equals(wndTmp.prevAltWindow)) {
                        menuSetSelected(menu, s);
                        handled = true;
                    }
                }
            }

            //handle selected item input
            if (!handled) {
                handled = handleInput(wndTmp, gameAction, keyCode);
            }
            
        }
        
        //handle input for any unselectable item
        if (!handled) {
            for (s=0; s<menu.items.length; s++) {
                if (menu.items[s]!=null && !menu.items[s].selectable && menu.items[s].acceptInput) {
                    handled = handleInput(menu.items[s], gameAction, keyCode);
                }
            }
        }

        //default menu input check
        if (!handled) {
            if (gameAction==menu.prevKey || gameAction==menu.prevAltKey) {
                if (selected-1<0) {
                    menuSetSelected(menu, menu.items.length-1);
                } else {
                    menuSetSelected(menu, menuGetSelected(menu)-1);
                }
            }
            else if (gameAction==menu.nextKey || gameAction==menu.nextAltKey) {
                if (selected+1 >= menu.items.length) {
                    menuSetSelected(menu, 0);
                } else {
                    menuSetSelected(menu, menuGetSelected(menu)+1);
                }
            }
            else {
                handled = false;
            }
        }
        
        wndTmp = null;
        return handled;
    }
    
    
    /**
     * Handle any input specific for a gfxTextWindow
     */
    public static boolean handleTextWindowInput(GTextWindow wnd, int gameAction, int keyCode) {
        boolean handled = true;
        switch (gameAction) {
            default:
                switch (keyCode) {
                    case 50:    //2
                        textWindowScrollUp(wnd, 1);
                        break;
                    case 53:    //5
                        textWindowScrollDown(wnd, 1);
                        break;
                    
                    default:
                        handled = false;
                        break;
                }
                break;
        }
        return handled;
    }
    
    
    /**
     * Handle any specific input for a GInputWindow.
     */
    public static boolean handleInputWindowInput(GInputWindow wnd, int gameAction, int keyCode) {

        boolean handled = true;
        
        if (!wnd.selectable) {
            return false;
        }
        
        // numeric input handling
        if (wnd.numeric) {
            if (keyCode>=48 && keyCode <=57) {
                if (wnd.textFill<wnd.maxChars) {
                    wnd.textFill++;
                    int startPos = wnd.cursorPos;
                    int endPos = wnd.textFill-1;
                    while(startPos<endPos) {
                        wnd.text[endPos] = wnd.text[endPos-1];
                        endPos--;
                    }
                    wnd.text[wnd.cursorPos] = ' ';
                }

                wnd.text[wnd.cursorPos] = (char)keyCode;
                if (wnd.textFill==wnd.cursorPos) {
                    wnd.textFill++;
                }
                inputWindowAdvanceCursor(wnd);
            } else if (keyCode == Canvas.KEY_STAR || keyCode == -8) {
                //BACKSPACE (-8 is the keycode submitted for the clear button by Nokia devices HAHAHAHA funny marlowe!)
                if (wnd.cursorPos==0 && wnd.textFill==1) {
                    wnd.text[wnd.cursorPos] = ' ';
                    if (wnd.cursorPos==wnd.textFill-1 && wnd.textFill>0) {
                        wnd.textFill--;
                    }
                } else if (wnd.cursorPos!=0) {
                    int startPos = wnd.cursorPos-1;
                    int endPos = wnd.textFill-1;
                    while (startPos<endPos) {
                        wnd.text[startPos]=wnd.text[startPos+1];
                        startPos++;
                    }
                    wnd.text[endPos]=' ';
                    wnd.textFill--;
                    wnd.cursorPos--;
                    inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);
                }
            } else {
                switch(gameAction) {
                    case Canvas.LEFT:
                        //CURSOR LEFT
                        if (wnd.cursorPos-1 >= 0) {
                            wnd.cursorPos--;
                            inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);
                        }
                        break;
                    case Canvas.RIGHT:
                        //CURSOR RIGHT
                        if (wnd.cursorPos+1 <= wnd.textFill) {
                            inputWindowAdvanceCursor(wnd);
                        }
                        break;
                    default:
                        handled = false;
                        break;
                }
            }
            return handled;
        }
        
        
        
        
        
        
        
        
        if (inputKeyTable==null) {
            return false;
        }
        
        if (System.currentTimeMillis() - wnd.inputLastKeypress < 900) {
            if (wnd.lastKey==keyCode) {
                wnd.keyRepeat++;
            } else {
                //another button was pressed
                wnd.keyRepeat = 0;
                if (wnd.cursorMove && keyCode>=48 && keyCode <=57) {
                    //allow to write char immediately
                    inputWindowAdvanceCursor(wnd);
                }
            }
        } else {
            //key repeat max interval time elapsed
            wnd.keyRepeat = 0;
            wnd.cursorBlock = false;
        }
        
        wnd.lastKey = keyCode;

        if (keyCode!=Canvas.KEY_STAR && ( (keyCode>=48 && keyCode <=57) || keyCode == Canvas.KEY_POUND )) {
            //KEY 0-9, # (# = key index 10)
            wnd.inputLastKeypress = System.currentTimeMillis();
            wnd.cursorMove = true;
            wnd.cursorBlock = true;
            wnd.flashCursor = true;
            wnd.lastCursorFlash = wnd.inputLastKeypress;
            
            if (wnd.keyRepeat == 0) {   //INITIAL KEYPRESS
                if (wnd.textFill<wnd.maxChars) {
                    wnd.textFill++;
                    int startPos = wnd.cursorPos;
                    int endPos = wnd.textFill-1;
                    while(startPos<endPos) {
                        wnd.text[endPos] = wnd.text[endPos-1];
                        endPos--;
                    }
                    wnd.text[wnd.cursorPos] = ' ';
                }            
            }
            
            if (keyCode!=Canvas.KEY_POUND) {
                keyCode-=48;
                //write the char
                wnd.text[wnd.cursorPos] = inputKeyTable[keyCode][wnd.keyRepeat % (inputKeyTable[keyCode].length)];
            } else {
                wnd.text[wnd.cursorPos] = inputKeyTable[10][wnd.keyRepeat % (inputKeyTable[10].length)];
            }

            if (wnd.textFill==wnd.cursorPos) {
                wnd.textFill++;
            }
            
            

        } else if (keyCode == Canvas.KEY_STAR || keyCode == -8) {
            //BACKSPACE (-8 is the keycode submitted for the clear button by Nokia devices HAHAHAHA funny marlowe!)
            if (wnd.cursorMove || (wnd.cursorPos==0 && wnd.textFill==1)) {
                wnd.text[wnd.cursorPos] = ' ';
                if (wnd.cursorPos==wnd.textFill-1 && wnd.textFill>0) {
                    wnd.textFill--;
                }
            } else if (wnd.cursorPos!=0) {
                int startPos = wnd.cursorPos-1;
                int endPos = wnd.textFill-1;
                while (startPos<endPos) {
                    wnd.text[startPos]=wnd.text[startPos+1];
                    startPos++;
                }
                wnd.text[endPos]=' ';
                wnd.textFill--;
                wnd.cursorPos--;
                inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);

            }

            wnd.cursorMove = false;
            //checkCursorLineBreak(wnd.cursorPos+1);
        } /*else if (keyCode == Canvas.KEY_POUND) {
            //SPECIAL CHARS
            if (wnd.textFill<wnd.maxChars) {
                wnd.textFill++;
                int startPos = wnd.cursorPos;
                int endPos = wnd.textFill-1;
                while(startPos<endPos) {
                    wnd.text[endPos] = wnd.text[endPos-1];
                    endPos--;
                }
                wnd.text[wnd.cursorPos] = ' ';
                inputWindowAdvanceCursor(wnd);
            }


        }*/ else {
            switch(gameAction) {
                case Canvas.LEFT:
                    //CURSOR LEFT
                    if (wnd.cursorPos-1 >= 0) {
                        wnd.cursorPos--;
                        inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);
                    }
                    break;
                case Canvas.RIGHT:
                    //CURSOR RIGHT
                    if (wnd.cursorPos+1 <= wnd.textFill) {
                        inputWindowAdvanceCursor(wnd);
                    }
                    break;
                default:
                    handled = false;
                    break;

            }
        }        
        
        
        
        return handled;
    }
    

    /**
     * Handle any specific input for a gfxInputWindow.
     */
/*    
    public static boolean handleInputWindowInput(GInputWindow wnd, int gameAction, int keyCode) {
        if (inputKeyTable==null) {
            return false;
        }
        
        boolean handled = true;
        
        if (System.currentTimeMillis() - wnd.inputLastKeypress < 750) {
            if (wnd.lastKey==keyCode) {
                wnd.keyRepeat++;
            } else {
                //another button was pressed
                wnd.keyRepeat = 0;
                if (wnd.cursorMove && keyCode>=48 && keyCode <=57) {
                    //allow to write char immediately
                    inputWindowAdvanceCursor(wnd);
                }
            }
        } else {
            //key repeat max interval time elapsed
            wnd.keyRepeat = 0;
        }
        
        wnd.lastKey = keyCode;

        if (keyCode!=Canvas.KEY_STAR && keyCode!=Canvas.KEY_POUND && keyCode>=48 && keyCode <=57) {
            //KEY 0-9
            wnd.inputLastKeypress = System.currentTimeMillis();
            keyCode-=48;
            //write the char
            wnd.text[wnd.cursorPos] = inputKeyTable[keyCode][wnd.keyRepeat % (inputKeyTable[keyCode].length)];
            if (wnd.textFill==wnd.cursorPos) {
                wnd.textFill++;
            }
            wnd.cursorMove = true;

        } else if (keyCode == Canvas.KEY_STAR) {
            //BACKSPACE
            if (wnd.cursorMove || (wnd.cursorPos==0 && wnd.textFill==1)) {
                wnd.text[wnd.cursorPos] = ' ';
                if (wnd.cursorPos==wnd.textFill-1 && wnd.textFill>0) {
                    wnd.textFill--;
                }
            } else if (wnd.cursorPos!=0) {
                int startPos = wnd.cursorPos-1;
                int endPos = wnd.textFill-1;
                while (startPos<endPos) {
                    wnd.text[startPos]=wnd.text[startPos+1];
                    startPos++;
                }
                wnd.text[endPos]=' ';
                wnd.textFill--;
                wnd.cursorPos--;
                inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);

            }

            wnd.cursorMove = false;
            //checkCursorLineBreak(wnd.cursorPos+1);
        } else if (keyCode == Canvas.KEY_POUND) {
            //SPACE
            if (wnd.textFill<wnd.maxChars) {
                wnd.textFill++;
                int startPos = wnd.cursorPos;
                int endPos = wnd.textFill-1;
                while(startPos<endPos) {
                    wnd.text[endPos] = wnd.text[endPos-1];
                    endPos--;
                }
                wnd.text[wnd.cursorPos] = ' ';
                inputWindowAdvanceCursor(wnd);
            }


        } else {
            switch(gameAction) {
                case Canvas.LEFT:
                    //CURSOR LEFT
                    if (wnd.cursorPos-1 >= 0) {
                        wnd.cursorPos--;
                        inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos+1);
                    }
                    break;
                case Canvas.RIGHT:
                    //CURSOR RIGHT
                    if (wnd.cursorPos+1 <= wnd.textFill) {
                        inputWindowAdvanceCursor(wnd);
                    }
                    break;
                default:
                    handled = false;
                    break;

            }
        }        
        
        
        
        return handled;
    }    
*/    
    
    
    /** Handle the input for a button */
    public static boolean handleButtonWindowInput(GTextWindow wnd, int gameAction, int keyCode) {
        boolean handled = true;
        
        switch (gameAction) {
            case Canvas.FIRE:
                wnd.activated=true;
                break;
            default:
                handled=false;
                break;
        }
        if (keyCode==wnd.activateKey2) {
            wnd.activated = true;
            handled = true;
        }

        return handled;
    }
    
    /** Handle the input for a button */
    public static boolean handleSpinButtonInput(GWindow wnd, int gameAction, int keyCode) {
        boolean handled = true;
        switch (gameAction) {
            case Canvas.UP:
                if (wnd.partnerWindow.type == LIST) {
                    listSelectPrev((GList)wnd.partnerWindow);
                }
                break;
            case Canvas.DOWN:
                if (wnd.partnerWindow.type == LIST) {
                    listSelectNext((GList)wnd.partnerWindow);
                }
                break;
            default:
                handled=false;
                break;
        }
        return handled;
    }


    /** Handle the input for a button */
    public static boolean handleListInput(GList wnd, int gameAction, int keyCode) {
        boolean handled = true;
        switch (gameAction) {
            case Canvas.UP:
                listSelectPrev(wnd);
                break;
            case Canvas.DOWN:
                listSelectNext(wnd);
                break;
            case Canvas.FIRE:
                wnd.activated = true;
            default:
                handled=false;
                break;
        }
        if (keyCode==wnd.activateKey2) {
            wnd.activated = true;
            handled = true;
        }
        return handled;
    }
    
    
    
    
    //// TEXT WINDOW EXTRAS ////

    
    public static void textWindowSetText(GTextWindow wnd, String newText) {
        textWindowRemoveText(wnd);
        textWindowAddText(wnd, newText.toCharArray());
    }
    
    public static void textWindowSetText(GTextWindow wnd, char[] newText) {
        textWindowRemoveText(wnd);
        textWindowAddText(wnd, newText);
    }

    public static void textWindowSetText(GTextWindow wnd, StringBuffer newText) {
        textWindowRemoveText(wnd);
        textWindowAddText(wnd, newText);
    }
    
    /** Add text to a gfxTextWindow (string). */
    public static void textWindowAddText(GTextWindow wnd, String text) {
        if (text!=null && text.length()>0) {
            textWindowAddText(wnd, text.toCharArray());
        }
    }
    public static void textWindowAddText(GTextWindow wnd, StringBuffer text) {
        if (text!=null && text.length()>0) {
            tmpChars = new char[text.length()];
            text.getChars(0, text.length(), tmpChars, 0);
            textWindowAddText(wnd, tmpChars);
            tmpChars = null;            
        }
    }
    
    
    /** Add text to a gfxTextWindow (char array). */
    public static void textWindowAddText(GTextWindow wnd, char[] text) {
        boolean shiftedLeft = false;
        
        if (text!=null && text.length>0) {
            //ensure text fits in window - no overflow
            int noOfCharsToCopy = 0;
            int leftShiftAmount = 0;
            int lastSpaceInLine = -1;
            int lastPosForcedNewLineByWrap = -1;
            // makes sure to copy a maximum of wnd.maxChars characters
            if (text.length > wnd.maxChars) {
                noOfCharsToCopy = wnd.maxChars;
            } else {
                noOfCharsToCopy = text.length;
            }
            
            if (noOfCharsToCopy > 0) {
                int lineFill = wnd.lastLineFill;
                
                //test if chars have to be left shifted to have enough room for the new text
                leftShiftAmount =  (wnd.maxChars - wnd.textFill) - noOfCharsToCopy; //free Space - noOfCharsToCopy
                
                // not enough free characters, left shifting necessary
                if (leftShiftAmount<0) {
                    leftShiftAmount *= (-1);
                    if (wnd.allowWrapInWords == false) {
                        // check if a word would be truncated at the beginning
                        while (   wnd.text[leftShiftAmount]!=' ' && wnd.text[leftShiftAmount]!='\n'
                               && wnd.text[leftShiftAmount-1]!=' ' && wnd.text[leftShiftAmount-1]!='\n'
                               && leftShiftAmount < wnd.text.length) { // word would still be truncated
                            leftShiftAmount++;
                        }
                    }

                    
                    shiftedLeft = true;
                    //startPlay line and fill counting anew
                    lineFill = 0;
                    wnd.noOfExistingLines = 1;
                    //shift left
                    for (int i=0; i<wnd.maxChars-noOfCharsToCopy && i+leftShiftAmount < wnd.text.length; i++) {
                        wnd.text[i] = wnd.text[i+leftShiftAmount];
                        
                        lineFill++;
                        
                        if (wnd.allowWrapInWords == false) {
                            // remember last space found in current line
                            if (wnd.text[i]==' ') {
                                lastSpaceInLine = lineFill;
                            }
                            if (lineFill == wnd.noOfCharsPerLine && i+1 < wnd.text.length) {
                                if (   wnd.text[i]!=' ' && wnd.text[i]!='\n'
                                    && wnd.text[i+1]!=' ' && wnd.text[i+1]!='\n') { //word would be wrapped inside
                                    if (lastSpaceInLine > 0) {
                                        lineFill = wnd.noOfCharsPerLine-lastSpaceInLine;    //linefill of new line equals now number of characters from last space 
                                                                                            // to last character in line
                                        wnd.text[i - lineFill] = '\n'; //force a new line
                                        wnd.noOfExistingLines++;
                                        lastSpaceInLine = -1;
                                    }
                                }

                            }
                            
                        }
                        
                        //take care of the number of lines plus the lastline fill
                        if (wnd.text[i]=='\n' || lineFill==wnd.noOfCharsPerLine+1) {
                            wnd.noOfExistingLines++;
                            lastSpaceInLine = -1; //reset last space found in line since a new line is encountered
                            if (wnd.text[i]=='\n' && lineFill==wnd.noOfCharsPerLine+1) {    //natural linebreak + forced linebreak immediately thereafter
                                lineFill = 0;
                                wnd.noOfExistingLines++;
                            } else if (!(wnd.text[i]=='\n') && lineFill==wnd.noOfCharsPerLine+1) {  //natural linebreak only
                                lineFill = 1;
                            } else if (wnd.text[i]=='\n' && !(lineFill==wnd.noOfCharsPerLine+1)) {  //forced linebreak only
                                lineFill = 0;
                            }
                        }
                    }
                    
                    //total textfill must be decreased by the num of chars we shifted left
                    wnd.textFill -= leftShiftAmount; 

                    
                    if (lineFill==0) { //no characters in the last line -> subtract 1 from linecount
                        lineFill =  textWindowGetLineFill(wnd, wnd.textFill - 1);
                        wnd.noOfExistingLines--;
                    }
                    
                    wnd.lastLineFill =  lineFill;   //set the lastLineFill
                    
                }
                
                
                //make sure lineFill is consistent   
                lineFill = wnd.lastLineFill;
            
                if (wnd.noOfExistingLines <= 0) {
                    wnd.noOfExistingLines = 1;
                }
                
                //copy the new text part
                for (int i=0; i<noOfCharsToCopy && i<text.length && wnd.textFill+i < wnd.maxChars; i++) {
                    lineFill++;
                    
                    wnd.text[wnd.textFill+i] = text[i];
                    
                    if (wnd.allowWrapInWords == false) {
                        // remember last space found in current line
                        if (text[i]==' ') {
                            lastSpaceInLine = lineFill;
                        }
                        if (lineFill == wnd.noOfCharsPerLine && wnd.textFill + i + 1 < wnd.text.length) {
                            if (   text[i]!=' ' && text[i]!='\n'
                                && (i+1) < text.length
                                && text[i + 1]!=' ' && text[i + 1]!='\n') { //word would be wrapped inside
                                if (lastSpaceInLine > 0) {
                                    lineFill = wnd.noOfCharsPerLine-lastSpaceInLine;    //linefill of new line equals now number of characters from last space 
                                                                                        // to last character in line
                                    lastPosForcedNewLineByWrap = wnd.textFill + i - lineFill;
                                    wnd.text[lastPosForcedNewLineByWrap] = '\n'; //force a new line
                                    wnd.noOfExistingLines++;
                                    lastSpaceInLine = -1;
                                }
                            }
                        }

                    }
                    
                    
                    if (lineFill==wnd.noOfCharsPerLine+1 || (wnd.textFill+i-1>=0 && lastPosForcedNewLineByWrap!= wnd.textFill+i-1 && wnd.text[wnd.textFill+i-1]=='\n')) {  //previous linebreak, lineFill starts anew
                        wnd.noOfExistingLines++;
                        lineFill = 1;
                        lastSpaceInLine = -1;
                    }
                    
                    
                    
                   /*
                    if (text[i]=='\n' || lineFill==wnd.noOfCharsPerLine+1) {
                        wnd.noOfExistingLines++;
                        if (text[i]=='\n' && lineFill==wnd.noOfCharsPerLine+1) {
                            lineFill = 1;
                            wnd.noOfExistingLines++;
                        } else if (!(text[i]=='\n') && lineFill==wnd.noOfCharsPerLine+1) {  //natural linebreak
                            lineFill = 1;
                        } else if (text[i]=='\n' && !(lineFill==wnd.noOfCharsPerLine+1)) { //forced linebreak
                            if (i+1<noOfCharsToCopy) {
                                lineFill = 0;
                            } else {
                                wnd.noOfExistingLines--;
                            }
                        }
                    }*/
                }
                
                wnd.lastLineFill = lineFill;
                wnd.textFill += noOfCharsToCopy;
            }

            
            if (shiftedLeft) {
                int firstVisibleLineNo_old = wnd.firstVisibleLineNo;
                wnd.firstVisibleCharNo = 0;
                wnd.firstVisibleLineNo = 0;
                /*
                if (firstVisibleLineNo_old > 0) {
                    textWindowScrollDown(wnd, firstVisibleLineNo_old);
                }
                 */
            }
            
            
            if (wnd.autoScrollExtents) {
                textWindowEnsureLastLineVisible(wnd);
            }
            
        }
        
    }
    
    /** Make sure the last line of a gfxTextWindow is visible. */
    private static void textWindowEnsureLastLineVisible(GTextWindow wnd) {
        int scrollOffset = (wnd.noOfExistingLines - wnd.firstVisibleLineNo) - wnd.noOfVisibleLines;
        if (scrollOffset > 0) {
            textWindowScrollDown(wnd, scrollOffset);
        }
    }
    
    private static void textWindowEnsureCharIsVisible(GTextWindow wnd, int charPos) {
        wnd.firstVisibleLineNo=0;
        wnd.firstVisibleCharNo=0;
        while(wnd.firstVisibleCharNo < charPos && wnd.firstVisibleLineNo < wnd.noOfExistingLines-1) {
            textWindowScrollDown(wnd, 1);
        }
        if (wnd.firstVisibleCharNo > charPos && wnd.firstVisibleLineNo > 0) {
            textWindowScrollUp(wnd, 1);
        }
    }
    
    /** Scroll down the text of a gfxTextWindow for a number of lines. */
    public static void textWindowScrollDown(GTextWindow wnd, int noOfLines) {
        int scrollOffset = ((wnd.noOfExistingLines - wnd.firstVisibleLineNo) -1);
        
        if (noOfLines<scrollOffset) {
            scrollOffset = noOfLines;
        }
        
        int lineFill = 1;
        int passedChars = wnd.firstVisibleCharNo;
        while (scrollOffset > 0 && wnd.firstVisibleCharNo >= 0 && passedChars+lineFill<=wnd.textFill && passedChars+lineFill-1 < wnd.text.length) {
            if (wnd.text[passedChars+lineFill-1]=='\n' || lineFill==wnd.noOfCharsPerLine) {
                wnd.firstVisibleCharNo = passedChars+lineFill;
                wnd.firstVisibleLineNo++;
                passedChars+=lineFill;
                scrollOffset--;
                lineFill = 1;
            } else {
                lineFill++;
            }
        }
    }

    public static void textWindowScrollDownFixed(GTextWindow wnd, int noOfLines) {
        int scrollOffset = ((wnd.noOfExistingLines - wnd.firstVisibleLineNo) -1);

        if (noOfLines<scrollOffset) {
            scrollOffset = noOfLines;
        }

        if (wnd.firstVisibleLineNo + wnd.noOfVisibleLines - 1 >= wnd.noOfExistingLines-1) {
                return;
        }

        int lineFill = 1;
        int passedChars = wnd.firstVisibleCharNo;
        while (scrollOffset > 0 && wnd.firstVisibleCharNo >= 0 && passedChars+lineFill<=wnd.textFill && passedChars+lineFill-1 < wnd.text.length) {
            if (wnd.text[passedChars+lineFill-1]=='\n' || lineFill==wnd.noOfCharsPerLine) {
                wnd.firstVisibleCharNo = passedChars+lineFill;
                wnd.firstVisibleLineNo++;
                passedChars+=lineFill;
                scrollOffset--;
                lineFill = 1;
            } else {
                lineFill++;
            }
        }
    }


    /** Scroll up the text of a gfxTextWindow for a number of lines. */
    public static void textWindowScrollUpFixed(GTextWindow wnd, int noOfLines) {
        //int scrollOffset = (wnd.firstVisibleLineNo);
        int scrollOffset = ((wnd.noOfExistingLines - wnd.firstVisibleLineNo) -1);

        if (noOfLines<scrollOffset) {
            scrollOffset = noOfLines;
        }

        if (scrollOffset > 0 && wnd.firstVisibleCharNo>0) {
            int lineFill = 1;
            wnd.firstVisibleCharNo--;
            if (wnd.firstVisibleCharNo==0) {
                wnd.firstVisibleLineNo=0;
            }

            int passedChars = wnd.firstVisibleCharNo;
            int lineLength = wnd.noOfCharsPerLine;

            // MOVE BACKWARDS through lines
            boolean reduceScrollOffset = false;
            while (scrollOffset >= 0 && wnd.firstVisibleCharNo > 0) {
                reduceScrollOffset = false;
                if (passedChars-lineFill>0 && wnd.text[passedChars]=='\n' && lineFill==1) {
                    // \n found at the end of the line and not first line
                    // get length of previous line
                    lineLength = textWindowGetLineFill(wnd, passedChars-1);
                    reduceScrollOffset = true;
                }
                if (passedChars-lineFill<0) {
                    wnd.firstVisibleCharNo=0;
                    wnd.firstVisibleLineNo=0;
                } else if (wnd.text[passedChars-lineFill]=='\n' || lineFill==lineLength) {
                    // \n found or line full
                    wnd.firstVisibleCharNo = passedChars-lineFill+1;
                    wnd.firstVisibleLineNo--;
                    passedChars-=lineFill;
                    reduceScrollOffset = true;
                    lineFill = 1;
                    lineLength = wnd.noOfCharsPerLine;
                } else {
                    lineFill++;
                }
                if (reduceScrollOffset) {
                    scrollOffset--;
                }
            }
        }
    }


    
    /** Scroll up the text of a gfxTextWindow for a number of lines. */
    public static void textWindowScrollUp(GTextWindow wnd, int noOfLines) {
        int scrollOffset = (wnd.firstVisibleLineNo);
        
        if (noOfLines<scrollOffset) {
            scrollOffset = noOfLines;
        }
        
        if (scrollOffset > 0 && wnd.firstVisibleCharNo>0) {
            int lineFill = 1;
            wnd.firstVisibleCharNo--;
            if (wnd.firstVisibleCharNo==0) {
                wnd.firstVisibleLineNo=0;
            }
            int passedChars = wnd.firstVisibleCharNo;
            int lineLength = wnd.noOfCharsPerLine;
            
            while (scrollOffset > 0 && wnd.firstVisibleCharNo > 0) {
                if (passedChars-lineFill>0 && wnd.text[passedChars]=='\n' && lineFill==1) {
                    lineLength = textWindowGetLineFill(wnd, passedChars);
                }
                if (passedChars-lineFill<0) {
                    wnd.firstVisibleCharNo=0;
                    wnd.firstVisibleLineNo=0;
                } else if (wnd.text[passedChars-lineFill]=='\n' || lineFill==lineLength) {
                    wnd.firstVisibleCharNo = passedChars-lineFill+1;
                    wnd.firstVisibleLineNo--;
                    passedChars-=lineFill;
                    scrollOffset--;
                    lineFill = 1;
                    lineLength = wnd.noOfCharsPerLine;
                } else {
                    lineFill++;
                }
            }
        }
    }


    /** Get the number of chars in a line where the char at charPos is in */
    private static int textWindowGetLineFill(GTextWindow wnd, int charPos) {
        if (charPos < 0 || charPos >= wnd.textFill || charPos >= wnd.text.length)
            return 0;

        if (wnd.text[charPos]=='\n')  {
            return 1;
        }

        
        int pos = charPos-1;
        int lineFill=0;
        int diff = 0;
        
        /*
        if (charPos==0) {
            diff=0;
        }
        */
        
        //count the chars before the actual position
        
        while (pos>=0 && wnd.text[pos]!='\n') {
            diff++;
            pos--;
        }

        /*if (pos==0 && diff < wnd.noOfCharsPerLine && wnd.text[0]!='\n') {
            diff++;
        }*/
        
        lineFill = diff % wnd.noOfCharsPerLine;
        /*
        if (lineFill==0 && charPos!=0 && diff!=0) {
            lineFill = wnd.noOfCharsPerLine;
        }
         */

        //count the char at the actual position
        lineFill++; 

        //count the chars after the actual position
        while (charPos+1 < wnd.textFill && wnd.text[charPos]!='\n' && lineFill<wnd.noOfCharsPerLine) {
            lineFill++;
            charPos++;
        }
        
        /*
         if (lineFill == 0)
            lineFill = 1;
         */
        return lineFill;
    }

    
    /** Remove all text from a text window. */
    public static void textWindowRemoveText(GTextWindow wnd) {
        if (wnd.type == INPUTWINDOW) {
            inputWindowRemoveText((GInputWindow)wnd);
        } else {
            if (wnd.text!=null) {
                for (int i=0; i<wnd.textFill && i < wnd.text.length; i++) {
                    wnd.text[i]=' ';
                }
            }
            wnd.textFill = 0;
            wnd.lastLineFill = 0;
            wnd.firstVisibleLineNo=0;
            wnd.firstVisibleCharNo=0;
            wnd.noOfExistingLines = 0;
        }
    }
    
    
    
   
    
    //// INPUT WINDOW EXTRAS ////
    
    /** Increment cursor position if possible */
    public static void inputWindowAdvanceCursor(GInputWindow wnd) {
        wnd.cursorMove = false;
        wnd.cursorBlock = false;
        
        if (wnd.maxChars==wnd.cursorPos+1) {
            return;
        }
        
        wnd.cursorPos++;
        if (wnd.textFill<wnd.cursorPos) {
            wnd.textFill++;
        }
        
        wnd.inputLastKeypress = 0;
        
        inputWindowCheckCursorLineBreak(wnd, wnd.cursorPos-1);
    }


    /** Check if the cursor moved to another line, take all actions necessary, 
      including scrolling. */
    private static void inputWindowCheckCursorLineBreak(GInputWindow wnd, int oldCursorPos) {
        if (wnd.cursorPos<oldCursorPos) {
            if ((oldCursorPos % wnd.noOfCharsPerLine) == 0 && wnd.cursorPos!=0) {
                //linebreak to previous line
                wnd.cursorLineNo--;
            }
        } else {
            if (wnd.cursorPos % wnd.noOfCharsPerLine == 0) {
                //linebreak to next line
                wnd.cursorLineNo++;
                if (wnd.cursorLineNo>wnd.noOfExistingLines-1) {
                    wnd.noOfExistingLines++;
                }
            }
        }

        if (wnd.cursorLineNo-wnd.firstVisibleLineNo >= wnd.noOfVisibleLines) {
            textWindowScrollDown(wnd, 1);
        } else if (wnd.cursorLineNo<wnd.firstVisibleLineNo) {
            textWindowScrollUp(wnd, 1);
        }
        
    }
    
    
    public static void inputWindowSetCursorToLineEnd(GInputWindow wnd) {
        wnd.cursorMove = false;
        wnd.cursorBlock = false;
        wnd.cursorPos += textWindowGetLineFill(wnd, wnd.cursorPos)-wnd.cursorPos;
    }
    
    public static void inputWindowSetCursorToTextEnd(GInputWindow wnd) {
        wnd.cursorPos = wnd.textFill;
        if (wnd.cursorPos == wnd.maxChars && wnd.cursorPos > 0) {
            wnd.cursorPos = wnd.maxChars - 1;
        }
        textWindowEnsureLastLineVisible(wnd);
    }
    
    public static char[] inputWindowGetText (GInputWindow wnd) {
        char[] text = null;
        if (wnd.textFill>0) {
            text = new char[wnd.textFill];
            for (int i=0; i<wnd.textFill; i++) {
                text[i] = wnd.text[i];
            }
        }
        return text;
    }

    public static String inputWindowGetTextStr(GInputWindow wnd) {
        char[] txt = inputWindowGetText(wnd);
        if (txt == null) {
            return null;
        }
        return new String(txt);
    }


    public static char[] textWindowGetText (GTextWindow wnd) {
        char[] text = null;
        if (wnd.textFill>0) {
            text = new char[wnd.textFill];
            for (int i=0; i<wnd.textFill; i++) {
                text[i] = wnd.text[i];
            }
        }
        return text;
    }

    
    
    /** Remove all text from an input window. */
    public static void inputWindowRemoveText(GInputWindow wnd) {
        for (int i=0; i<wnd.textFill; i++) {
            wnd.text[i]=' ';
        }
        wnd.cursorPos = 0;
        wnd.textFill = 0;
        wnd.cursorLineNo = 0;
        wnd.cursorMove = false;
        
        wnd.lastLineFill = 0;
        wnd.firstVisibleLineNo=0;
        wnd.firstVisibleCharNo=0;
        wnd.noOfExistingLines = 0;
    }



    
    
    //OTHER:
    /**
     * Draw a window as a progress bar.
     */
    public static void drawProgress(Graphics g, GWindow wnd, int completedSteps, int totalSteps) {
        drawWindow(g, wnd, true);
        saveGraphicsSettings(g);
        g.setClip(wnd.x, wnd.y, wnd.width, wnd.width);
        g.setColor(wnd.activeBorderColor);
        if (completedSteps > 0 && (totalSteps) > 0) {
            g.fillRect(wnd.x + wnd.borderSize, wnd.y + wnd.borderSize, (wnd.width-2*wnd.borderSize)*completedSteps/(totalSteps), wnd.height-2*wnd.borderSize);
        }
        restoreGraphicsSettings(g);
    }
    
    /** Draw any Window (base class or derived). */
    public static void drawWindow(Graphics g, GWindow wnd, boolean restoreGraphicsSettings) {
        if (g==null || !wnd.visible)
            return;
        if (restoreGraphicsSettings) {
            saveGraphicsSettings(g);
        }

        if(wnd.type == BUTTON && ((GTextWindow)wnd).buttonImage!=null) {
            g.setClip(wnd.x, wnd.y, wnd.width, wnd.height);
            g.drawImage(((GTextWindow)wnd).buttonImage, wnd.x, wnd.y, Graphics.TOP|Graphics.LEFT);
        } else {
        
        //draw window outline
        drawDefaultWindow(g, wnd);
        //draw stuff specific for each window type
        switch (wnd.type) {
            case TEXTWINDOW:
                drawTextWindow(g, (GTextWindow)wnd);
                break;
            case INPUTWINDOW:
                drawTextWindow(g, (GTextWindow)wnd);
                if (wnd.selected) {
                    tmpInputWindow = (GInputWindow)wnd;
                    curTime = System.currentTimeMillis();
                    if (curTime - tmpInputWindow.lastCursorFlash > 400) {
                        tmpInputWindow.flashCursor = !tmpInputWindow.flashCursor;
                        tmpInputWindow.lastCursorFlash = curTime;
                    }
                    if ((tmpInputWindow.flashCursor || tmpInputWindow.cursorBlock) && tmpInputWindow.drawCursor) {
                        inputWindowDrawCursor(g, tmpInputWindow);    
                    }
                    if (tmpInputWindow.showKeyMapping) {
                        if (curTime - tmpInputWindow.inputLastKeypress < 750 && !tmpInputWindow.numeric) {
                             inputWindowShowCurrentKeyMapping(g, tmpInputWindow, false, null);
                        } else if (tmpInputWindow.textFill > 0 && !tmpInputWindow.numeric){
                            inputWindowShowCurrentKeyMapping(g, tmpInputWindow, true, null);
                        } else  if (tmpInputWindow.textFill == 0 && tmpInputWindow.emptyInfo!=null) {
                            inputWindowShowCurrentKeyMapping(g, tmpInputWindow, true, tmpInputWindow.emptyInfo);
                        }
                    }
                }
                break;
            case IMAGEWINDOW:
                drawImageWindow(g, (GImageWindow)wnd);
                break;
            case MENU:
                drawMenu(g, (GMenu)wnd);
                break;
            case BUTTON:
                drawTextWindow(g, (GTextWindow)wnd);
                break;
            case SPINBUTTON:
                drawSpinButton(g, wnd);
                break;
            case LIST:
                drawList(g, (GList)wnd);
                break;
        }
        }

        if (restoreGraphicsSettings) {
            restoreGraphicsSettings(g);
        }
    }

    /** Draw an object of type GWindow. */    
    public static void drawDefaultWindow (Graphics g, GWindow wnd) {
        g.setClip(wnd.x, wnd.y, wnd.width, wnd.height);
        if (!wnd.selected) {
            //not selected -> inactive border
            if (wnd.borderSize > 0 && wnd.borderColor != TRANSPARENT) {
                g.setColor(wnd.borderColor);
                if (wnd.borderSize==1)
                    g.drawRect(wnd.x, wnd.y, wnd.width-1, wnd.height-1);
                else if (wnd.backColor==TRANSPARENT) {
                    for (e=0; e<wnd.borderSize; e++) {
                        g.drawRect(wnd.x+e, wnd.y+e, wnd.width-(2*e)-1, wnd.height-(2*e)-1);
                    }
                }
                else {
                    g.fillRect(wnd.x, wnd.y, wnd.width, wnd.height);
                }
            }
            if (wnd.backColor != TRANSPARENT) {
                //draw background
                g.setColor(wnd.backColor);
                g.fillRect(wnd.x + wnd.borderSize, wnd.y + wnd.borderSize, wnd.width - (2*wnd.borderSize), wnd.height - (2*wnd.borderSize));
            }
        } else {
            //selected -> active border
            if (wnd.borderSize > 0 && wnd.activeBorderColor != TRANSPARENT) {
                g.setColor(wnd.activeBorderColor);
                if (wnd.borderSize==1)
                    g.drawRect(wnd.x, wnd.y, wnd.width-1, wnd.height-1);
                else
                    g.fillRect(wnd.x, wnd.y, wnd.width, wnd.height);
            }
            if (wnd.activeBackColor != TRANSPARENT) {
                //draw background
                g.setColor(wnd.activeBackColor);
                g.fillRect(wnd.x + wnd.borderSize, wnd.y + wnd.borderSize, wnd.width - (2*wnd.borderSize), wnd.height - (2*wnd.borderSize));
            }
        }
    }

    public static void drawImageWindow(Graphics g, GImageWindow wnd) {
        if (wnd.image!=null) {
            g.setClip(wnd.x, wnd.y, wnd.width, wnd.height);
            g.drawImage(wnd.image, wnd.x - wnd.clipOffsetX, wnd.y - wnd.clipOffsetY, Graphics.LEFT | Graphics.TOP);
        }
    }

    /** Draw a TextWindow. */
    public static void drawTextWindow(Graphics g, GTextWindow wnd) {
        if (wnd.font==null || wnd.text==null)
            return;
        
        //set "cursor" to beginning of first visible line
        d4 = wnd.firstVisibleCharNo;             //charPos
        d5 = 0;                              //colPos

        if (wnd.centerTextH) {
            d6 = textWindowGetLineFill(wnd, d4)*wnd.font.charWidth; //line length
            d7 = ((wnd.width - 2*(wnd.borderSize + wnd.innerOffset)) - d6) / 2; //xOffset
        } else {
            d7 = 0;
        }
        
        d1 = wnd.x + wnd.borderSize + wnd.innerOffset + d7;   //lineX
        d2 = wnd.y + wnd.borderSize + wnd.innerOffset;   //lineY
        if (wnd.centerTextV) {
            d9 = wnd.noOfExistingLines*wnd.font.charHeight; // total lines height
            d10 = wnd.height - (2*(wnd.borderSize+wnd.innerOffset));    // inner window height
            if (d10 > d9) {
                d2 += (d10 - d9) / 2;
            }
        }
        d3 = 0;                              //lineDrawCount

        
        while (d4<wnd.textFill && d3<wnd.noOfVisibleLines) {
            if (wnd.text[d4]=='\n') {
                d3++;
                d4++;
                if (wnd.centerTextH && d4<wnd.textFill) {
                    d6 = textWindowGetLineFill(wnd, d4)*wnd.font.charWidth; //line length
                    d7 = ((wnd.width - 2*(wnd.borderSize + wnd.innerOffset)) - d6) / 2; //xOffset
                }
                d1 = wnd.x + wnd.borderSize + wnd.innerOffset + d7;
                d2 += wnd.font.charHeight;
                d5 = 0;
            } else {
                if (!wnd.password) {
                    wnd.font.drawChar(g, wnd.text[d4], d1, d2);
                } else {
                    if (wnd.type == INPUTWINDOW && ((GInputWindow)wnd).cursorBlock && d4 == ((GInputWindow)wnd).cursorPos) {
                        wnd.font.drawChar(g, wnd.text[d4], d1, d2);
                    } else {
                        wnd.font.drawChar(g, '*', d1, d2);
                    }
                }
                d4++;
                d5++;
                d12 = (d5%wnd.noOfCharsPerLine);
                if (d12==0) {
                    if (wnd.centerTextH && d4<wnd.textFill) {
                        d6 = textWindowGetLineFill(wnd, d4)*wnd.font.charWidth; //line length
                        d7 = ((wnd.width - 2*(wnd.borderSize + wnd.innerOffset)) - d6) / 2; //xOffset
                    }
                    d1 = wnd.x + wnd.borderSize + wnd.innerOffset + d7;
                    d2 += wnd.font.charHeight;
                    d3++;
                    d5 = 0;
                } else {
                    if (d12!=1 || d4 == 1 || wnd.text[d4-1] != ' ') {   // do not draw preceding spaces which were caused by word wrap
                        d1 += wnd.font.charWidth;
                    }
                }
            }
         }
    }    

   /** Draw gfxInputWindow cursor. */
    private static void inputWindowDrawCursor(Graphics g, GInputWindow wnd) {
        if (wnd.font!=null) {
            //check if cursor should be moved
            if (wnd.cursorMove && System.currentTimeMillis() - wnd.inputLastKeypress >= 750) {
                inputWindowAdvanceCursor(wnd);
                tmpInputWindow.cursorBlock = false;
                tmpInputWindow.flashCursor = true;
                tmpInputWindow.lastCursorFlash = curTime;
            }
            g.setColor(wnd.cursorColor);
            d = wnd.borderSize+wnd.innerOffset;
            d1 = wnd.x + d + ((wnd.cursorPos%wnd.noOfCharsPerLine)*wnd.font.charWidth)-1;
            d2 = wnd.y + d + ((wnd.cursorLineNo-wnd.firstVisibleLineNo)*wnd.font.charHeight)-1;
            g.setClip(d1, d2, wnd.font.charWidth+2, wnd.font.charHeight+2);
            if (!wnd.cursorBlock) {
                g.drawLine(d1, d2, d1, d2 + wnd.font.charHeight+1);
                g.drawLine(d1+1, d2, d1+1, d2 + wnd.font.charHeight+1);
            } else {
                g.drawRect(d1, d2, wnd.font.charWidth+1, wnd.font.charHeight+1);
            }
        }
    }    
    
    /** Draw a menu. */
    public static void drawMenu(Graphics g, GMenu wnd) {
        if (wnd.captionWindow!=null) {
            drawWindow(g, wnd.captionWindow, false);
        }
        if (wnd.items!=null) {
            for (int m1=0; m1<wnd.items.length; m1++) {
                if (wnd.items[m1]!=null && wnd.visible) {
                    if (wnd.items[m1].useParentColors) {
                        saveWindowColors(wnd.items[m1]);
                        wnd.items[m1].borderColor = wnd.borderColor;
                        wnd.items[m1].activeBorderColor = wnd.activeBorderColor;
                        wnd.items[m1].backColor = wnd.backColor;
                        wnd.items[m1].activeBackColor = wnd.activeBackColor;
                    }
                    drawWindow(g, wnd.items[m1], false);
                    if (wnd.items[m1].useParentColors) {
                        restoreWindowColors(wnd.items[m1]);
                    }
                }
            }
        }
    }
        
    
    
    /** Draw a GList object. */
    public synchronized static void drawList(Graphics g, GList list) {
        if (list.font==null) {
            return;
        }

        //int d, i, j, k, l, m, n, d1, d2, d3, d4, d5, d6, w, h;        
        
        d2 = list.totalGapY >> 1;
        d3 = list.x + list.xSpace + list.borderSize + list.innerOffset;
        d4 = list.y + list.borderSize + list.innerOffset + d2;
        d = d3 - list.xSpace;
        d7 = list.width -(((list.innerOffset)+(list.borderSize)) << 1);
        d8 = list.font.charHeight + (d2 << 1);
        d9 = d + ((list.xSpace - list.iconWidth) >> 1);
        w1 = list.firstVisibleEntry;
        for (d1=w1; d1 < w1+list.noOfVisibleEntries && d1 < list.entries.size(); d1++) {
            if (list.selectedEntry==d1 && list.drawSelection) {
                //draw selection
                d5 = d4 - d2;
                g.setColor(list.activeBackColor);
                g.setClip(d, d5, d7, d8);
                g.fillRect(d, d5, d7, d8);
            }
            GListEntry gse = (GListEntry)list.entries.elementAt(d1);
            if (list.useIcons) {
                //draw icon
                d5 = d4 - d2;
                d5 += (d8 - list.iconHeight) >> 1;
                //d6 = list.entriesIcons[d1];
                if (gse.icon!=null) {
                    gse.icon.draw(g, d9, d5, list.iconWidth, list.iconHeight);
                    /* g.setClip(d9, d5, list.iconWidth, list.iconHeight);
                    g.drawImage(gse.iconImage, d9 - gse.iconX,
                                                 d5 - gse.iconY,
                                                 Graphics.LEFT | Graphics.TOP);
                     */
                }
                /*
                if (d6 < list.iconImages.length && list.iconImages[d6]!=null) {
                    g.drawImage(list.iconImages[d6], d9 - list.iconCoordinates[d6][0], 
                                                 d5 - list.iconCoordinates[d6][1], 
                                                 Graphics.LEFT | Graphics.TOP);
                }*/
            }
            //draw entry text
            list.font.drawString(g, gse.entry, d3, d4);
            d4 += list.font.charHeight + list.totalGapY;
        }
        
        //arrows if necessary
        if (w1 > 0) {
            //draw up arrow
            d1 = list.x + list.width - list.innerOffset - list.borderSize - 6; 
            d2 = list.y + list.innerOffset + list.borderSize + 1;
            g.setColor(0xffffff - list.backColor);
            g.setClip(d1, d2, 6, 3);
            g.drawLine(d1 + 2, d2, d1 + 2, d2);
            g.drawLine(d1 + 1, d2+1, d1+ 3, d2+1);
            g.drawLine(d1, d2+2, d1+4, d2+2);
        }
        if (w1 + list.noOfVisibleEntries < list.entries.size()) {
            d1 = list.x + list.width - list.innerOffset - list.borderSize - 6; 
            d2 = list.y + list.height - list.innerOffset - list.borderSize - 4;
            //draw down arrow
            g.setColor(0xffffff - list.backColor);
            g.setClip(d1, d2, 5, 3);
            g.drawLine(d1 + 2, d2+2, d1+2, d2+2);
            g.drawLine(d1 + 1, d2+1, d1+3, d2+1);
            g.drawLine(d1, d2, d1+4, d2);
        }
        
        //check if more enties and next entry is partly visible
        w1 += list.noOfVisibleEntries;
        d2 = list.totalGapY >> 1;
        d5 = d4 - d2;
        h1 = (list.y + list.height - list.innerOffset - list.borderSize) - d5;
        if (w1 < list.entries.size() &&  h1 > 0) {
            GListEntry gse2 = (GListEntry)list.entries.elementAt(w1);
            if (gse2!=null) {
                //check if text is visible
                if (h1 > d2) {
                    list.font.drawString(g, gse2.entry, d3, d4, list.width, h1 - d2);
                    //list.font.drawString(g, (char[])list.entries.elementAt(w1), d3, d4, list.width, h1 - d2);
                }
                if (list.useIcons && gse2.icon!= null) {
                    //draw any visible parts of the entry icon
                    //d6 = list.entriesIcons[w1];
                    gse2.icon.draw(g, d9, d5, list.iconWidth, h1);
                    /*
                    g.setClip(d9, d5,  list.iconWidth, h1);
                    g.drawImage(gse2.iconImage, d9 - gse2.iconX,
                                                 d5 - gse2.iconY,
                                                 Graphics.LEFT | Graphics.TOP);
                                                 */
                }
            }
        }
    }    
    
    /** Draw a spin button (up/down spinner) */
    public static void drawSpinButton (Graphics g, GWindow wnd) {
        g.setColor(wnd.activeBorderColor);
        d1 = wnd.x + wnd.width/2;
        d2 = wnd.y + wnd.height/2 - 4;
        d3 = wnd.y + wnd.height/2 + 3;
        for (d4=0; d4<3; d4++) {
            g.drawLine(d1-d4, d2+d4, d1+d4, d2+d4);
            g.drawLine(d1-d4, d3-d4, d1+d4, d3-d4);
        }
    }
    
    

}
