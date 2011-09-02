<?php
require_once('DBTableField.php');

class DBTable {
	private $_tableName;
	private $_idField = 'id';
	private $_fields;

	private $_dbc;
	
	public function __construct($tableName) {
		$this->_dbc = DBC::instance();
		$this->_tableName = $tableName;
		$this->fieldsFromTable();
	}
	
	public function getIdFieldName() {
		return $this->_idField;
	}
	
	public function getFields() {
		return $this->_fields;
	}
	
	public function getName() {
		return $this->_tableName;
	}
	
	private function fieldsFromTable() {
		$sql = 'describe '.$this->_tableName;
		if ($result = $this->_dbc->query($sql)) {
			while ($row = $this->_dbc->nextRow($result)) {
				$field = new DBTableField($row);
				$this->_fields[$field->getName()] = $field;
			}
		}
	}

}
?>