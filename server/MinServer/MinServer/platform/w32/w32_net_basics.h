#ifndef w32_net_basics_h__
#define w32_net_basics_h__

#include <winsock2.h>
#include <WS2tcpip.h>
//#include <Windows.h>
#include <stdlib.h>
#include <string>
#include <stdio.h>
#include <math.h>


namespace min {

	class NetPort {

		public:

			static bool netStart() {
				WSADATA wsaData;	
				if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
					fprintf(stderr, "WSAStartup failed.\n");
					return false;
				}
				return true;
			}

			static bool netStop() {
				WSACleanup();
				return true;
			}


			static bool closeSocket(int socketDescriptor) {
				shutdown(socketDescriptor, SD_BOTH);
				closesocket(socketDescriptor);
				return true;
			}

			static bool setSocketNonBlocking(int socketDescriptor) {
				u_long nNoBlock = 1;
				ioctlsocket(socketDescriptor, FIONBIO, &nNoBlock);
				return true;
			}

			static void setNoTCPDelay(int socketDescriptor) {
				int flag = 1;
				setsockopt(socketDescriptor,            /* socket affected */
					IPPROTO_TCP,     /* set option at TCP level */
					TCP_NODELAY,     /* name of option */
					(char *) &flag,  /* the cast is historical
									 cruft */
									 sizeof(int));
			}

			static void allowSocketAddrReuse(int socketDescriptor) {
				const char yes = '1';
				setsockopt(socketDescriptor, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));
			}

			static int lastNetError() {
				return WSAGetLastError();
			}

			static bool hadOperationOnBlockedSocket() {
				return WSAGetLastError() == WSAEWOULDBLOCK;
			}


			static int socketError(int socketDescriptor) {
				 int nOptionValue;
				 int nOptionValueLength = sizeof(nOptionValue);

				 //Get error code specific to this socket
				 getsockopt(socketDescriptor, SOL_SOCKET, SO_ERROR, (char*)&nOptionValue, &nOptionValueLength);

				 return nOptionValue;
			}

			static void uintToNBytes(unsigned int i, unsigned char* buf, unsigned int numBytes, int startIndex = 0) {
				if (numBytes == 0) return;

				unsigned int cursor = startIndex;
				while (numBytes > 0) {
					--numBytes;
					*(buf+cursor) = (i >> (numBytes * 8));
					++cursor;
				}
			}

			static void intToNBytes(int i, unsigned char* buf, unsigned int numBytes, int startIndex = 0) {
				if (numBytes == 0) return;

				*(buf+startIndex) = ((i >> ((--numBytes) * 8)) & 127);
				unsigned int cursor = startIndex+1;
				while (numBytes > 0) {
					--numBytes;
					*(buf + cursor) = (i >> (numBytes * 8));
					++cursor;
				}

				if (i < 0) {
					*(buf+startIndex) |= 128;
				}
			}

			static unsigned int uintFromNBytes(const unsigned char* buf, unsigned int numBytes, int startIndex = 0) {
				if (numBytes == 0) return 0;

				unsigned int result = 0;
				unsigned int cursor = startIndex;
				while (numBytes > 0) {
					--numBytes;
					result |= (*(buf+cursor) << (numBytes * 8));
					++cursor;
				}
				return result;
			}

			static int intFromNBytes(const unsigned char* buf, unsigned int numBytes, int startIndex = 0) {
				if (numBytes == 0) return 0;

				unsigned int usedBytes = numBytes;
				int result = (*(buf+startIndex) & 127) << ((--numBytes) * 8);
				unsigned int cursor = startIndex + 1;
				while (numBytes > 0) {
					--numBytes;
					result |= *(buf+cursor) << (numBytes * 8);
					++cursor;
				}
				if ((*(buf+startIndex) & 128) != 0) {
					result -= (1 << ((usedBytes * 8) - 1)); 
				}

				return result;
			}

			static void uintTo3Bytes(unsigned int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = i >> 16;
				*(buf+startIndex+1) = i >> 8;
				*(buf+startIndex+2) = i;
			}

			static void intTo3Bytes(int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = (i >> 16) & 127;
				*(buf+startIndex+1) = i >> 8;
				*(buf+startIndex+2) = i;
				if (i < 0) {
					*(buf+startIndex) |= 128;
				}
			}

			static unsigned int uintFrom3Bytes(const unsigned char* buf, int startIndex = 0) {
				unsigned int result = *(buf+startIndex) << 16;
				result |= *(buf+startIndex+1) << 8;
				result |= *(buf+startIndex+2);
				return result;
			}

			static int intFrom3Bytes(const unsigned char* buf, int startIndex = 0) {
				int result = (*(buf+startIndex) & 127) << 16;
				result |= *(buf+startIndex+1) << 8;
				result |= *(buf+startIndex+2);
				if ((*(buf+startIndex) & 128) != 0) {
					result -= 8388608;
				}
				return result;
			}


			static void uintTo4Bytes(unsigned int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = (i >> 24);
				*(buf+startIndex+1) = i >> 16;
				*(buf+startIndex+2) = i >> 8;
				*(buf+startIndex+3) = i;
			}

			static void intTo4Bytes(int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = (i >> 24) & 127;
				*(buf+startIndex+1) = i >> 16;
				*(buf+startIndex+2) = i >> 8;
				*(buf+startIndex+3) = i;
				if (i < 0) {
					*(buf+startIndex) |= 128;
				}
			}

			static unsigned int uintFrom4Bytes(const unsigned char* buf, int startIndex = 0) {
				unsigned int result = *(buf+startIndex) << 24;
				result |= *(buf+startIndex+1) << 16;
				result |= *(buf+startIndex+2) << 8;
				result |= *(buf+startIndex+3);
				return result;
			}

			static int intFrom4Bytes(const unsigned char* buf, int startIndex = 0) {
				int result = (*(buf+startIndex) & 127) << 24;
				result |= *(buf+startIndex+1) << 16;
				result |= *(buf+startIndex+2) << 8;
				result |= *(buf+startIndex+3);
				if ((*(buf+startIndex) & 128) != 0) {
						result -= 2147483648;
				}
				return result;
			}

			static void uintTo2Bytes(unsigned int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = (i >> 8);
				*(buf+startIndex+1) = i;
			}

			static void intTo2Bytes(int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = (i >> 8) & 127;
				*(buf+startIndex+1) = i;
				if (i < 0) {
					*(buf+startIndex) |= 128;
				}
			}

			static unsigned int uintFrom2Bytes(const unsigned char* buf, int startIndex = 0) {
				unsigned int result = (*(buf+startIndex) & 255) << 8;
				result |= *(buf+startIndex+1);
				return result;
			}

			static int intFrom2Bytes(const unsigned char* buf, int startIndex = 0) {
				int result = (*(buf+startIndex) & 127) << 8;
				result |= *(buf+startIndex+1);
				if ((*(buf+startIndex) & 128) != 0) {
					result -= 32768;
				}
				return result;
			}


			static void uintToByte(unsigned int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = i;
			}

			static void intToByte(int i, unsigned char* buf, int startIndex = 0) {
				*(buf+startIndex) = i & 127;
				if (i < 0) {
					*(buf+startIndex) |= 128;
				}
			}

			static unsigned int uintFromByte(const unsigned char* buf, int startIndex = 0) {
				unsigned int result = *(buf+startIndex);
				return result;
			}

			static int intFromByte(const unsigned char* buf, int startIndex = 0) {
				int result = *(buf+startIndex) & 127;
				if ((*(buf+startIndex) & 128) != 0) {
					result -= 128;
				}
				return result;
			}

			// naive float packing, sufficient for most cases
			// -------------------------------------------------------
			static void doubleTo4BytesP2(double d, unsigned char* buf, int startIndex = 0) {
				int sign = 0;
				int iValue = 0;
				if (d < 0) {					
					sign = 1;
					d = -d;
				}
				iValue = (int)((d * 10.0 * 10.0)+0.5);
				
				*(buf+startIndex) = ((iValue >> 24) & 0x7f) | (sign << 7);
				*(buf+startIndex+1) = (iValue >> 16);
				*(buf+startIndex+2) = (iValue >> 8);
				*(buf+startIndex+3) = (iValue);
			}
			
			static double doubleFrom4BytesP2(const unsigned char* buf, int startIndex = 0) {
				int wholeResult = (((*(buf+startIndex)) & 0x7f) << 24) | ((*(buf+startIndex+1)) << 16) | ((*(buf+startIndex+2)) << 8) | ((*(buf+startIndex+3)));
				
				double result = (double)(wholeResult / 100.0);
				if (((*(buf+startIndex)) & 0x80) == 0x80) {
					return -result;
				}
				return result;
			}

			static std::string stringFromBytes(const unsigned char* buf, int numBytes, int startIndex = 0) {
				return std::string((const char*)(buf+startIndex), numBytes);
			}

			static void stringToBytes(std::string str, unsigned char* buf, int startIndex = 0) {
				memcpy(buf+startIndex, str.data(), str.length());
			}

			static void stringToBytes(std::string str, unsigned char* buf, int numBytes, int startIndex = 0) {
				memcpy(buf+startIndex, str.data(), numBytes);
			}

			// note that value is returned by copying it into destBuf 
			static void ucharCopy(unsigned char* destBuf, const unsigned char* buf, int numBytes, int startIndexSrc = 0, int startIndexDest = 0) {
				const unsigned char* srcBuf = buf + startIndexSrc;
				destBuf += startIndexDest;
				memcpy(destBuf, srcBuf, numBytes);
			}


		private:
			NetPort() {};
	};
}

#endif // w32_net_basics_h__
