#ifndef MsgGameCharacterHighscoreListEntry_h__
#define MsgGameCharacterHighscoreListEntry_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * One single list entry in for display in the highscore list (so).
 */

class MsgGameCharacterHighscoreListEntry : public min::MinMessage {


	public:
		/** -. */
		unsigned int rank;
		/** -. */
		unsigned int experience;
		/** Current index in the list. */
		unsigned int listIndex;
		/** Total list entires that are to be sent. */
		unsigned int listLength;
		/** Length of the character name. */
		unsigned int nameLength;
		/** Character name to display. */
		std::string name;


		MsgGameCharacterHighscoreListEntry(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_LIST_ENTRY;
			_msgMinLength = 15;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterHighscoreListEntry(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_LIST_ENTRY;
			_msgMinLength = 15;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterHighscoreListEntry(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_LIST_ENTRY;
			_msgMinLength = 15;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterHighscoreListEntry() {}

		inline void initDefaultValues() {
			rank = 0;
			experience = 0;
			listIndex = 0;
			listLength = 0;
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
			rank = min::NetPort::uintFrom4Bytes(bytes, 4);
			experience = min::NetPort::uintFrom4Bytes(bytes, 8);
			listIndex = min::NetPort::uintFromByte(bytes, 12);
			listLength = min::NetPort::uintFromByte(bytes, 13);
			nameLength = min::NetPort::uintFromByte(bytes, 14);
			if(15 + nameLength > length) {
				_msgIsValid = false;
				return false;
			}
			if (nameLength > 12) {
				_msgIsValid = false;
				return false;
			}
			name = min::NetPort::stringFromBytes(bytes, nameLength, 15);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(rank, bytes, 4);
			min::NetPort::uintTo4Bytes(experience, bytes, 8);
			min::NetPort::uintToByte(listIndex, bytes, 12);
			min::NetPort::uintToByte(listLength, bytes, 13);
			min::NetPort::uintToByte(nameLength, bytes, 14);
			min::NetPort::stringToBytes(name, bytes, nameLength, 15);
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
	// %%GENERATOR_END%%MSG_IMPL_USER_CONTENT%%

};

}

#endif
