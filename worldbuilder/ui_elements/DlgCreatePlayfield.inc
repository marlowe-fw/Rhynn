<style type="text/css">
	#playfield-graphics, #playfield-graphics-available {border:1px solid white;width:316px;min-width:316px;margin-top:15px;overflow:auto;}
	#playfield-graphics {height:64px;max-height:64px;}
	
	#playfield-graphics-available {height:112px;max-height:112px;}
	#playfield-graphics	img, #playfield-graphics-available img {margin:2px;width:48px;height:48px;}
	.bg-thumb {margin:2px;width:48px;height:48px;}
</style>

<form name="createPlayfield">
<table>
	<tr>
		<td>Playfield Name</td>
		<td><input type="text" value="" name="name" maxlength="32" /></td>
	</tr>
	<tr>
		<td>Description</td>
		<td><input type="text" value="" name="description" maxlength="128" /></td>
	</tr>
	<tr>
		<td>Width</td>
		<td style="whitespace:nowrap">
		<div style="float:left;width:75px;" id="sliderWidth"></div>
		&nbsp; <input type="text" value="10" name="width" size="2" maxlength="2" /></td>
	</tr>
	<tr>
		<td>Height</td>
		<td style="whitespace:nowrap">
		<div style="float:left;width:75px;" id="sliderHeight"></div>
		&nbsp; <input type="text" value="10" name="height" size="2" maxlength="2" /></td>
	</tr>
</table>

</form>

<div id="playfield-graphics-selector" class="clearfix">

	<div id="playfield-graphics">
	</div>

	<div id="playfield-graphics-available"></div>

</div">
