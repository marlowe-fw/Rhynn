#include "FWServer.h"
#include "FWClient.h"
#include "world_objects/Character.h"
#include "world_objects/Playfield.h"
#include "Random.h"

#include "state/ClientCondition.h"

#include "messages/MsgGameCharacterMove.h"
#include "messages/MsgGameCharacterMoveInfo.h"
#include "messages/MsgGameCharacterAdd.h"
#include "messages/MsgGameCharacterRemove.h"
#include "messages/MsgGameCharacterChatAllInfo.h"
#include "messages/MsgGameCharacterChatInfo.h"
#include "messages/MsgGameCharacterHitInfo.h"
#include "messages/MsgGameCharacterHitMissInfo.h"
#include "messages/MsgGameCharacterKilled.h"

using namespace fws;

ModuleCharacterInteraction::ModuleCharacterInteraction(FWServer& newServer, FWWorld& newWorld)
: server(newServer), world(newWorld)
{}

// %%GENERATOR_START%%HANDLER_IMPL%%

/**
 * Character moves on the playfield, this is the single most frequent message sent by clients (co).
 */
bool ModuleCharacterInteraction::handleMessageGameCharacterMove(FWClient* pCurClient, MsgGameCharacterMove& msgObj) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameCharacterMove");
		return false;
	}

	Character* c = pCurClient->getUser()->getSelectedCharacter();

	Playfield* pf = world.getPlayfield(c->getPlayfieldId());
	unsigned int curCharacterId = c->getId();

	// todo: check timing, last move, distance etc.

	unsigned int newX = msgObj.x;
	unsigned int newY = msgObj.y;

	if (!pf->validPositionForCharacter(c, newX, newY)) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to walk onto invalid position.");
		return false;
	}

	unsigned int newCellX = static_cast<unsigned int>(newX / PlayfieldCell::c_DefaultWidth);
	unsigned int newCellY = static_cast<unsigned int>(newY / PlayfieldCell::c_DefaultHeight);
	unsigned int curCellX = static_cast<unsigned int>(c->getX() / PlayfieldCell::c_DefaultWidth);
	unsigned int curCellY = static_cast<unsigned int>(c->getY() / PlayfieldCell::c_DefaultHeight);

	if (newCellX != curCellX || newCellY != curCellY) {
		server.onCharacterCellChange(c, pCurClient, pf, curCellX, curCellY, newCellX, newCellY);
	}


	// everything fine, set new position
	c->setX(newX);
	c->setY(newY);
	c->setDirection(Direction(msgObj.direction));

	// broadcast position to others
	// todo: this can be optimized: when there was a cell change, some of the character will have already gotten an add character message
	std::vector<Character*> others;
	pf->appendObjectsInVisRangePx(c->getX(), c->getY(), &others, 0);

	MsgGameCharacterMoveInfo mvMsg;
	mvMsg.x = c->getX();
	mvMsg.y = c->getY();
	mvMsg.direction = c->getDirection().intVal();
	mvMsg.objectId = curCharacterId;

	server.broadCastMessageToCharacters(others, mvMsg, curCharacterId);

	return true;
}


/**
 * Client sends request to deliver a public chat message to all in range (co).
 */
bool ModuleCharacterInteraction::handleMessageGameCharacterChatAllRequest(FWClient* pCurClient, MsgGameCharacterChatAllRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_active)) {
		server.handleClientError(pCurClient, "handleMessageGameCharacterChatAllRequest");
		return false;
	}

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	if (c->isDead())
		return false;

	Playfield* pf = world.getPlayfield(c->getPlayfieldId());

	MsgGameCharacterChatAllInfo bcMsg;
	bcMsg.chatMsg = msg.chatMsg;
	bcMsg.objectId = c->getId();
	bcMsg.checkValidateLength();

	server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(c->getX(), c->getY(), *pf, bcMsg, c->getId());

	return true;
}

/**
 * Client sends request to deliver a private / chat message (co).
 */
bool ModuleCharacterInteraction::handleMessageGameCharacterChatRequest(FWClient* pCurClient, MsgGameCharacterChatRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_active)) {
		server.handleClientError(pCurClient, "handleMessageGameCharacterChatRequest");
		return false;
	}


	Character* c = pCurClient->getUser()->getSelectedCharacter();

	unsigned int receiverId = msg.receiverId;
	Character* receiverCharacter = world.getLiveCharacter(receiverId);

	bool isOnline = false;
	if (receiverCharacter!=0) {
		User* u = receiverCharacter->getUser();
		if (u!=0 && u->getClient()!=0) {
			isOnline = true;
			MsgGameCharacterChatInfo outMsg;
			outMsg.senderId = c->getId();
			outMsg.receiverId = msg.receiverId;
			outMsg.senderName = c->getName();
			outMsg.chatMsg = msg.chatMsg;
			outMsg.checkValidateLength();
			server.queueMessageForSending(outMsg, u->getClient(), false);
		}
	}

	if (!isOnline) {
		// todo: send some sort of notification that this message was not delivered because receiver was no longer online

	}

	return true;
}

/**
 * Client requests to attack / hit another character (co). This should be among the most frequent incoming messages after the movement messages..
 */
bool ModuleCharacterInteraction::handleMessageGameCharacterAttackRequest(FWClient* pCurClient, MsgGameCharacterAttackRequest& msg) {
	if (!pCurClient->conditionApplies(ClientCondition::character_alive)) {
		if (!pCurClient->conditionApplies(ClientCondition::character_active))
			server.handleClientError(pCurClient, "handleMessageGameCharacterAttackRequest");
		return false;
	}


	Character* c = pCurClient->getUser()->getSelectedCharacter();
	Playfield* playfield = c->getPlayfield();

	Character* target = playfield->getCharacter(msg.targetId);
	if (target==0 || target->getActiveStatus() != Character::as_active || target->isDead()) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to attack a character while target was inactive or otherwise not suitable for attack.");
		return false;
	}

	// todo: check within attack range
	// todo: check attackPossible / peaceful
	// todo: check fight timestamps / frequency


	// calculate whether hit or miss
	int pSum = c->getMaxAttack() + c->getMaxDefense();
	int pThreshold = 2 * (int)((float)c->getMaxDefense() / 3.5f);

	float pRandom = (float)(fwutil::Random::nextInt(pSum));


	if (pRandom >= pThreshold) {
		// hit
		// calculate hit
		int damage = (int)((fwutil::Random::nextInt(c->getMaxDamage())) * 0.14f);

		if (damage < 1)
			damage = 1;

		int curHealth = target->getHealthCurrent();

		// check dead
		if (curHealth <= damage) {

			// todo: factor out to onCharacterKilled()

			// todo: check reward experience, consider shared experience
			// todo: update kill statistics
			// todo: report clan kills

			// send hit message to all in range, include health info
			MsgGameCharacterKilled killedMsg;
			killedMsg.characterId = target->getId();
			server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(target->getX(), target->getY(), *playfield, killedMsg, 0);

			playfield->removeCharacter(target);

			// todo: for bots do not adjust the health level / reset health to 100%
			target->setHealthCurrent((int)(target->getMaxHealth() * 0.75f));
			target->setManaCurrent((int)(target->getMaxMana() * 0.75f));
			//target->setX(target->getRespawnX());
			//target->setY(target->getRespawnY());
			target->storeToDB();

			target->setDead(true);


		} else {
			// subtract hit
			target->setHealthCurrent(curHealth - damage);

			// send hit message to all in range, include health info
			MsgGameCharacterHitInfo hitMsg;
			hitMsg.attackerId = c->getId();
			hitMsg.targetId = target->getId();
			hitMsg.hitValue = 0;
			hitMsg.curHealth = target->getHealthCurrent();

			server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(target->getX(), target->getY(), *playfield, hitMsg, 0);
		}


	} else {
		// miss
		// send defend message to all in range
		MsgGameCharacterHitMissInfo hitMissMsg;
		hitMissMsg.attackerId = c->getId();
		hitMissMsg.targetId = target->getId();
		server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(target->getX(), target->getY(), *playfield, hitMissMsg, 0);
	}


	return true;
}
// %%GENERATOR_END%%HANDLER_IMPL%%