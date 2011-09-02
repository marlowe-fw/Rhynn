#ifndef ModuleQuests_h__
#define ModuleQuests_h__

// %%GENERATOR_START%%MESSAGE_INCLUDES%%
// %%GENERATOR_END%%MESSAGE_INCLUDES%%

namespace fwworld {
	class FWWorld;
}

using namespace fwworld;

namespace fws {

class FWServer;
class FWClient;

class ModuleQuests {

public:
	ModuleQuests(FWServer& server, FWWorld& world);

	// %%GENERATOR_START%%HANDLER_DECL%%
	// %%GENERATOR_END%%HANDLER_DECL%%

private:
	FWServer& server;
	FWWorld& world;
};


}
#endif // ModuleQuests_h__