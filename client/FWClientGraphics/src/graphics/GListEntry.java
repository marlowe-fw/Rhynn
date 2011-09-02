/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graphics;


/**
 *
 * @author marlowe
 */
public class GListEntry {
    /* To be used later when the OO is used throughout. */
    String entry = "";
    Object entryData = null;

    GImageClip icon = null;

    public GListEntry(String entry, Object entryData) {
        this.entry = entry;
        this.entryData = entryData;
    }


    public GListEntry(String entry, Object entryData, GImageClip icon) {
        this.entry = entry;
        this.entryData = entryData;
        this.icon = icon;
    }

    public void setIcon(GImageClip icon) {
        this.icon = icon;
    }    

}
