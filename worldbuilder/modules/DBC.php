<?php

class DBC {
	
	private $_handle = null;
	private static $_instance = null;
	
	private function __construct() {
		$this->connect();
	}
	
	public static function instance() {
		if (self::$_instance == null)
			self::$_instance = new DBC();
		
		return self::$_instance;
	}
	
	private function connect() {
		$this->_handle = mysql_connect('localhost','root', 'xypass12');
		mysql_select_db('fw_core', $this->_handle);
	}

	public function query($query) {
		return mysql_query($query, $this->_handle);
	}
	
	public function nextRow($result) {
		return mysql_fetch_array($result, MYSQL_ASSOC);
	}

	public function lastError() {
		return mysql_errno().': '.mysql_error();
	}
	
	public function affectedRows() {
		return mysql_affected_rows($this->_handle);
	}

	public function lastInsertId() {
		return mysql_insert_id($this->_handle);
	}

	
	public function sqlfix($s, $pbQuotes=true)	{
		$magic = false;
		if (get_magic_quotes_gpc()) {
			$s = stripslashes($s);
		} else {
			$magic = true;
		}
		if (function_exists("mysql_real_escape_string")) {
			if ($pbQuotes) {
				return "'".mysql_real_escape_string($s, $this->_handle)."'";
			} else {
				return mysql_real_escape_string($s, $this->_handle);
			}
		} else if (!$magic) {
			$s = addslashes($s);
		}
		return $s;
	}

}
?>