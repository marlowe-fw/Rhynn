var Dialog = function(name, customOptions) {
	var _name = name;
	var _elementId = 'wrapper_' + _name;
	var _loadInitiated = false;
	var that = this;
	
	var _options = {
		'modal':true,
		'width':'auto'
	};
	$.extend(_options,customOptions);
	
	this.beforeShow = function() {};
	this.afterShow = function() {};
	
	this.getName = function() {
		return _name;
	}
	
	this.getElementId = function() {
		return _elementId;
	}	

	this.getElement = function() {
		return $('#' + _elementId);
	}	
	
	this.show = function() {
		var element = $('#' + _elementId);
		
		if (element.length == 0) {
			if (_loadInitiated) {
				alert('failed to load dialog: ' + _name);
				_loadInitiated = false;
				return;
			}
			
			_loadInitiated = true;
			// make sure dialog contents are loaded
			element = $('<div />');
			element.attr({'id':_elementId}).css({'display':'none'});
			$('body').append(element);
			var curTime = new Date().getTime();
			$('#' + _elementId).load('ui_elements/' + _name + '.inc?' + curTime, that.show);
			return;
		}
		_loadInitiated = false;

		that.beforeShow();
				
		element.dialog(_options);
		
		that.afterShow();
	}
	
	this.close = function() {
		$('#' + _elementId).dialog('close');
	}
}