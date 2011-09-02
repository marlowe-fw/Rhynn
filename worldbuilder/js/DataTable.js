var DataTable = function(elementId, customOptions) {

	var that = this;
		
	// todo: make private
	this._elementId = elementId;		
	this._dataTable = null;

	var options = {
		dataClicked : function(row, cell) {}
	};
	
	var loadOptions = {
		identityField : "id",
		idPrefix : "dt_",
		displayFields : null
	}
	
	$.extend(options, customOptions);
	
			
	this.load = function(jsonQuery, options) {		
		$.extend(loadOptions, options);
		$.ajax({
			url:"handlers/query.php", data : {"request": jsonQuery}, dataType : "json",
			type:"post", success : populateList
		});
	}
	
	function checkRemoveDataTable() {
		if (that._datatable != null) {
			that._datatable.fnDestroy();
			that._datatable = null;
		} else {
			$(that._elementId + " tbody tr td").live('click', function() {
				options.dataClicked($(this).closest('tr'), $(this));
			});		
		}
	}
	
	function populateList(result) {
		checkRemoveDataTable();
		var html = generateTable(result);
		$(that._elementId).html(html);
		addDataTable();
	}

	function generateTable(result) {
		if (loadOptions.displayFields == null) 
			loadOptions.displayFields = result['fields'];
		
		var fields = loadOptions.displayFields;
		
		var html = [];
		
		html.push('<thead><tr>');
		
		for(var i=0; i<fields.length; i++) {
			html.push('<th>' + fields[i] + '</th>');
		}
		html.push('</tr></thead><tbody>');

		var rows = result['data'];
		for(var i=0; i < rows.length; i++) {
			var row = rows[i];
			html.push('<tr');
			if (loadOptions.identityField != null) {
				html.push(' id="' + loadOptions.idPrefix + row[loadOptions.identityField] + '"');
			}
			html.push('>');
			
			for(var f=0; f<fields.length; f++) {
				html.push('<td>' + row[fields[f]] + '</td>');
			}
			html.push('</tr>');
		}
		html.push('</tbody>');
		
		return html.join("");
	}
	
			
	function addDataTable() {		
		that._datatable = $(that._elementId).dataTable({
				"bJQueryUI": true,
				"bPaginate": false,
				"bLengthChange": true,
				"bFilter": false,
				"bSort": true,
				"bInfo": false,
				"bAutoWidth": true,
				"bDestroy":true
			});	
	}
	

	
};