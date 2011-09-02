var BrushTileSet = function(playfieldId, tilesetIndex) {
	var that = this;
	var _tilesetIndex = tilesetIndex;
	var _tileSelections = [];
	var _playfieldId = playfieldId;
	var _elementId = "brush-tileset-" + _playfieldId + "-" + _tilesetIndex;
	var	_elementSelector = "#" + _elementId;
	var _activeTileSelection = null;
	
	// add all tileselections
	for (var x=0;x<4;x++) {
		_tileSelections.push([]);
		for (var y=0;y<4;y++) {
			_tileSelections[x].push(new TileSelection(_tilesetIndex, (y*4)+x));
		}
	}
		
	$(_elementSelector + " td").live('click', function() {
		var row = $(this).closest('tr').index();
		var col = $(this).index();
		that.setActive(col,row);
	});
	
	this.activeChanged = function(col,row) {}
	
	this.setInactive = function() {
		$(_elementSelector + " td").removeClass("active");
		_activeTileSelection = null;
	}
	
	this.setActive = function(col, row) {
		this.setInactive()
		$(_elementSelector + " td").eq((row*4)+col).addClass('active');
		_activeTileSelection = _tileSelections[col][row];
		this.activeChanged(_tilesetIndex,col,row);
	}
	
	this.getActive = function() {
		return _activeTileSelection;
	}
	
	this.createHtml = function() {
		var html = [];
		html.push('<table id="'+_elementId+'" class="tile-grid">');
		for (var y=0;y<4;y++) {
			html.push('<tr>');
			for (var x=0; x<4; x++) {
				html.push('<td class="' + _tileSelections[x][y].getClass() + '"></td>');
			}
			html.push('</tr>');
		}
		html.push('</table>');
		
		return html.join("");
	}
}
