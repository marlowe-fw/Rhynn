#ifndef WorldObject_h__
#define WorldObject_h__

#include "../FWWorld.h"
//class FWWorld;

namespace fwworld {

class WorldObject {

public:
	enum ObjectType {
		otUnknown=0,
		otCharacter=1,
		otItem=2,
		otPlayfield=3,
		otUser=4,
		otQuest=5,
		otQuestItem=6,
		otChest=7,
		otCharacterClass=8,
		otGameServer=9,
		otGraphic=10,
		otPlayfieldGraphic=11,
		otMAX
	};

	WorldObject(FWWorld& newWorld) : world(newWorld),objectTypeId(otUnknown) {
		numObjects++;
	}

	virtual ~WorldObject() {
		numObjects--;
	}

	static unsigned long getNumObjects() {
		return numObjects;
	}

private:
	static unsigned long numObjects;

protected:
	/** The world object which holds the DB connection and the timer. */
	FWWorld& world;
	ObjectType objectTypeId;


};

}



#endif // WorldObject_h__