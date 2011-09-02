var gResourceFiles = [];

function includeResourceFile(filename, filetype){
	if ($.inArray(filename, gResourceFiles)!=-1) {
		return;
	}

	if (filetype=="js"){
		var fileref=document.createElement('script');
		fileref.setAttribute("type","text/javascript");
		fileref.setAttribute("src", filename);
	}
	else if (filetype=="css"){
		var fileref=document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}
	if (typeof fileref!="undefined") {
		document.getElementsByTagName("head")[0].appendChild(fileref);
		gResourceFiles.push(filename);
	}
}

function ajaxRequest(url, request, callback) {
	$.ajax({
		url:url, data : {"request": request}, dataType : "json",
		type:"post", 
		success : function(response) {callback(request,response);}, 
		error:function(jqXHR, textStatus, errorThrown) {alert(textStatus);}
	});	
}

function sliderWithInput($sliderEl, $input, options) {
	options.slide = function( event, ui ) {
		$input.val(ui.value);
	};
	
	$sliderEl.slider(options);
	
	$input.change(function() {
		var value = $(this).val();
		if ( ! isNaN (value-0))
			$sliderEl.slider("value", value);
	});
}


currentFlashTimeout = null;
loadingCounter = 0;

function flashMsg(msg,selector) {
	if (currentFlashTimeout != null)
		clearTimeout(currentFlashTimeout);

	if (typeof(selector) == 'undefined')
		selector = '#status';
	
	$(selector).html('<span style="display:none;">'+msg+'</span>');
	$span = $(selector).find('span');
	$span.fadeIn(500,function() {
		currentFlashTimeout = setTimeout(function() {currentFlashTimeout = null;$span.fadeOut(2000);},2000);
	});
}


function showLoading(msg, modal) {
	$('#global-loader').fadeIn();
	
	if (typeof(msg) != 'undefined' && msg != '') {
		$('#status').html(msg);
	}
	
	if (typeof(modal) != 'undefined' && modal) {
		$('#loading-overlay').remove();
		$loadingDiv = $('<div />').attr({'id':'loading-overlay'});
		$('#wrapper').prepend($loadingDiv);
	}
	loadingCounter++;
}

function hideLoading() {
	loadingCounter--;
	if (loadingCounter <= 0) {
		loadingCounter = 0;
		$('#global-loader').fadeOut(600);
	}
	$('#status').html('');
	$('#loading-overlay').fadeOut(600, function() {$('#loading-overlay').remove()});
}

function dateToSQL(dt) {
		var d = dt.getDate();
		var m = dt.getMonth();
		var y = dt.getFullYear();
		var hh = dt.getHours();
		var mm = dt.getMinutes();
		var ss = dt.getSeconds();
		
		if (d<10)
			d = '0'+d;
		if (m<10)
			m = '0'+m;
		if (hh<10)
			hh = '0'+hh;
		if (mm<10)
			mm = '0'+mm;
		if (ss<10)
			s = '0'+ss;
			
		
		return y + "-" + m + "-" + d + " " + hh + ":" + mm + ":" + ss;
}