package rhynn;




import javax.microedition.lcdui.Image;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class Tileset {
    Image tilesetImage = null;
    int numTilesX = 0;
    int numTilesY = 0;
    int graphicId = 0;
    //int index = 0;

    public Tileset(int graphicId, Image img)
    {
        //this.index = index;
        this.graphicId = graphicId;
        load (img);
    }

    public void load(Image img) {
        if (img != null) {
            tilesetImage = img;
            numTilesX = (int)(img.getWidth() / PlayfieldCell.defaultWidth);
            numTilesY = (int)(img.getHeight() / PlayfieldCell.defaultHeight);
        }
    }

    public int getGraphicId() {
        return graphicId;
    }

    public int clipX(int tileIndex) {
        return ((tileIndex % numTilesX) * PlayfieldCell.defaultWidth);
    }

    public int clipY(int tileIndex) {
        return (((int)(tileIndex / numTilesX)) * PlayfieldCell.defaultHeight);
    }

}