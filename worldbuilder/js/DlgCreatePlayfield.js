var DlgCreatePlayfield = function() {
	var that = this;
	
	var _saveProgress = 0;
	var _savedPlayfieldId = 0;
	var _savedPlayfieldName = '';
	
	var _customOptions = {
		buttons : { 
			"Create": createPlayfield
		}
	}
	
	var _dlg = new Dialog('DlgCreatePlayfield', _customOptions);

	_dlg.afterShow = function() {
		var $el = _dlg.getElement();
		
		var sliderOptions = {
			value:10,
			min: 1,
			max: 99,
			step: 1
		};
		
		var $sliderW = $el.find('#sliderWidth');
		var $inputW = $el.find("input[name='width']");
		var $sliderH = $el.find('#sliderHeight');
		var $inputH = $el.find("input[name='height']");

		sliderWithInput($sliderW, $inputW, sliderOptions);
		sliderWithInput($sliderH, $inputH, sliderOptions);
	
		loadGraphics();
	}
	
	function loadGraphics() {
		var jsonQuery = {
			"query":"graphics",
			"fields":"id,filename",
			"conditions" : "type='background'"
		};
		ajaxRequest("handlers/query.php", jsonQuery, onGraphicsLoaded);	
		$("#playfield-graphics-available").html("<li>loading..</li>");
	}
	
	function onGraphicsLoaded(request, response) {
		$("#playfield-graphics-available,#playfield-graphics").html("");
		for (var i=0; i<response['data'].length; i++) {
			var row = response['data'][i];
			var $img = $("<img />").attr({"id":"gfx_" + row.id, "src":"FWWorldGraphics/background/" + row.filename }).addClass("bg-thumb");
			$("#playfield-graphics-available").prepend($img);
		}
		setupDragAndDrop();
	}
	
	function setupDragAndDrop() {
		$("#playfield-graphics img,#playfield-graphics-available img").draggable({
				revert:'invalid',
				helper: 'clone', appendTo: '#playfield-graphics-selector',
				containment:"#playfield-graphics-selector"
		});
		
		$("#playfield-graphics").droppable({
			accept: "#playfield-graphics-available img",
			drop: function( event, ui ) {
				$(this).prepend(ui.draggable);
				ui.draggable.css({opacity:"0.1"}).animate({opacity:"1.0"}, 700);
			}
		});	

		$("#playfield-graphics-available").droppable({
			accept: "#playfield-graphics img",
			drop: function( event, ui ) {
				$(this).prepend(ui.draggable);
				ui.draggable.css({opacity:"0.1"}).animate({opacity:"1.0"}, 700);
			}
		});	

	}
	

	function createPlayfield() {
		_saveProgress = 0;
	
		showLoading('creating playfield..', true);
		
		var $el = _dlg.getElement();
		var name = $el.find('input[name="name"]').val();
		var description = $el.find('input[name="description"]').val();
		var width = $el.find('input[name="width"]').val();
		var height = $el.find('input[name="height"]').val();
		
		var totalBytes = width * height;
		var data = '';		
				
		var created_date = dateToSQL(new Date());
		
		for (var i=0; i<totalBytes; i++) {
			data += '0000';
		}
		
		var jsonStore = {
			"store":"playfields",
			"values":{"width":width,"height":height,"name":name,"description":description,"created_date":created_date,"data":data}
		};
		
		_savedPlayfieldName = name;
		
		ajaxRequest("handlers/store.php", jsonStore, saveDone);
	}
	
	function saveDone(request, response) {
		_saveProgress++;
		if (_saveProgress < 2) {
			_savedPlayfieldId = response.results[0].last_insert_id;
			var records = [];
			$("#playfield-graphics img").each(function() {
				var graphicId = $(this).attr('id').replace("gfx_", "");
				records.push({
					"store":"playfield_graphics", 
					"values" : {"playfield_id":_savedPlayfieldId,"graphic_id":graphicId}
					});
			});
		
			ajaxRequest("handlers/store.php", records, saveDone);
		
			return;
		}
		_dlg.close();
		hideLoading();
		flashMsg("playfield saved");
		that.onSaved(_savedPlayfieldId, _savedPlayfieldName);
	}
	
	this.onSaved = function(playfieldId, playfieldName) {}
	
	this.show = function() {
		var $el = _dlg.getElement()
		
		$el.find("input[name='name']").val('');
		$el.find("input[name='description']").val('');
		
		$el.find("input[name='width']").val(10);
		$el.find("input[name='height']").val(10);
		
		_dlg.show();
	}
	
	this.close = function() {
		_dlg.close();
	}
	
}