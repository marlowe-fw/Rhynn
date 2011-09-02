#include "FWServer.h"
#include "FWClient.h"
#include "ConfigSetting.h"
#include "world_objects/Graphic.h"
#include "world_objects/Playfield.h"
#include <iostream>
#include <fstream>
#include <string>
#include "messages/MsgGameCharacterRemove.h"
#include "messages/MessageHandler.h"
#include "messages/MsgGameServerListRequest.h"

using namespace fws;
using namespace fwworld;


FWServer::FWServer(int newPort, std::string newConfigFileName) : MinServer(newPort),
world(0),
configFileName(newConfigFileName),
lastMediumPulse(0),
lastLongPulse(0),
lastXLongPulse(0),
lastHourBatchPulse(0),
serverModuleGeneric(0),
serverModuleCharacters(0),
serverModuleCharacterInteraction(0),
serverModuleFriends(0),
serverModuleItems(0),
serverModulePlayfields(0),
serverModuleQuests(0),
serverModuleUsers(0),
messageHandlersRegistered(false)
{
	clientSendLimit = 2048;
	std::cout << "FWServer Module " << fws_version_major << "." << fws_version_minor << "." << fws_version_sub << std::endl;
	std::cout << "required client version: " << required_client_version_major << "." << required_client_version_minor << "." << required_client_version_sub << std::endl;
	std::cout << "config file: " << configFileName << std::endl;
}

FWServer::~FWServer() {
	if (serverModuleGeneric != 0) {delete serverModuleGeneric;}
	if (serverModuleCharacters != 0) {delete serverModuleCharacters;}
	if (serverModuleCharacterInteraction != 0) {delete serverModuleCharacterInteraction;}
	if (serverModuleFriends != 0) {delete serverModuleFriends;}
	if (serverModuleItems != 0) {delete serverModuleItems;}
	if (serverModulePlayfields != 0) {delete serverModulePlayfields;}
	if (serverModuleQuests != 0) {delete serverModuleQuests;}
	if (serverModuleUsers != 0) {delete serverModuleUsers;}
	cleanupMessageHandlers();

	if (world != 0) {
		world->log.log("Server shut down", fwutil::Log::Info);
		delete world;
	}

	std::cout << std::endl << std::endl << "================" << std::endl << "FWServer: EXIT" << std::endl << "================" << std::endl << std::endl;

}

min::MinClient* FWServer::newClientInstance() {
	return _TRACK_NEW(new FWClient(min::MinClient::default_client_buffer_in_size));
}


void FWServer::onServerStart() {
	std::cout << "FWServer start" << std::endl;
}

void FWServer::onServerStop() {
	std::cout << "FWServer stop" << std::endl;
	allClients.clear();
	// parent class will actually delete the clients, clients' destructors will take care of destructing contained users, characters etc.
	receivers.clear();	// used for the test messages only
}

void FWServer::onClientConnect(min::MinClient* client) {
	FWClient* fwClient = static_cast<FWClient*>(client);
	allClients.insert(std::pair<int, FWClient*>(client->getClientSocket(), fwClient));
	std::cout << world->clock.getTimestampStr() << " Client connected: " << client->getClientSocket() << " online: " << allClients.size() << std::endl;
}

/**
 * Called by the base class whenever a client is added to the disconnected clients queue.
 * Called for every client when the server shuts down.
 */
void FWServer::onClientRemoval(min::MinClient* client, min::MinClient::ClientRemovalInfo reason) {
	FWClient* pCurClient = static_cast<FWClient*>(client);
	removeClient(pCurClient, false); // do not request disconnect in parent or we would end up here again
	std::cout << world->clock.getTimestampStr() << " Client disconnected: " << pCurClient->getClientSocket() << " online: " << allClients.size() << std::endl;

}

/**
 * Actively remove a client from the server, request a disconnect in the base class.
 * @param client The client to remove
 * @param requestDisconnectInParent Whether or not to request a disconnect for this client in the
 * parent (base) class object, defaults to true and should be the desired behavior in most cases.
 */
void FWServer::removeClient(FWClient* client, bool requestDisconnectInParent /*=true*/) {
	if (requestDisconnectInParent) {
		requestDisconnectClient(client);
	}
	std::cout << world->clock.getTimestampStr(true) << " remove client: " << client->getClientSocket() << std::endl;

	if (client->hasUser()) {
		std::cout << world->clock.getTimestampStr(true) << " client user: " << client->getUser()->getId() << std::endl;
		serverModuleUsers->removeUser(client->getUser());
	}
	receivers.erase(client->dataGetIndex());
	allClients.erase(client->getClientSocket());

}

void FWServer::checkRemoveInactiveClients() {
	clock_ms_t curTime = world->clock.getTimestampMS();
	clock_ms_t lastActive = 0;
	for (std::map<int, FWClient*>::iterator it = allClients.begin(); it != allClients.end(); ) {
		FWClient* client = it->second;
		lastActive = client->getLastActive();
		// increment iterator here as client might be removed from map (when calling removeClient)
		it++;

		if (lastActive == 0) {
			client->setLastActive(curTime);
		} else if (curTime - lastActive > 60000) {
			// idle timeout reached
			std::cout << world->clock.getTimestampStr(true) << " inactive: " << client->getClientSocket() << std::endl;
			removeClient(client, true);
		}
	}
}

/**
 * Execute an incoming message which was received for the given client.
 */
void FWServer::executeMessage(min::MinClient* client, const unsigned char* msg) {
	//std::cout << "got a message in FWServer" << std::endl;
	FWClient* pCurClient = static_cast<FWClient*>(client);

	int len = msg[0];
	if (len < 4) {	// todo: define limits elsewhere
		handleClientError(pCurClient, "Invalid message length: " + fwutil::Common::intToString(len), true);
		std::cout << "invalid message length" << std::endl;
	} else {
		//std::cout << "msg length: " << len << std::endl;
		unsigned int id = min::NetPort::uintFrom3Bytes(msg, 1);

		//if (id != 2700)
			//std::cout << world->clock.getTimestampStr() << " " << pCurClient->getClientSocket() << ", id: " << id << ", length: " << len << std::endl;

		if (!pCurClient->isPacketValid(msg, len)) {
			handleClientError(pCurClient, "Invalid packet from client, msg: " + fwutil::Common::intToString(id), true);
			return;
		}

		pCurClient->setLastActive(world->clock.getTimestampMS());

		if (id <= FWSMessageIDs::MAX_MESSAGE_ID && messageHandlers[id] != 0) {
			if (!messageHandlers[id]->handle(pCurClient, msg, len)) {
				handleClientError(pCurClient, "Handler aborted (invalid message format), message id " + fwutil::Common::intToString(id), true);
				std::cout << "Handler aborted (invalid message format), message id " << id << std::endl;
			}
		} else {
			handleClientError(pCurClient, "Unregistered message id, message id: " + fwutil::Common::intToString(id), true);
			std::cout << "unregistered message id: " << id << std::endl;
		}
	}
}




/**
 * Establish a connection to the database and set the conn object for the server.
 * @return true on success, false otherwise
 */
bool FWServer::initDB() {
	std::cout << "connecting to database .. ";

	std::string server = configSettings.getEntryValueString("database.server");
	int port = configSettings.getEntryValueInt("database.port");
	std::string username = configSettings.getEntryValueString("database.username");
	std::string password = configSettings.getEntryValueString("database.password");
	std::string schema = configSettings.getEntryValueString("database.schema");

	try {
		conn = mysqlpp::Connection(schema.c_str(), server.c_str(), username.c_str(), password.c_str(), port);
		std::cout << "OK" << std::endl;
	} catch (const mysqlpp::Exception& er) {
		std::cout << "FAILED" << std::endl;
		std::cout << "*** MySQL Error: " << er.what() << std::endl;
		return false;
	}

	return true;
}


/**
 * Initialize the server.
 * This also initializes the db connection and clock.
 * @return true on success, false otherwise
 */
bool FWServer::init() {
	configSettings = fwutil::ConfigSetting(configFileName);

	if (configSettings.hasError()) {
		std::cout << "***" << std::endl;
		std::cout << "*** CRITICAL ERROR: Error reading config file: " << configFileName << std::endl;
		std::cout << "*** Is the config file in the same folder as the server executable?" << std::endl;
		std::cout << "***" << std::endl;
		return false;
	}

	if (!initDB()) {
		std::cout << "***" << std::endl;
		std::cout << "*** CRITICAL ERROR: Database initialization failed" << std::endl;
		std::cout << "*** Check your configuration file: " << configFileName << std::endl;
		std::cout << "***" << std::endl;
		return false;
	}

	int gfxNetChunkSize = configSettings.getEntryValueInt("graphics.netChunkSize");
	if (gfxNetChunkSize > 0) {
		Graphic::netChunkSize = gfxNetChunkSize;
		std::cout << "gfx s_NetChunkSize: " << gfxNetChunkSize << std::endl;
	}

	unsigned int playfieldNetChunkSize = configSettings.getEntryValueInt("playfields.s_NetChunkSize");
	if (playfieldNetChunkSize > 0) {
		while (playfieldNetChunkSize % Playfield::bytesPerCell != 0 && playfieldNetChunkSize > Playfield::bytesPerCell) {
			playfieldNetChunkSize--;
		}
		Playfield::s_NetChunkSize = playfieldNetChunkSize;
		std::cout << "playfield s_NetChunkSize: " << playfieldNetChunkSize << std::endl;
	}

	std::string imageBasePath = configSettings.getEntryValueString("graphics.imageBasePath");
	std::cout << "imageBasePath: " << imageBasePath << std::endl;
	if (!(imageBasePath.empty())) {
		Graphic::imageBasePath = imageBasePath;
	}

	std::string logFileName = configSettings.getEntryValueString("logging.mainLogFile");
	std::cout << "mainLogFile: " << logFileName << std::endl;
	if (logFileName.empty()) {
		logFileName = "./logs/log.txt";
	}

	// setup and create world
	std::string worldConfigFilename = configSettings.getEntryValueString("world.configFile");
	std::cout << "world config file: " << worldConfigFilename << std::endl;
	world = _TRACK_NEW(new FWWorld(conn, clock, logFileName, worldConfigFilename));

	// create server modules
	serverModuleGeneric = _TRACK_NEW(new ModuleGeneric(*this, *world));
	serverModuleCharacters = _TRACK_NEW(new ModuleCharacters(*this, *world));
	serverModuleCharacterInteraction = _TRACK_NEW(new ModuleCharacterInteraction(*this, *world));
	serverModuleFriends = _TRACK_NEW(new ModuleFriends(*this, *world));
	serverModuleItems = _TRACK_NEW(new ModuleItems(*this, *world));
	serverModulePlayfields = _TRACK_NEW(new ModulePlayfields(*this, *world));
	serverModuleQuests = _TRACK_NEW(new ModuleQuests(*this, *world));
	serverModuleUsers = _TRACK_NEW(new ModuleUsers(*this, *world));

	// character start pos
	Character::s_initialPlayfieldId = configSettings.getEntryValueInt("characters.initialPlayfieldId");
	Character::s_initialPlayfieldX = configSettings.getEntryValueInt("characters.initialPlayfieldX");
	Character::s_initialPlayfieldY = configSettings.getEntryValueInt("characters.initialPlayfieldY");

	registerMessageHandlers();

	world->log.log("Server initialized", fwutil::Log::Info);
	return true;
}

/**
 * A catch-all action to take when an invalid action / state is encountered by a client (indicating possible packet tampering).
 */
void FWServer::handleClientError(FWClient* pCurClient, const std::string& info /*= ""*/, bool disconnect /*= true*/) {
	if (pCurClient->hasUser()) {
		pCurClient->getUser()->getUserSession().logError("Invalid client action or state, detail: " + info);
	} else {
		// todo: add message to global log, be sure to log the ip
		// also, be sure to check packet frequency and simply drop flooders
	}
	if (disconnect) {
		removeClient(pCurClient, true);
	}
}

/**
* A catch-all action to take when a suspicious action is committed by a client.
*/
void FWServer::onClientSuspiciousAction(FWClient* pCurClient, const std::string& info /*= ""*/, bool disconnect /*= true*/) {
	if (pCurClient->hasUser()) {
		pCurClient->getUser()->getUserSession().logSuspiciousAction(info);
	}
	if (disconnect) {
		removeClient(pCurClient, true);
	}
}

/**
* A catch-all action to take when a an error is raised for the current client connection.
*/
void FWServer::onClientError(FWClient* pCurClient, const std::string& info /*= ""*/, bool disconnect /*= true*/) {
	std::cout << "CLIENT ERROR: " << info << std::endl;
	if (pCurClient->hasUser()) {
		pCurClient->getUser()->getUserSession().logError(info);
	}
	if (disconnect) {
		removeClient(pCurClient, true);
	}
}


/**
* Custom logic to execute on every server loop iteration.
*/
void FWServer::executeLogic() {
	clock_ms_t curTime = clock.getTimestampMS();

	if (curTime - lastMediumPulse > 3000) {
		serverModulePlayfields->checkScheduledItems(curTime);
		lastMediumPulse = curTime;

		if (curTime - lastLongPulse > 30000) {
			checkRemoveInactiveClients();
			lastLongPulse = curTime;
		}

		if (curTime - lastHourBatchPulse > 3600000) {
			pulseDB();
			lastHourBatchPulse = curTime;
		}

	}

}

void FWServer::pulseDB() {
	if (!conn.ping()) {
		world->log.log("DB Ping indicates database connection is down and could not be re-established", fwutil::Log::Error);
		std::cout << world->clock.getTimestampStr(true) << " DB Ping indicates database connection is down and could not be re-established" << std::endl;
	} else {
		std::cout << world->clock.getTimestampStr(true) << " DB Ping ok" << std::endl;
	}
}
