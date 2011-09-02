#ifndef MinClient_h__
#define MinClient_h__

#include "net_basics.h"
#include "NetBuffer.h"
#include <vector>
#include <map>


namespace min {

	class MinMessage;
	class MinClientObserver;

class MinClient {

public:
	enum ClientState {None, Connected, Disconnected, DisconnectNotified, Error, AuthorizedShutdown};
	enum ClientRemovalInfo {Remove_ClientDisconnect, Remove_ServerShutdown, Remove_Logic, Remove_Error, Remove_Kicked};
	/** The size of the incoming buffer determines how many bytes a client can receive *at most* in one receiving (using recv internally) action. */
	static const int default_client_buffer_in_size = 768;
	/** Determines how many bytes can be stored in one go without splitting the bytes up into multiple rows (called lanes). */
	static const unsigned long int default_client_buffer_out_lane_size = 4096;
	static const unsigned int default_client_buffer_out_max_lanes = 0;	// 0 for no limit

	MinClient();
	MinClient(int bufferSizeIn);
	MinClient(int newBufferSizeIn, unsigned long outBufferLaneSize, unsigned int outBufferMaxLanes);
	virtual ~MinClient();
	int receiveMax();
	int sendAll(const unsigned char* const msg);
	unsigned int sendFromBuffer(unsigned int clientSendLimit, int* error, bool forceSendAll = false);
	bool sendAllFromBuffer();
	bool putMessageIntoOutBuffer(MinMessage& msg, bool validateLength = true);
	bool putMessageAndSendAll(MinMessage& msg, bool validateLength = true);
	// todo: use setState inside those
	void setConnected(int socket);
	int setDisconnected();
	void disallowTCPDelay();
	bool hasCompleteMessage() const;
	void removeCompleteMessage();
	inline void setInOutgoingQueue(bool val) {inOutgoingQueue = val;}
	inline bool isInOutgoingQueue() {return inOutgoingQueue;}
	//bool addMessageToOutQueue();
	//int sendOutQueueMessages(int limit = 0);

	inline int getState() const {return state;}
	// todo
	void setState(int newState);

	inline int getClientSocket() const {return clientSocket;}
	inline const unsigned char* getBufferIn() const {return bufferIn;}
	unsigned char* requestBytesFromOutBuffer(unsigned long numBytes);
	bool putBytesIntoOutBuffer(const unsigned char* copyBytes, unsigned long numBytes);
	inline unsigned long getOutBufferRemainingFill() {return bufferOut->getRemainingFill();}	

	void addObserver(MinClientObserver* observer);
	void removeObserver(MinClientObserver* observer);

protected:
	int state;
	unsigned int curMsgLength;
	unsigned int bufferSizeIn;
	unsigned int bufferFill;
	int clientSocket;
	unsigned char* bufferIn;

	NetBuffer* bufferOut;
	bool inOutgoingQueue;


private:
	std::vector<MinClientObserver*> observers;

	void notifyDestroyed();
	void notifyStateChanged();

	};
}

#endif // MinClient_h__
