package rhynn;




import graphics.GImageClip;
import javax.microedition.lcdui.Graphics;
import net.NetTools;

/*
 * Item.java
 *
 * Created on 27. November 2003, 13:23
 */


/**
 *
 * @author marlowe
 */
public class Item extends WorldObject {

    public static final int USAGE_TYPE_UNKNOWN = 0;
    public static final int USAGE_TYPE_EQUIP = 1;
    public static final int USAGE_TYPE_USE = 2;
    public static final int USAGE_TYPE_GOLD = 3;

    public static final int CLIENT_TYPE_UNKNOWN = 0;
    public static final int CLIENT_TYPE_WEAPON_1 = 1;
    public static final int CLIENT_TYPE_SHIELD_1 = 2;
    public static final int CLIENT_TYPE_ARMOR = 3;
    public static final int CLIENT_TYPE_HELMET = 4;
    public static final int CLIENT_TYPE_BOOTS = 5;
    public static final int CLIENT_TYPE_GLOVES = 6;
    

    public static final int ATTR_TYPE_UNKNOWN = 0;
    public static final int ATTR_TYPE_HEALTH = 1;
    public static final int ATTR_TYPE_MANA = 2;
    public static final int ATTR_TYPE_ATTACK = 3;
    public static final int ATTR_TYPE_DEFENSE = 4;
    public static final int ATTR_TYPE_DAMAGE = 5;
    public static final int ATTR_TYPE_SKILL = 6;
    public static final int ATTR_TYPE_MAGIC = 7;
    public static final int ATTR_TYPE_HEALTHREG = 8;
    public static final int ATTR_TYPE_MANAREG = 9;

    public static final int DEFAULT_WIDTH = 15;
    public static final int DEFAULT_HEIGHT = 15;
    public static final int SLOT_WIDTH = 16;
    public static final int SLOT_HEIGHT = 16;

    public Item() {
        graphicsDim = DEFAULT_WIDTH;
    }

    public void fillFromListMessage(byte[] message) {
        objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        clientTypeId = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
        usageType = message[12];
System.out.println("=============== got usage type: " + usageType);
        setId = NetTools.intFrom4Bytes(message[13], message[14], message[15], message[16]);
        graphicsId = NetTools.intFrom4Bytes(message[17], message[18], message[19], message[20]);
        graphicsX = NetTools.intFrom2Bytes(message[21], message[22]);
        graphicsY = NetTools.intFrom2Bytes(message[23], message[24]);
System.out.println("=============== got graphics id: " + graphicsId);
        premiumOnly = message[25] > 0 ? true : false;
        canSell = message[26] > 0 ? true : false;
        canDrop = message[27] > 0 ? true : false;
        units = NetTools.intFrom2Bytes(message[28], message[29]);
        unitsSell = NetTools.intFrom2Bytes(message[30], message[31]);
        price = NetTools.intFrom3Bytes(message[32], message[33], message[34]);
        equipped = message[35];

        healthEffect = NetTools.intFrom2Bytes(message[36], message[37]);
        manaEffect = NetTools.intFrom2Bytes(message[38], message[39]);
        attackEffect = NetTools.intFrom2Bytes(message[40], message[41]);
        defenseEffect = NetTools.intFrom2Bytes(message[42], message[43]);
        damageEffect = NetTools.intFrom2Bytes(message[44], message[45]);
        skillEffect = NetTools.intFrom2Bytes(message[46], message[47]);
        magicEffect = NetTools.intFrom2Bytes(message[48], message[49]);
        healthRegenerateEffect = NetTools.intFrom2Bytes(message[50], message[51]);
        manaRegenerateEffect = NetTools.intFrom2Bytes(message[52], message[53]);
        actionEffect1 = NetTools.intFrom2Bytes(message[54], message[55]);
        actionEffect2 = NetTools.intFrom2Bytes(message[56], message[57]);
        
        effectDuration = NetTools.intFrom2Bytes(message[58], message[59]);
        requiredSkill = NetTools.intFrom2Bytes(message[60], message[61]);
        requiredMagic = NetTools.intFrom2Bytes(message[62], message[63]);

        frequency = NetTools.uintFrom1Byte(message[64]);
        range = NetTools.uintFrom1Byte(message[65]);

System.out.println("got fr: " + frequency);
System.out.println("got range: " + range);

        name = new String(message, 67, message[66]);
        int nextIndex = 67 + message[66];
        description = new String(message, nextIndex+1, message[nextIndex]);
    }

    public int getAttributValue(int attrType) {
        switch(attrType) {
            case ATTR_TYPE_HEALTH: return healthEffect;
            case ATTR_TYPE_MANA: return healthEffect;
            case ATTR_TYPE_ATTACK: return healthEffect;
            case ATTR_TYPE_DEFENSE: return healthEffect;
            case ATTR_TYPE_DAMAGE: return healthEffect;
            case ATTR_TYPE_SKILL: return healthEffect;
            case ATTR_TYPE_MAGIC: return healthEffect;
            case ATTR_TYPE_HEALTHREG: return healthEffect;
            case ATTR_TYPE_MANAREG: return healthEffect;
            default: return 0;
        }
    }




    public int categoryId;
    public int clientTypeId;
    public int setId;
    public boolean premiumOnly;
    /** UNKNOWN,EQUIP,USE,GOLD */
    public int usageType;
    public boolean canSell;
    public boolean canDrop;
    /** Number of times this item can be used. */
    public int units;
    /** Number of times this item can be sold. */
    public int unitsSell;
    public int price;
    public int healthEffect;
    public int manaEffect;
    public int attackEffect;
    public int defenseEffect;
    public int damageEffect;
    public int skillEffect;
    public int magicEffect;
    public int healthRegenerateEffect;
    public int manaRegenerateEffect;
    public int actionEffect1;
    public int actionEffect2;

    public int effectDuration;
    public int requiredSkill;
    public int requiredMagic;

    public int range;

    /** Attack frequency bonus = attackrate*(1000)ms. */
    public int frequency = 6;

    
    /** Whether this Item is equipped on a character or not. */
    public int equipped;    
    /** Additional data: Map ID, Range of long distance weapon. */
    public int data;






}
