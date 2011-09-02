#include "FWClient.h"
#include "state/ClientCondition.h"
#ifdef __VALIDATOR_IMPL
#include "crypto/FWPacketValidatorImpl.h"
#endif

using namespace fws;

FWClient::FWClient(int bufferSize) : min::MinClient(bufferSize, 16384, 7),
usesMessageSignatures(false),
lastActive(0)
{
	// since packets for FW are small, insist on disabling the tcp delay
	disallowTCPDelay();
	createPacketValidator();
}


FWClient::FWClient(int bufferSize, unsigned long outBufferLaneSize, unsigned int outBufferMaxLanes) : min::MinClient(bufferSize, outBufferLaneSize, outBufferMaxLanes),
usesMessageSignatures(false),
lastActive(0)
{
	// since packets for FW are small, insist on disabling the tcp delay
	disallowTCPDelay();
	createPacketValidator();
}

FWClient::~FWClient() {
	std::cout << "Client in FW destructed: " << clientSocket << std::endl;
	delete packetValidator;
}

void FWClient::createPacketValidator() {
#ifdef __VALIDATOR_IMPL
	packetValidator = new FWPacketValidatorImpl();
#else
	packetValidator = new FWPacketValidator();
#endif
}

bool FWClient::conditionApplies(int condition) {

	switch (condition) {

		case ClientCondition::character_selected:
			return hasSelectedCharacter();

		case ClientCondition::character_active:
			return hasSelectedCharacter() && getUser()->getSelectedCharacter()->getActiveStatus() == Character::as_active;

		case ClientCondition::character_alive:
			if (hasSelectedCharacter()) {
				Character* c = getUser()->getSelectedCharacter();
				return !c->isDead();
			}
	}
	return false;
}