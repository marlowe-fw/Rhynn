var PlayfieldTabManager = function(elementSelector) {
	var that = this;
	var _elementSelector = elementSelector;
	var _contentElement = $(elementSelector);
	
	_contentElement.html('<ul></ul><div style="display:none;" id="hidden-playfield-tabs"></div>');
	_contentElement.tabs();
		
	this.addTab = function(playfieldId, name) {
		var tabId = 'playfield-tab-' + playfieldId;
		if ($("#" + tabId).length == 0) {
			// playfield tab does not exist yet
			$('#hidden-playfield-tabs').append('<div id="' + tabId + '></div>');
			_contentElement.tabs("add", "#" + tabId, name);
			var pt = new PlayfieldTab("#" + tabId);
			pt.load(playfieldId);
			autoAdjustElementDimensions();
		}
		
		_contentElement.tabs("select", tabId);
	}
	
	function autoAdjustElementDimensions(tabElement) {
		var tabsOffset = _contentElement.offset();
		var mainControlsHeight = _contentElement.find('.playfield-main-controls').height();
		var maxGridHeight = $(window).height() - tabsOffset.top - 90;
		var maxGridWidth = $('#playfield-tabs').width() - 200;
		_contentElement.find('.playfield-grid-container').css(
		{'height': maxGridHeight + 'px','min-height': maxGridHeight + 'px','width': maxGridWidth + 'px','min-width': maxGridWidth + 'px'}
		);
	}
	
	$(window).resize(function() {
		autoAdjustElementDimensions();		
	});
	
}