#ifndef PlayfieldCell_h__
#define PlayfieldCell_h__

#include "PlayfieldCellFunction.h"
#include "TileInfo.h"
#include <vector>
#include <iostream>

namespace fwworld {

class Character;
class Item;

class PlayfieldCell {


public:
	static const int c_DefaultWidth = 24;
	static const int c_DefaultHeight = 24;

	PlayfieldCell();
	PlayfieldCell(PlayfieldCellFunction newFunction, TileInfo newTileInfo);
	virtual ~PlayfieldCell();
	void removeCharacterById(int characterId);
	void removeItemById(int itemId);
	void addCharacter(Character* character);
	void addItem(Item* item);

	inline std::vector<Character*>& getCharacters() {
		return characters;
	}

	inline std::vector<Item*>& getItems() {
		return items;
	}


	inline void assign(PlayfieldCellFunction newCellFunction, TileInfo newTileInfo)
	{
		cellFunction = newCellFunction;
		cellTileInfo = newTileInfo;
	}

	inline void setFunction(const PlayfieldCellFunction& newCellFunction) {
		cellFunction = newCellFunction;
	}

	inline void setTileInfo(const TileInfo& newTileInfo) {
		std::cout << "set tile info: " << newTileInfo.toInt() << std::endl;
		cellTileInfo = newTileInfo;
	}

	inline const PlayfieldCellFunction& getFunction() const {
		return cellFunction;
	}

	inline const TileInfo& getTileInfo() const {
		return cellTileInfo;
	}

	inline bool hasMainFunction(int fVal) {
		return cellFunction.hasMainFunction(fVal);
	}

	inline bool hasTriggerFunction(int fVal) {
		return cellFunction.hasTriggerFunction(fVal);
	}


private:
	std::vector<Character*> characters;
	std::vector<Item*> items;

	PlayfieldCellFunction cellFunction;
	TileInfo cellTileInfo;


};

}


#endif // PlayfieldCell_h__
