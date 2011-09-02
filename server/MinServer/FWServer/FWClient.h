#ifndef FWClient_h__
#define FWClient_h__

#include "MinClient.h"
#include "world_objects/User.h"
#include "FWPacketValidator.h"

using namespace fwworld;

namespace fws {


class FWClient : public min::MinClient {
	public:
		FWClient(int bufferSize);
		FWClient(int bufferSize, unsigned long outBufferLaneSize, unsigned int outBufferMaxLanes);
		virtual ~FWClient();

		void createPacketValidator();

		/** For use with the test messages only (listener index). */
		inline int dataGetIndex() {return index;}
		/** For use with the test messages only (listener index). */
		inline void dataSetIndex(int newIndex) {index = newIndex;}

		/** Get the user associated with this client. */
		inline User* getUser() {
			if (user!=0) {
				return user.get();
			} else {
				return 0;
			}
		}
		/** Set the user for this client, it takes ownership of the user. */
		inline void setUser(SPUser newUser) {user = newUser;}

		/**
		 * Check if this client is associated with a user.
		 * @return true If associated with a user, false otherwise
		 */
		inline bool hasUser() {return user != 0;}

		/**
		* Check if this client is associated with a user which has been validated (login ok and challenge number test ok).
		* @return true If associated with a validated user, false otherwise
		*/
		inline bool hasValidatedUser() {return hasUser() && user->getIsValidated();}

		/**
		* Check if this client is associated with a user which has a selected character (only possible for validated users).
		* @return true If associated with a selected character, false otherwise
		*/
		inline bool hasSelectedCharacter() {
			return hasUser() && user->hasSelectedCharacter();
		}


		inline void initializePacketValidator(int newValue) {
			packetValidator->initialize(newValue);
		}

		inline bool isPacketValid(const unsigned char* msg, int len) {
			return packetValidator->isPacketValid(msg, len, usesMessageSignatures);
		}


		/**
		 * Set whether this client is required to send a message signature with every message which is sent to the server.
		 * @param newValue Set to true if this client must use packet signatures, set to false otherwise
		 */
		inline void setUsesMessageSignatures(bool newValue) {
			usesMessageSignatures = newValue;
		}

		/**
		 * Get whether this client is required to send a message signature with every message which is sent to the server.
		 * @param newValue true if this client must use packet signatures, false otherwise
		 */
		inline bool getUsesMessageSignatures() const {
			return usesMessageSignatures;
		}

		inline clock_ms_t getLastActive() {
			return lastActive;
		}

		inline void setLastActive(clock_ms_t time) {
			lastActive = time;
		}

		bool conditionApplies(int condition);

	private:

		FWPacketValidator* packetValidator;


		/** For use with the test messages only (listener index). */
		int index;
		/** The user object associated with this client, the client owns the user because it defines the life duration of the user. */
		SPUser user;

		bool usesMessageSignatures;


		clock_ms_t lastActive;
};


}

#endif // FWClient_h__
