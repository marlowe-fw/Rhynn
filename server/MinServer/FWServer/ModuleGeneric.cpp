#include "ModuleGeneric.h"
#include "FWClient.h"
#include "FWServer.h"
#include "FWWorld.h"
#include "world_objects/Gameserver.h"

#include "state/ClientCondition.h"

#include "messages/MsgGamePong.h"
#include "messages/MsgGameServerEntry.h"
#include "messages/MsgGameVersion.h"
#include "messages/MsgGameGraphicsLoadChunk.h"
#include "messages/MsgGameGraphicsLoadInfo.h"


//#include <vector>
using namespace fws;
using namespace fwworld;
using namespace fwutil;

ModuleGeneric::ModuleGeneric(FWServer& newServer, FWWorld& newWorld) 
: server(newServer), world(newWorld)
{}

// %%GENERATOR_START%%HANDLER_IMPL%%

bool ModuleGeneric::handleMessageSystemChat(FWClient* pCurClient, MsgSystemChat& msgObj) {	
	//std::cout << "received system chat, length: " << length << std::endl;
	//std::cout << "msg: " << msgObj.chatText << std::endl;
	int curSocket = pCurClient->getClientSocket();
	// broadcast to other clients
	for(std::map<int, FWClient*>::iterator it = server.allClients.begin(); it != server.allClients.end(); ++it) {
		if (curSocket != (*it).first) {
			FWClient* pOtherClient = (*it).second;
			server.queueMessageForSending(msgObj, pOtherClient);
		}
	}
	return true;
}

bool ModuleGeneric::handleMessageTestObjectMove(FWClient* pCurClient, MsgTestObjectMove& msgObj) {
	server.queueMessageForSending(msgObj, pCurClient);
	std::map<int, FWClient*>::iterator it = server.receivers.find(msgObj.listenerIndex);
	if (it != server.receivers.end()) {
		FWClient* pCurListenerClient = (*it).second;
		server.queueMessageForSending(msgObj, pCurListenerClient);			
	}
	return true;
}


bool ModuleGeneric::handleMessageTestRegisterListener(FWClient* pCurClient, MsgTestRegisterListener& msgObj) {
	std::cout << "registered: " << msgObj.listenerIndex << std::endl;
	pCurClient->dataSetIndex(msgObj.listenerIndex);
	std::map<int, FWClient*>::iterator it = server.receivers.find(msgObj.listenerIndex);
	server.receivers.insert(std::pair<int, FWClient*>(msgObj.listenerIndex,pCurClient));
	return true;
}


bool ModuleGeneric::handleMessageTestPayload(FWClient* pCurClient, MsgTestPayload& msgObj) {
	static int numReceives = 0;
	numReceives++;
	if (numReceives % 100000 == 0) {
		std::cout << "payloads: " << numReceives << std::endl;
	}


	//int index = Min::NetPort::intFrom2Bytes(msg, 4);
	std::map<int, FWClient*>::iterator it;
	//std::cout << "got payload " << std::endl;
	for (it = server.receivers.begin(); it != server.receivers.end(); ++it) {
		//memcpy(outBuffer, msg, msg[0]);
		//(*it).second->sendAll(msg);
		(*it).second->putMessageAndSendAll(msgObj, false);
		// -- queueMessageForSending(msgObj, (*it).second, false);
	}

	return true;
}




bool ModuleGeneric::handleMessageSystemPing(FWClient* pCurClient, MsgSystemPing& msgObj) {
	std::cout << "received ping" << std::endl;
	server.queueMessageForSending(msgObj, pCurClient);
	return true;
}


// ------------------------
// Game messages
// ------------------------

/**
* Request the game servers for display on the client (co).
*/
bool ModuleGeneric::handleMessageGameServerListRequest(FWClient* pCurClient, MsgGameServerListRequest& msgObj) {
	// send back game servers
	std::vector<SPGameserver> gameServers = world.gameServers;
	for (std::vector<SPGameserver>::iterator it = gameServers.begin(); it!=gameServers.end(); it++) {
		const SPGameserver g = *it;
		MsgGameServerEntry returnMsg;
		returnMsg.ip = g->getIp();
		returnMsg.serverName = g->getName();

		server.queueMessageForSending(returnMsg, pCurClient, true);
	}

	return true;
}

/**
* Get the game version, client sends along his current version (co).
*/
bool ModuleGeneric::handleMessageGameVersionRequest(FWClient* pCurClient, MsgGameVersionRequest& msgObj) {
	MsgGameVersion returnMsg;
	returnMsg.versionMajor = FWServer::required_client_version_major;
	returnMsg.versionMinor = FWServer::required_client_version_minor;
	returnMsg.versionSub = FWServer::required_client_version_sub;
	server.queueMessageForSending(returnMsg, pCurClient, false);

	// todo: allow to send back custom message (read from config file)

	// in either case check if client version is high enough
	if (
		msgObj.versionMajor < FWServer::required_client_version_major ||
		(
		msgObj.versionMajor == FWServer::required_client_version_major &&  msgObj.versionMinor < FWServer::required_client_version_minor
		) ||
		(
		msgObj.versionMajor == FWServer::required_client_version_major &&  msgObj.versionMinor == FWServer::required_client_version_minor && msgObj.versionSub < FWServer::required_client_version_sub
		)
		) 
	{
		// version too low, queue for disconnect (client will still receive the version info message)
		server.removeClient(pCurClient, true);
	}
	return true;
}



/**
* Client ping to server (co).
*/
bool ModuleGeneric::handleMessageGamePing(FWClient* pCurClient, MsgGamePing& msgObj) {
	// send back pong
	MsgGamePong returnMsg;
	server.queueMessageForSending(returnMsg, pCurClient, false);
	if (pCurClient->conditionApplies(ClientCondition::character_alive) && pCurClient->conditionApplies(ClientCondition::character_active)) { 
		server.getModuleCharacters()->checkAutomaticVitalityRefill(pCurClient->getUser()->getSelectedCharacter());
	}
	return true;
}




typedef boost::shared_ptr<MsgGameGraphicsLoadChunk> SPMsgGameGraphicsLoadChunk;

/**
* Request to send the image data associated with the given graphics id to the client (co). The client will store the image using a record store mechanism, if available.
*/
bool ModuleGeneric::handleMessageGameGraphicsLoadRequest(FWClient* pCurClient, MsgGameGraphicsLoadRequest& msgObj) {
	if (!pCurClient->hasValidatedUser()) {
		server.handleClientError(pCurClient, "handleMessageGameGraphicsLoadRequest");
		return false;
	}

	int graphicId = msgObj.graphicId;

	Graphic* graphic = 0;
	User* u = pCurClient->getUser();

	if (graphicId > 0) {
		graphic = world.getGraphic(graphicId);
	} else {
		u->logSuspiciousAction("Client tried to request graphic with id <= 0");
	}

	MsgGameGraphicsLoadInfo returnMsg;
	std::vector<SPMsgGameGraphicsLoadChunk> chunkMessages;
	if (graphic != 0) {
		returnMsg.imageSize = graphic->getImageDataSize();
		//std::cout << world.clock.getTimestampStr() << " gfx found" << std::endl;
		MsgGameGraphicsLoadChunk::getChunkMessagesFromImageData(pCurClient, *graphic, Graphic::netChunkSize, chunkMessages);
	} else {
		returnMsg.imageSize = 0;
		std::cout << "gfx not found!" << std::endl;
	}

	

	server.queueMessageForSending(returnMsg, pCurClient, true);

	if (chunkMessages.size() > 0) {
		for (std::vector<SPMsgGameGraphicsLoadChunk>::iterator it = chunkMessages.begin(); it!= chunkMessages.end(); it++) {
			/*
			i++;
			if (i%3 == 0) {
				std::cout << "sleeping ..";
				Sleep(3000);
				std::cout << "done." << std::endl;

			}*/
			SPMsgGameGraphicsLoadChunk mc = *(it);
			if (!server.queueMessageForSending(*mc, pCurClient, true)) {
				// something went wrong, probably buffer full
				u->logError("Sending image chunks to client failed, graphicId: " + fwutil::Common::intToString(graphicId));
				break;
			} else {

			}
		}
	}

	return true;
}

/**
 * Client sends a generic debug message to the server (co).
 */
bool ModuleGeneric::handleMessageGameDebug(FWClient* pCurClient, MsgGameDebug& msg) {
	//queueMessageForSending(returnMsg, pCurClient, true)
	
	User* u = pCurClient->getUser();
	if (u!=0) {
		u->logDebug("Rcv: " + msg.message,0);
	} else {
		std::cout << world.clock.getTimestampStr(true) << " " << msg.message << std::endl;
	}

	return true;
}


// %%GENERATOR_END%%HANDLER_IMPL%%