#ifndef FWPacketValidator_h__
#define FWPacketValidator_h__

namespace fws {

class FWPacketValidator {


public:
		FWPacketValidator() {};
		virtual ~FWPacketValidator() {};

		virtual void initialize(long initValue) {};
		virtual bool isPacketValid(const unsigned char* msg, int len, bool useSignature) {return true;};

};

}

#endif // FWPacketValidator_h__
