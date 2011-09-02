#ifndef MsgGameUserCharacterClassForList_h__
#define MsgGameUserCharacterClassForList_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "world_objects/CharacterClass.h"
using namespace fwworld;
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Character class containing the basic character class parameters for use on the client (display, and base values for character select (so).
 */

class MsgGameUserCharacterClassForList : public min::MinMessage {


	public:
		/** -. */
		unsigned int classId;
		/** . */
		unsigned int premiumOnly;
		/** . */
		unsigned int graphicId;
		/** -. */
		unsigned int graphicsX;
		/** -. */
		unsigned int graphicsY;
		/** -. */
		unsigned int graphicsDim;
		/** -. */
		unsigned int healthBase;
		/** -. */
		unsigned int healthModifier;
		/** -. */
		unsigned int manaBase;
		/** -. */
		unsigned int manaModifier;
		/** -. */
		unsigned int attackBase;
		/** -. */
		unsigned int attackModifier;
		/** -. */
		unsigned int defenseBase;
		/** -. */
		unsigned int defenseModifier;
		/** -. */
		unsigned int damageBase;
		/** -. */
		unsigned int damageModifier;
		/** -. */
		unsigned int skillBase;
		/** -. */
		unsigned int skillModifier;
		/** -. */
		unsigned int magicBase;
		/** -. */
		unsigned int magicModifier;
		/** -. */
		unsigned int healthregenerateBase;
		/** -. */
		unsigned int healthregenerateModifier;
		/** -. */
		unsigned int manaregenerateBase;
		/** -. */
		unsigned int manaregenerateModifier;
		/** -. */
		unsigned int nameLength;
		/** -. */
		std::string name;


		MsgGameUserCharacterClassForList(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CLASS_FOR_LIST;
			_msgMinLength = 46;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameUserCharacterClassForList(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CLASS_FOR_LIST;
			_msgMinLength = 46;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameUserCharacterClassForList(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CLASS_FOR_LIST;
			_msgMinLength = 46;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameUserCharacterClassForList() {}

		inline void initDefaultValues() {
			classId = 0;
			premiumOnly = 0;
			graphicId = 0;
			graphicsX = 0;
			graphicsY = 0;
			graphicsDim = 0;
			healthBase = 0;
			healthModifier = 0;
			manaBase = 0;
			manaModifier = 0;
			attackBase = 0;
			attackModifier = 0;
			defenseBase = 0;
			defenseModifier = 0;
			damageBase = 0;
			damageModifier = 0;
			skillBase = 0;
			skillModifier = 0;
			magicBase = 0;
			magicModifier = 0;
			healthregenerateBase = 0;
			healthregenerateModifier = 0;
			manaregenerateBase = 0;
			manaregenerateModifier = 0;
			nameLength = 0;
			name = "";
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
			classId = min::NetPort::uintFrom4Bytes(bytes, 4);
			premiumOnly = min::NetPort::uintFromByte(bytes, 8);
			graphicId = min::NetPort::uintFrom4Bytes(bytes, 9);
			graphicsX = min::NetPort::uintFrom2Bytes(bytes, 13);
			graphicsY = min::NetPort::uintFrom2Bytes(bytes, 15);
			graphicsDim = min::NetPort::uintFromByte(bytes, 17);
			healthBase = min::NetPort::uintFrom2Bytes(bytes, 18);
			healthModifier = min::NetPort::uintFromByte(bytes, 20);
			manaBase = min::NetPort::uintFrom2Bytes(bytes, 21);
			manaModifier = min::NetPort::uintFromByte(bytes, 23);
			attackBase = min::NetPort::uintFrom2Bytes(bytes, 24);
			attackModifier = min::NetPort::uintFromByte(bytes, 26);
			defenseBase = min::NetPort::uintFrom2Bytes(bytes, 27);
			defenseModifier = min::NetPort::uintFromByte(bytes, 29);
			damageBase = min::NetPort::uintFrom2Bytes(bytes, 30);
			damageModifier = min::NetPort::uintFromByte(bytes, 32);
			skillBase = min::NetPort::uintFrom2Bytes(bytes, 33);
			skillModifier = min::NetPort::uintFromByte(bytes, 35);
			magicBase = min::NetPort::uintFrom2Bytes(bytes, 36);
			magicModifier = min::NetPort::uintFromByte(bytes, 38);
			healthregenerateBase = min::NetPort::uintFrom2Bytes(bytes, 39);
			healthregenerateModifier = min::NetPort::uintFromByte(bytes, 41);
			manaregenerateBase = min::NetPort::uintFrom2Bytes(bytes, 42);
			manaregenerateModifier = min::NetPort::uintFromByte(bytes, 44);
			nameLength = min::NetPort::uintFromByte(bytes, 45);
			if(46 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 16) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 46);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(classId, bytes, 4);
			min::NetPort::uintToByte(premiumOnly, bytes, 8);
			min::NetPort::uintTo4Bytes(graphicId, bytes, 9);
			min::NetPort::uintTo2Bytes(graphicsX, bytes, 13);
			min::NetPort::uintTo2Bytes(graphicsY, bytes, 15);
			min::NetPort::uintToByte(graphicsDim, bytes, 17);
			min::NetPort::uintTo2Bytes(healthBase, bytes, 18);
			min::NetPort::uintToByte(healthModifier, bytes, 20);
			min::NetPort::uintTo2Bytes(manaBase, bytes, 21);
			min::NetPort::uintToByte(manaModifier, bytes, 23);
			min::NetPort::uintTo2Bytes(attackBase, bytes, 24);
			min::NetPort::uintToByte(attackModifier, bytes, 26);
			min::NetPort::uintTo2Bytes(defenseBase, bytes, 27);
			min::NetPort::uintToByte(defenseModifier, bytes, 29);
			min::NetPort::uintTo2Bytes(damageBase, bytes, 30);
			min::NetPort::uintToByte(damageModifier, bytes, 32);
			min::NetPort::uintTo2Bytes(skillBase, bytes, 33);
			min::NetPort::uintToByte(skillModifier, bytes, 35);
			min::NetPort::uintTo2Bytes(magicBase, bytes, 36);
			min::NetPort::uintToByte(magicModifier, bytes, 38);
			min::NetPort::uintTo2Bytes(healthregenerateBase, bytes, 39);
			min::NetPort::uintToByte(healthregenerateModifier, bytes, 41);
			min::NetPort::uintTo2Bytes(manaregenerateBase, bytes, 42);
			min::NetPort::uintToByte(manaregenerateModifier, bytes, 44);
			min::NetPort::uintToByte(nameLength, bytes, 45);
			min::NetPort::stringToBytes(name, bytes, nameLength, 46);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 16) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength;
			return true;
		}

		inline bool isValid() {
			return _msgIsValid;
		}

	// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%
		void fromCharacterClass(const CharacterClass& classObj) {
			classId = classObj.getId();
			premiumOnly = (classObj.getAvailableStatus() == AvailableStatus::premium_only) ? 1 : 0;
			graphicId = classObj.getGraphicId();
			graphicsX = classObj.getGraphicsX();
			graphicsY = classObj.getGraphicsY();
			graphicsDim = classObj.getGraphicsDim();
			healthBase = classObj.getHealthBase();
			healthModifier = classObj.getHealthModifier();
			manaBase = classObj.getManaBase();
			manaModifier = classObj.getManaModifier();
			attackBase = classObj.getAttackBase();
			attackModifier = classObj.getAttackModifier();
			defenseBase = classObj.getDefenseBase();
			defenseModifier = classObj.getDefenseModifier();
			damageBase = classObj.getDamageBase();
			damageModifier = classObj.getDamageModifier();
			skillBase = classObj.getSkillBase();
			skillModifier = classObj.getSkillModifier();
			magicBase = classObj.getMagicBase();
			magicModifier = classObj.getMagicModifier();
			healthregenerateBase = classObj.getHealthregenerateBase();
			healthregenerateModifier = classObj.getHealthregenerateModifier();
			manaregenerateBase = classObj.getManaregenerateBase();
			manaregenerateModifier = classObj.getManaregenerateModifier();
			name = classObj.getDisplayName();

			//std::cout << "from obj: got name: " << name << std::endl;
			checkValidateLength();
		}
	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
