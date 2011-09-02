#ifndef MsgGameUserCharacterForList_h__
#define MsgGameUserCharacterForList_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "world_objects/Character.h"
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Send a flat character entry to the client for display in the character selection list at the client (so).
 */

class MsgGameUserCharacterForList : public min::MinMessage {


	public:
		/** The database id of the character. */
		unsigned int objectId;
		/** The class this character belongs to (e.g. human, elf, dwarf, orc, wizard). */
		unsigned int classId;
		/** Clan this character belongs to. */
		unsigned int clanId;
		/** Current playfield this character is on (0 for none). */
		unsigned int playfieldId;
		/** . */
		unsigned int graphicId;
		/** -. */
		unsigned int graphicsX;
		/** -. */
		unsigned int graphicsY;
		/** -. */
		unsigned int graphicsDim;
		/** xPos on the playfield. */
		unsigned int x;
		/** yPos on the playfield. */
		unsigned int y;
		/** Character experience level. */
		unsigned int level;
		/** Number of points you character may spend on attribute increase. */
		unsigned int levelPoints;
		/** -. */
		unsigned int experience;
		/** -. */
		unsigned int gold;
		/** Max health value defined as a character attribute. The actual maximum health is base + extra. */
		unsigned int healthBase;
		/** Extra health as a result of equipped items and / or other effects. */
		unsigned int healthExtra;
		/** The health this character actually has out of base + extra. */
		unsigned int healthCurrent;
		/** Max mana value defined as a character attribute. The actual maximum mana is base + extra. */
		unsigned int manaBase;
		/** Extra mana as a result of equipped items and / or other effects. */
		unsigned int manaExtra;
		/** The mana this character actually has out of base + extra. */
		unsigned int manaCurrent;
		/** -. */
		unsigned int attackBase;
		/** -. */
		unsigned int attackExtra;
		/** -. */
		unsigned int defenseBase;
		/** -. */
		unsigned int defenseExtra;
		/** -. */
		unsigned int damageBase;
		/** -. */
		unsigned int damageExtra;
		/** -. */
		unsigned int skillBase;
		/** -. */
		unsigned int skillExtra;
		/** -. */
		unsigned int magicBase;
		/** -. */
		unsigned int magicExtra;
		/** -. */
		unsigned int healthregenerateBase;
		/** -. */
		unsigned int healthregenerateExtra;
		/** -. */
		unsigned int manaregenerateBase;
		/** -. */
		unsigned int manaregenerateExtra;
		/** -. */
		unsigned int nameLength;
		/** -. */
		std::string name;
		/** -. */
		unsigned int statusMsgLength;
		/** -. */
		std::string statusMsg;


		MsgGameUserCharacterForList(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_FOR_LIST;
			_msgMinLength = 85;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterForList(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_FOR_LIST;
			_msgMinLength = 85;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterForList(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_FOR_LIST;
			_msgMinLength = 85;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterForList() {}

		inline void initDefaultValues() {
			objectId = 0;
			classId = 0;
			clanId = 0;
			playfieldId = 0;
			graphicId = 0;
			graphicsX = 0;
			graphicsY = 0;
			graphicsDim = 0;
			x = 0;
			y = 0;
			level = 0;
			levelPoints = 0;
			experience = 0;
			gold = 0;
			healthBase = 0;
			healthExtra = 0;
			healthCurrent = 0;
			manaBase = 0;
			manaExtra = 0;
			manaCurrent = 0;
			attackBase = 0;
			attackExtra = 0;
			defenseBase = 0;
			defenseExtra = 0;
			damageBase = 0;
			damageExtra = 0;
			skillBase = 0;
			skillExtra = 0;
			magicBase = 0;
			magicExtra = 0;
			healthregenerateBase = 0;
			healthregenerateExtra = 0;
			manaregenerateBase = 0;
			manaregenerateExtra = 0;
			nameLength = 0;
			name = "";
			statusMsgLength = 0;
			statusMsg = "";
		}

		bool valuesFromBytes(const unsigned char* bytes, bool readLength = true) {
			if (readLength) {
				length = min::NetPort::uintFromByte(bytes, 0);
				if (length < _msgMinLength) {
					_msgIsValid = false;
					return false;
				}
			}
			//msgId = min::NetPort::uintFrom3Bytes(bytes, 1);
			objectId = min::NetPort::uintFrom4Bytes(bytes, 4);
			classId = min::NetPort::uintFrom4Bytes(bytes, 8);
			clanId = min::NetPort::uintFrom3Bytes(bytes, 12);
			playfieldId = min::NetPort::uintFrom4Bytes(bytes, 15);
			graphicId = min::NetPort::uintFrom4Bytes(bytes, 19);
			graphicsX = min::NetPort::uintFrom2Bytes(bytes, 23);
			graphicsY = min::NetPort::uintFrom2Bytes(bytes, 25);
			graphicsDim = min::NetPort::uintFromByte(bytes, 27);
			x = min::NetPort::uintFrom2Bytes(bytes, 28);
			y = min::NetPort::uintFrom2Bytes(bytes, 30);
			level = min::NetPort::uintFromByte(bytes, 32);
			levelPoints = min::NetPort::uintFrom2Bytes(bytes, 33);
			experience = min::NetPort::uintFrom4Bytes(bytes, 35);
			gold = min::NetPort::uintFrom4Bytes(bytes, 39);
			healthBase = min::NetPort::uintFrom2Bytes(bytes, 43);
			healthExtra = min::NetPort::uintFrom2Bytes(bytes, 45);
			healthCurrent = min::NetPort::uintFrom2Bytes(bytes, 47);
			manaBase = min::NetPort::uintFrom2Bytes(bytes, 49);
			manaExtra = min::NetPort::uintFrom2Bytes(bytes, 51);
			manaCurrent = min::NetPort::uintFrom2Bytes(bytes, 53);
			attackBase = min::NetPort::uintFrom2Bytes(bytes, 55);
			attackExtra = min::NetPort::uintFrom2Bytes(bytes, 57);
			defenseBase = min::NetPort::uintFrom2Bytes(bytes, 59);
			defenseExtra = min::NetPort::uintFrom2Bytes(bytes, 61);
			damageBase = min::NetPort::uintFrom2Bytes(bytes, 63);
			damageExtra = min::NetPort::uintFrom2Bytes(bytes, 65);
			skillBase = min::NetPort::uintFrom2Bytes(bytes, 67);
			skillExtra = min::NetPort::uintFrom2Bytes(bytes, 69);
			magicBase = min::NetPort::uintFrom2Bytes(bytes, 71);
			magicExtra = min::NetPort::uintFrom2Bytes(bytes, 73);
			healthregenerateBase = min::NetPort::uintFrom2Bytes(bytes, 75);
			healthregenerateExtra = min::NetPort::uintFrom2Bytes(bytes, 77);
			manaregenerateBase = min::NetPort::uintFrom2Bytes(bytes, 79);
			manaregenerateExtra = min::NetPort::uintFrom2Bytes(bytes, 81);
			nameLength = min::NetPort::uintFromByte(bytes, 83);
			if(84 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 84);
			unsigned int curIndex = 84 + nameLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			statusMsgLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + statusMsgLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (statusMsgLength > 24) {
				_msgIsValid = false;
				return false;
			}
			statusMsg = min::NetPort::stringFromBytes(bytes, statusMsgLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(objectId, bytes, 4);
			min::NetPort::uintTo4Bytes(classId, bytes, 8);
			min::NetPort::uintTo3Bytes(clanId, bytes, 12);
			min::NetPort::uintTo4Bytes(playfieldId, bytes, 15);
			min::NetPort::uintTo4Bytes(graphicId, bytes, 19);
			min::NetPort::uintTo2Bytes(graphicsX, bytes, 23);
			min::NetPort::uintTo2Bytes(graphicsY, bytes, 25);
			min::NetPort::uintToByte(graphicsDim, bytes, 27);
			min::NetPort::uintTo2Bytes(x, bytes, 28);
			min::NetPort::uintTo2Bytes(y, bytes, 30);
			min::NetPort::uintToByte(level, bytes, 32);
			min::NetPort::uintTo2Bytes(levelPoints, bytes, 33);
			min::NetPort::uintTo4Bytes(experience, bytes, 35);
			min::NetPort::uintTo4Bytes(gold, bytes, 39);
			min::NetPort::uintTo2Bytes(healthBase, bytes, 43);
			min::NetPort::uintTo2Bytes(healthExtra, bytes, 45);
			min::NetPort::uintTo2Bytes(healthCurrent, bytes, 47);
			min::NetPort::uintTo2Bytes(manaBase, bytes, 49);
			min::NetPort::uintTo2Bytes(manaExtra, bytes, 51);
			min::NetPort::uintTo2Bytes(manaCurrent, bytes, 53);
			min::NetPort::uintTo2Bytes(attackBase, bytes, 55);
			min::NetPort::uintTo2Bytes(attackExtra, bytes, 57);
			min::NetPort::uintTo2Bytes(defenseBase, bytes, 59);
			min::NetPort::uintTo2Bytes(defenseExtra, bytes, 61);
			min::NetPort::uintTo2Bytes(damageBase, bytes, 63);
			min::NetPort::uintTo2Bytes(damageExtra, bytes, 65);
			min::NetPort::uintTo2Bytes(skillBase, bytes, 67);
			min::NetPort::uintTo2Bytes(skillExtra, bytes, 69);
			min::NetPort::uintTo2Bytes(magicBase, bytes, 71);
			min::NetPort::uintTo2Bytes(magicExtra, bytes, 73);
			min::NetPort::uintTo2Bytes(healthregenerateBase, bytes, 75);
			min::NetPort::uintTo2Bytes(healthregenerateExtra, bytes, 77);
			min::NetPort::uintTo2Bytes(manaregenerateBase, bytes, 79);
			min::NetPort::uintTo2Bytes(manaregenerateExtra, bytes, 81);
			min::NetPort::uintToByte(nameLength, bytes, 83);
			min::NetPort::stringToBytes(name, bytes, nameLength, 84);
			unsigned int curIndex = 84 + nameLength;
			min::NetPort::uintToByte(statusMsgLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(statusMsg, bytes, statusMsgLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 12) {
				_msgIsValid = false; 
				return false;
			}
			statusMsgLength = (int)statusMsg.size();
			if (statusMsgLength > 24) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength + statusMsgLength;
			return true;
		}

		inline bool isValid() {
			return _msgIsValid;
		}

	// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%
		void fromCharacter(const Character& character) {
			objectId = character.getId();
			classId = character.getClassId();
			clanId = character.getClanId();
			playfieldId = character.getPlayfieldId();
			graphicId = character.getGraphicId();
			graphicsX = character.getGraphicsX();
			graphicsY = character.getGraphicsY();
			graphicsDim = character.getGraphicsDim();
			x = character.getX();
			y = character.getY();
			level = character.getLevel();
			levelPoints = character.getLevelPoints();
			experience = character.getExperience();
			gold = character.getGold();
			healthBase = character.getHealthBase();
			healthExtra = character.getHealthEffectsExtra();
			healthCurrent = character.getHealthCurrent();
			manaBase = character.getManaBase();
			manaExtra = character.getManaEffectsExtra();
			manaCurrent = character.getManaCurrent();
			attackBase = character.getAttackBase();
			attackExtra = character.getAttackEffectsExtra();
			defenseBase = character.getDefenseBase();
			defenseExtra = character.getDefenseEffectsExtra();
			skillBase = character.getSkillBase();
			skillExtra = character.getSkillEffectsExtra();
			magicBase = character.getMagicBase();
			magicExtra = character.getMagicEffectsExtra();
			healthregenerateBase = character.getHealthregenerateBase();
			healthregenerateExtra = character.getHealthregenerateEffectsExtra();
			manaregenerateBase = character.getManaregenerateBase();
			manaregenerateExtra = character.getManaregenerateEffectsExtra();
			name = character.getName();
			statusMsg = character.getCustomStatusMsg();

			checkValidateLength();			


		}
	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
