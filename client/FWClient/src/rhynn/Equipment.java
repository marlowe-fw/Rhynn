package rhynn;




import javax.microedition.lcdui.Graphics;
import graphics.GFont;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class Equipment {

    public static final int ET_WEAPON_1 = 0;
    public static final int ET_SHIELD_1 = 1;
    public static final int ET_ARMOR = 2;
    public static final int ET_HELMET = 3;
    public static final int ET_BOOTS = 4;
    public static final int ET_GLOVES = 5;
    public static int NUM_EQUIPMENT_SLOTS = 6;

    public static int DEFAULT_NUM_BELT_SLOTS = 4;
    int numBeltSlots = DEFAULT_NUM_BELT_SLOTS;
    Inventory parentInventory;

    Item[] items;
    Item[] beltItems;
    int selectedBeltSlot = 0;

    public Equipment ghostCopy() {
        Equipment copy = new Equipment(null, this.numBeltSlots);

        for (int i=0; i<numBeltSlots; i++) {
            copy.items[i] = items[i];
        }
        return copy;
    }



    public Equipment(Inventory parentInv, int newNumBeltSlots) {
        parentInventory = parentInv;
        numBeltSlots = newNumBeltSlots;
        beltItems = new Item[numBeltSlots];
        items = new Item[NUM_EQUIPMENT_SLOTS];
    }

    public static int getEquipmentTypeFromClientType(int clientType) {
        switch(clientType) {
            case Item.CLIENT_TYPE_WEAPON_1: return Equipment.ET_WEAPON_1;
            case Item.CLIENT_TYPE_SHIELD_1: return Equipment.ET_SHIELD_1;
            case Item.CLIENT_TYPE_ARMOR: return Equipment.ET_ARMOR;
            //Fix to equp helmet :)
            case Item.CLIENT_TYPE_HELMET: return Equipment.ET_HELMET;
            
            case Item.CLIENT_TYPE_BOOTS: return Equipment.ET_BOOTS;
            case Item.CLIENT_TYPE_GLOVES: return Equipment.ET_GLOVES;
            default: return -1;
        }
    }


    public boolean unequip(Item it, int baseSkill, int baseMagic, boolean notify) {
        int eqType = getEquipmentTypeFromClientType(it.clientTypeId);

        if (eqType >= 0 && eqType < NUM_EQUIPMENT_SLOTS) {
            if (items[eqType]!=null && items[eqType].objectId == it.objectId) {
                items[eqType].equipped = 0;
                items[eqType] = null;
                // check if other items need to be unequipped as a consequence of this item being removed (skill / magic might have dropped)
                int totalCurrentSkill = baseSkill + getEffects(Item.ATTR_TYPE_SKILL);
                int totalCurrentMagic = baseMagic + getEffects(Item.ATTR_TYPE_MAGIC);
                for (int i=0; i<NUM_EQUIPMENT_SLOTS; i++) {
                    if (items[i] != null && (totalCurrentSkill < items[i].requiredSkill || totalCurrentMagic < items[i].requiredMagic)) {
                        // item is not supported
                        unequip(items[i], baseSkill, baseMagic, false);
                        break;
                    }
                }
                if (parentInventory != null && notify) {
                    parentInventory.onEquipmentChanged();
                }
                return true;
            }
        }
        return false;
    }

    public int getEffects(int attrType) {
        int totalVal = 0;
        for (int i=0; i<NUM_EQUIPMENT_SLOTS; i++) {
            if (items[i]!=null) {
                totalVal += items[i].getAttributValue(attrType);
            }
        }
        return totalVal;
    }

    public Item getEquippedItem(int eqType) {
        if (eqType >= 0 && eqType < NUM_EQUIPMENT_SLOTS) {
            return items[eqType];
        }
        return null;
    }

    public boolean equip(Item it, int baseSkill, int baseMagic, boolean checkCanEquip) {
        int eqType = getEquipmentTypeFromClientType(it.clientTypeId);
        System.out.println("eq type: " + eqType);
        if (eqType >= 0 && eqType < NUM_EQUIPMENT_SLOTS && (!checkCanEquip || canEquip(it, baseSkill, baseMagic))) {
                if (items[eqType]!=null) {
                    unequip(items[eqType], baseSkill, baseMagic, false);
                }
                it.equipped = 1;
                items[eqType] = it;
                if (parentInventory != null) {
                    parentInventory.onEquipmentChanged();
                }
                return true;
        }

        return false;
    }


    public boolean canEquip(Item it, int baseSkill, int baseMagic) {
        int eqType = getEquipmentTypeFromClientType(it.clientTypeId);
        int totalCurrentSkill = baseSkill + getEffects(Item.ATTR_TYPE_SKILL);
        int totalCurrentMagic = baseMagic + getEffects(Item.ATTR_TYPE_MAGIC);

        if (items[eqType] != null) {
            // already an iten equipped for this eq slot
            // we need to consider the effect values without this item as this one will be unequipped before equipping the new one
            // as this might also have cascading effects, we need to simulate the unequip, and decide afterwards
            // using a simple temp copy to simulate the unequip
            Equipment ghost = ghostCopy();
            ghost.unequip(ghost.items[eqType], baseSkill, baseMagic, false);
            return ghost.canEquip(it, baseSkill, baseMagic);
        } else {
            if (totalCurrentSkill >= it.requiredSkill && totalCurrentMagic >= it.requiredMagic) {
                return true;
            } else {
                return false;
            }
        }
    }

    public int getWidth() {return 42;}
    public int getHeight() {return 68;}


    private void drawEquipmentSlot(Graphics g, int xSlotPos, int ySlotPos, int eqType, int selObjectId) {
        int color = (items[eqType]!=null && items[eqType].objectId == selObjectId) ?  0x00c0ff : 0x004080;
        g.setColor(color);
        g.setClip(xSlotPos, ySlotPos, Item.SLOT_WIDTH, Item.SLOT_HEIGHT);
        g.fillRect(xSlotPos, ySlotPos, Item.SLOT_WIDTH, Item.SLOT_HEIGHT);
        if (items[eqType]!=null)  {
            items[eqType].draw(g, xSlotPos+1, ySlotPos+1);
        }
    }



    public void draw(Graphics g, int xPos, int yPos, GFont font, String title, int selObjectId) {
        // draw background
        int w = getWidth();
        int h = getHeight();
        g.setClip(xPos, yPos, w, h);
        g.setColor(0);
        g.fillRect(xPos, yPos, w, h);
        g.setColor(0x202020);
        g.fillRect(xPos+2, yPos+10, w - 4, h-12);

        font.drawString(g, title, xPos + 12, yPos + 2);

        int ySlotPos = yPos + 12;
        int xSlotPos = xPos + 4;
        int xSlotPos2 = xSlotPos+2+Item.SLOT_WIDTH;

        drawEquipmentSlot(g, xSlotPos, ySlotPos, ET_HELMET, selObjectId);
        drawEquipmentSlot(g, xSlotPos2, ySlotPos, ET_ARMOR, selObjectId);
        ySlotPos += 2+Item.SLOT_HEIGHT;
        drawEquipmentSlot(g, xSlotPos, ySlotPos, ET_WEAPON_1, selObjectId);
        drawEquipmentSlot(g, xSlotPos2, ySlotPos, ET_SHIELD_1, selObjectId);
        ySlotPos += 2+Item.SLOT_HEIGHT;
        drawEquipmentSlot(g, xSlotPos, ySlotPos, ET_BOOTS, selObjectId);
        drawEquipmentSlot(g, xSlotPos2, ySlotPos, ET_GLOVES, selObjectId);
    }


}
