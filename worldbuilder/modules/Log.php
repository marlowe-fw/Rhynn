<?php
class Log {
	
	private static $lineSeparator = '<br />';
	private static $logText = '';
	
	public static function logLine($msg) {
		if (self::$logText != '') 
			self::$logText .= self::$lineSeparator;
			
		self::$logText .= $msg;
	}
	
	public static function getLog() {
		return self::$logText;
	}
	
	public static function clear() {
		self::$logText = '';
	}
	
}

?>