<?php
require_once("DBTable.php");

class DBTableCache {
	private static $_tables = array();

	public static function getTable($tableName) {
		if (!isset(self::$_tables[$tableName])) {
			self::$_tables[$tableName] = new DBTable($tableName);
		}
		return self::$_tables[$tableName];
	}
	
}

?>