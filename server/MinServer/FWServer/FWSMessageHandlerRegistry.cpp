#include "FWServer.h"

using namespace fws;

void FWServer::registerMessageHandlers() {

	// clear out handler list
	for (unsigned int i=0; i < FWSMessageIDs::MAX_MESSAGE_ID; ++i) {
		messageHandlers[i] = 0;
	}


	std::cout << "registering message handlers .. ";


	// %%GENERATOR_START%%REGISTER_HANDLERS%%
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_MOVE] = _TRACK_NEW((new MessageHandler<ModuleCharacterInteraction, MsgGameCharacterMove>(serverModuleCharacterInteraction, &ModuleCharacterInteraction::handleMessageGameCharacterMove)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacterInteraction, MsgGameCharacterChatAllRequest>(serverModuleCharacterInteraction, &ModuleCharacterInteraction::handleMessageGameCharacterChatAllRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_CHAT_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacterInteraction, MsgGameCharacterChatRequest>(serverModuleCharacterInteraction, &ModuleCharacterInteraction::handleMessageGameCharacterChatRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_ATTACK_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacterInteraction, MsgGameCharacterAttackRequest>(serverModuleCharacterInteraction, &ModuleCharacterInteraction::handleMessageGameCharacterAttackRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_GET_CHARACTERS_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserGetCharactersRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserGetCharactersRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_PERMISSION_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserCharacterCreatePermissionRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserCharacterCreatePermissionRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHARACTER_CREATE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserCharacterCreateRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserCharacterCreateRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHARACTER_RENAME_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserCharacterRenameRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserCharacterRenameRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHARACTER_DELETE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserCharacterDeleteRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserCharacterDeleteRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHARACTER_SELECT_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameUserCharacterSelectRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameUserCharacterSelectRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameCharacterHighscoreRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameCharacterHighscoreRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_CHARACTER_RESPAWN_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleCharacters, MsgGameCharacterRespawnRequest>(serverModuleCharacters, &ModuleCharacters::handleMessageGameCharacterRespawnRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_FRIEND_LIST_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleFriends, MsgGameFriendListRequest>(serverModuleFriends, &ModuleFriends::handleMessageGameFriendListRequest)));
	messageHandlers[FWSMessageIDs::MSGID_SYSTEM_CHAT] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgSystemChat>(serverModuleGeneric, &ModuleGeneric::handleMessageSystemChat)));
	messageHandlers[FWSMessageIDs::MSGID_SYSTEM_PING] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgSystemPing>(serverModuleGeneric, &ModuleGeneric::handleMessageSystemPing)));
	messageHandlers[FWSMessageIDs::MSGID_TEST_OBJECT_MOVE] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgTestObjectMove>(serverModuleGeneric, &ModuleGeneric::handleMessageTestObjectMove)));
	messageHandlers[FWSMessageIDs::MSGID_TEST_PAYLOAD] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgTestPayload>(serverModuleGeneric, &ModuleGeneric::handleMessageTestPayload)));
	messageHandlers[FWSMessageIDs::MSGID_TEST_REGISTER_LISTENER] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgTestRegisterListener>(serverModuleGeneric, &ModuleGeneric::handleMessageTestRegisterListener)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_PING] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgGamePing>(serverModuleGeneric, &ModuleGeneric::handleMessageGamePing)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_DEBUG] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgGameDebug>(serverModuleGeneric, &ModuleGeneric::handleMessageGameDebug)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_SERVER_LIST_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgGameServerListRequest>(serverModuleGeneric, &ModuleGeneric::handleMessageGameServerListRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_VERSION_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgGameVersionRequest>(serverModuleGeneric, &ModuleGeneric::handleMessageGameVersionRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_GRAPHICS_LOAD_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleGeneric, MsgGameGraphicsLoadRequest>(serverModuleGeneric, &ModuleGeneric::handleMessageGameGraphicsLoadRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_ITEM_PICKUP_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleItems, MsgGameItemPickupRequest>(serverModuleItems, &ModuleItems::handleMessageGameItemPickupRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_ITEM_EQUIP_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleItems, MsgGameItemEquipRequest>(serverModuleItems, &ModuleItems::handleMessageGameItemEquipRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_ITEM_UNEQUIP_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleItems, MsgGameItemUnequipRequest>(serverModuleItems, &ModuleItems::handleMessageGameItemUnequipRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_ITEM_DROP_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleItems, MsgGameItemDropRequest>(serverModuleItems, &ModuleItems::handleMessageGameItemDropRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_ITEM_USE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleItems, MsgGameItemUseRequest>(serverModuleItems, &ModuleItems::handleMessageGameItemUseRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_PLAYFIELD_ENTER_WORLD_REQUEST] = _TRACK_NEW((new MessageHandler<ModulePlayfields, MsgGamePlayfieldEnterWorldRequest>(serverModulePlayfields, &ModulePlayfields::handleMessageGamePlayfieldEnterWorldRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_PLAYFIELD_LOAD_REQUEST] = _TRACK_NEW((new MessageHandler<ModulePlayfields, MsgGamePlayfieldLoadRequest>(serverModulePlayfields, &ModulePlayfields::handleMessageGamePlayfieldLoadRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_PLAYFIELD_ENTER_REQUEST] = _TRACK_NEW((new MessageHandler<ModulePlayfields, MsgGamePlayfieldEnterRequest>(serverModulePlayfields, &ModulePlayfields::handleMessageGamePlayfieldEnterRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_REGISTER_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserRegisterRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserRegisterRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_GET_EMAIL_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserGetEmailRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserGetEmailRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_EMAIL_CHANGE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserEmailChangeRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserEmailChangeRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_LOGIN_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserLoginRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserLoginRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_CHALLENGENUMBER] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserChallengenumber>(serverModuleUsers, &ModuleUsers::handleMessageGameUserChallengenumber)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserPasswordResetCodeRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserPasswordResetCodeRequest)));
	messageHandlers[FWSMessageIDs::MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST] = _TRACK_NEW((new MessageHandler<ModuleUsers, MsgGameUserPasswordResetNewRequest>(serverModuleUsers, &ModuleUsers::handleMessageGameUserPasswordResetNewRequest)));
	// %%GENERATOR_END%%REGISTER_HANDLERS%%

	//pingHandler = new MessageHandler<FWServer, MsgGamePing> (this, &FWServer::bryllup);
	// todo: TRACK NEW
	//messageHandlers1[FWSMessageIDs::MSGID_GAME_SERVER_LIST_REQUEST] = new MessageHandler<FWServer, MsgGameServerListRequest>(this, &FWServer::handleMessageGameServerListRequest1);
	messageHandlersRegistered = true;
	std::cout << "OK" << std::endl;
}


void FWServer::cleanupMessageHandlers() {
	/*if (pingHandler!=0) {
		delete pingHandler;
	}*/
    if (messageHandlersRegistered) {
        for (unsigned int i=0; i < FWSMessageIDs::MAX_MESSAGE_ID; ++i) {
            if (messageHandlers[i]!=0) {
                delete messageHandlers[i];
            }
        }
    }
}