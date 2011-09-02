<script type="text/javascript" src="js/lib/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/Datatable.js"></script>
<link href="js/lib/datatables/media/css/demo_page.css" rel="stylesheet" type="text/css" />
<link href="js/lib/datatables/media/css/custom_table_jui.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
var PlayfieldList = function(elementSelector, customOptions) {
	var _dtPlayfields = null;
	var _elementSelector = elementSelector;
	
	var options = {
		playfieldSelected : function(id, name) {
			alert(id + "," + name);
		}
	}
	
	$.extend(options, customOptions);
	
	var dtOptions = {
		dataClicked : function(row, cell) {
			var id = $(row).attr('id').replace('playfields_', '');
			name = $(row).find('td').first().html();
			options.playfieldSelected(id, name);
		}
	}

	_dtPlayfields = new DataTable(_elementSelector, dtOptions);
	this.dataClicked = _dtPlayfields.dataClicked;	
	
	
	this.load = function() {
		var jsonQuery = {
			"query":"playfields",
			"fields":"id, name",
			"orderby":"id desc, name desc"
		};
		
		var loadOptions = {
			"identityField":"id",
			idPrefix : "playfields_",
			"displayFields":["name"]
		}
		
		_dtPlayfields.load(jsonQuery, loadOptions);
	}
	
}

</script>
