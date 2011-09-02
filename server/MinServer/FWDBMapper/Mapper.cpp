#include "Mapper.h"

using namespace fwmapper;

Mapper::Mapper(mysqlpp::Connection& newConn) : conn(newConn) {}
Mapper::~Mapper() {}

std::string Mapper::createMap(std::string table) {
	return NULL;
}

