var TileSelection = function(tilesetIndex, tilesetSubIndex) {
	var _numTilesX = 4;	// todo: derive from image dimensions and default tile dimensions
	var _numTilesY = 4;
	
	this.TilesetIndex = tilesetIndex;
	this.TilesetSubIndex = tilesetSubIndex;
	
	this.assign = function(tileSelection) {
		this.TilesetIndex = tileSelection.TilesetIndex;
		this.TilesetSubIndex = tileSelection.TilesetSubIndex;		
	}
	
	this.getClass = function() {
		var row = Math.floor((this.TilesetSubIndex/_numTilesX));
		var col = this.TilesetSubIndex%_numTilesX;
		return "tileset_" + this.TilesetIndex + " tile_" + row + "_" + col;
	}
}

var PlayfieldCell = function(tileSelection, cellFunction) {
	this.TileSelection = new TileSelection(); 
	this.CellFunction = new CellFunction();
	
	this.TileSelection.assign(tileSelection);
	this.CellFunction.assign(cellFunction);
	
	this.createHtml = function() {
		return '<td class="' + this.TileSelection.getClass() + '">' + this.CellFunction.getHtml() + '</td>';
	}
}