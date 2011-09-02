<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		
		<script type="text/javascript" src="js/lib/jquery.min.js"></script>
		<script type="text/javascript" src="js/globalUtils.js"></script>
		
		<script type="text/javascript" src="js/lib/jquery-ui/js/jquery-ui.custom.min.js"></script>
		<script type="text/javascript" src="ui_elements/playfield_list.js"></script>

		<script type="text/javascript" src="js/Dialog.js"></script>
		<script type="text/javascript" src="js/DlgCreatePlayfield.js"></script>
		
		<link href="js/lib/jquery-ui/css/ui-darkness/jquery-ui.custom.css" rel="stylesheet" type="text/css" />		
		<link href="css/worldbuilder.css" rel="stylesheet" type="text/css" />		
	

		
		<?php include_once('ui_elements/playfield_list.inc'); ?>
		<?php include_once('ui_elements/playfield_container.inc'); ?>
		
		<script type="text/javascript">
			
			var pm = null;
			var pl = null;
			
			function initPlayfieldTabs() {
				pm = new PlayfieldTabManager("#playfield-tabs");
			}
				
			function initPlayfieldList() {
				var plOptions = {playfieldSelected : pm.addTab};
			
				pl = new PlayfieldList("#playfield-list", plOptions);				
				$("#playfield-reload").click(function () {
					reloadPlayfieldList();
					return false;
				});
				reloadPlayfieldList();
			}
			
			function reloadPlayfieldList() {
				pl.load();
			}
			
			$(function() {				
				initPlayfieldTabs();
				initPlayfieldList();
				
				$('#btnNewPlayfield').click(function() {					
					var createDlg = new DlgCreatePlayfield();
					createDlg.onSaved = function(playfieldId, playfieldName) {
						reloadPlayfieldList();
						pm.addTab(playfieldId, playfieldName);
					};
					createDlg.show();
				});
			});
		</script>
		
	</head>
	
	<body>
		<div class="clearfix" id="topBar">
			<div id="global-loader-container">
				<img id="global-loader" src="img/loader_s.gif" />
			</div>
			<div id="status"></div>
			
		</div>
		
		<div id="wrapper">
			<div id="content">
			
				<div id="playfield-tabs">
				</div>
			</div>
		</div>
		
		<div id="nav" class="clearfix left">
			<div>
			Playfields <a href="#" id="playfield-reload">reload</a>
			<table id="playfield-list" style="width:220px;"></table>
			</div>
			<input type="button" id="btnNewPlayfield" value="Create New Playfield" style="margin-top:10px;clear:both;" />
		</div>
		
	</body>	
</html>