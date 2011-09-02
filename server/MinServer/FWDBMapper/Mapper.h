#ifndef Mapper_h__
#define Mapper_h__

#include <string>
#include "mysql++.h"

namespace fwmapper {

class Mapper {

private:
	mysqlpp::Connection conn;

public: 
	Mapper(mysqlpp::Connection& newConn);
	virtual ~Mapper();
	
	std::string createMap(std::string table);

};

}

#endif // Mapper_h__