package rhynn;




import graphics.GFont;
import graphics.GImageClip;
import graphics.GTextWindow;
import graphics.GTools;
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class Inventory {
    public static int DEFAULT_NUM_SLOTS = 22;
    public static int DEFAULT_SLOTS_PER_ROW = 11;

    private int numSlots = DEFAULT_NUM_SLOTS;
    private int numSlotsPerRow = 11;
    
    private int numItems = 0;
    private int selectedSlot = 0;
    Item[] items = null;
    
    private static int CURSOR_FLASH_DELAY = 500;
    private long lastCursorFlash = 0;
    boolean cursorFlash = false;

    //Vector changeObservers = new Vector();
    Equipment equipment;
    Character character;
    
    static GTextWindow itemDescriptionWindow = null;

    public Inventory(Character c, int newNumSlots, int newSlotsPerRow) {
        numSlots = newNumSlots;
        numSlotsPerRow = newSlotsPerRow;
        items = new Item[numSlots];

        equipment = new Equipment(this, Equipment.DEFAULT_NUM_BELT_SLOTS);
        character = c;
    }

    public Item getEquippedItem(int eqType) {
        return equipment.getEquippedItem(eqType);
    }

    public void checkInitDescriptionWindow(GFont font, int xPos, int yPos, int displayWidth) {
        if (itemDescriptionWindow==null) {
            itemDescriptionWindow = GTools.textWindowCreate(xPos + 4, yPos + 14, displayWidth, 2*font.charHeight, null, 48, font, false);
            GTools.windowSetBorder(itemDescriptionWindow, 0, 0);
            GTools.windowSetColors(itemDescriptionWindow, GTools.TRANSPARENT, GTools.TRANSPARENT, GTools.TRANSPARENT, GTools.TRANSPARENT);

        }
    }

    public int addItem(Item i) {
        if (!isFull()) {
            items[numItems] = i;
            if (i.equipped > 0 && i.usageType == Item.USAGE_TYPE_EQUIP) {
                equip(i);
            }
            // todo: check for belt, too
            return numItems++;
        }
        return -1;
    }

    public Item removeSelectedItem(int units) {
        return removeItemAt(selectedSlot, units);
    }

    public void onEquipmentChanged() {
        if (character != null) {
            character.onEquipmentChanged();
        }
    }

    private Item removeItemAt(int slot, int units) {
        if (slot < numItems && slot >= 0) {
            Item it = items[slot];
            if (units < 0) {
                it.units = 0;
            } else {
                it.units -= units;
            }

            if (it.units <= 0) {
                // no units left, remove completely
                if (it.equipped > 0) {
                    int skill = character!=null ? character.skillBase : 0;
                    int magic = character !=null ? character.skillBase : 0;
                    equipment.unequip(it, skill, magic, true);
                }

                // shift items behind this one up by one
                for (int j=slot; j<numItems-1; j++) {
                    items[j] = items[j+1];
                }
                items[numItems-1] = null;
                numItems--;
            }
            return it;
        }
        return null;
    }

    public Item removeItemById(int id) {
        for (int i=0; i<numItems; i++) {
            if (items[i].objectId == id) {
                removeItemAt(i, -1);
            }
        }
        return null;
    }


    public int getEffects(int attrType) {
        return equipment.getEffects(attrType);
    }



    public int numItems() {
        return numItems;
    }

    public int numFreeSlots() {
        return numSlots - numItems;
    }

    public boolean isFull() {
        return !(numFreeSlots() > 0);
    }

    public int navigateNextRow() {
        int newSlot = selectedSlot + numSlotsPerRow;
        if (newSlot > numSlots-1) {
            return selectedSlot;
        }
        selectedSlot = newSlot;
        return selectedSlot;
    }

    public int navigatePrevRow() {
        int newSlot = selectedSlot - numSlotsPerRow;
        if (newSlot < 0) {
            return selectedSlot;
        }
        selectedSlot = newSlot;
        return selectedSlot;
    }


    public int navigateNextSlot() {
        selectedSlot = (selectedSlot == numSlots-1) ? 0 : selectedSlot+1;
        return selectedSlot;
    }

    public int navigatePrevSlot() {
        selectedSlot = (selectedSlot == 0) ? numSlots-1 : selectedSlot-1;
        return selectedSlot;
    }

    public void setInitialSelection() {
        selectedSlot = 0;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public Item getSelectedItem() {
        return items[selectedSlot];
    }

    public Item getItemAt(int slot) {
        if (slot > numItems-1 || slot < 0) {
            return null;
        }
        return items[slot];
    }


    public Item getItem(int id) {
        for (int i=0; i<numItems; i++) {
            if (items[i].objectId == id) {
                return items[i];
            }
        }
        return null;
    }

    public boolean equip(Item it) {
        return equipment.equip(it, character.skillBase, character.magicBase, true);
    }

    public boolean unequip(Item it) {
        return equipment.unequip(it, character.skillBase, character.magicBase, true);
    }


    public int selectSlot(int newSlotIndex) {
        if (newSlotIndex > 0 && newSlotIndex < numSlots) {
            selectedSlot = newSlotIndex;
        }
        return selectedSlot;
    }

    public int getSelectedXOffset() {
        return (selectedSlot % numSlotsPerRow) * Item.SLOT_WIDTH;
    }

    public int getSelectedYOffset() {
        return (selectedSlot / numSlotsPerRow) * Item.SLOT_HEIGHT;
    }



    private void drawItemAttributes(Graphics currentGraphics, GFont font, Item item, int xPos, int yPos, int displayWidth) {
        displayWidth = (displayWidth > 176) ? 176 : displayWidth;

        checkInitDescriptionWindow(font, xPos, yPos, displayWidth);
        GTools.textWindowSetText(itemDescriptionWindow, item.description);

        // adjust description displayheight
        int atDisplay_DescriptionHeight = (itemDescriptionWindow.noOfExistingLines > 1) ? 30 : 24;

        // name /description
        currentGraphics.setClip(xPos, yPos, displayWidth, atDisplay_DescriptionHeight);
        currentGraphics.setColor(0, 0, 80);
        currentGraphics.fillRect(xPos, yPos, displayWidth, atDisplay_DescriptionHeight);
        currentGraphics.setColor(255, 204, 0);
        currentGraphics.drawRect(xPos, yPos, displayWidth-1, atDisplay_DescriptionHeight-1);

        // draw Item name
        font.drawString(currentGraphics, item.name, 4, yPos + 4);
        // draw item description
        GTools.drawTextWindow(currentGraphics, itemDescriptionWindow);
        // draw separation line
        currentGraphics.setClip(2, itemDescriptionWindow.y - 3, displayWidth - 4, 1);
        currentGraphics.setColor(80, 80, 80);
        currentGraphics.drawLine(2, itemDescriptionWindow.y - 3, displayWidth - 2, itemDescriptionWindow.y - 3);

        int yCursor = yPos + atDisplay_DescriptionHeight + 2;
        int backColor = 0;


        drawAttributeBGSegment(currentGraphics, xPos, yPos + atDisplay_DescriptionHeight, 2);

        if (item.requiredSkill > 0) {
            backColor = (item.requiredSkill <= character.getTotalSkill()) ? 0 : 0x800000;
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Req. Skill", item.requiredSkill, false, false);
            yCursor += 8;
        }

        if (item.requiredMagic > 0) {
            backColor = (item.requiredMagic <= character.getTotalMagic()) ? 0 : 0x800000;
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Req. Magic", item.requiredMagic, false, false);
            yCursor += 8;
        }
        
        if (Equipment.getEquipmentTypeFromClientType(item.clientTypeId) == Equipment.ET_WEAPON_1) {
            backColor = 0;
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Attackrate", item.frequency, false, false);
            yCursor += 8;

            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Range", item.range, false, false);
            yCursor += 8;
        } else {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Units", item.units, false, false);
            yCursor += 8;
        }

        drawAttributeBGSegment(currentGraphics, xPos, yCursor, 6);
        yCursor += 6;
        

        backColor = 0x006000;
        if (item.healthEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Health", item.healthEffect, true, true);
            yCursor += 8;
        }
        if (item.manaEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Mana", item.manaEffect, true, true);
            yCursor += 8;
        }
        if (item.attackEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Attack", item.attackEffect, true, true);
            yCursor += 8;
        }
        if (item.defenseEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Defense", item.defenseEffect, true, true);
            yCursor += 8;
        }
        if (item.damageEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Damage", item.damageEffect, true, true);
            yCursor += 8;
        }
        if (item.skillEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Skill", item.skillEffect, true, true);
            yCursor += 8;
        }
        if (item.magicEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Magic", item.magicEffect, true, true);
            yCursor += 8;
        }
        if (item.healthRegenerateEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Healthfill", item.healthRegenerateEffect, true, true);
            yCursor += 8;
        }
        if (item.manaRegenerateEffect > 0) {
            drawAttributeLine(currentGraphics, font, backColor, xPos, yCursor, "Manafill", item.manaRegenerateEffect, true, true);
            yCursor += 8;
        }

        // bottom line
        currentGraphics.setColor(0xFFCE00);
        currentGraphics.setClip(xPos, yCursor, 89, 1);
        currentGraphics.drawLine(xPos, yCursor, xPos+88, yCursor);

        /*
        int backYStart = yPos + atDisplay_DescriptionHeight;
        currentGraphics.setClip(xPos, backYStart, xPos, yCursor);
        currentGraphics.fillRect(xPos, yPos, yPos, yPos);*/

    }


    private void drawAttributeBGSegment(Graphics currentGraphics, int xPos, int yPos, int height) {
        int lineWidth = 87;
        currentGraphics.setColor(0);
        currentGraphics.setClip(xPos, yPos, lineWidth+2, height);
        currentGraphics.fillRect(xPos+1, yPos, lineWidth , height);
        currentGraphics.setColor(0xFFCE00);
        currentGraphics.drawLine(xPos, yPos, xPos, yPos + height);
        currentGraphics.drawLine(xPos+lineWidth+1, yPos, xPos+lineWidth+1, yPos + height);
    }

    private void drawAttributeLine(Graphics g, GFont font, int backColor, int xPos, int yPos, String text, int value, boolean includeSign, boolean includeDecimal) {
        int xValueOffset = 58;
        int xOffset = 3;
        int lineHeight = 6;
        int lineWidth = 87;

        drawAttributeBGSegment(g, xPos, yPos, lineHeight+2);

        g.setColor(backColor);
        g.setClip(xPos+1, yPos, lineWidth, lineHeight);
        g.fillRect(xPos+1, yPos, lineWidth, lineHeight);

        
        font.drawString(g, text, xPos + xOffset, yPos);

        if (includeSign) {
            font.drawChar(g, (value >= 0) ? '+' : '-', xPos + xValueOffset, yPos);
        }
        xValueOffset += 5;

        int fullVal = value / 10;
        String valString = "" + fullVal;
        font.drawString(g, valString, xPos + xValueOffset, yPos);

        if (includeDecimal) {
            int decVal = value % 10;
            int xDecOffset = xValueOffset + (valString.length() * 5);
            font.drawString(g, "." + decVal, xPos + xDecOffset, yPos);
        }
    }


    public void drawEquipment(Graphics g, int viewWidth, int viewHeight, GFont font, String title, boolean drawSelection) {
        int selectedId = 0;
        if (drawSelection) {
            Item it = getSelectedItem();
            if (it != null) {
                selectedId = it.objectId;
            }
        }
        equipment.draw(g, viewWidth - equipment.getWidth(), viewHeight - equipment.getHeight(), font, title, selectedId);
    }

    private void drawItemWithSlot(Graphics g, GImageClip backgroundSlot, int xPos, int yPos, Item it, boolean selected, boolean meetsRequirements) {

        int backcolor = -1;
        if (it != null)
        {
            if (!meetsRequirements) {
                backcolor = 0xA0000;
            } else if (it.equipped > 0) {
                if (it.usageType == Item.USAGE_TYPE_EQUIP) {
                    backcolor = selected ? 0x00c0ff : 0x004080;
                } else if (it.usageType == Item.USAGE_TYPE_USE) {
                    backcolor = selected ? 0xc0c0ff : 0x505080;
                }
            } else if (selected) {
                backcolor = 0x00f064;
            }
        }

        if (backcolor > -1) {
            g.setColor(backcolor);
            g.setClip(xPos, yPos, Item.SLOT_WIDTH, Item.SLOT_HEIGHT);
            g.fillRect(xPos, yPos, Item.SLOT_WIDTH, Item.SLOT_HEIGHT);
        } else if (backgroundSlot != null) {
            backgroundSlot.draw(g, xPos, yPos);
        }

        if (it!=null) {
            it.draw(g, xPos+1, yPos+1);
        }

        long curTime = System.currentTimeMillis();
        // draw selection caret
        if (selected) {
            if (curTime - lastCursorFlash > CURSOR_FLASH_DELAY) {
                lastCursorFlash = curTime;
                cursorFlash = !cursorFlash;
            }

            g.setClip(xPos, yPos, Item.SLOT_WIDTH, Item.SLOT_HEIGHT);
            g.setColor(cursorFlash ? 0xffffff : 0xffcc00);
            g.drawRect(xPos, yPos, Item.SLOT_WIDTH-1, Item.SLOT_HEIGHT-1);
        }


    }


    // todo: possibly encapsulate DrawSettings
    public void draw(Graphics g, GFont font, GImageClip backgroundSlot, int xPos, int yPos, int width, int height, Character ownerCharacter, boolean drawAttributes) {
        // todo: decrease max slots per row if display too small
        int yInc = Item.SLOT_HEIGHT;
        int xInc = Item.SLOT_WIDTH;
        int curX = xPos;
        int curY = yPos;
        for (int i=0; i<numSlots; i++) {
            boolean meetsRequirements = true;
            if (i < numItems) {
                meetsRequirements = ownerCharacter != null && ownerCharacter.meetsItemRequirements(items[i]);
            }
            drawItemWithSlot(g, backgroundSlot, curX, curY, items[i], i==selectedSlot, meetsRequirements);

            if (i!=0 && (i+1)%(numSlotsPerRow) == 0) {
                curY += yInc;
                curX = xPos;
            } else {
                curX += xInc;
            }
        }

        if (drawAttributes) {
            Item selItem = getSelectedItem();
            if (selItem != null) {
                drawItemAttributes(g, font, selItem, xPos, yPos + Item.SLOT_HEIGHT*2, width);
            }
        }

    }
}
