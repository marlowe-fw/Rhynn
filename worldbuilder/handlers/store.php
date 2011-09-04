<?php
require_once('../startup.php');

if (empty($_REQUEST['request'])) {
	die('Invalid request');
}

$result = array("success" => true, "msg" => "", "results" => array());
$rows = $_REQUEST['request'];

if (isset($rows['store']))	// not in array form (single row) - wrap in array
	$rows = array($rows);
	
foreach($rows as $row) {
	$curResult = execStore($row);
	if ($curResult['success'] !== true) {
		$result['success'] = false;
	}
	if (!empty($curResult['msg'])) {
		if (!empty($result['msg']))
			$result['msg'] += "\n";
			
		$result['msg'] += $curResult['msg'];
	}
		
	$result['results'][] = $curResult;
}

print(json_encode($result));


function execStore($jsonRequest) {
	$store = new DBStore($jsonRequest['store']);
	$store->setValues($jsonRequest['values']);

	return $store->store();
}
?>