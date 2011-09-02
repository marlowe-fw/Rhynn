var PlayfieldGrid = function(elementSelector) {
	var that = this;
	var _elementSelector = elementSelector;
	var _contentElement = $(elementSelector);
	
	var _cells = null;
	var _numCellsX = 0;
	var _numCellsY = 0;
	
	var _mouseDown = false;
	
	this.cellClick = function(col,row,tableCell) {}
	
	$(_elementSelector + " td").live('hover', function(e) {
		e.preventDefault();
		if (_mouseDown)
			triggerCellClick(this);
	});
	
	$(_elementSelector + " td").live('click', function(e) {
		e.preventDefault();
		triggerCellClick(this);
	});
	
	$(_elementSelector).mousedown(function() {_mouseDown = true;});
	$(_elementSelector).mouseup(function() {_mouseDown = false;});
	$(_elementSelector).mouseleave(function() {_mouseDown = false;});


	function triggerCellClick(tableCell) {
		var $td = $(tableCell);
		var col = $td.index();
		var row = $td.closest('tr').index();
		that.cellClick(col, row, tableCell);
	}
	
	this.cellAt = function(col,row) {
		return _cells[col][row];
	}
	
	this.load = function(playfieldId) {
		_contentElement.html('loading..');
		
		var jsonQuery = {
			"query":"playfields",
			"fields":"width,height,HEX(data)",
			"conditions" : "id=" + playfieldId
		};
		ajaxRequest("handlers/query.php", jsonQuery, onLoad);
	}
	
	this.setCell = function(col,row,tileSelection,cellFunction) {
		var $row = $(_elementSelector + " table.playfield-grid tr").eq(row);
		var $cell = $row.find("td").eq(col);
		$cell.attr('class', tileSelection.getClass());
		$cell.html(cellFunction.getHtml());
		
		_cells[col][row].TileSelection.assign(tileSelection);
		_cells[col][row].CellFunction.assign(cellFunction);
				
		//alert(_cells[1][1].CellFunction.isBlocked());
	}
	
	function onLoad(request, response) {
		var playfieldDetails = response['data'][0];
		_numCellsX = playfieldDetails.width;
		_numCellsY = playfieldDetails.height;
		
		cellsFromData(playfieldDetails["HEX(data)"]);
		_contentElement.html(that.createHtml());
	}
		
	
	this.createHtml = function() {
		var html = [];
	
		html.push('<table class="playfield-grid"><tbody>');
		
		for(var y=0; y<_numCellsY; y++) {
			html.push('<tr>');
			for (var x=0; x<_numCellsX; x++) {
				html.push(_cells[x][y].createHtml());
			}
			html.push('</tr>');
		}
		html.push('</tbody></table>');		
		
		return html.join('');
	}

	
	function cellsFromData(data) {
		var hexNumbersPerCell = 4;	// 2 bytes
		
		_cells = [];
		
		for (var x=0; x<_numCellsX; x++) {
			_cells.push([]);
			for(var y=0; y<_numCellsY; y++) {
				var index = (y*_numCellsX*hexNumbersPerCell) + (x*hexNumbersPerCell);
				var hexCell = data.substr(index,hexNumbersPerCell);				
				var intCell = parseInt(hexCell, 16);
				_cells[x].push(cellFromData(intCell));
			}
		}
	}
	
	this.debug = function() {
		for (var x=0; x<_numCellsX; x++) {
			for(var y=0; y<_numCellsY; y++) {
				alert(x + "," + y + ":" + _cells[x][y].CellFunction.MainFunction);
			}
		}			
			
	}
	
	this.getData = function() {
		var hexNumbersPerCell = 4;	// 2 bytes
		
		var data = new Array(_numCellsX*_numCellsY*hexNumbersPerCell);
		
		for (var x=0; x<_numCellsX; x++) {
			for(var y=0; y<_numCellsY; y++) {
				var hexData = hexDataFromCell(_cells[x][y]);
				var index = (y*_numCellsX*hexNumbersPerCell) + (x*hexNumbersPerCell);
				data[index] = hexData;
			}
		}
		
		return data.join("");
	}

	
	function cellFromData (intData) {
		var cellTiledata = intData & 0xff;
		var cellFunctionData = (intData >> 8) & 0xff;
		
		var tilesetIndex = (cellTiledata & 0xE0) >> 5;
		var tileIndex = cellTiledata & 0x1F;
		
		var functionMainVal = cellFunctionData & 0x0F;
		var functionTriggerVal = ((cellFunctionData & 0xF0) >> 4);
	
		var tileSelection = new TileSelection(tilesetIndex, tileIndex);
		var cellFunction = new CellFunction(functionMainVal, functionTriggerVal);
		
		return new PlayfieldCell(tileSelection, cellFunction);
	}
	
	function hexDataFromCell(cell) {
		var tileSelection = cell.TileSelection;
		var cellFunction = cell.CellFunction;
				
		var functionByte = (cellFunction.MainFunction & 0x0F) | ((cellFunction.Trigger & 0x0F) << 4);
		var tilesetByte = ((tileSelection.TilesetIndex & 0x07) << 5) | (tileSelection.TilesetSubIndex & 0x1F);
		
		var functionByteHex = functionByte.toString(16).toUpperCase();
		var tilesetByteHex = tilesetByte.toString(16).toUpperCase();
		
		if (functionByteHex.length == 1)
			functionByteHex = "0" + functionByteHex;

		if (tilesetByteHex.length == 1)
			tilesetByteHex = "0" + tilesetByteHex;
		
		return  functionByteHex + "" + tilesetByteHex;
	}
	
}
