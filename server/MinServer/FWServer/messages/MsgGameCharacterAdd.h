#ifndef MsgGameCharacterAdd_h__
#define MsgGameCharacterAdd_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
#include "world_objects/Character.h"
using namespace fwworld;
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Server informs a client that a character is now in his interest zone, i.e. visible and relevant (so).
 */

class MsgGameCharacterAdd : public min::MinMessage {


	public:
		/** -. */
		unsigned int objectId;
		/** -. */
		unsigned int clanId;
		/** -. */
		unsigned int graphicId;
		/** -. */
		unsigned int graphicsX;
		/** -. */
		unsigned int graphicsY;
		/** -. */
		unsigned int graphicsDim;
		/** -. */
		unsigned int level;
		/** -. */
		unsigned int x;
		/** -. */
		unsigned int y;
		/** -. */
		unsigned int direction;
		/** -. */
		unsigned int healthCurrent;
		/** -. */
		unsigned int maxHealth;
		/** -. */
		unsigned int nameLength;
		/** -. */
		std::string name;


		MsgGameCharacterAdd(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_ADD;
			_msgMinLength = 30;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterAdd(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_ADD;
			_msgMinLength = 30;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterAdd(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_ADD;
			_msgMinLength = 30;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterAdd() {}

		inline void initDefaultValues() {
			objectId = 0;
			clanId = 0;
			graphicId = 0;
			graphicsX = 0;
			graphicsY = 0;
			graphicsDim = 0;
			level = 0;
			x = 0;
			y = 0;
			direction = 0;
			healthCurrent = 0;
			maxHealth = 0;
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
			objectId = min::NetPort::uintFrom4Bytes(bytes, 4);
			clanId = min::NetPort::uintFrom2Bytes(bytes, 8);
			graphicId = min::NetPort::uintFrom4Bytes(bytes, 10);
			graphicsX = min::NetPort::uintFrom2Bytes(bytes, 14);
			graphicsY = min::NetPort::uintFrom2Bytes(bytes, 16);
			graphicsDim = min::NetPort::uintFromByte(bytes, 18);
			level = min::NetPort::uintFromByte(bytes, 19);
			x = min::NetPort::uintFrom2Bytes(bytes, 20);
			y = min::NetPort::uintFrom2Bytes(bytes, 22);
			direction = min::NetPort::uintFromByte(bytes, 24);
			healthCurrent = min::NetPort::uintFrom2Bytes(bytes, 25);
			maxHealth = min::NetPort::uintFrom2Bytes(bytes, 27);
			nameLength = min::NetPort::uintFromByte(bytes, 29);
			if(30 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 30);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(objectId, bytes, 4);
			min::NetPort::uintTo2Bytes(clanId, bytes, 8);
			min::NetPort::uintTo4Bytes(graphicId, bytes, 10);
			min::NetPort::uintTo2Bytes(graphicsX, bytes, 14);
			min::NetPort::uintTo2Bytes(graphicsY, bytes, 16);
			min::NetPort::uintToByte(graphicsDim, bytes, 18);
			min::NetPort::uintToByte(level, bytes, 19);
			min::NetPort::uintTo2Bytes(x, bytes, 20);
			min::NetPort::uintTo2Bytes(y, bytes, 22);
			min::NetPort::uintToByte(direction, bytes, 24);
			min::NetPort::uintTo2Bytes(healthCurrent, bytes, 25);
			min::NetPort::uintTo2Bytes(maxHealth, bytes, 27);
			min::NetPort::uintToByte(nameLength, bytes, 29);
			min::NetPort::stringToBytes(name, bytes, nameLength, 30);
			return true;
		}

		inline bool checkValidateLength() {
			nameLength = (int)name.size();
			if (nameLength > 12) {
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
	void fromCharacter(const Character& c) {
		objectId = c.getId();
		clanId = c.getId();
		graphicId = c.getGraphicId();
		graphicsX = c.getGraphicsX();
		graphicsY = c.getGraphicsY();
		graphicsDim = c.getGraphicsDim();
		level = c.getLevel();
		x = c.getX();
		y = c.getY();
		direction = c.getDirection().intVal();
		healthCurrent = c.getHealthCurrent();
		maxHealth = c.getMaxHealth();
		name = c.getName();

		checkValidateLength();
	}




	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
