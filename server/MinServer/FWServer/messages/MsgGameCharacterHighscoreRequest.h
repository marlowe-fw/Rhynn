#ifndef MsgGameCharacterHighscoreRequest_h__
#define MsgGameCharacterHighscoreRequest_h__


#include "MinMessage.h"
#include "../FWSMessageIDs.h"

// %%GENERATOR_START%%MSG_IMPL_USER_HEADERS%%
// %%GENERATOR_END%%MSG_IMPL_USER_HEADERS%%

namespace fws {

/**
 * Request highscores from server for the highscore list (co).
 */

class MsgGameCharacterHighscoreRequest : public min::MinMessage {


	public:
		/** First rank to display (highest rank is 1). */
		unsigned int startRank;
		/** Number of ranks to display. */
		unsigned int numRanks;


		MsgGameCharacterHighscoreRequest(bool init = true) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST;
			_msgMinLength = 10;
			length = _msgMinLength;
			if (init) {initDefaultValues();}
			_msgIsValid = true;
		}

		MsgGameCharacterHighscoreRequest(const unsigned char* buf) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST;
			_msgMinLength = 10;
			_msgIsValid = true;
			valuesFromBytes(buf);
		}

		MsgGameCharacterHighscoreRequest(const unsigned char* buf, unsigned int totalLength) {
			msgId = FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST;
			_msgMinLength = 10;
			_msgIsValid = true;
			length = totalLength;
			if (_msgMinLength <= length) {
				valuesFromBytes(buf, false);
			} else {
				_msgIsValid = false;
			}
		}

		virtual ~MsgGameCharacterHighscoreRequest() {}

		inline void initDefaultValues() {
			startRank = 0;
			numRanks = 0;
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
			startRank = min::NetPort::uintFrom4Bytes(bytes, 4);
			numRanks = min::NetPort::uintFrom2Bytes(bytes, 8);
			return true;
		}

		bool valuesToBytes(unsigned char* bytes, bool validateLength = true) {
			if (validateLength && !checkValidateLength()) {
				return false;
			}
			min::NetPort::uintToByte(length, bytes, 0);
			min::NetPort::uintTo3Bytes(msgId, bytes, 1);
			min::NetPort::uintTo4Bytes(startRank, bytes, 4);
			min::NetPort::uintTo2Bytes(numRanks, bytes, 8);
			return true;
		}

		inline bool checkValidateLength() {
			length = _msgMinLength ;
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
