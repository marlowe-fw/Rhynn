var CellFunction = function(mainFunctionVal, triggerVal) {
	var _blocked = 0x1;
	var _peaceful = 0x2;
	var that = this;
	
	this.MainFunction = typeof(mainFunctionVal) == 'undefined' ? 0 : mainFunctionVal;
	this.Trigger = typeof(trigerVal) == 'undefined' ? 0 : triggerVal;
	
	this.assign = function(cellfunction) {
		this.MainFunction = cellfunction.MainFunction;
		this.Trigger = cellfunction.Trigger;		
	}
	
	this.getHtml = function() {
		var cls = [];
		if (hasMainFunction(_blocked)) {cls.push("<div class=fn-blocked></div>");}
		if (hasMainFunction(_peaceful)) {cls.push("<div class=fn-peaceful></div>");}
		return cls.join("");
	}
	
	var hasMainFunction = function(fVal) {
		return (that.MainFunction & fVal) == fVal;
	}
	
	var addMainFunction = function(fVal) {
		that.MainFunction |= fVal;
	}

	var removeMainFunction = function(fVal) {
		that.MainFunction &= (~fVal);
	}
	
	var toggleMainFunction = function(toggleVal, fnVal) {
		if (toggleVal) {
			addMainFunction(fnVal);
		} else {
			removeMainFunction(fnVal);
		}
	}
	
	this.set = function(val) {
		this.MainFunction = val;
	}
	
	this.isBlocked = function() {return hasMainFunction(_blocked);}
	this.isPeaceful = function() {return hasMainFunction(_peaceful);}
	
	this.setBlocked = function(toggleVal) {toggleMainFunction(toggleVal,_blocked);}
	this.setPeaceful = function(toggleVal) {toggleMainFunction(toggleVal,_peaceful);}
}
