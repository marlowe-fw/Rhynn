<?php

abstract class DBTableFieldType {
	protected $_validationRegex = null;
	private static $_subClasses = null;
	
	public static function createFieldType($typeStr) {
		if (DBTableFieldType::$_subClasses == null) {
			DBTableFieldType::$_subClasses = array();
			foreach (get_declared_classes() as $className)
			{
				if (is_subclass_of($className, 'DBTableFieldType'))
					DBTableFieldType::$_subClasses[] = $className;
			}
		}
		
		
		foreach(DBTableFieldType::$_subClasses as $subClass) {
			$fieldType = new $subClass($typeStr);
			if ($fieldType->isInitialized()) {
				return $fieldType;
			}
		}
		
		return null;
	}
	
	
	public function __construct($typeStr) {
		 $this->_validationRegex = $this->buildValidationRegex($typeStr);	
		 if ($this->_validationRegex != null) {
		 		$this->_validationRegex = "/^" . $this->_validationRegex . "$/";
		 }	
	}
	
	public final function isInitialized() {return $this->_validationRegex != null;}
	
	public function validates($value) {
		if ($this->isInitialized())
			return preg_match($this->_validationRegex, $value) > 0;
		
		return false;
	}
		
	protected abstract function typeRegex();	
	protected abstract function buildValidationRegex($typeStr);
}




class FieldType_Int extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^int\(\d+\)(?:\s(unsigned))?/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0) 	
			return null;
		
		$valRegex = '';
		
		if (count($matches) == 1) {
			$valRegex .= "[-+]?\b";
		}
		
		$valRegex .= "\d+\b";
				
		return $valRegex;
	}
	
}

class FieldType_Float extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^(?:float|double)(?:\s(unsigned))?/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0) 	
			return null;
		
		$valRegex = '';
		
		if (count($matches) == 1) {
			$valRegex .= "[-+]?\b";
		}
		
		$valRegex .= "[0-9]*\.?[0-9]+\b";
				
		return $valRegex;
	}
	
}

class FieldType_String extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^(?:(?:var)?char\((\d+)\)|text)/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0) 	
			return null;
		
		$valRegex = ".";
		
		if (count($matches) > 1) {
			$valRegex .= "{0,".$matches[1]."}";
		} else {
			$valRegex .= "*";
		}
						
		return $valRegex;
	}
	
}

class FieldType_Blob extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^blob/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0) 	
			return null;
		
		$valRegex = ".*";
						
		return $valRegex;
	}
	
}

class FieldType_Enum extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^(?:enum|set)\((.+)\)/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0 || count($matches) < 2) 	
			return null;
		
		$matches[1] = str_replace("'","",$matches[1]);
		$valRegex = "(" . str_replace(',', '|', $matches[1]) . ")";
		return $valRegex;
	}	
}

class FieldType_DateTime extends DBTableFieldType {
	
	protected function typeRegex() {
		return "/^datetime/i";
	}
	
	protected function buildValidationRegex($typeStr) {
		if (preg_match($this->typeRegex(),$typeStr,$matches) == 0)
			return null;
		
		$valRegex = "\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}";		
		return $valRegex;
	}
}


/*
$intType = DBTableFieldType::createFieldType("enum('first')");
var_dump($intType->validates("first"));
*/

/*
$typeStr = "int(10) unsigned";
$field = new FieldType_Int($typeStr);
var_dump($field->validates("10"));
	
$typeStr = "float unsigned";
$field = new FieldType_Float($typeStr);
var_dump($field->validates("10.5"));

$typeStr = "varchar(2)";
$field = new FieldType_String($typeStr);
var_dump($field->validates("ab"));

$typeStr = "blob";
$field = new FieldType_Blob($typeStr);
var_dump($field->validates("ab"));

$typeStr = "datetime";
$field = new FieldType_DateTime($typeStr);
var_dump($field->validates("2011-10-10 00:00:00"));

$typeStr = "enum('first','second')";
$field = new FieldType_Enum($typeStr);
var_dump($field->validates("first"));
*/
?>