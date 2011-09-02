#ifndef ClientCondition_h__
#define ClientCondition_h__

namespace fws {

class ClientCondition {
public:
	static const int none = 0;
	static const int character_selected = 2;
	static const int character_active = 8;
	static const int character_alive = 16;

private:
	ClientCondition() {};
};

}
#endif // ClientCondition_h__