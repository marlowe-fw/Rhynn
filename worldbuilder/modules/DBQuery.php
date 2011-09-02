<?php
require_once('ErrorHandler.php');

class DBQuery {

	public function __construct($tableName) {
		$this->_dbc = DBC::instance();
		$this->_table = DBTableCache::getTable($tableName);
		$this->_fields = $this->_table->getFields();
		$this->_resultFields = array();
	}

	public function query($query) {
		$fieldStr = "*";
		$whereStr = "";
		$limitStr = "";
		$orderByStr = "";
		
		if (isset($query['fields']))
			$fieldStr = $query['fields'];
			
		if (isset($query['conditions']))
			$whereStr = " where " . $query['conditions'];
			
		if (isset($query['orderby']))
			$orderByStr = " order by " . $query['orderby'];

		if (isset($query['limit']))
			$limitStr = " limit " . $query['limit'];
			
		
		$sql = "select ".$fieldStr ." from ".$this->_table->getName().$whereStr.$orderByStr.$limitStr;		
		
		return $this->getResults($sql);
	}
	
	private function getResults($sql) {
		$resultSetArray = array(
			'success' => false,
			'msg' => '',
			'count' => 0,
			'fields' => array(),
			'data' => array()
		);

		$result = $this->_dbc->query($sql);

		if ($result == false) {
			$resultSetArray['msg'] = $this->_dbc->lastError() . ", " . $sql;
			return $resultSetArray;
		}
		
		$resultSetArray['success'] = true;
		
		while($rowValues = $this->_dbc->nextRow($result)) {			
			if (empty($resultSetArray['fields'])) {
				$resultSetArray['fields'] = array_keys($rowValues);
			}

			$resultSetArray['count']++;
			$resultSetArray['data'][] = $rowValues;
		}
		
		return $resultSetArray;
	}
}

?>
