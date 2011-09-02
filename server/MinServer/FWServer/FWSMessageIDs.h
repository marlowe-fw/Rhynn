#ifndef FWSMessageIDs_h__
#define FWSMessageIDs_h__

namespace fws {

class FWSMessageIDs {
	public:
		static const unsigned int MAX_MESSAGE_ID = 65535;
		static const unsigned int MAX_INTERNAL_MESSAGE_ID = 1023;

		// %%GENERATOR_START%%MSG_IDS%%
		static const unsigned int MSGID_GAME_CHARACTER_MOVE = 2700;
		static const unsigned int MSGID_GAME_CHARACTER_MOVE_INFO = 2705;
		static const unsigned int MSGID_GAME_CHARACTER_ADD = 2710;
		static const unsigned int MSGID_GAME_CHARACTER_REMOVE = 2713;
		static const unsigned int MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST = 2740;
		static const unsigned int MSGID_GAME_CHARACTER_CHAT_ALL_INFO = 2741;
		static const unsigned int MSGID_GAME_CHARACTER_CHAT_REQUEST = 2750;
		static const unsigned int MSGID_GAME_CHARACTER_CHAT_INFO = 2751;
		static const unsigned int MSGID_GAME_CHARACTER_ATTACK_REQUEST = 2800;
		static const unsigned int MSGID_GAME_CHARACTER_HIT_INFO = 2803;
		static const unsigned int MSGID_GAME_CHARACTER_HIT_MISS_INFO = 2804;
		static const unsigned int MSGID_GAME_CHARACTER_KILLED = 2820;
		static const unsigned int MSGID_GAME_USER_CHARACTER_CLASS_FOR_LIST = 2150;
		static const unsigned int MSGID_GAME_USER_GET_CHARACTERS_REQUEST = 2160;
		static const unsigned int MSGID_GAME_USER_CHARACTER_FOR_LIST = 2162;
		static const unsigned int MSGID_GAME_USER_CHARACTER_CREATE_PERMISSION_REQUEST = 2180;
		static const unsigned int MSGID_GAME_USER_CHARACTER_CREATE_PERMISSION_RESULT = 2181;
		static const unsigned int MSGID_GAME_USER_CHARACTER_CREATE_REQUEST = 2182;
		static const unsigned int MSGID_GAME_USER_CHARACTER_CREATE_RESULT = 2183;
		static const unsigned int MSGID_GAME_USER_CHARACTER_RENAME_REQUEST = 2190;
		static const unsigned int MSGID_GAME_USER_CHARACTER_RENAME_RESULT = 2191;
		static const unsigned int MSGID_GAME_USER_CHARACTER_DELETE_REQUEST = 2195;
		static const unsigned int MSGID_GAME_USER_CHARACTER_DELETE_RESULT = 2196;
		static const unsigned int MSGID_GAME_USER_CHARACTER_SELECT_REQUEST = 2200;
		static const unsigned int MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST = 2204;
		static const unsigned int MSGID_GAME_CHARACTER_HIGHSCORE_LIST_ENTRY = 2205;
		static const unsigned int MSGID_GAME_CHARACTER_RESPAWN_REQUEST = 2207;
		static const unsigned int MSGID_GAME_CHARACTER_RESPAWN_RESULT = 2208;
		static const unsigned int MSGID_GAME_CHARACTER_INCREASE_VITALITY = 2215;
		static const unsigned int MSGID_GAME_FRIEND_LIST_REQUEST = 2350;
		static const unsigned int MSGID_GAME_FRIEND_LIST_END = 2351;
		static const unsigned int MSGID_SYSTEM_CHAT = 64;
		static const unsigned int MSGID_SYSTEM_PING = 65;
		static const unsigned int MSGID_TEST_OBJECT_MOVE = 520;
		static const unsigned int MSGID_TEST_PAYLOAD = 521;
		static const unsigned int MSGID_TEST_REGISTER_LISTENER = 530;
		static const unsigned int MSGID_GAME_PING = 1024;
		static const unsigned int MSGID_GAME_PONG = 1025;
		static const unsigned int MSGID_GAME_DEBUG = 1026;
		static const unsigned int MSGID_GAME_SERVER_LIST_REQUEST = 1028;
		static const unsigned int MSGID_GAME_SERVER_ENTRY = 1029;
		static const unsigned int MSGID_GAME_VERSION_REQUEST = 1030;
		static const unsigned int MSGID_GAME_VERSION = 1031;
		static const unsigned int MSGID_GAME_GRAPHICS_LOAD_REQUEST = 1060;
		static const unsigned int MSGID_GAME_GRAPHICS_LOAD_INFO = 1061;
		static const unsigned int MSGID_GAME_GRAPHICS_LOAD_CHUNK = 1062;
		static const unsigned int MSGID_GAME_ITEM_ADD = 2410;
		static const unsigned int MSGID_GAME_ITEM_REMOVE = 2412;
		static const unsigned int MSGID_GAME_ITEM_PICKUP_REQUEST = 2424;
		static const unsigned int MSGID_GAME_ITEM_INVENTORY_ADD = 2425;
		static const unsigned int MSGID_GAME_ITEM_INVENTORY_ADD_FAIL = 2426;
		static const unsigned int MSGID_GAME_ITEM_INVENTORY_END = 2427;
		static const unsigned int MSGID_GAME_ITEM_EQUIP_REQUEST = 2430;
		static const unsigned int MSGID_GAME_ITEM_UNEQUIP_REQUEST = 2432;
		static const unsigned int MSGID_GAME_ITEM_DROP_REQUEST = 2440;
		static const unsigned int MSGID_GAME_ITEM_USE_REQUEST = 2460;
		static const unsigned int MSGID_GAME_PLAYFIELD_ENTER_WORLD_REQUEST = 2310;
		static const unsigned int MSGID_GAME_PLAYFIELD_INFO = 2320;
		static const unsigned int MSGID_GAME_PLAYFIELD_GRAPHICS_INFO = 2321;
		static const unsigned int MSGID_GAME_PLAYFIELD_LOAD_REQUEST = 2325;
		static const unsigned int MSGID_GAME_PLAYFIELD_LOAD_CHUNK = 2327;
		static const unsigned int MSGID_GAME_PLAYFIELD_ENTER_REQUEST = 2330;
		static const unsigned int MSGID_GAME_PLAYFIELD_ENTER_RESULT = 2332;
		static const unsigned int MSGID_GAME_USER_REGISTER_REQUEST = 2050;
		static const unsigned int MSGID_GAME_USER_REGISTER_RESULT = 2051;
		static const unsigned int MSGID_GAME_USER_GET_EMAIL_REQUEST = 2055;
		static const unsigned int MSGID_GAME_USER_GET_EMAIL_RESULT = 2056;
		static const unsigned int MSGID_GAME_USER_EMAIL_CHANGE_REQUEST = 2057;
		static const unsigned int MSGID_GAME_USER_EMAIL_CHANGE_RESULT = 2058;
		static const unsigned int MSGID_GAME_USER_LOGIN_REQUEST = 2065;
		static const unsigned int MSGID_GAME_USER_LOGIN_RESULT = 2066;
		static const unsigned int MSGID_GAME_USER_FORCED_LOGOUT = 2070;
		static const unsigned int MSGID_GAME_USER_CHALLENGENUMBER = 2075;
		static const unsigned int MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST = 2100;
		static const unsigned int MSGID_GAME_USER_PASSWORD_RESET_CODE_RESULT = 2101;
		static const unsigned int MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST = 2104;
		static const unsigned int MSGID_GAME_USER_PASSWORD_RESET_NEW_RESULT = 2105;
		// %%GENERATOR_END%%MSG_IDS%%
};

}

#endif // FWSMessageIDs_h__