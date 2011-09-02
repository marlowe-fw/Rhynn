<?php
include_once('Log.php');

class ErrorHandler {
	
	const WARNING = 0;
	const CRITICAL = 1;
	
	public static function error($errorMsg, $errorType) {
		if ($errorType == self::WARNING) {
			Log::logLine('Warning: '.$errorMsg);
		} else {
			Log::logLine('Critical: '.$errorMsg);
			exit(Log::getLog());
		}
	}
}
?>