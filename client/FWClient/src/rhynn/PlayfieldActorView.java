package rhynn;




import graphics.GImageClip;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import graphics.GTools;
import java.util.Vector;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class PlayfieldActorView implements PlayfieldObserver {

    private class DeadCharacter {
        public int objectId;
        public int x;
        public int y;
        public long timeOfDeath;
        GImageClip spriteDead = null;

        public DeadCharacter(Character c) {
            spriteDead = GlobalResources.getImageClip(GlobalResources.SPRITE_DEAD);
            spriteDead.flash(10, 70);
            objectId = c.objectId;
            x = c.xCenter();
            y = c.yCenter();
            timeOfDeath = System.currentTimeMillis();
        }

        public void draw(Graphics g, ViewGeometry viewGeometry) {
            int drawX = geometry.viewX + x - geometry.leftPosX;
            int drawY = geometry.viewY + y - geometry.topPosY;
            drawX -= spriteDead.getWidth() / 2;
            drawY -= spriteDead.getHeight() / 2;
            spriteDead.draw(g, drawX, drawY);
        }
    }

    private Playfield playfield;
    private Character actorCharacter;
    private Character selectedCharacter = null;

    ViewGeometry geometry = new ViewGeometry();

    int actorPosX;
    int actorPosY;
    int scrollToleranceX;
    int scrollToleranceY;
    int charHalf;
    int viewHalfX;
    int viewHalfY;

    private Vector deadCharacters = new Vector();

    /*
    private long lastAttackTime;
    private int lastAttackedCharacterId = 0;
    */

    public int getPlayfieldPosLeft() {return geometry.leftPosX;}
    public int getPlayfieldPosTop() {return geometry.topPosY;}
    public int getViewX() {return geometry.viewX;}
    public int getViewY() {return geometry.viewY;}
    public int getViewWidth() {return geometry.viewWidth;}
    public int getViewHeight() {return geometry.viewHeight;}
    public Character getCharacter(int objectId) {return playfield.getCharacter(objectId);}

    public PlayfieldActorView(Playfield playfield, Character character, int viewX, int viewY, int viewWidth, int viewHeight) {
        this.playfield = playfield;
        playfield.setObserver(this);
        this.actorCharacter = character;
        this.charHalf = (int)(character.graphicsDim / 2);

        geometry.viewY = viewY;
        geometry.viewX = viewX;
        geometry.viewWidth = viewWidth;
        geometry.viewHeight = viewHeight;

        if (geometry.viewWidth > playfield.getWidth()) {
            geometry.viewX += (int)((geometry.viewWidth - playfield.getWidth())/2);
            geometry.viewWidth = playfield.getWidth();
        }
        if (geometry.viewHeight > playfield.getHeight()) {
            geometry.viewY += (int)((geometry.viewHeight - playfield.getHeight())/2);
            geometry.viewHeight = playfield.getHeight();
        }

        this.viewHalfX = geometry.viewWidth/2;
        this.viewHalfY = geometry.viewHeight/2;
        actorPosX = character.x;
        actorPosY = character.y;
        setScrollTolerancePercent(33);
        centerViewOnActor();
    }

    public void setScrollTolerancePercent(int tolerancePercent) {
        scrollToleranceX = ((int)((geometry.viewWidth * tolerancePercent) / 100)/2);
        scrollToleranceY = ((int)((geometry.viewHeight * tolerancePercent) / 100)/2);
    }

    public void centerViewOnActor() {
        int centerPosX = actorCharacter.x + charHalf;
        int centerPosY = actorCharacter.y + charHalf;
        geometry.leftPosX = centerPosX - (int)(geometry.viewWidth/2);
        geometry.topPosY = centerPosY - (int)(geometry.viewHeight/2);
        if (geometry.leftPosX + geometry.viewWidth > playfield.getWidth()) {
            geometry.leftPosX -= geometry.leftPosX + geometry.viewWidth - playfield.getWidth();
        }
        if (geometry.leftPosX < 0) {
            geometry.leftPosX = 0;
        }

        if (geometry.topPosY +  geometry.viewHeight > playfield.getHeight()) {
            geometry.topPosY -= geometry.topPosY + geometry.viewHeight - playfield.getHeight();
        }
        if (geometry.topPosY < 0) {
            geometry.topPosY = 0;
        }


    }

    private void checkPosUpdate() {
        if (actorPosX != actorCharacter.x) {
            actorPosX = actorCharacter.x;
            int charCenterX = actorPosX + charHalf;
            if (charCenterX-geometry.leftPosX < viewHalfX - scrollToleranceX) {
                // actorCharacter outside center tolerance strip, need to scroll left
                geometry.leftPosX = charCenterX - viewHalfX + scrollToleranceX;
                if (geometry.leftPosX < 0) geometry.leftPosX = 0;
            } else if (charCenterX-geometry.leftPosX > viewHalfX + scrollToleranceX) {
                // actorCharacter outside center tolerance strip, need to scroll right
                geometry.leftPosX = charCenterX - viewHalfX - scrollToleranceX ;
                if (geometry.leftPosX > playfield.getWidth()-geometry.viewWidth) geometry.leftPosX = playfield.getWidth()-geometry.viewWidth;
            }
        }


        if (actorPosY != actorCharacter.y) {
            actorPosY = actorCharacter.y;
            int charCenterY = actorPosY + charHalf;
            if (charCenterY-geometry.topPosY < viewHalfY - scrollToleranceY) {
                // actorCharacter outside center tolerance strip, need to scroll up
                geometry.topPosY = charCenterY - viewHalfY + scrollToleranceY;
                if (geometry.topPosY < 0) geometry.topPosY = 0;
            } else if (charCenterY-geometry.topPosY > viewHalfY + scrollToleranceY) {
                // actorCharacter outside center tolerance strip, need to scroll down
                geometry.topPosY = charCenterY - viewHalfY - scrollToleranceY ;
                if (geometry.topPosY > playfield.getHeight()-geometry.viewHeight) geometry.topPosY = playfield.getHeight()-geometry.viewHeight;
            }
        }
    }


    public void draw(Graphics g, boolean drawVisRect) {
        checkPosUpdate();
        playfield.draw(g, geometry.leftPosX, geometry.topPosY, geometry.viewX, geometry.viewY, geometry.viewWidth, geometry.viewHeight);
        checkDrawDeadCharacters(g);
        drawItems(g, drawVisRect);
        drawCharacters(g, drawVisRect);
    }

    private void checkDrawDeadCharacters(Graphics g) {
        long curTime = System.currentTimeMillis();
        for (int i=0; i<deadCharacters.size(); i++) {
            DeadCharacter dc = (DeadCharacter)deadCharacters.elementAt(i);
            if (curTime - dc.timeOfDeath > 15000) {
                deadCharacters.removeElementAt(i);
                i--;
            } else {
                dc.draw(g, geometry);
            }
        }
    }

    public void drawCharacters(Graphics g, boolean drawVisRect) {
        Enumeration e = playfield.getCharacters().elements();        
        while(e.hasMoreElements()) {
            Character c = (Character)e.nextElement();
            if (c.objectId == actorCharacter.objectId)
                continue;   // do not draw actor
            drawSingleCharacter(g, c, drawVisRect);
        }
        drawSingleCharacter(g, actorCharacter, drawVisRect);
    }

    public void drawItems(Graphics g, boolean drawVisRect) {
        // todo: skip non-visible items?
        Enumeration e = playfield.getItems().elements();
        while(e.hasMoreElements()) {
            Item it = (Item)e.nextElement();
            if (objectInsideView(it, 0)) {
                it.draw(g, geometry);
                if (drawVisRect) {
                    it.drawVisibilityRect(g, geometry, playfield.getNumCellsX(), playfield.getNumCellsY(), false);
                }
            }
        }

    }

    private void drawSingleCharacter(Graphics g, Character c, boolean drawVisRect) {
        int xStart = c.x - geometry.leftPosX;
        int yStart = c.y - geometry.topPosY;

        if(xStart + c.graphicsDimHalf() >= 0 && yStart >= 6 && xStart + c.graphicsDimHalf() < geometry.viewWidth && yStart + c.graphicsDimHalf() < geometry.viewHeight) {
            if (!c.previouslyInRange) {
                c.previouslyInRange = true;
                if (c.objectId != actorCharacter.objectId)
                    c.setHighPrioMessage(c.name, 4000);
            }
        } else {
            // c not visible
            c.previouslyInRange = false;
        }

        if (objectInsideView(c, 0)) {
            c.draw(g, geometry);
            if (drawVisRect) {
                c.drawVisibilityRect(g, geometry, playfield.getNumCellsX(), playfield.getNumCellsY(), false);
            }
        }

        c.checkDrawAttackAnimation(g, this, c.objectId == actorCharacter.objectId);
        //c.checkDrawIcon();
    }


    public void drawSelectedCharacterCursor(Graphics currentGraphics, int cursorType, int color) {
        if (selectedCharacter == null)
            return;

            currentGraphics.setColor(color);

            int info1Line_height = 8;

            int dim = (selectedCharacter.graphicsDim);

            int screenX = selectedCharacter.x - geometry.leftPosX + geometry.viewX;
            int screenY = selectedCharacter.y - geometry.topPosY + geometry.viewY;

            int d9 = 0;
            int d12 = 0;
            int d4 = 0;
            int d5 = 0;
            int d6 = 0;
            int d7 = 0;
            int d10 = 0;
            int d11 = 0;

            /*
            currentGraphics.setColor(0xff0000);
            currentGraphics.setClip(screenX, screenY, selectedCharacter.graphicsDim, selectedCharacter.graphicsDim);
            currentGraphics.drawRect(screenX, screenY, selectedCharacter.graphicsDim, selectedCharacter.graphicsDim);
            */

            if (screenY >= geometry.viewHeight + geometry.viewY - 2 || screenY + selectedCharacter.graphicsDim <= 7 + geometry.viewY
             || screenX >= geometry.viewWidth - 2 || screenX + (selectedCharacter.graphicsDim) <= 2)
            { // actorCharacter not fully visible on screen
                screenX+=(dim/2);
                screenY+=(dim/2);
                if (geometry.viewWidth-screenX < 9) {
                    screenX = geometry.viewWidth-9;
                    d9 = DirectionInfo.RIGHT; // direction: right

                } else if (screenX < 9) {
                    screenX=9;
                    d9 = DirectionInfo.LEFT; // direction: left
                }

                if (geometry.viewHeight + geometry.viewY - screenY < 9) {
                    screenY = geometry.viewHeight + geometry.viewY - 9;
                    d9 = DirectionInfo.DOWN; // direction: down  (overrides h-direction)
                } else if (screenY < 10 + geometry.viewY + info1Line_height) {
                    screenY= 10 + geometry.viewY + info1Line_height;
                    d9 = DirectionInfo.UP; // direction: up  (overrides h-direction)
                }

                //if (flash) {
                    GTools.drawArrow(currentGraphics, d9, screenX, screenY, 5, 0xFFCC00);
                //} else {
                 //   GTools.drawArrow(currentGraphics, d9, screenX, screenY, 5, 0xFFFFFF);
                //}
                return;
            } else {    // actorCharacter visible on screen

                if (cursorType == GlobalSettings.CHARACTER_CURSOR_TRIGGER_TARGET ) { // Trigger target find
                    d9 = 6;
                    d12 = 12;
                } else {
                    d9 = 3;
                    d12 = 6;
                }

                // xStart
                d4 = screenX-d9;
                if (d4 < 0) d4 = 0;

                // yStart
                d10 = screenY - d9;
                if (d10 < 0) d10 = 0;

                // width
                d5 = dim + d12;
                if (d4 + d5 > geometry.viewWidth) d5 = geometry.viewWidth - d4;

                // height
                d11 = dim + d12;

                if (d10 + d11 > geometry.viewHeight + geometry.viewY) d11 = geometry.viewHeight  + geometry.viewY - d10;
                currentGraphics.setClip(d4, d10, d5, d11);

                if (cursorType == GlobalSettings.CHARACTER_CURSOR_TRIGGER_TARGET) {  // trigger target find

                        boolean isInWeaponRange = true;
                        // todo: use other cursor type
                        /*
                        if (isInWeaponRange) { // range of spells is twice as far as default weapon range
                            currentGraphics.setColor(0,255,0);  // in range: green
                        } else {
                            currentGraphics.setColor(255,204,0);    // not in range: orange
                        }*/

                        // draw spell cursor
                        // outer strokes
                        currentGraphics.drawLine( screenX - 3, screenY - 3, screenX + 3, screenY - 6);  //topleft h
                        currentGraphics.drawLine(screenX + dim + 2, screenY - 3, screenX + dim - 4, screenY - 6); //topright h
                        currentGraphics.drawLine( screenX - 3, screenY - 3, screenX - 6, screenY + 3);  //topleft v
                        currentGraphics.drawLine( screenX + dim+2, screenY - 3, screenX + dim+5, screenY + 3);    //topright v
                        currentGraphics.drawLine( screenX - 3, screenY + dim + 2, screenX + 3, screenY + dim + 5);    //bottomleft h
                        currentGraphics.drawLine( screenX + dim + 2, screenY + dim + 2, screenX + dim - 4, screenY + dim + 5);  //bottomright h
                        currentGraphics.drawLine(screenX - 3, screenY + dim + 2, screenX - 6, screenY + dim - 4); //bottomleft v
                        currentGraphics.drawLine(screenX + dim+2, screenY + dim + 2, screenX + dim+5, screenY + dim - 4);   //bottomright v

                        // todo: use custom color as a paramter
                        int weaponRechargeTime = 0;

                        // inner strokes
                        // -- if (weaponRechargeStartTime > 0) {currentGraphics.setColor(128,192,128);}
                        currentGraphics.drawLine( screenX - 3, screenY - 2, screenX + 3, screenY - 5);  //topleft h
                        currentGraphics.drawLine(screenX + dim + 2, screenY - 2, screenX + dim - 4, screenY - 5); //topright h
                        currentGraphics.drawLine( screenX - 2, screenY - 3, screenX - 5, screenY + 3);  //topleft v
                        currentGraphics.drawLine( screenX + dim+1, screenY - 3, screenX + dim+4, screenY + 3);    //topright v
                        currentGraphics.drawLine( screenX - 3, screenY + dim + 1, screenX + 3, screenY + dim + 4);    //bottomleft h
                        currentGraphics.drawLine( screenX + dim + 2, screenY + dim + 1, screenX + dim - 4, screenY + dim + 4);  //bottomright h
                        currentGraphics.drawLine(screenX - 2, screenY + dim + 2, screenX - 5, screenY + dim - 4); //bottomleft v
                        currentGraphics.drawLine(screenX + dim+1, screenY + dim + 2, screenX + dim+4, screenY + dim - 4);   //bottomright v


                } else if (cursorType == GlobalSettings.CHARACTER_CURSOR_FIGHT) {   // fight find

                    d6 = screenX + dim/2;
                    d7 = screenY + dim/2;

                    // -- currentGraphics.setColor(255,255,255);

                    // top
                    currentGraphics.drawLine( d6 - 6, screenY - 3, d6 + 5, screenY - 3);
                    //currentGraphics.drawLine( d6 - 6, screenY - 2, d6 + 5, screenY - 2);

                    // left
                    currentGraphics.drawLine( screenX - 3, d7 - 6, screenX - 3, d7 + 5);
                    //currentGraphics.drawLine( screenX - 2, d7 - 6, screenX - 2, d7 + 5);

                    // right
                    currentGraphics.drawLine( screenX + dim + 2, d7 - 6, screenX + dim + 2, d7 + 5);
                    //currentGraphics.drawLine( screenX + dim + 1, d7 - 6, screenX + dim + 1, d7 + 5);

                    // bottom
                    currentGraphics.drawLine( d6 - 6, screenY + dim + 2, d6 + 5, screenY + dim + 2);
                    //currentGraphics.drawLine( d6 - 6, screenY + dim + 1, d6 + 5, screenY + dim + 1);

                    currentGraphics.drawRect(screenX-2, screenY-2, dim+3, dim+3);


                } else if (cursorType == GlobalSettings.CHARACTER_CURSOR_FIGHT_ACTIVE) {    // fight active
                    boolean db1 = false;

                    boolean isInWeaponRange = true; // isInWeaponRange(selectedCharacter, equipment[WEAPON],1)
                    // todo use color param

                    /*
                    if (isInWeaponRange) {
                        currentGraphics.setColor(0,255,0);
                        db1 = false;
                    } else {
                        currentGraphics.setColor(255,192,0);
                        db1 = true;
                    }*/

                    // outer strokes
                    currentGraphics.drawLine( screenX - 3, screenY - 3, screenX + 3, screenY - 3);  //topleft h
                    currentGraphics.drawLine( screenX + dim - 4, screenY - 3, screenX + dim + 2, screenY - 3);    //topright h
                    currentGraphics.drawLine( screenX - 3, screenY - 3, screenX - 3, screenY + 3);  //topleft v
                    currentGraphics.drawLine( screenX + dim+2, screenY - 3, screenX + dim+2, screenY + 3);    //topright v
                    currentGraphics.drawLine( screenX - 3, screenY + dim + 2, screenX + 3, screenY + dim + 2);    //bottomleft h
                    currentGraphics.drawLine( screenX + dim - 4, screenY + dim + 2, screenX + dim + 2, screenY + dim + 2);  //bottomright h
                    currentGraphics.drawLine( screenX - 3, screenY + dim - 4, screenX - 3, screenY + dim + 2);    //bottomleft v
                    currentGraphics.drawLine( screenX + dim+2, screenY + dim - 4, screenX + dim+2, screenY + dim + 2);  //bottomright v


                    int weaponRechargeTime =0;
                    // todo use color param
                    // inner strokes
                    if (weaponRechargeTime > 0) {currentGraphics.setColor(128,192,128);}
                    currentGraphics.drawLine( screenX - 2, screenY - 2, screenX + 3, screenY - 2);  //topleft h
                    currentGraphics.drawLine( screenX + dim - 4, screenY - 2, screenX + dim + 1, screenY - 2);    //topright h
                    currentGraphics.drawLine( screenX - 2, screenY - 2, screenX - 2, screenY + 3);  //topleft v
                    currentGraphics.drawLine( screenX + dim+1, screenY - 2, screenX + dim+1, screenY + 3);    //topright v
                    currentGraphics.drawLine( screenX - 2, screenY + dim + 1, screenX + 3, screenY + dim + 1);    //bottomleft h
                    currentGraphics.drawLine( screenX + dim - 4, screenY + dim + 1, screenX + dim + 1, screenY + dim + 1);  //bottomright h
                    currentGraphics.drawLine( screenX - 2, screenY + dim - 4, screenX - 2, screenY + dim + 1);    //bottomleft v
                    currentGraphics.drawLine( screenX + dim+1, screenY + dim - 4, screenX + dim+1, screenY + dim + 1);  //bottomright v

                    boolean isPeaceful = false; // isPeaceful(selectedCharacter.x + (dim/2), selectedCharacter.y + (dim/2))
                    // todo: use color param

                    if (db1 && isPeaceful) {
                            d6 = (selectedCharacter.graphicsDim)/2;
                            screenX = selectedCharacter.x + d6 - geometry.leftPosX - 5;
                            screenY = selectedCharacter.y - geometry.topPosY - 8  + geometry.viewY;
                            currentGraphics.setClip(screenX, screenY, 10, 6);
                            // todo pass custom gfx clip
                            /*
                            Image ingame = null;
                            currentGraphics.drawImage(ingame, screenX-32, screenY-23, Graphics.LEFT|Graphics.TOP); // peaceful icon
                             */

                    }

                } else {
                    /*
                    if (cursorType == GlobalSettings.CHARACTER_CURSOR_DEFAULT) {  // SUBSTATE_FRIEND_FIND
                        currentGraphics.setColor(255,255,255);
                    } else {
                        currentGraphics.setColor(208,255,255);
                    }*/
                    currentGraphics.drawRect(screenX-2, screenY-2, dim+3, dim+3);
                    //currentGraphics.setColor(192,192,192);
                    currentGraphics.drawRect(screenX-3, screenY-3, dim+5, dim+5);
                }
            }
    }

    public void onCharacterKilled(int targetId) {
        Character target = playfield.getCharacter(targetId);
        if (target!=null) {
            deadCharacters.addElement(new DeadCharacter(target));
        }
    }

    public void onHitCharacter(int attackerId, int targetId) {
        Character target = playfield.getCharacter(targetId);
        if (target!=null) {
            target.flash(6,200);
            if (target.objectId != actorCharacter.objectId) {
                target.HealthBarDisplay.setOnForDuration(true, 1400);
            }
        }
    }

    public void onHitMissCharacter(int attackerId, int targetId) {
        Character target = playfield.getCharacter(targetId);
        if (target!=null) {
            GImageClip defendIcon = GlobalResources.getImageClip(GlobalResources.SPRITE_ICON_DEFEND);
            defendIcon.flash(4, 200);
            defendIcon.Enabled.setOnForDuration(true, 800);
            target.setIcon(defendIcon);
        }
    }


    public void onAttackCharacter(Character attacker, int targetId) {
        if (attacker != null) {
            attacker.lastAttackTime = System.currentTimeMillis();
            attacker.lastAttackedCharacterId = targetId;
            attacker.attackAnimate = 2;

            if (attacker.objectId != actorCharacter.objectId) {
                GImageClip attackIcon = GlobalResources.getImageClip(GlobalResources.SPRITE_ICON_ATTACK);
                attackIcon.flash(4, 200);
                attackIcon.Enabled.setOnForDuration(true, 800);
                attacker.setIcon(attackIcon);
            }
        }
    }

    public void onAttackCharacter(int attackerId, int targetId) {
        Character attacker = playfield.getCharacter(attackerId);
        onAttackCharacter(attacker, targetId);
    }

    private Character getClosestCharacter(boolean excludeActor) {
        Hashtable chars = playfield.getCharacters();
        Enumeration e = chars.elements();
        Character closestCharacter = null;
        int minDist2 = 0;
        while (e.hasMoreElements()) {
            Character c = (Character)e.nextElement();
            if (excludeActor && c.objectId == actorCharacter.objectId)
                continue;
            int xDist2 = (actorPosX - c.x); xDist2 *= xDist2;
            int yDist2 = (actorPosY - c.y); yDist2 *= yDist2;

            int dist2 = xDist2 + yDist2;

            if (dist2 < minDist2 || closestCharacter == null) {
                minDist2 = dist2;
                closestCharacter = c;
            }
        }
        return closestCharacter;
    }

    public Character selectClosestCharacter(boolean excludeActor) {
        Character c = getClosestCharacter(excludeActor);
        if (c!=null && objectCenterInsideView(c, 0) && setSelectedCharacter(c.objectId)) {
            return c;
        }
        return null;
    }


    public boolean setSelectedCharacter(int objectId) {
        Character c = playfield.getCharacter(objectId);
        if (c!=null) {
            selectedCharacter = c;
            return true;
        }
        return false;
    }



    public Character setSelectedCharacterNextDir(int dirInfo, boolean excludeActor) {
        int tolerancePx = 12;

        Character curSelCharacter = getSelectedCharacter();
        if (curSelCharacter==null) {
            curSelCharacter = getClosestCharacter(excludeActor);
            if (curSelCharacter!=null && objectCenterInsideView(curSelCharacter, tolerancePx) && setSelectedCharacter(curSelCharacter.objectId)) {
                return getSelectedCharacter();
            }
            return null;
        }

        int startX = geometry.leftPosX-tolerancePx;
        int startY = geometry.topPosY-tolerancePx;
        int endX = geometry.leftPosX + geometry.viewWidth + tolerancePx;
        int endY = geometry.topPosY + geometry.viewHeight + tolerancePx;
        int halfDim = curSelCharacter.graphicsDim>>1;

        int cx = curSelCharacter.x + halfDim;
        int cy = curSelCharacter.y + halfDim;
        if (cx < startX || cx > endX || cy < startY || cy > endY) {
            // not in range
            return null;
        }

        Character newSelCharacter = null;

        Hashtable allChars = playfield.getCharacters();
        Enumeration e = allChars.elements();

        Character closestLeft = null;
        Character farthestLeft = null;
        Character closestRight = null;
        Character farthestRight = null;
        Character closestUp = null;
        Character farthestUp = null;
        Character closestDown = null;
        Character farthestDown = null;
        int minLeft = startX-1;
        int minRight = endX+1;
        int minUp = startY-1;
        int minDown = endY+1;
        int maxLeft = cx;
        int maxRight = cx;
        int maxUp = cy;
        int maxDown = cy;

        while (e.hasMoreElements()) {
            Character otherChar = (Character)e.nextElement();
            if (otherChar.objectId == curSelCharacter.objectId || (excludeActor && otherChar.objectId == actorCharacter.objectId))
                continue;

            int otherX = otherChar.x + halfDim;
            int otherY = otherChar.y + halfDim;

            if (otherX < startX || otherX > endX || otherY < startY || otherY > endY) {
                // already selected characer or not in range, skip
                continue;
            }

            if ((otherX < cx || (otherX == cx && otherChar.objectId < curSelCharacter.objectId)) && (otherX > minLeft || (otherX == minLeft && (closestLeft == null || otherChar.objectId > closestLeft.objectId)))) {
                closestLeft = otherChar;
                minLeft = otherX;
            } else if ((otherX > cx || (otherX == cx && otherChar.objectId > curSelCharacter.objectId)) && (otherX < minRight || (otherX == minRight && (closestRight == null || otherChar.objectId < closestRight.objectId)))) {
                closestRight = otherChar;
                minRight = otherX;
            }

            if (otherX < maxLeft || (otherX == maxLeft && (farthestLeft == null || otherChar.objectId > farthestLeft.objectId))) {
                farthestLeft = otherChar;
                maxLeft = otherX;
            }
            if (otherX > maxRight || (otherX == maxRight && (farthestRight == null || otherChar.objectId < farthestRight.objectId))) {
                farthestRight = otherChar;
                maxRight = otherX;
            }

            if ((otherY < cy || (otherY == cy && otherChar.objectId < curSelCharacter.objectId)) && (otherY > minUp || (otherY == minUp && (closestUp == null || otherChar.objectId > closestUp.objectId)))) {
                closestUp = otherChar;
                minUp = otherY;
            } else if ((otherY > cy || (otherY == cy && otherChar.objectId > curSelCharacter.objectId)) && (otherY < minDown || (otherY == minDown && (closestDown == null || otherChar.objectId < closestDown.objectId)))) {
                closestDown = otherChar;
                minDown = otherY;
            }

            if (otherY < maxUp || (otherY == maxUp && (farthestUp == null || otherChar.objectId > farthestUp.objectId))) {
                farthestUp = otherChar;
                maxUp = otherY;
            }
            if (otherY > maxDown || (otherY == maxDown && (farthestDown == null || otherChar.objectId < farthestDown.objectId))) {
                farthestDown = otherChar;
                maxDown = otherY;
            }

        }

        if (closestLeft == null) {closestLeft = farthestRight;}
        if (closestRight == null) {closestRight = farthestLeft;}
        if (closestUp == null) {closestUp = farthestDown;}
        if (closestDown == null) {closestDown = farthestUp;}

        switch (dirInfo) {
            case DirectionInfo.LEFT: newSelCharacter = closestLeft; break;
            case DirectionInfo.RIGHT: newSelCharacter = closestRight; break;
            case DirectionInfo.UP: newSelCharacter = closestUp; break;
            case DirectionInfo.DOWN: newSelCharacter = closestDown; break;
        }

        if (newSelCharacter!=null && setSelectedCharacter(newSelCharacter.objectId)) {
            return newSelCharacter;
        } else {
            return curSelCharacter;
        }
    }

    public Character deselectSelectedCharacter() {
        Character c = selectedCharacter;
        if (selectedCharacter!=null) {
            selectedCharacter = null;
        }
        return c;
    }

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public int getSelectedCharacterId() {
        return selectedCharacter != null ? selectedCharacter.objectId : 0;
    }




    public void onCharacterRemoved(int id) {
        if (selectedCharacter!=null && selectedCharacter.objectId == id) {
            deselectSelectedCharacter();
        }
    }

    public boolean objectInsideView(WorldObject wo, int tolerancePx) {
        // any of the four corner points visible
        return 
                pointInsideView(wo.x, wo.y, tolerancePx) ||
                pointInsideView(wo.x + wo.graphicsDim, wo.y, tolerancePx) ||
                pointInsideView(wo.x, wo.y + wo.graphicsDim, tolerancePx) ||
                pointInsideView(wo.x + wo.graphicsDim, wo.y + wo.graphicsDim, tolerancePx);
    }

    private boolean pointInsideView(int x, int y, int tolerancePx) {
        int startX = geometry.leftPosX-tolerancePx;
        int startY = geometry.topPosY-tolerancePx;
        int endX = geometry.leftPosX + geometry.viewWidth + tolerancePx;
        int endY = geometry.topPosY + geometry.viewHeight + tolerancePx;
        return (x >= startX && x <= endX && y >= startY && y <= endY);
    }

    private boolean objectCenterInsideView(WorldObject wo, int tolerancePx) {
        return pointInsideView(wo.xCenter(), wo.yCenter(), tolerancePx);
    }

    public boolean selectedCharacterInsideView(int tolerancePx) {
        Character c = getSelectedCharacter();
        if (c!=null) {
            return objectInsideView(c, tolerancePx);
        }
        return false;
    }

    public boolean attackPossible(Character other, boolean includeRechargeCheck) {
        boolean possible =
                other != null
                && (!includeRechargeCheck || !actorCharacter.isRechargingForAttack())
                && actorCharacter.isInWeaponRange(other)
                && !playfield.isBlockedLine(actorCharacter.xCenter(), actorCharacter.yCenter(), other.xCenter(), other.yCenter())
                && !characterIsPeaceful(actorCharacter)
                && !characterIsPeaceful(other);

                /*
                if (!possible) {
                    System.out.println("not possible: " + actorCharacter.isRechargingForAttack() + ", " + actorCharacter.isInWeaponRange(other) + ", " + playfield.isBlockedLine(actorCharacter.xCenter(), actorCharacter.yCenter(), other.xCenter(), other.yCenter()) + ", "  + playfield.isBlockedLine(actorCharacter.xCenter(), actorCharacter.yCenter(), other.xCenter(), other.yCenter()) + ", " + characterIsPeaceful(actorCharacter) + ", " + characterIsPeaceful(other));
                }*/

                return possible;
    }

    public boolean characterIsPeaceful(Character c) {
        return playfield.hasFunctionAt(PlayfieldCell.function_peaceful, c.xCenter(), c.yCenter());
    }
}
