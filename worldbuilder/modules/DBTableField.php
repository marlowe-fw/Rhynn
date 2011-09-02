<?php
require_once('DBTableFieldType.php');

class DBTableField {
	protected $_name;
	protected $_type;
	protected $_isNullable;
	protected $_isAutoIncrement;
	
	//protected $_customValidation = array();	// could also take a delegate
	
	protected $_defaultValue = null;

	protected $_isMissing = true;	
	protected $_value = null;	
	
	public function getName() {
		return $this->_name;
	}
	
	public function __construct($description) {
		$this->_name = $description['Field'];
		$this->_type = DBTableFieldType::createFieldType($description['Type']);
		$this->_isNullable = $description['Null'] == 'YES';
		$this->_isAutoIncrement = $description['Extra'] == 'auto_increment';
		$this->_defaultValue = $description['Default'] == 'NULL' ? null : $description['Default'];
	}
	
	public function setValue($value) {
		$this->_value = $value;
		$this->_isMissing = false;
	}
	
	public function getValue() {
		return $this->_value;
	}
	
	public function getValueForStore($dbc) {
		return $this->_type->valueForStore($this->_value, $dbc);
	}
	
	public function hasValue() {
		return !$this->_isMissing;
	}
	
	public function isAutoIncrement() {
		return $this->_isAutoIncrement;
	}
	
	public function hasValidDefaultValue() {
		return $this->_defaultValue != null || $this->_isNullable;
	}
	
	public function validates() {				
		if ($this->_isMissing) {
			$this->_value = $this->_defaultValue; 
			print '<p>MISSING</p>';
		}
		
		if ($this->_value === null)
			return ($this->_isNullable);
		
		if ($this->_type != null && $this->_type->isInitialized()) {
			return $this->_type->validates($this->_value); /*&& custom validation rules*/
		}
		
		return false;
	}

}
?>