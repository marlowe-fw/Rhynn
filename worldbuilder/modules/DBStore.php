<?php
require_once('ErrorHandler.php');
require_once('DBTable.php');
require_once('DBTableCache.php');
require_once('DBTableField.php');

class DBStore {

	protected $_dbc;
	protected $_table;
	protected $_fields;

	protected $_values = array();
	protected $_isUpdate = false;	


	public function __construct($tableName) {
		$this->_dbc = DBC::instance();
		$this->_table = DBTableCache::getTable($tableName);
		$this->_fields = $this->_table->getFields();
	}
	
	public function setValues($values) {
		$this->_values = $values;
	} 
	
	public function setValuesFromJSON($json) {
		$this->setValues(json_decode($json, true));
	}
	
	public function lastError() {
		
	}
		
	private function validates(&$errorMsg) {
		// validate all fields against data set
		foreach($this->_fields as $fieldName => $field) {
			if (!isset($this->_values[$fieldName])) {
				if ($this->_isUpdate || $this->isIdAndAutoIncrement($field) || $field->hasValidDefaultValue())
				{
					continue;
				} else {
					$errorMsg = $this->_table->getName() . " DBStore: Missing required field:".$fieldName;
					return false;
				}
			}
						
			$field->setValue($this->_values[$fieldName]);
			
			if (!$field->validates()) {
				$errorMsg = $this->_table->getName() . " DBStore: not validating:".$fieldName.":".$this->_values[$fieldName];
				return false;
			}
		}
		
		return true;
	}
		
	private function isIdAndAutoIncrement($field)  {
		return $this->isId($field) && $field->isAutoIncrement();
	}

	private function isId($field)  {
		return $field->getName() == $this->_table->getIdFieldName();
	}

	
	public function store() {
		$resultArray = array(
			'success' => false,
			'msg' => ''
		);
		
		// if id is present, this is an update
		$this->_isUpdate = isset($this->_values[$this->_table->getIdFieldName()]);
		
		if (!$this->validates($resultArray['msg']))
			return $resultArray;
		
		$sql = '';
				
		$fieldCount = 0;
		
		if ($this->_isUpdate) {
			$sql = $this->getUpdateSQL();
		} else {
			$sql = $this->getInsertSQL();
		}
		
		if (!$this->_dbc->query($sql)) {
			$resultArray['success'] = false;
			$resultArray['msg'] = 'DB Error: '.$this->_dbc->lastError();
		} else {
			$resultArray['success'] = true;
			if ($this->_isUpdate) {
				$resultArray['affected_rows'] = $this->_dbc->affectedRows();
			} else {
				$resultArray['last_insert_id'] = $this->_dbc->lastInsertId();
			}
		}
		
		return $resultArray;
	}
	
	private function getUpdateSQL() {
		$sql = 'update ' .$this->_table->getName() . ' set ';
	
		$fieldCount = 0;
		foreach($this->_fields as $fieldName => $field) {
			if ($fieldName == $this->_table->getIdFieldName())
				continue;
			
			if ($field->hasValue()) {
				$value = $field->getValue();
				
				if ($fieldCount > 0)
					$sql .= ',';
				
				$sql .= $fieldName . '='; 
				if ($value === null)
					$sql .= 'null';
				else
					$sql .= $field->getValueForStore($this->_dbc);
			
				$fieldCount++;
			}
			
		}
		
		$sql .= " where ".$this->_table->getIdFieldName()." = ".$this->_dbc->sqlfix($this->_fields[$this->_table->getIdFieldName()]->getValue());
		
		return $sql;
	}
	
	private function getInsertSQL() {
		$sql = 'insert into ' .$this->_table->getName();
	
		$fieldsStr = '';
		$valuesStr = '';
	
		$fieldCount = 0;
		foreach($this->_fields as $fieldName => $field) {
			
			if ($field->hasValue()) {
				$value = $field->getValue();
				
				if ($fieldCount > 0) {
					$fieldsStr .= ',';
					$valuesStr .= ',';
				}
				$fieldsStr .= $fieldName;
				
				if ($value === null)
					$valuesStr .= 'null';
				else
					$valuesStr .= $field->getValueForStore($this->_dbc);
			
				$fieldCount++;
			}
			
		}
		
		$sql .= '('.$fieldsStr.') values ('.$valuesStr.')';
		
		return $sql;

	}
	
}

?>