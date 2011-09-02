#include "ModulePlayfields.h"
#include "FWServer.h"
#include "FWClient.h"
#include "FWWorld.h"
#include "world_objects/Playfield.h"


#include "messages/MsgGamePlayfieldInfo.h"
#include "messages/MsgGamePlayfieldGraphicsInfo.h"
#include "messages/MsgGamePlayfieldLoadChunk.h"
#include "messages/MsgGamePlayfieldEnterResult.h"
#include "messages/MsgGameCharacterAdd.h"
#include "messages/MsgGameItemAdd.h"
#include "messages/MsgGameItemRemove.h"
#include "messages/MsgGameCharacterRespawnResult.h"
#include "messages/MsgGameCharacterRemove.h"

using namespace fws;
using namespace fwworld;
using namespace fwutil;


ModulePlayfields::ModulePlayfields(FWServer& newServer, FWWorld& newWorld)
: server(newServer), world(newWorld)
{}

// %%GENERATOR_START%%HANDLER_IMPL%%

/**
 * Character requests to come onto the world for the first time (after having selected the character for play).
 */
bool ModulePlayfields::handleMessageGamePlayfieldEnterWorldRequest(FWClient* pCurClient, MsgGamePlayfieldEnterWorldRequest& msgObj) {
	if (!pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGamePlayfieldEnterWorldRequest");
		return false;
	}

	Character* c = pCurClient->getUser()->getSelectedCharacter();
	if (c->getActiveStatus() != Character::as_inactive || world.getLiveCharacter(c->getId()) != 0) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to enter world while the character was not in an inactive state or already in the game.");
		return false;
	}
	// send playfield info
	// note: check access for user when actually requesting to step on the playfield / and possibly also when requesting the data
	Playfield* playfield = world.getPlayfield(c->getPlayfieldId());
	if (playfield == 0) {
		server.onClientError(pCurClient, "Character had a playfield set which does not exist: " + fwutil::Common::intToString(c->getPlayfieldId()));
		return false;
	}

	// mark this character as waiting to load the playfield
	c->setActiveStatus(Character::as_inactive_loadPlayfield);

	MsgGamePlayfieldInfo returnMsg;
	returnMsg.playfieldId = playfield->getId();
	returnMsg.name = playfield->getName();
	returnMsg.width = playfield->getWidth();
	returnMsg.height = playfield->getHeight();
	server.queueMessageForSending(returnMsg, pCurClient, true);

	// send playfield graphics info
	// this is the very raw way of doing it, so this will need refactoring and abstraction at some point

	std::vector<SPPlayfieldGraphic> bgGfx = playfield->getBackgroundGraphics();
	std::vector<SPPlayfieldGraphic> chGfx = playfield->getCharacterGraphics();

	MsgGamePlayfieldGraphicsInfo bgGiMsg;
	bgGiMsg.type = 0;	// todo: currently hardcoded!
	bgGiMsg.idBuffer.resetNew(((unsigned int)bgGfx.size())*4);
	MsgGamePlayfieldGraphicsInfo chGiMsg;
	chGiMsg.type = 1;	// todo: currently hardcoded!
	chGiMsg.idBuffer.resetNew(((unsigned int)chGfx.size())*4);

	int curIndex = 0;

	unsigned char* bgRawBuffer = bgGiMsg.idBuffer.getData();
	for (std::vector<SPPlayfieldGraphic>::iterator it = bgGfx.begin(); it != bgGfx.end(); it++) {
		SPPlayfieldGraphic g = (*it);
		min::NetPort::intTo4Bytes(g->getGraphicId(), bgRawBuffer, curIndex);
		curIndex += 4;
	}

	curIndex = 0;

	unsigned char* chRawBuffer = chGiMsg.idBuffer.getData();
	for (std::vector<SPPlayfieldGraphic>::iterator it = chGfx.begin(); it != chGfx.end(); it++) {
		SPPlayfieldGraphic g = (*it);
		min::NetPort::intTo4Bytes(g->getGraphicId(), chRawBuffer, curIndex);
		curIndex += 4;
	}

	server.queueMessageForSending(bgGiMsg, pCurClient, true);
	server.queueMessageForSending(chGiMsg, pCurClient, true);


	return true;
}

/**
 * Client requests to send data for the current playfield, server will need to check if client is indeed waiting to load the playfield (co).
 */
bool ModulePlayfields::handleMessageGamePlayfieldLoadRequest(FWClient* pCurClient, MsgGamePlayfieldLoadRequest& msgObj) {
	if (!pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGamePlayfieldDataRequest");
		return false;
	}

	// check conditions
	Character* c = pCurClient->getUser()->getSelectedCharacter();
	if (c->getActiveStatus() != Character::as_inactive_loadPlayfield || world.getLiveCharacter(c->getId()) != 0) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to request playfield data while the character was not in the playfield load state or already in the game.");
		return false;
	}

	Playfield* playfield = world.getPlayfield(c->getPlayfieldId());
	if (!server.checkGenericPlayfieldAccess(pCurClient, *c, playfield)) {
		return false;
	}

	// get playfield data
	// send it in chunks as defined by the config file
	typedef boost::shared_ptr<MsgGamePlayfieldLoadChunk> SPMsgGamePlayfieldLoadChunk;
	std::vector<SPMsgGamePlayfieldLoadChunk> chunkMessages;
	MsgGamePlayfieldLoadChunk::getChunkMessagesFromPlayfieldData(pCurClient, *playfield, Playfield::s_NetChunkSize, chunkMessages);

	if (chunkMessages.size() > 0) {
		for (std::vector<SPMsgGamePlayfieldLoadChunk>::iterator it = chunkMessages.begin(); it!= chunkMessages.end(); it++) {
			SPMsgGamePlayfieldLoadChunk mc = *(it);
			if (!server.queueMessageForSending(*mc, pCurClient, true)) {
				// something went wrong, probably buffer full
				pCurClient->getUser()->getUserSession().logError("Sending playfield chunks to client failed, playfield id: " + fwutil::Common::intToString(playfield->getId()));
				break;
			}
		}
	}

	return true;
}


/**
 * Character requests to step on a playfield. This is not to be confused with the request enter world message which is only issued at the very beginning of the game. The enter playfield message is sent each time before the client character is actually added to the playfield (co).
 */
bool ModulePlayfields::handleMessageGamePlayfieldEnterRequest(FWClient* pCurClient, MsgGamePlayfieldEnterRequest& msgObj) {
	if (!pCurClient->hasSelectedCharacter()) {
		server.handleClientError(pCurClient, "handleMessageGamePlayfieldEnterPlayfieldRequest");
		return false;
	}

	// check conditions
	// ---------------------------------
	Character* c = pCurClient->getUser()->getSelectedCharacter();
	if (c->getActiveStatus() != Character::as_inactive_loadPlayfield || world.getLiveCharacter(c->getId()) != 0) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to request playfield data while the character was not in the playfield load state or already in the game.");
		return false;
	}

	Playfield* playfield = world.getPlayfield(c->getPlayfieldId());
	if (!server.checkGenericPlayfieldAccess(pCurClient, *c, playfield)) {
		return false;
	}

	if (!playfield->validPositionForCharacter(c, c->getX(), c->getY())) {
		server.onClientSuspiciousAction(pCurClient, "Character tried to step on blocked or invalid cell when requesting to enter playfield.");
		return false;
	}

	// todo: handle friends online / offline (location changed)
	world.addLiveCharacter(c);
	placeCharacterOnPlayfield(c, playfield, false);

	return true;
}

// %%GENERATOR_END%%HANDLER_IMPL%%

void ModulePlayfields::placeCharacterOnPlayfield(Character* c, Playfield* playfield, bool forRespawn) {
	FWClient* pCurClient = c->getUser()->getClient();

	// add to live characters, and playfield / cell
	// ---------------------------------
	c->setActiveStatus(Character::as_active);

	playfield->addCharacter(c);
	c->setPlayfield(playfield);

	// notify client that he may now indeed step onto playfield and switch to game mode - yay!
	// ---------------------------------
	if (forRespawn) {
		MsgGameCharacterRespawnResult returnMsg;
		returnMsg.success = 1;
		returnMsg.infoMessage = "";
		returnMsg.curHealth = c->getHealthCurrent();
		returnMsg.curMana = c->getManaCurrent();
		returnMsg.respawnX = c->getX();
		returnMsg.respawnY = c->getY();
		server.queueMessageForSending(returnMsg, pCurClient, true);
	} else {
		MsgGamePlayfieldEnterResult returnMsg;
		returnMsg.success = 1;
		returnMsg.infoMessage = "";
		server.queueMessageForSending(returnMsg, pCurClient, true);
	}

	// get and send objects in range
	// ---------------------------------
	std::vector<Character*> others;
	std::vector<Item*> newItems;
	playfield->appendObjectsInVisRangePx(c->getX(), c->getY(), &others, &newItems);

	unsigned int curCharacterId = c->getId();

	MsgGameCharacterAdd addMsgSelf;
	addMsgSelf.fromCharacter(*c);

	MsgGameCharacterAdd addMsgOther;

	for (std::vector<Character*>::iterator it = others.begin(); it!=others.end(); it++) {
		Character* otherChar = (*it);
		if (otherChar->getId() != curCharacterId) {
			User* otherUser = otherChar->getUser();
			if (otherUser!=0) {
				// user
				addMsgOther.fromCharacter(*otherChar);
				server.queueMessageForSending(addMsgOther, pCurClient, true);	// send others to self
				
				// note: we do not need to validate the length of this message as it doesn't change (last param false)
				server.queueMessageForSending(addMsgSelf, otherUser->getClient(), false);	// send self to others
			} else {
				// bot
			}
		}
	}

	// send items in range
	MsgGameItemAdd addMsgItem;
	for (std::vector<Item*>::iterator n = newItems.begin(); n!=newItems.end(); n++) {
		Item* newItem = (*n);
		addMsgItem.fromItem(*newItem);
		server.queueMessageForSending(addMsgItem, pCurClient, true);
	}
}

void ModulePlayfields::removeCharacterFromPlayfield(Character* c) {
	Playfield* playfield = world.getPlayfield(c->getPlayfieldId());
	if (playfield!=0) {
		playfield->removeCharacter(c);
		if(c->getActiveStatus() == Character::as_active) {
			notifyCharacterRemovedFromPlayfield(c, playfield);
		}
	}
}

void ModulePlayfields::notifyCharacterRemovedFromPlayfield(Character* c, Playfield* playfield) {
	MsgGameCharacterRemove msgRem;
	msgRem.objectId = c->getId();
	server.getModulePlayfields()->broadCastMessageToCharactersInVisRangePx(c->getX(), c->getY(), *playfield, msgRem);
}


void ModulePlayfields::checkScheduledItems(clock_ms_t curTime) {
	std::map<unsigned int, SPPlayfield> playfields = world.playfields;
	for(std::map<unsigned int, SPPlayfield>::iterator it = playfields.begin(); it!=playfields.end(); it++) {
		std::vector<Item*> respawnedItems;
		std::vector<Item*> clearedItems;
		SPPlayfield pf = (*it).second;
		pf->checkScheduledItems(curTime, respawnedItems, clearedItems);
		// for each Item  notify add item
		MsgGameItemAdd msgAdd;
		for (std::vector<Item*>::iterator it = respawnedItems.begin(); it != respawnedItems.end(); it++) {
			Item* curItem = *it;
			msgAdd.fromItem(*curItem);
			broadCastMessageToCharactersInVisRangePx(curItem->getX(), curItem->getY(), *pf, msgAdd);
		}
		MsgGameItemRemove msgRem;
		for (std::vector<Item*>::iterator it = clearedItems.begin(); it != clearedItems.end(); it++) {
			Item* curItem = *it;
			msgRem.itemId = curItem->getId();
			broadCastMessageToCharactersInVisRangePx(curItem->getX(), curItem->getY(), *pf, msgRem);
		}
	}
}

void ModulePlayfields::broadCastMessageToCharactersInVisRangePx(unsigned int x, unsigned int y, Playfield& playfield, min::MinMessage& msg, unsigned int excludeObjectId/*=0*/) {
	std::vector<Character*> charsInRange;
	playfield.appendObjectsInVisRangePx(x, y, &charsInRange, 0);
	server.broadCastMessageToCharacters(charsInRange, msg, excludeObjectId);
}