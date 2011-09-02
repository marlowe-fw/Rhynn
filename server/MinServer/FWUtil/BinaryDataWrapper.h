#ifndef BinaryDataWrapper_h__
#define BinaryDataWrapper_h__

#include <iostream>
#include <string>
#include "mem_dbg.h"

namespace fwutil {

class BinaryDataWrapper {
	private:
		unsigned int size;
		unsigned char* data;

		inline void freeData() {
			if (data!=0) {
				delete[] data;
			}
			data = 0;
			size = 0;
		}

	public:

		BinaryDataWrapper() : size(0), data(0) {}

		BinaryDataWrapper(unsigned int initialSize) :size(initialSize), data(_TRACK_NEW(new unsigned char[initialSize])) {
			memset(data, 0, size);
		}

		BinaryDataWrapper(const unsigned char* buffer, unsigned int copyLength, unsigned int copyOffset) : size(0), data(0) {
			//data = new unsigned char[size];
			//memcpy(data, buffer+copyOffset, size);
			fromBuffer(buffer, copyLength, copyOffset);
		}

		virtual ~BinaryDataWrapper() {
			freeData();
		}

		BinaryDataWrapper(const BinaryDataWrapper& other) {
			// uses the assignment operator for actual copying
			size = other.size;
			if (size > 0) {
				data = _TRACK_NEW(new unsigned char[size]);
				memcpy(data, other.data, size);
			} else {
				data = 0;
			}
		}


		inline const BinaryDataWrapper& operator=(const BinaryDataWrapper& other) {
			if (&other != this) {
				freeData();
				size = other.size;
				if (size > 0) {
					data = _TRACK_NEW(new unsigned char[size]);
					memcpy(data, other.data, size);
				}
			}
			return *this;
		}


		inline const BinaryDataWrapper& operator=(const std::string& str) {
			fromBuffer((unsigned char*)str.c_str(), (unsigned int)str.length());
			return *this;
		}

		operator std::string() const {
			if (size > 0) {
				return std::string((const char*)data, size);
			} else {
				return "";
			}
		}

		inline void resetNew(unsigned int newSize) {
			freeData();
			size = newSize;
			if (size > 0) {
				data = _TRACK_NEW(new unsigned char[size]);
				memset(data, 0, size);
			}
		}

		inline unsigned char* getData() const {return data;}

		/**
		 * Read write access using array subscript.
		 * @param index The index to access in the data array.
		 * @return Reference to the element at index
		 */
		inline unsigned char& operator[](const int index) const {
			if (static_cast<unsigned int>(index) < size) {
				return data[index];
			}
			return data[size-1];
			// to handle properly, would need throw an exception at this point (or use assert)
		}

		inline unsigned int getSize() const {return size;}

		inline void copyToBuffer(unsigned char* buffer, unsigned int offset) const {
			if (data != 0 && size !=0) {
				memcpy(buffer + offset, data, size);
			}
		}

		inline void fromBuffer(const unsigned char* buffer, unsigned int length, unsigned int copyOffset=0) {
			freeData();
			if (length > 0) {
				size = length;
				data = _TRACK_NEW(new unsigned char[size]);
				memcpy(data, buffer+copyOffset, size);
			}
		}


		/** Helper for explicit to string conversion, note that this calls the defined string cast operator of BinaryDataWrapper. */
		inline std::string str() {return static_cast<std::string>(*this);}

		inline std::string strSQL() {return static_cast<std::string>(*this);}



};

/** Allow to be embedded in ostream. */
inline std::ostream& operator <<(std::ostream& os, const BinaryDataWrapper& ps) {
	return os << static_cast<std::string>(ps);
}

} // end namespace fws



#endif // BinaryDataWrapper_h__
