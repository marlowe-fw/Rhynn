<style type="text/css">
	.ui-tabs .ui-tabs-panel {padding:5px;}

	.ui-button-text-only .ui-button-text {
		padding: 0 3px;
		font-size:0.8em;
	}

	.ui-button .ui-button-text {
		display: block;
		line-height: 1.0;
	}	

	td.tile_0_0 {background-position: 0 0}
	td.tile_0_1 {background-position: -24px 0}
	td.tile_0_2 {background-position: -48px 0}
	td.tile_0_3 {background-position: -72px 0}

	td.tile_1_0 {background-position: 0 -24px}
	td.tile_1_1 {background-position: -24px -24px}
	td.tile_1_2 {background-position: -48px -24px}
	td.tile_1_3 {background-position: -72px -24px}

	td.tile_2_0 {background-position: 0 -48px}
	td.tile_2_1 {background-position: -24px -48px}
	td.tile_2_2 {background-position: -48px -48px}
	td.tile_2_3 {background-position: -72px -48px}

	td.tile_3_0 {background-position: 0 -72px}
	td.tile_3_1 {background-position: -24px -72px}
	td.tile_3_2 {background-position: -48px -72px}
	td.tile_3_3 {background-position: -72px -72px}

	
	table.playfield-grid, table.tile-grid {border-collapse:separate;background:black;border-spacing:1px;}
	table.playfield-grid {border-spacing:1px;}
	table.tile-grid {border-spacing:3px;}
	
	table.playfield-grid td, table.tile-grid td {width:24px;height:24px;min-height:24px;min-width:24px;margin:0px;padding:0px;background-color:#333333;}
	table.playfield-grid td {vertical-align:top;padding:0px;}
	table.tile-grid td {opacity:0.4;}
	table.tile-grid td.active, table.tile-grid td.active:hover {opacity:1.0;width:22px;height:22px;min-height:22px;min-width:22px;border:1px dashed white;background-color:#000000;}
	table.playfield-grid td:hover, table.tile-grid td:hover {border:1px solid #99ff99;width:22px;height:22px;min-height:22px;min-width:22px;}
	table.tile-grid td:hover {opacity:1.0;}
	
	.playfield-tab-container {}
	.playfield-brush-controls {width:150px;min-width:150px;float:left;border:qpx solid red;}
	.playfield-grid-container {overflow:auto;max-width:100%;mx-height:100%;float:left;}
	.playfield-main-controls {border-bottom:1px solid #202020;margin-bottom:5px;float:left;}
	.playfield-button-save {}
	
	.functionValues {margin:5px 0;}
	.functionValues > span {border:1px solid #999999;padding:2px 5px;cursor:pointer;font-weight:bold;}
	.functionValues > span:hover {border:1px solid #cccccc;}
	.functionValues > span.active {border:1px solid white;background:#FFAF0F;color:black;}
	
	.fn-blocked,.fn-peaceful {float:left;width:8px;height:8px;min-width:8px;min-height:8px;opacity:0.8;}
	.fn-blocked {background-image:url(img/fn-blocked.png);}
	.fn-peaceful {background-image:url(img/fn-peaceful.png);}
</style>

<script type="text/javascript" src="js/PlayfieldTabManager.js"></script>
<script type="text/javascript" src="js/PlayfieldTab.js"></script>
<script type="text/javascript" src="js/PlayfieldGrid.js"></script>
<script type="text/javascript" src="js/BrushSettings.js"></script>
<script type="text/javascript" src="js/BrushTileset.js"></script>
<script type="text/javascript" src="js/CellFunction.js"></script>
<script type="text/javascript" src="js/TileSelection.js"></script>

<script type="text/javascript">
</script>
