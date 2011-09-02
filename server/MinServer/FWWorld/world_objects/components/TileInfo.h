#ifndef TileInfo_h__
#define TileInfo_h__

class TileInfo {

public:

	TileInfo(unsigned int intValue) 
	{
		fromInt(intValue);
	}

	TileInfo(int tilesetIndex, int tileIndex) 
	: tilesetIndex(tilesetIndex), tileIndex(tileIndex)
	{}

	virtual ~TileInfo() {}

	inline void fromInt(unsigned int intValue) {
		tilesetIndex = (intValue & 0xE0) >> 5;
		tileIndex = intValue & 0x1F;
	}

	inline int toInt() const {
		return ((tilesetIndex & 0x07) << 5) | (tileIndex & 0x1F);
	}

	inline int getTilesetIndex() const {
		return tilesetIndex;
	}
	inline int getTileIndex() const {
		return tileIndex;
	}
	inline void setTilesetIndex(unsigned int newIndex) {
		tilesetIndex = newIndex;
	}
	inline void setTileIndex(unsigned int newIndex) {
		tileIndex = newIndex;
	}

private:
	int tilesetIndex;
	int tileIndex;
};

#endif // TileInfo_h__