var BrushSettings = function(playfieldId, elementSelector) {
	var _elementSelector = elementSelector;
	var _contentElement = $(elementSelector);
	var _playfieldId = playfieldId;
	
	var _brushTilesets = [];
	var _activeBrushTileset = null;
	var _activeCellFunction = new CellFunction();
	
	_contentElement.append(
	'<div class="functionValues">\
		<span class="blocked" title="blocked">B</span>\
		<span class="peaceful" title="peaceful">P</span>\
	</div>'
	);
	
	_contentElement.find(".functionValues span").click(function() {
		$(this).toggleClass('active');
		var isActive = $(this).hasClass('active');
		
		if ($(this).hasClass('blocked')) {_activeCellFunction.setBlocked(isActive);}
		if ($(this).hasClass('peaceful')) {_activeCellFunction.setPeaceful(isActive);}
	});
	
	
	function onActiveTileSetChanged(tilesetIndex,col,row) {
		$.each(_brushTilesets, function(index, value) {
			if (index != tilesetIndex)
				value.setInactive();
		});
		_activeBrushTileset = _brushTilesets[tilesetIndex];
	}
	
	this.addTileSelectionControls = function(tilesetCount) {
		for (var i=0; i<tilesetCount; i++) {
			var bts = new BrushTileSet(_playfieldId, i);
			bts.activeChanged = onActiveTileSetChanged;
			_brushTilesets.push(bts);
			_contentElement.append(bts.createHtml());
		}
		if (tilesetCount  > 0) {
			_activeBrushTileset = _brushTilesets[0];
			_brushTilesets[0].setActive(0,0);
		}
	}
		
	this.getActiveTileSelection = function() {
		if (_activeBrushTileset!=null)
			return _activeBrushTileset.getActive();
	}
	
	this.getActiveCellFunction = function() {
		return _activeCellFunction;
	}
	
}
