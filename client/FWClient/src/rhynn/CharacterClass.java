package rhynn;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import net.NetTools;

/**
 *
 * @author marlowe
 */
public class CharacterClass {
		public boolean premiumOnly;
		public String displayName;
    
        public int classId;

        public int graphicsId;
        public int graphicsX;
		public int graphicsY;
		public int graphicsDim;

		public int healthBase;
		public int healthModifier;
		public int manaBase;
		public int manaModifier;
		public int attackBase;
		public int attackModifier;
		public int defenseBase;
		public int defenseModifier;
		public int damageBase;
		public int damageModifier;
		public int skillBase;
		public int skillModifier;
		public int magicBase;
		public int magicModifier;
		public int healthregenerateBase;
		public int healthregenerateModifier;
		public int manaregenerateBase;
		public int manaregenerateModifier;

        public void fillFromMessage(byte[] message) {
            classId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);

            premiumOnly = (message[8] != 0);
            graphicsId = NetTools.intFrom4Bytes(message[9], message[10],message[11],message[12]);
            graphicsX = NetTools.intFrom2Bytes(message[13], message[14]);
            graphicsY = NetTools.intFrom2Bytes(message[15], message[16]);

            graphicsDim = message[17];
            healthBase = NetTools.intFrom2Bytes(message[18], message[19]);
            healthModifier = message[20];
            manaBase = NetTools.intFrom2Bytes(message[21], message[22]);
            manaModifier = message[23];
            attackBase = NetTools.intFrom2Bytes(message[24], message[25]);
            attackModifier = message[26];
            defenseBase = NetTools.intFrom2Bytes(message[27], message[28]);
            defenseModifier = message[29];
            damageBase = NetTools.intFrom2Bytes(message[30], message[31]);
            damageModifier = message[32];
            skillBase = NetTools.intFrom2Bytes(message[33], message[34]);
            skillModifier = message[35];
            magicBase = NetTools.intFrom2Bytes(message[36], message[37]);
            magicModifier = message[38];
            healthregenerateBase = NetTools.intFrom2Bytes(message[39], message[40]);
            healthregenerateModifier = message[41];
            manaregenerateBase = NetTools.intFrom2Bytes(message[42], message[43]);
            manaregenerateModifier = message[44];
            displayName = new String(message, 46, message[45]);
        }

}
