/*
 * GList.java
 *
 * Created on 10. September 2003, 10:55
 */

package graphics;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Vector;

import javax.microedition.midlet.*;

/**
 *
 * @author  papke
 * @version
 */
public class GList extends GWindow{

    public Vector entries = new Vector();
   
    public void setIconForEntry(GImageClip gic, int entryIndex) {
        GListEntry gle = (GListEntry)entries.elementAt(entryIndex);
        if (gle != null) {
            gle.setIcon(gic);
        }
    }

    public boolean contains(Object value) {
        int len = entries.size();
        for(int i=0; i<len; i++) {
            GListEntry gle = (GListEntry)entries.elementAt(i);
            if (gle.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public Vector getEntires() {
        return entries;
    }

    public void setEntries(Vector entries) {
        this.entries = entries;
    }


    //protected int maxEntries;
    
    protected int selectedEntry;    
    protected int noOfCharsPerLine;
    public int noOfVisibleEntries;
    public int entryGapY;
    protected int totalGapY;
    protected int firstVisibleEntry;
    
    protected GFont font;
    public int xSpace;

    protected boolean activated = false;
    public boolean drawSelection = true;
    public boolean cycleWrap = false;


    //icon information
    protected boolean useIcons=false;
    protected int iconWidth, iconHeight;


    /*
    
    protected int[][] iconCoordinates=null;
    protected int[] entriesIcons = null;
    protected Image[] iconImages = null;
    
    */

}
