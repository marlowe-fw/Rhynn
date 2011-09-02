#ifndef HighscoreEntry_h__
#define HighscoreEntry_h__


namespace fwworld {

class HighscoreEntry {

public:
	std::string characterName;
	unsigned int rank;
	unsigned int experience;

	HighscoreEntry(std::string characterName, unsigned int rank, unsigned int experience)
	: characterName(characterName), rank(rank), experience(experience)
	{}

	~HighscoreEntry() {}

};

}

#endif // HighscoreEntry_h__