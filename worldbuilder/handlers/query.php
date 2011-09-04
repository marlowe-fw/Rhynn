<?php
require_once('../startup.php');

if (empty($_REQUEST['request'])) {
	die('Invalid request');
}

$jsonRequest = $_REQUEST['request'];


$pf = new DBQuery($jsonRequest['query']);
$result = $pf->query($jsonRequest);

print(json_encode($result));
?>