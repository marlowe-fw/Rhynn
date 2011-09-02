package rhynn;




import graphics.GImageClip;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/*
 * WorldObject.java
 */

/**
 *
 * @author  marlowe
 */
public class WorldObject {

    // =====================
    // CORE DB DATA
    // =====================
    
    /** world (database) id of the object. */
    public int objectId;

    /** id of the gameobject / or user that owns this object */
    public int ownerId;
    
    /**
     * Classification id of the object.
     * 0 Player
     * 1 Bot
     * 2 Weapon shortrange
     * 3 Weapon longrange
     * 4 Weapon ammo
     * 5 Shield
     * 6 Helmet
     * 7 Hand protection
     * 8 Armour
     * 9 Boots
     * 10 Health
     * 11 Mana
     * 12 Scroll
     * 13 Gold
     * 14 Key
     * 15 Map
     * 16 Treasure Chest
     */
    public int classId;

    /** Sub classification id: subclass.*/
    public int subclassId;

    /** triggerid of this object. */
    public int triggertype;

    /** Playfield ID to which this object belongs. */
    public int playfieldId;
    
    /** 
     * Flag to indicate whether the graphic for this object 
     * is on a static tilemap or on a dynamic one 
     */
    public int graphicsel;

    public int graphicsId;
    
    /** X Posistion on the graphic tilemap. */
    public int graphicsX;

    /** Y Posistion on the graphic tilemap. */
    public int graphicsY;
    
    /** 
     * Graphic dimension in graphic units (5 pixels) to use for this object 
     * (width and height must therefore be equal. 
     */
    public int graphicsDim;

    
    /** x position in pixels in the world. */
    public int x;
    
    /** y position in pixels in the world. */
    public int y;

    
    /** Object name. */
    public String name;
    
    /** Description for this object. */
    public String description;
        
    /** Objects maximum curMana value. */
    public int healthBase;
    /** Helath regeneration. */
    public int healthregenerateBase;
    
    
    /** Objects maximum curMana value. */
    public int manaBase;
    /** Mana regeneration. */
    public int manaregenerateBase;
    
    
    /** Object's maximum attackBase value. */
    public int attackBase;
    /** Object's maximum defend value . */
    public int defenseBase;
    /** Object's skillBase value. */
    public int skillBase;
    /** Object's magicBase value. */
    public int magicBase;
    /** Object's damageBase value. */
    public int damageBase;
    /** Amount of gold of this object. */
    public int gold;

    
    
    // =====================
    // EXTRAS
    // =====================
    
    /** End time of the extra animation. */
    //public long extraAniEndTime=0;
    
    /** Text message of this Game Object. */
    public char[] msgText = null;
    
    /** Display name of the Game Object when 'talk to all' received. */
    public boolean msgDisplayName = true;


    /** Icon to show. */
    //public int icon;
    
    /** Extra icon to show. */
    public int extraicon;

    /** Time left to visualize an etraicon of this object. */
    public long extraIconShowDuration;
    
    /** Duration of the current extra icon FlashPhase. */
    public long extraFlashPhaseDuration;
    
    /** 
     * Indicates wether the extra icon should be displayed or not, 
     * causing it to blink.
     */
    public boolean extraFlashPhase;

    
    
    /** Time left for the icon to show up. */
    public long iconShowDuration;
    
    /** Time left for the message to show up. */
    public long msgShowDuration;

    /** Time left to visualize a hit of this object. */
    public long hitShowDuration;

    /** Time to wait before showing the hit display, used for hits that result 
        from spells. */
    public long hitDisplayDelay;
    
    /** Time left for visualization of an attackBase of the player for this object. */
    public long attackShowDuration;
    
    /** Duration of the current FlashPhase. */
    public long flashPhaseDuration;
    
    /** 
     * Indicates wether the object should be displayed or not, 
     * causing it to blink.
     */
    public boolean flashPhase;

    
    /*
    private Image image;


    public void setImage(Image img) {
        image = img;
    }
     */

    /** Direction of the object. */
    public int direction;

    public long lastAnimationChange;


    //private Image curImage;
    public GImageClip curImageClip;



    public int graphicsDimHalf() {
        return (int)(graphicsDim/2);
    }

    public int xCenter() {
        return x + graphicsDimHalf();
    }

    public int yCenter() {
        return y + graphicsDimHalf();
    }

    public int xEnd() {
        return x + graphicsDim-1;
    }

    public int yEnd() {
        return y + graphicsDim-1;
    }


    public void useImage(Image img) {
        curImageClip = new GImageClip(img, graphicsX, graphicsY, graphicsDim, graphicsDim);
    }

    public void setDirection(int newDirection) {
        this.direction = newDirection;
        curImageClip.setCurrentFrame(animationFrameFromDirection());
    }

    private int animationFrameFromDirection() {
        switch(direction) {
            case DirectionInfo.UP:
                return 0;
            case DirectionInfo.RIGHT:
                return 2;
            case DirectionInfo.DOWN:
                return 4;
            case DirectionInfo.LEFT:
                return 6;
        }
        return 4;
    }

    public int getDirection() {
        return direction;
    }


    public void draw(Graphics g, int xPos, int yPos) {
        if (curImageClip!=null) {
            curImageClip.draw(g, xPos, yPos);
        }
    }

    public void drawVisibilityRect(Graphics g, ViewGeometry viewGeometry, int numCellsX, int numCellsY, boolean highlight) {
        if (highlight) {
            g.setColor(255,255,255);
        } else {
            g.setColor(192,0,0);
        }


        int cellX = (int)(x / PlayfieldCell.defaultWidth);
        int cellY = (int)(y / PlayfieldCell.defaultHeight);

        int cellXStart = cellX - Playfield.s_VisibilityCellRange;
        int cellXEnd = cellX + Playfield.s_VisibilityCellRange;
        int cellYStart = cellY - Playfield.s_VisibilityCellRange;
        int cellYEnd = cellY + Playfield.s_VisibilityCellRange;

        if (cellXStart < 0) {
            cellXEnd += -cellXStart;
            cellXStart = 0;
        } else if (cellX < Playfield.s_VisibilityCellRangeWSize) {
            cellXStart = 0;	// snap to left border
        }

        if (cellXEnd >= numCellsX) {
            cellXStart -= (cellXEnd-numCellsX+1);
            cellXEnd = numCellsX-1;
            if (cellXStart < 0) {cellXStart = 0;}
        } else if (numCellsX - 1 - cellX < Playfield.s_VisibilityCellRangeWSize) {
            cellXEnd = numCellsX-1;
        }

        if (cellYStart < 0) {
            cellYEnd += -cellYStart;
            cellYStart = 0;
        } else if (cellY < Playfield.s_VisibilityCellRangeWSize) {
            cellYStart = 0;	// snap to top border
        }

        if (cellYEnd >= numCellsY) {
            cellYStart -= (cellYEnd-numCellsY+1);
            cellYEnd = numCellsY-1;
            if (cellYStart < 0) {cellYStart = 0;}
        } else if (numCellsY - 1 - cellY < Playfield.s_VisibilityCellRangeWSize) {
            cellYEnd = numCellsY - 1;
        }

        
        int rx = (cellXStart*PlayfieldCell.defaultWidth);
        int rw = ((cellXEnd-cellXStart)+1) * PlayfieldCell.defaultHeight;
        //int rx2 = drawX+graphicsDim+PlayfieldCell.defaultWidth;
        int ry = (cellYStart*PlayfieldCell.defaultHeight);
        int rh = ((cellYEnd-cellYStart)+1) * PlayfieldCell.defaultHeight;
        //int ry2 = drawY+graphicsDim+PlayfieldCell.defaultHeight;


        int drawX = rx-viewGeometry.leftPosX;
        int drawY = ry-viewGeometry.topPosY;

        g.setClip(drawX+viewGeometry.viewX, drawY+viewGeometry.viewY, rw,rh);
        //g.fillRect(rx1+viewX, ry1+viewY, 24*3,24*3);
        g.drawRect(drawX+viewGeometry.viewX, drawY+viewGeometry.viewY, rw-1,rh-1);
        
    }

    public void draw(Graphics g, ViewGeometry viewGeometry) {

            int drawX = x-viewGeometry.leftPosX;
            int drawY = y-viewGeometry.topPosY;

            int endX = drawX + graphicsDim;
            int endY = drawY + graphicsDim;
            int clipWidth = graphicsDim;
            int clipHeight = graphicsDim;
            int clipX = drawX;
            int clipY = drawY;

            if (drawX < 0) {
                if (endX < 0) {
                    return;
                } else {
                    clipWidth -= -drawX;
                    clipX = 0;
                }
            } else if (endX >= viewGeometry.viewWidth) {
                if (drawX >= viewGeometry.viewWidth) {
                    return;
                } else {
                    clipWidth -= endX - (viewGeometry.viewWidth);
                }
            }

            if (drawY < 0) {
                if (endY < 0) {
                    return;
                } else {
                    clipHeight -= -drawY;
                    clipY = 0;
                }
            } else if (endY >= viewGeometry.viewHeight) {
                if (drawY >= viewGeometry.viewHeight) {
                    return;
                } else {
                    clipHeight -= endY - (viewGeometry.viewHeight);
                }
            }

            curImageClip.draw(g, drawX + viewGeometry.viewX, drawY + viewGeometry.viewY, clipX + viewGeometry.viewX, clipY + viewGeometry.viewY, clipWidth, clipHeight);

    }



}

