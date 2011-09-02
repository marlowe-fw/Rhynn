#include "PlayfieldCell.h"
#include "../Character.h"
#include "../Item.h"
#include <boost/bind.hpp>

using namespace fwworld;


PlayfieldCell::PlayfieldCell()
: cellFunction(
			   PlayfieldCellFunction(
				   PlayfieldCellFunction::function_none,
				   PlayfieldCellFunction::triggerfunction_none)
			  ),
cellTileInfo(TileInfo(0,0))
{
	characters.reserve(8);
}

PlayfieldCell::PlayfieldCell(PlayfieldCellFunction newFunction, TileInfo newTileInfo)
: cellFunction(newFunction), cellTileInfo(newTileInfo)
{}

PlayfieldCell::~PlayfieldCell() {};

void PlayfieldCell::addCharacter(Character* character) {
	//std::cout << "adding character to cell" << std::endl;
	characters.push_back(character);
}

void PlayfieldCell::addItem(Item* item) {
	//std::cout << "adding item to cell" << std::endl;
	items.push_back(item);
}


void PlayfieldCell::removeCharacterById(int characterId) {
	characters.erase(
		std::remove_if(characters.begin(), characters.end(), boost::bind(&Character::getId, _1) == characterId)
		, characters.end()
	);
}

void PlayfieldCell::removeItemById(int id) {
	// todo: remember, when this is called for a playfield item, take appropriate measures in the playfield (allPlayfieldItems, respawnItems)
	//std::cout << "removing item from cell" << std::endl;
	items.erase(
		std::remove_if(items.begin(), items.end(), boost::bind(&Item::getId, _1) == id)
		, items.end()
		);
}
