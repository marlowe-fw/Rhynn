#ifndef MsgGameItemInventoryAdd_h__
#define MsgGameItemInventoryAdd_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "world_objects/Item.h"
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Send an item character entry to the client to add to the inventory (so).
 */

class MsgGameItemInventoryAdd : public min::MinMessage {


	public:
		/** -. */
		unsigned int objectId;
		/** -. */
		int clientTypeId;
		/** -. */
		unsigned int usageType;
		/** Set this item belongs to, if any. */
		unsigned int setId;
		/** -. */
		unsigned int graphicId;
		/** -. */
		unsigned int graphicsX;
		/** -. */
		unsigned int graphicsY;
		/** -. */
		unsigned int premiumOnly;
		/** -. */
		unsigned int canSell;
		/** -. */
		unsigned int canDrop;
		/** -. */
		unsigned int units;
		/** -. */
		unsigned int unitsSell;
		/** -. */
		unsigned int price;
		/** -. */
		unsigned int equippedStatus;
		/** -. */
		unsigned int healthEffect;
		/** -. */
		unsigned int manaEffect;
		/** -. */
		unsigned int attackEffect;
		/** -. */
		unsigned int defenseEffect;
		/** -. */
		unsigned int damageEffect;
		/** -. */
		unsigned int skillEffect;
		/** -. */
		unsigned int magicEffect;
		/** -. */
		unsigned int healthRegenerateEffect;
		/** -. */
		unsigned int manaRegenerateEffect;
		/** -. */
		unsigned int actionEffect1;
		/** -. */
		unsigned int actionEffect2;
		/** -. */
		unsigned int effectDuration;
		/** -. */
		unsigned int requiredSkill;
		/** -. */
		unsigned int requiredMagic;
		/** -. */
		unsigned int frequency;
		/** -. */
		unsigned int range;
		/** -. */
		unsigned int nameLength;
		/** -. */
		std::string name;
		/** -. */
		unsigned int descriptionLength;
		/** -. */
		std::string description;


		MsgGameItemInventoryAdd(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD;
			_msgMinLength = 68;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameItemInventoryAdd(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD;
			_msgMinLength = 68;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameItemInventoryAdd(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_ITEM_INVENTORY_ADD;
			_msgMinLength = 68;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameItemInventoryAdd() {}

		inline void initDefaultValues() {
			objectId = 0;
			clientTypeId = 0;
			usageType = 0;
			setId = 0;
			graphicId = 0;
			graphicsX = 0;
			graphicsY = 0;
			premiumOnly = 0;
			canSell = 0;
			canDrop = 0;
			units = 0;
			unitsSell = 0;
			price = 0;
			equippedStatus = 0;
			healthEffect = 0;
			manaEffect = 0;
			attackEffect = 0;
			defenseEffect = 0;
			damageEffect = 0;
			skillEffect = 0;
			magicEffect = 0;
			healthRegenerateEffect = 0;
			manaRegenerateEffect = 0;
			actionEffect1 = 0;
			actionEffect2 = 0;
			effectDuration = 0;
			requiredSkill = 0;
			requiredMagic = 0;
			frequency = 0;
			range = 0;
			nameLength = 0;
			name = "";
			descriptionLength = 0;
			description = "";
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
			clientTypeId = min::NetPort::intFrom4Bytes(bytes, 8);
			usageType = min::NetPort::uintFromByte(bytes, 12);
			setId = min::NetPort::uintFrom4Bytes(bytes, 13);
			graphicId = min::NetPort::uintFrom4Bytes(bytes, 17);
			graphicsX = min::NetPort::uintFrom2Bytes(bytes, 21);
			graphicsY = min::NetPort::uintFrom2Bytes(bytes, 23);
			premiumOnly = min::NetPort::uintFromByte(bytes, 25);
			canSell = min::NetPort::uintFromByte(bytes, 26);
			canDrop = min::NetPort::uintFromByte(bytes, 27);
			units = min::NetPort::uintFrom2Bytes(bytes, 28);
			unitsSell = min::NetPort::uintFrom2Bytes(bytes, 30);
			price = min::NetPort::uintFrom3Bytes(bytes, 32);
			equippedStatus = min::NetPort::uintFromByte(bytes, 35);
			healthEffect = min::NetPort::uintFrom2Bytes(bytes, 36);
			manaEffect = min::NetPort::uintFrom2Bytes(bytes, 38);
			attackEffect = min::NetPort::uintFrom2Bytes(bytes, 40);
			defenseEffect = min::NetPort::uintFrom2Bytes(bytes, 42);
			damageEffect = min::NetPort::uintFrom2Bytes(bytes, 44);
			skillEffect = min::NetPort::uintFrom2Bytes(bytes, 46);
			magicEffect = min::NetPort::uintFrom2Bytes(bytes, 48);
			healthRegenerateEffect = min::NetPort::uintFrom2Bytes(bytes, 50);
			manaRegenerateEffect = min::NetPort::uintFrom2Bytes(bytes, 52);
			actionEffect1 = min::NetPort::uintFrom2Bytes(bytes, 54);
			actionEffect2 = min::NetPort::uintFrom2Bytes(bytes, 56);
			effectDuration = min::NetPort::uintFrom2Bytes(bytes, 58);
			requiredSkill = min::NetPort::uintFrom2Bytes(bytes, 60);
			requiredMagic = min::NetPort::uintFrom2Bytes(bytes, 62);
			frequency = min::NetPort::uintFromByte(bytes, 64);
			range = min::NetPort::uintFromByte(bytes, 65);
			nameLength = min::NetPort::uintFromByte(bytes, 66);
			if(67 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 24) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 67);
			unsigned int curIndex = 67 + nameLength;
			if(curIndex + 1 > length) {
				_msgIsValid = false;
				return false;
			}
			descriptionLength = min::NetPort::uintFromByte(bytes, curIndex);
			curIndex += 1;
			if(curIndex + descriptionLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (descriptionLength > 48) {
				_msgIsValid = false;
				return false;
			}
			description = min::NetPort::stringFromBytes(bytes, descriptionLength, curIndex);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(objectId, bytes, 4);
			min::NetPort::intTo4Bytes(clientTypeId, bytes, 8);
			min::NetPort::uintToByte(usageType, bytes, 12);
			min::NetPort::uintTo4Bytes(setId, bytes, 13);
			min::NetPort::uintTo4Bytes(graphicId, bytes, 17);
			min::NetPort::uintTo2Bytes(graphicsX, bytes, 21);
			min::NetPort::uintTo2Bytes(graphicsY, bytes, 23);
			min::NetPort::uintToByte(premiumOnly, bytes, 25);
			min::NetPort::uintToByte(canSell, bytes, 26);
			min::NetPort::uintToByte(canDrop, bytes, 27);
			min::NetPort::uintTo2Bytes(units, bytes, 28);
			min::NetPort::uintTo2Bytes(unitsSell, bytes, 30);
			min::NetPort::uintTo3Bytes(price, bytes, 32);
			min::NetPort::uintToByte(equippedStatus, bytes, 35);
			min::NetPort::uintTo2Bytes(healthEffect, bytes, 36);
			min::NetPort::uintTo2Bytes(manaEffect, bytes, 38);
			min::NetPort::uintTo2Bytes(attackEffect, bytes, 40);
			min::NetPort::uintTo2Bytes(defenseEffect, bytes, 42);
			min::NetPort::uintTo2Bytes(damageEffect, bytes, 44);
			min::NetPort::uintTo2Bytes(skillEffect, bytes, 46);
			min::NetPort::uintTo2Bytes(magicEffect, bytes, 48);
			min::NetPort::uintTo2Bytes(healthRegenerateEffect, bytes, 50);
			min::NetPort::uintTo2Bytes(manaRegenerateEffect, bytes, 52);
			min::NetPort::uintTo2Bytes(actionEffect1, bytes, 54);
			min::NetPort::uintTo2Bytes(actionEffect2, bytes, 56);
			min::NetPort::uintTo2Bytes(effectDuration, bytes, 58);
			min::NetPort::uintTo2Bytes(requiredSkill, bytes, 60);
			min::NetPort::uintTo2Bytes(requiredMagic, bytes, 62);
			min::NetPort::uintToByte(frequency, bytes, 64);
			min::NetPort::uintToByte(range, bytes, 65);
			min::NetPort::uintToByte(nameLength, bytes, 66);
			min::NetPort::stringToBytes(name, bytes, nameLength, 67);
			unsigned int curIndex = 67 + nameLength;
			min::NetPort::uintToByte(descriptionLength, bytes, curIndex);
			curIndex += 1;
			min::NetPort::stringToBytes(description, bytes, descriptionLength, curIndex);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 24) {
				_msgIsValid = false; 
				return false;
			}
			descriptionLength = (int)description.size();
			if (descriptionLength > 48) {
				_msgIsValid = false; 
				return false;
			}
			length = _msgMinLength  + nameLength + descriptionLength;
			return true;
		}

		inline bool isValid() {
			return _msgIsValid;
		}

	// %%GENERATOR_START%%MSG_IMPL_USER_CONTENT%%
		void fromItem(const Item& i) {
			objectId = i.getId();
			clientTypeId = i.getClientTypeId();
			setId = i.getSetId();
			graphicId = i.getGraphicId();
			graphicsX = i.getGraphicsX();
			graphicsY = i.getGraphicsY();
			premiumOnly = (i.getAvailableStatus() == AvailableStatus::premium_only) ? 1 : 0;
			usageType = (i.getUsageType()).val();
			canSell = i.getCanSell().naturalVal();
			canDrop = i.getCanDrop().naturalVal();
			units = i.getUnits();
			unitsSell = i.getUnitsSell();
			price = i.getPrice();
			equippedStatus = i.getEquippedStatus().val();
			healthEffect = i.getHealthEffect();
			manaEffect = i.getManaEffect();
			attackEffect = i.getAttackEffect();
			defenseEffect = i.getDefenseEffect();
			damageEffect = i.getDamageEffect();
			skillEffect = i.getSkillEffect();
			magicEffect = i.getMagicEffect();
			healthRegenerateEffect = i.getHealthregenerateEffect();
			manaRegenerateEffect = i.getManaregenerateEffect();
			actionEffect1 = i.getActionEffect1();
			actionEffect2 = i.getActionEffect2();
			effectDuration = i.getEffectDuration();
			requiredSkill = i.getRequiredSkill();
			requiredMagic = i.getRequiredMagic();
			frequency = i.getFrequency();
			range = i.getRange();
			name = i.getName();
			description = i.getDescription();
			checkValidateLength();

			//std::cout << "desc length: "<< description.size() << std::endl;
			//std::cout << "desc length2: "<< ((int)description.size()) << std::endl;

			//std::cout << "sending item, message length: " << length << std::endl;
		}

	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
