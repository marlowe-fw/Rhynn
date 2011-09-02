package rhynn;



import graphics.GFont;
import javax.microedition.lcdui.Image;
import graphics.GImageClip;
import javax.microedition.lcdui.Graphics;
import net.NetTools;
import FWUtils.SwitchSetting;

/*
 * Character.java
 *
 * Created on 27. November 2003, 12:31
 */

/**
 *
 * @author marlowe
 */
public class Character extends WorldObject /*implements InventoryObserver*/ {



    public final static int MAX_HITSHOWDURATION = 1800;
    public final static int MAX_HITSHOWDURATION_LOCAL = 1500;
    public final static int MAX_ATTACKSHOWDURATION = 450;
    public final static long MAX_DEADSHOWDURATION = 10000;



    public static long MOVE_ANIMATION_INTERVAL = 100;
    public static int ITEM_PICKUP_RADIUS = 32;

    public Inventory inventory;

    private String publicChatMessage;
    private String highPrioMessage;
    private long publicChatMessageTimeout;
    private long highPrioMessageTimeout;

    public int clanId;
    public String customMessage;
    public int curHealth;
    public int curMana;
    public int healthEffectsExtra;
    public int manaEffectsExtra;
    public int attackEffectsExtra;
    public int defenseEffectsExtra;
    public int damageEffectsExtra;
    public int skillEffectsExtra;
    public int magicEffectsExtra;
    public int healthregenerateEffectsExtra;
    public int manaregenerateEffectsExtra;

    public int healthModifier;
    public int manaModifier;
    public int attackModifier;
    public int defenseModifier;
    public int damageModifier;
    public int skillModifier;
    public int magicModifier;

    /** Level of the character. */
    public int level;

    /** Points available to add to the attributes of this character. */
    public int levelpoints;
    
    /** Experience. */
    public int experience;
    
    // =====================
    /// Additional info
    // =====================
        
    public int triggerx;
    
    public int triggery;
    
    public int attackAnimate;
    
    // Magic spell visual info
    public byte magicAniPhase;
    public byte magicAniPos;
    public long magicAniLastCycle;
    
    public long spellVisualsEndTime;
    public byte spellVisualsColorType;
    public long spellVisualsEndTime1;
    public byte spellVisualsColorType1;
    
    public boolean previouslyInRange;

    private long weaponRechargeFullDuration = 0;
    private long weaponRechargeEndTime = 0;

    public long lastAttackTime = 0;
    public int lastAttackedCharacterId = 0;

    public SwitchSetting HealthBarDisplay = new SwitchSetting(false);

    private GImageClip icon = null;


    public int getTotalMaxHealth() {return healthBase + healthEffectsExtra;}
    public int getTotalMaxMana() {return manaBase + manaEffectsExtra;}
    public int getTotalAttack() {return attackBase + attackEffectsExtra;}
    public int getTotalDefense() {return defenseBase + defenseEffectsExtra;}
    public int getTotalDamage() {return damageBase + damageEffectsExtra;}
    public int getTotalSkill() {return skillBase + skillEffectsExtra;}
    public int getTotalMagic() {return magicBase + magicEffectsExtra;}
    public int getTotalHealthregenerate() {return healthregenerateBase + healthregenerateEffectsExtra;}
    public int getTotalManaregenerate() {return manaregenerateBase + manaregenerateEffectsExtra;}


    public Character() {
        inventory = new Inventory(this, Inventory.DEFAULT_NUM_SLOTS, Inventory.DEFAULT_SLOTS_PER_ROW);
        previouslyInRange = false;
    }

    public void flash(int iterations, long singleFlashDuration) {
        curImageClip.flash(iterations, singleFlashDuration);
    }

    public void setIcon(GImageClip imgClip) {
        icon = imgClip;
    }

    public boolean isInWeaponRange(Character fwgo) {
        int xOwn, yOwn, xOther, yOther;
        boolean inRange = false;

        int width_self = graphicsDim;
        int height_self = graphicsDim;

        int overSize_H_other = (((fwgo.graphicsDim) - width_self) >> 1);
        int overSize_V_other = (((fwgo.graphicsDim) - height_self) >> 1);


        xOwn = xCenter();
        yOwn = yCenter();
        xOther = fwgo.xCenter();
        yOther = fwgo.yCenter();

        int xDistance = xOwn - xOther;
        int yDistance = yOwn - yOther;

        int xDistanceOS = xDistance;
        int yDistanceOS = yDistance;

        // take into account reduced distance if monster is bigger than average
        if (xDistance > 0) {
            xDistanceOS -= overSize_H_other;
            if (xDistanceOS < 0) xDistanceOS = 0;
        } else {
            xDistanceOS += overSize_H_other;
            if (xDistanceOS > 0) xDistanceOS = 0;
        }

        if (yDistance > 0) {
            yDistanceOS -= overSize_V_other;
            if (yDistanceOS < 0) yDistanceOS = 0;
        } else {
            yDistanceOS += overSize_V_other;
            if (yDistanceOS > 0) yDistanceOS = 0;
        }

        int factor = 1;
        // do range / peaceful check
        if (factor >= 2) {
            factor = (factor * 26) * (factor * 26);
        }

        Item weapon = inventory.getEquippedItem(Equipment.ET_WEAPON_1);
        
        int useRange = 0;

        if (weapon!=null) {
            int plainRange = weapon.range / 10; // actual value is times 10
            useRange = GlobalSettings.DEFAULT_WEAPON_RANGE + (plainRange * plainRange) - plainRange + 2;
        } else {
            useRange = GlobalSettings.DEFAULT_WEAPON_RANGE;
        }

            inRange =  (fwgo!=null &&
                    xDistanceOS * xDistanceOS
                  + yDistanceOS * yDistanceOS
                  <= useRange*useRange + factor /*&&
                  !isPeaceful(xOther, yOther) && !isPeaceful(xOwn, yOwn)*/);


        // if object is in range and neither player nor target is on a peaceful
        // cell (AND player is not using a long range weapon, make sure a direct)
        // AND player is not using a spell
        // line from player to target does not collide with any blocking field
        //if (true) {
            //((inRange && weapon==null) || (inRange && weapon!=null && weapon.classId!=3)) {


        //}

        return inRange;
    }

    
    private long getMaxRechargeTimeForAttack() {
        Item weapon = inventory.getEquippedItem(Equipment.ET_WEAPON_1);
        if (weapon != null) {
            return 5000 - ((weapon.frequency/10)*300);
        }
        return GlobalSettings.DEFAULT_RECHARGE_TIME_NO_WEAPON;
    }

    public void startRechargeForAttack() {
        long curTime = System.currentTimeMillis();
        weaponRechargeFullDuration = getMaxRechargeTimeForAttack();
        weaponRechargeEndTime = curTime + weaponRechargeFullDuration;
    }

    boolean isRechargingForAttack() {
        return System.currentTimeMillis() < weaponRechargeEndTime;
    }

    void cancelRechargeForAttack() {
        if (isRechargingForAttack()) {
            weaponRechargeEndTime = 0;
        }
    }

    public void drawRechargeForAttack(Graphics currentGraphics, int xPos, int yPos, int width, int height, boolean active) {
        currentGraphics.setClip(xPos, yPos, width, height);
        //background
        if (active) {
            currentGraphics.setColor(0,255,0);
        } else {
            currentGraphics.setColor(255,192,0);
        }
        currentGraphics.fillRect(xPos+1, yPos+1, width-2, height-2);
        if (isRechargingForAttack()) {
            long remainder = weaponRechargeEndTime - System.currentTimeMillis();
            int blackFillHeight = (int) ((remainder * (height-2)) / weaponRechargeFullDuration);
            //negative gauge
            currentGraphics.setColor(0,0,0);
            currentGraphics.fillRect(xPos+1, yPos+1, width-2, blackFillHeight);
            currentGraphics.setColor(32,64,0);
        } else {
            currentGraphics.setColor(0,80,0);
        }
        currentGraphics.drawRect(xPos, yPos, width-1, height-1);
        
    }

    public void setPublicChatMessage(String msg, long timeout) {
        publicChatMessage = readjustMsg(msg);
        //publicChatMessage = msg;
        publicChatMessageTimeout = System.currentTimeMillis() + timeout;
    }

    public void setHighPrioMessage(String msg, long timeout) {
        highPrioMessage = readjustMsg(msg);
        //publicChatMessage = msg;
        highPrioMessageTimeout = System.currentTimeMillis() + timeout;
    }

    public void onHit(boolean triggerSimpleAnimation) {
        extraIconShowDuration = MAX_HITSHOWDURATION / 2;
        extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
        extraFlashPhase = true;
        //extraicon = SPRITE_ICON_ATTACK;
        if (triggerSimpleAnimation) {
            // enemey, show simple attackBase animation
            attackAnimate = 2;
        }
    
    }


    public boolean checkDisplayMessage(Graphics currentGraphics, ViewGeometry viewGeometry) {
        long curTime = System.currentTimeMillis();

        // todo: display temp name msg

        if (curTime < publicChatMessageTimeout && publicChatMessage != null) {
            drawMessage(currentGraphics, viewGeometry, publicChatMessage, true);
            return true;
        }

        if (curTime < highPrioMessageTimeout && highPrioMessage != null) {
            drawMessage(currentGraphics, viewGeometry, highPrioMessage, false);
            return true;
        }

        // todo: status msg

        return false;
    }


    public void draw(Graphics g, ViewGeometry viewGeometry) {
        super.draw(g, viewGeometry);
        checkDisplayIcon(g, viewGeometry);
        checkDisplayMessage(g, viewGeometry);

        if (HealthBarDisplay.isOn() && !curImageClip.isFlashing()) {
            drawHealth(g, viewGeometry);
        }
    }

    private void checkDisplayIcon(Graphics g, ViewGeometry geometry) {
        if (icon == null || !icon.Enabled.isOn())
            return;
        
        int drawX = geometry.viewX + x - geometry.leftPosX;
        int drawY = geometry.viewY + y - geometry.topPosY;
        drawX += (graphicsDim / 2) - (icon.getWidth() / 2);
        drawY -= icon.getHeight() + 2;
        if (HealthBarDisplay.isOn())
            drawY -= 4;
        
        icon.draw(g, drawX, drawY);
    }

    private void drawHealth(Graphics currentGraphics, ViewGeometry viewGeometry) {
        int height = 2;
        int width = graphicsDim - 8;

        int drawX = viewGeometry.viewX + x-viewGeometry.leftPosX + 4;
        int drawY = viewGeometry.viewY + y-viewGeometry.topPosY - (height + 2);

        currentGraphics.setClip(drawX, drawY, width, height);
        if (curHealth < getTotalMaxHealth()) {
            // black background fill
            currentGraphics.setColor(0,0,0);
            currentGraphics.fillRect(drawX, drawY, width, height);
        }
        currentGraphics.setColor(255,0,0);
        currentGraphics.fillRect(drawX, drawY, (curHealth*width)/getTotalMaxHealth(), height);
    }

    // todo: this method should know nothing about display name or not
    private void drawMessage(Graphics currentGraphics, ViewGeometry viewGeometry, String message, boolean displayName) {
        // note below algorithm is ripped from existing code, it works well but can likely be optimized
        GFont font = GlobalSettings.getFont();

        int msgLength = message.length();

        int curLineFill = 0; // current linefill
        int maxLineLen = 0; // maximum line length
        // find the maximum line length for this text to allow decent centering
        for(int d=0; d<msgLength; d++) {
            curLineFill++;
            if (d > 0 &&  (curLineFill==17 || message.charAt(d)=='\n')) {  //line break
                if (message.charAt(d)=='\n') {
                    curLineFill--;
                }
                if (curLineFill > maxLineLen) {
                    maxLineLen = curLineFill;
                }
                curLineFill = 0; // reset linefill
            }
        }
        if (maxLineLen == 0) {
            if (displayName && name.length()+1 > msgLength) {
                maxLineLen =  name.length()+1;
            } else {
                maxLineLen =  msgLength;
            }
        }

        int halfDim = graphicsDim >> 1;

        //get rel. position to upper left screen corner
        int charScreenX = (x - viewGeometry.leftPosX) + halfDim + viewGeometry.viewX;
        int charScreenY = (y - viewGeometry.topPosY) + halfDim + viewGeometry.viewY;

        int textYPos = y - (font.charHeight<<1);    //y pos of text

        if (charScreenX < viewGeometry.viewX  || charScreenY < viewGeometry.viewY || charScreenX > viewGeometry.viewX + viewGeometry.viewWidth || charScreenY > viewGeometry.viewY + viewGeometry.viewHeight || textYPos < 1) {
            return; //object not visible to at least half of its extends
        }
        int wHalf = (maxLineLen*font.charWidth)>>1;   //half width in pixels of the text
        int textXPos = charScreenX - wHalf;     //x pos of text

        //adjust textpos so msg is fully visible
        int textXPosEnd = charScreenX + wHalf;  //text endpos
        int viewXPos = viewGeometry.viewX;
        int viewXPosEnd = viewXPos + viewGeometry.viewWidth;

        if (textXPosEnd > viewXPosEnd)  { //right overlap
            textXPos -= (textXPosEnd-viewXPosEnd);
        } else if(textXPos < viewXPos) { //left overlap
            textXPos += viewXPos-textXPos;
        }

        //display the message
        int d6 = (textYPos - viewGeometry.topPosY) + viewGeometry.viewY;
        if (displayName) {
                font.drawString(currentGraphics, name + ":", textXPos, d6-font.charHeight-3);
        }

        int curTextXPos = textXPos;
        curLineFill = 0; // current linefill
        for(int d=0; d<msgLength; d++) {
            curLineFill++;
            if (d > 0 &&  (curLineFill==17 || message.charAt(d-1)=='\n')) {  //line break
                d6 += font.charHeight;
                curTextXPos=textXPos;   // reset leftPosX
                curLineFill = 0; // reset linefill
            }
            font.drawChar(currentGraphics, message.charAt(d), curTextXPos, d6);
            curTextXPos+=font.charWidth;
        }


        // todo: status msg
    }

    private String readjustMsg(String msg) {
        char[] chars = msg.toCharArray();
        
        int k = 0; // lineFill
        int m = -1; //lastSpaceInLine
        for(int n=0; n<chars.length; n++) {
            k++; //lineFill++
            if (chars[n]==' ') {
                m = k; // remember last space in current line (measured in line fill, starting from 1)
            }
            if (k==16 || chars[n]=='\n') {    // end of line encountered
                if (   chars[n]!=' ' && chars[n]!='\n' && chars.length > n+1
                    && chars[n+1]!= ' ' && chars[n+1]!= '\n' && m > 0) { // word would be wrapped inside, and space exists in line
                    k = k - m; // lineFill = lineFill - lastSpaceInLine -> transferred to next line
                    chars[n - k] = '\n';  // force linebreak at the last remembered space position
                } else {
                    k = 0; // normal line break: reset linefill
                }
                m = -1; // reset lastSpaceInLine
            }
        }

        return new String(chars);
    }


    public void onEquipmentChanged() {
        // re-calculate equipment effects on the attributes
        healthEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_HEALTH);
        manaEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_MANA);
        attackEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_ATTACK);
        defenseEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_DEFENSE);
        damageEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_DAMAGE);
        skillEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_SKILL);
        magicEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_MAGIC);
        healthregenerateEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_HEALTHREG);
        manaregenerateEffectsExtra = inventory.getEffects(Item.ATTR_TYPE_MANAREG);
    }

    public boolean meetsItemRequirements(Item it) {
        return getTotalSkill() >= it.requiredSkill && getTotalMagic() >= it.requiredMagic;
    }

    public void useImage(Image img) {
        curImageClip = new GImageClip(img, graphicsX, graphicsY, graphicsDim*8, graphicsDim);
        curImageClip.setNumFrames(8);
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


    public void checkAnimate(long gameTime) {
        if (gameTime - lastAnimationChange < MOVE_ANIMATION_INTERVAL)
            return;
        else
            animate(gameTime);
    }

    public void animate(long gameTime) {
        int base = animationFrameFromDirection();
        int animation = curImageClip.getCurrentFrame();

        if (animation < base) animation = base;
        else {
            animation++;
            if (animation > base + 1) animation = base;
        }
        curImageClip.setCurrentFrame(animation);
        
        if (gameTime > 0) {
            lastAnimationChange = gameTime;
        }
    }


    public void checkDrawAttackAnimation(Graphics currentGraphics, PlayfieldActorView view, boolean showLocal) {
        Character target = null;

        int playfieldPosLeft = view.getPlayfieldPosLeft();
        int playfieldPosTop = view.getPlayfieldPosTop();
        int viewX = view.getViewX();
        int viewY = view.getViewY();
        int viewWidth = view.getViewWidth();
        int viewHeight = view.getViewHeight();

        long curTime = System.currentTimeMillis();
        long remainingAttackShowTime = 0;
        long remainingAttackTotalTime = 0;
        if (lastAttackedCharacterId > 0) {
            target = view.getCharacter(lastAttackedCharacterId);
            remainingAttackShowTime = lastAttackTime + Character.MAX_ATTACKSHOWDURATION - curTime;
            remainingAttackTotalTime = lastAttackTime + Character.MAX_HITSHOWDURATION_LOCAL - curTime;
            if (remainingAttackTotalTime <= 0) {
                lastAttackedCharacterId = 0;
                return;
            }
        } else {
            return;
        }

        if (showLocal && target != null) {
            if (remainingAttackShowTime > 0) {

                // draw projectile, direct line
                currentGraphics.setColor(255, 32, 0);
                int d1 = target.x - playfieldPosLeft + viewX + target.graphicsDimHalf();
                int d2 = target.y - playfieldPosTop + viewY + target.graphicsDimHalf();

                int d3 = x - playfieldPosLeft + viewX + graphicsDimHalf();
                int d4 = y - playfieldPosTop + viewY + graphicsDimHalf();

                // get endpoints of a line directly connecting the object with the player

                // xDistance
                int d9 = d1 - d3;
                // yDistance
                int d10 = d2 - d4;

                currentGraphics.setClip(viewX, viewY, viewWidth, viewHeight);
                //currentGraphics.setClip(d1, d2, d9, d10);
                //currentGraphics.drawLine(d1, d2, d3, d4);

                long indicator = (long)((remainingAttackShowTime*Character.MAX_ATTACKSHOWDURATION) / Character.MAX_ATTACKSHOWDURATION);

                int d11 = (int)((indicator * d9) / Character.MAX_ATTACKSHOWDURATION);
                int d12 = (int)((indicator * d10) / Character.MAX_ATTACKSHOWDURATION);

                d11 = d1 - d11;
                d12 = d2 - d12;

                currentGraphics.fillRect(d11-1, d12-1, 3, 3);
                currentGraphics.setColor(255, 255, 255);
                currentGraphics.fillRect(d11, d12, 1, 1);
            }

            // local red hit rectangle on target
            if (remainingAttackTotalTime < 1100 && remainingAttackTotalTime > 390 && (remainingAttackTotalTime / 130) % 2 == 0) {
                currentGraphics.setColor(255,0,0);
                int d1 = target.x -  playfieldPosLeft - 1 + viewX;
                int d2 = target.y - playfieldPosTop - 1 + viewY;
                int d0 = (target.graphicsDim)+1;
                currentGraphics.setClip(d1, d2, d0+1, d0+1);
                currentGraphics.drawRect(d1, d2, d0, d0);
            }

        }


        // make the attack step and draw slice
        int d5 = x - playfieldPosLeft;
        int d6 = y - playfieldPosTop + 8;

        if (attackAnimate > 0) {
            if (attackAnimate == 2) {
                animate(0);
                attackAnimate--;

                currentGraphics.setClip(d5, d6, 18, 5);
                if (direction == DirectionInfo.UP || direction == DirectionInfo.LEFT) {
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5-5, d6-29, Graphics.LEFT | Graphics.TOP);    // white attackBase flash
                } else {
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5-23, d6-29, Graphics.LEFT | Graphics.TOP); // white attackBase flash
                }

            } else if (remainingAttackShowTime < Character.MAX_ATTACKSHOWDURATION - 50) {
                animate(0);
                attackAnimate--;
            }
        }


    }



    /*
    private int checkDisplayIconForObject(Character c, boolean allowDrawHealth) {

        if ((currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) && bDrawHealthAll) {
            if (allowDrawHealth) {
                drawHealthManaState(c, curGametime, 4);
            }
        } else {
            bDrawHealthAll = false;
        }

        boolean bAllowIconAttack = true;


        c.attackShowDuration = decreasedTime(c.attackShowDuration, 0);

        if (c.icon == ICON_HIT) {
            c.hitDisplayDelay =  decreasedTime(c.hitDisplayDelay, 0);
        } else {
            c.hitDisplayDelay = 0;
        }

        c.extraIconShowDuration = decreasedTime(c.extraIconShowDuration, 0);

        if (c.hitDisplayDelay <= 0) { c.hitShowDuration = decreasedTime(c.hitShowDuration, 0); }

        // ATTACK
        if (c.extraIconShowDuration > 0 || c.extraIconShowDuration == -1) {
            // decrease flashphase time
            c.extraFlashPhaseDuration = decreasedTime(c.extraFlashPhaseDuration, 0);
            if (c.extraFlashPhaseDuration == 0) {
                c.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                c.extraFlashPhase = !c.extraFlashPhase;
            }

            // check extra fight animation for any object but player object
            if (c.attackAnimate > 0  && c.objectId != playerObject.objectId) {
                if (c.attackAnimate == 2) {
                    // switch move ani
                    c.animation = (byte)((c.animation+1) % 2);
                    c.attackAnimate--;
                    if (c.classId == 0) {
                        // for players show also attackBase hit 'stripe'
                        d6 = (c.graphicsDim * DIM)>>1;
                        d7 = c.x + d6 - xPos - 9;
                        d8 = c.y - yPos + 8;
                        bAllowIconAttack = false;
                        currentGraphics.setClip(d7, d8, 18, 5);
                        if (c.direction == DirectionInfo.UP || c.direction == DirectionInfo.LEFT) {
                            currentGraphics.drawImage(ingame, d7-5, d8-29, anchorTopLeft);    // white attackBase slice
                        } else {
                            currentGraphics.drawImage(ingame, d7-23, d8-29, anchorTopLeft); // white attackBase slice
                        }
                    }
                } else if (c.extraIconShowDuration < 1600) {
                    c.animation = (byte)((c.animation+1) % 2);
                    c.attackAnimate--;
                }
            }


            if (c.extraFlashPhase) {
                if (c.extraicon >= ICON_ATTACK_SPELL1 && bAllowIconAttack) {
                    d6 = (c.graphicsDim * DIM)>>1;
                    d7 = (14 + 9 * (c.extraicon-ICON_ATTACK_SPELL1));
                    d1 = c.x + d6 - xPos - 4;
                    d2 = c.y - yPos - 19  + TOP_INFOHEIGHT;
                    currentGraphics.setClip(d1, d2, 9, 12);
                    currentGraphics.drawImage(ingame, d1-d7, d2-16, anchorTopLeft); // spell visuals at attacker
                } else {
                    switch (c.extraicon) {
                        case SPRITE_ICON_ATTACK:
                            if (bAllowIconAttack) {
                                //d6 = (c.graphicsDim * DIM)/2;
                                d6 = (c.graphicsDim * DIM)>>1;
                                d1 = c.x + d6 - xPos - 4;
                                d2 = c.y - yPos - 12  + TOP_INFOHEIGHT;
                                currentGraphics.setClip(d1, d2, 9, 7);
                                currentGraphics.drawImage(ingame, d1-32, d2-16, anchorTopLeft); // attackBase icon
                            }
                            break;
                    }
               }
            }

        }

        // DEFEND
        if (c.hitShowDuration > 0 || c.hitShowDuration == -1) {
            // decrease flashphase time
            c.flashPhaseDuration = decreasedTime(c.flashPhaseDuration, 0);
            if (c.flashPhaseDuration == 0) {
                c.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                c.flashPhase = !c.flashPhase;
            }

            if (c.flashPhase) {
                switch (c.icon) {
                    case ICON_HIT:
                        if (c.hitDisplayDelay <= 0 && !bDrawHealthAll) {
                            drawHealthManaState(c, curGametime, 2);
                            //c.attackShowDuration = 0;
                        }
                        break;
                    case ICON_HITLOCAL:
                    case ICON_BONUSLOCAL:
                        if (c.icon == ICON_BONUSLOCAL) {
                            currentGraphics.setColor(0,255,0);
                        } else {
                            currentGraphics.setColor(255,0,0);
                        }
                        d1 = c.x - xPos - 1;
                        d2 = c.y - yPos - 1 + TOP_INFOHEIGHT;
                        d6 = (c.graphicsDim * DIM)+1;
                        currentGraphics.setClip(d1, d2, d6+1, d6+1);
                        currentGraphics.drawRect(d1, d2, d6, d6);

                        break;
                    case SPRITE_ICON_DEFEND:
                        if (!(c.extraIconShowDuration > 0 && c.extraicon==SPRITE_ICON_ATTACK)) {
                            //d6 = (c.graphicsDim * DIM)/2;
                            d6 = (c.graphicsDim * DIM)>>1;
                            d1 = c.x + d6 - xPos - 4;
                            d2 = c.y - yPos - 12  + TOP_INFOHEIGHT;
                            currentGraphics.setClip(d1, d2, 9, 7);
                            currentGraphics.drawImage(ingame, d1-32, d2, anchorTopLeft);    // defend icon
                            //c.attackShowDuration = 0;
                        }
                        break;
                    case ICON_DEAD:
                        //d6 = (c.graphicsDim * DIM)/2;
                        d6 = (c.graphicsDim * DIM)>>1;
                        d1 = c.x + d6 - xPos - 5;
                        d2 = c.y + d6 - yPos - 6  + TOP_INFOHEIGHT;
                        currentGraphics.setClip(d1, d2, 10, 12);
                        currentGraphics.drawImage(ingame, d1-41, d2, anchorTopLeft);    // death icon
                        c.attackShowDuration = 0;
                        return -1;
                }
            } else if (c.icon==ICON_HIT){
                //c.attackShowDuration = 0;
                if (c.hitDisplayDelay <= 0) {
                    return -1;
                }
            }
        }  else if (c.icon==ICON_DEAD) {
            c.attackShowDuration = 0;
            return -1;
        }
        return 1;
    }
    */
    
    public void fillFromListMessage(byte[] message) {
        objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        
        classId = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
        clanId = NetTools.intFrom3Bytes(message[12], message[13], message[14]);

        playfieldId = NetTools.intFrom4Bytes(message[15], message[16], message[17], message[18]);
        graphicsId = NetTools.intFrom4Bytes(message[19], message[20], message[21], message[22]);

        graphicsX = NetTools.intFrom2Bytes(message[23], message[24]);
        graphicsY = NetTools.intFrom2Bytes(message[25], message[26]);
        graphicsDim = message[27];

        x = NetTools.intFrom2Bytes(message[28], message[29]);
        y = NetTools.intFrom2Bytes(message[30], message[31]);

        level = message[32];
        levelpoints = NetTools.intFrom2Bytes(message[33], message[34]);
        experience = NetTools.intFrom4Bytes(message[35], message[36], message[37], message[38]);

        gold = NetTools.intFrom4Bytes(message[39], message[40], message[41], message[42]);

        healthBase = NetTools.intFrom2Bytes(message[43], message[44]);  // curHealth base
        healthEffectsExtra = NetTools.intFrom2Bytes(message[45], message[46]);
        curHealth = NetTools.intFrom2Bytes(message[47], message[48]); // current curHealth

        manaBase = NetTools.intFrom2Bytes(message[49], message[50]);
        manaEffectsExtra = NetTools.intFrom2Bytes(message[51], message[52]);
        curMana = NetTools.intFrom2Bytes(message[53], message[54]);

        attackBase = NetTools.intFrom2Bytes(message[55], message[56]);
        attackEffectsExtra = NetTools.intFrom2Bytes(message[57], message[58]);

        defenseBase = NetTools.intFrom2Bytes(message[59], message[60]);
        defenseEffectsExtra = NetTools.intFrom2Bytes(message[61], message[62]);

        damageBase = NetTools.intFrom2Bytes(message[63], message[64]);
        damageEffectsExtra = NetTools.intFrom2Bytes(message[65], message[66]);

        skillBase = NetTools.intFrom2Bytes(message[67], message[68]);
        skillEffectsExtra = NetTools.intFrom2Bytes(message[69], message[70]);

        magicBase = NetTools.intFrom2Bytes(message[71], message[72]);
        magicEffectsExtra = NetTools.intFrom2Bytes(message[73], message[74]);

        healthregenerateBase = NetTools.intFrom2Bytes(message[75], message[76]);
        healthregenerateEffectsExtra = NetTools.intFrom2Bytes(message[77], message[78]);

        manaregenerateBase = NetTools.intFrom2Bytes(message[79], message[80]);
        manaregenerateEffectsExtra = NetTools.intFrom2Bytes(message[81], message[82]);


        name = new String(message, 84, message[83]);
        int nextIndex = 84 + message[83];
        customMessage = new String(message, nextIndex+1, message[nextIndex]);
    }


    public void printDetails() {
        System.out.println("===============================================");
        System.out.println("Character Details: " + name + " (" + objectId + ")");
        System.out.println("===============================================");
        //System.out.println("description: " + description);
        System.out.println("classId: " + classId);
        System.out.println("clanId: " + clanId);
        System.out.println("playfieldId: " + playfieldId);
        System.out.println("graphicsId: " + playfieldId);
        System.out.println("graphicsX: " + graphicsX);
        System.out.println("graphicsY: " + graphicsY);
        System.out.println("graphicsDim: " + graphicsDim);
        System.out.println("x: " + x);
        System.out.println("y: " + y);        
        //System.out.println("subclass: " + subclassId);
        //System.out.println("trigger: " + triggertype);
        System.out.println("level: " + level);
        System.out.println("levelpoints: " + levelpoints);
        System.out.println("experience: " + experience);
        System.out.println("gold: " + gold);
        System.out.println("curHealth: " + curHealth);
        System.out.println("healthBase: " + healthBase);
        System.out.println("healthEffectsExtra: " + healthEffectsExtra);
        System.out.println("healthregenerateBase: " + healthregenerateBase);
        System.out.println("healthregenerateEffectsExtra: " + healthregenerateEffectsExtra);
        System.out.println("curMana: " + curMana);
        System.out.println("manaBase: " + manaBase);
        System.out.println("manaEffectsExtra: " + manaEffectsExtra);
        System.out.println("manaregenerateBase: " + manaregenerateBase);
        System.out.println("manaregenerateEffectsExtra: " + manaregenerateEffectsExtra);
        System.out.println("attackBase: " + attackBase);
        System.out.println("attackEffectsExtra: " + attackEffectsExtra);
        System.out.println("defense: " + defenseBase);
        System.out.println("defenseEffectsExtra: " + defenseEffectsExtra);
        System.out.println("skillBase: " + skillBase);
        System.out.println("skillEffectsExtra: " + skillEffectsExtra);
        System.out.println("magicBase: " + magicBase);
        System.out.println("magicEffectsExtra: " + magicEffectsExtra);
        System.out.println("damageBase: " + damageBase);
        System.out.println("damageEffectsExtra: " + damageEffectsExtra);
        System.out.println("status msg: " + customMessage);
        System.out.println("===============================================");
    }



}
