var PlayfieldTab = function(elementSelector) {
	var _elementSelector = elementSelector;
	var _contentElement = $(elementSelector);
	var _playfieldId = 0;
	var _grid = null;
	var _brushSettings = null;
		
	this.load = function(playfieldId) {
		_playfieldId = playfieldId;
		// add grid
		var gridId = "pf-grid-" + playfieldId;
		var brushControlsId = "pf-brush-controls-" + playfieldId;
		_contentElement.html(
		'<div class="playfield-tab-container clearfix">'		
		+'<div class="playfield-brush-controls" id="'+brushControlsId+'"></div>'
		+'<div class="playfield-main-controls"><input type="button" value="Save Playfield" class="playfield-button-save" /></div>'
		+'<div class="playfield-grid-container" id="' + gridId + '"></div>'
		+'</div>'
		);
		_grid = new PlayfieldGrid("#" + gridId);
		_grid.cellClick = gridCellClick;
		
		_brushSettings = new BrushSettings(_playfieldId, "#" + brushControlsId);
		
		_grid.load(playfieldId);
		loadGraphics(brushControlsId);		
		
		_contentElement.find(".playfield-button-save").click(function() {
			var data = _grid.getData();
			var jsonStore = {
				"store":"playfields",
				"values":{"id":_playfieldId, "data":data}
			};
			ajaxRequest("handlers/store.php", jsonStore, onSaved);
			showLoading('saving playfield..',true);
		});
	}
	
	function onSaved(request, response) {
		hideLoading();
		if (!response.success) {
			flashMsg(response.msg);
			return;
		} else {
			flashMsg("playfield saved");
		}
	}
	
	function gridCellClick(col, row, tableCell) {
		var tileSelection = _brushSettings.getActiveTileSelection();
		var cellFunction = _brushSettings.getActiveCellFunction();		
		_grid.setCell(col, row, tileSelection, cellFunction);
		//_grid.debug();
	}
	
	
	function loadGraphics() {
		var jsonQuery = {
			"query":"playfield_graphics pg join graphics g on pg.graphic_id = g.id",
			"fields":"pg.graphic_id, g.filename",
			"conditions" : "pg.playfield_id=" + _playfieldId
		};
		
		ajaxRequest("handlers/query.php", jsonQuery, onLoadGraphics);		
	}
	
	function onLoadGraphics(request, response) {
		var rows = response['data'];
		
		var style = [];
		var html = [];
		for (var i=0; i<rows.length; i++) {			
			style.push(_elementSelector + ' td.tileset_' + i + ' {background-image:url(FWWorldGraphics/background/' + rows[i]['filename'] + ');}');
		}
		
		$("head").append('<style type="text/css">' + style.join("") + '</style>');		
		
		 _brushSettings.addTileSelectionControls(response['count']);
	}
}