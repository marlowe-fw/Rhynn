#ifndef DateTime_h__
#define DateTime_h__

#include "CommonUtils.h"
#include <time.h>

namespace fwutil {

class DateTime {
	private:
		int year;
		int month;
		int day;
		int hour;
		int minute;
		int second;

		/**
		 * Structure holding the current date and time internally.
		 */
		struct tm timeinfo;


	public:

		/** 
		 * Constructs a new DateTime object containing the current local system date and time. 
		 * @param useCurrent if set to true will initialize the object with the current date and time, 
		 * otherwise sets all members to 0, defaults to false
		 */
		DateTime(bool useCurrent = false) 
		: year(0), month(0), day(0), hour(0), minute(0), second(0)
		{
			if (useCurrent) {
				setCurrent();
			}
		}

		/** Construct a DateTime object by using the provided values, no error checking is done. */
		DateTime(int newYear, int newMonth, int newDay, int newHour, int newMinute, int newSecond) 
		: year(newYear), month(newMonth), day(newDay), hour(newHour), minute(newMinute), second(newSecond)
		{}
		
		/*
		// Construct a DateTime object from an SQL datetime string.
		DateTime(const std::string& sqlDateString) {
			setFromSQL(sqlDateString);
		}*/

		/** Set the  date and time from an SQL datetime string. */
		bool setFromSQL(const std::string& sqlDateString) {
			year = month = day = hour = minute = second = 0;
			if (sqlDateString.length() != 19) {
				// not valid
				return false;
			}

			int sqlYear = Common::stringToInt(sqlDateString.substr(0, 4));
			int sqlMonth = Common::stringToInt(sqlDateString.substr(5, 2));
			int sqlDay = Common::stringToInt(sqlDateString.substr(8, 2));
			int sqlHour = Common::stringToInt(sqlDateString.substr(11, 2));
			int sqlMinute = Common::stringToInt(sqlDateString.substr(14, 2));
			int sqlSecond = Common::stringToInt(sqlDateString.substr(17, 2));

			// just a very rough validity check, 0 values are allowed (we may find 0000-00-00 00:00:00 in SQL)
			if (sqlYear >= 0 &&
				sqlMonth >= 0 && sqlMonth <= 12 && 
				sqlDay >= 0 && sqlDay <= 31 && 
				sqlHour >= 0 && sqlHour <= 23 && 
				sqlMinute >= 0 && sqlMinute <= 59 && 
				sqlSecond >= 0 && sqlSecond <= 59)
			{
				// assign
				year = sqlYear;
				month = sqlMonth;
				day = sqlDay;
				hour = sqlHour;
				minute = sqlMinute;
				second = sqlSecond;
				return true;
			}

			return false;
		}


		/** Year of this date. */
		inline int getYear() {return year;}
		/** Month of the year of this date. */
		inline int getMonth() {return month;}
		/** Day of the month of this date. */
		inline int getDay() {return day;}
		/** Hour of the day of this date. */
		inline int getHour() {return hour;}
		/** Minute of the hour of this date. */
		inline int getMinute() {return minute;}
		/** Second of the minute of this date. */
		inline int getSecond() {return day;}

		/** Fill the date and time with the current date and time. */
		void setCurrent() {
			time_t rawtime;
			time( &rawtime );
			#if defined(_WIN32)
				 localtime_s(&timeinfo,&rawtime);
			#else
				timeinfo = *(localtime(&rawtime));
			#endif
			if (timeinfo.tm_sec > 59) {timeinfo.tm_sec = 59;}	// take into account leap seconds - this second will just last twice as long

			//std::cout << "current date / time: " << timeinfo.tm_year << "-" << timeinfo.tm_mon << "-" << timeinfo.tm_mday << " " << timeinfo.tm_hour << ":" << timeinfo.tm_min << ":" << timeinfo.tm_sec << std::endl;
			year = timeinfo.tm_year + 1900;
			month = timeinfo.tm_mon + 1; // starts with January == 0
			day = timeinfo.tm_mday;
			hour = timeinfo.tm_hour;
			minute = timeinfo.tm_min;
			second = timeinfo.tm_sec;
		}


		/**
		 * Convert to string (in SQL format). 
		 * @return a string in the SQL notation
		 */
		operator std::string() const {
			return strSQL();
		}

		std::string strLog() const {
			std::stringstream SQLDateTime; 

			SQLDateTime << Common::intToString(year%100);


			// month, starts at 0
			if (month < 10) {
				SQLDateTime <<  "0" << month; 
			} else {
				SQLDateTime  << month;
			}

			// day of month
			if (day < 10) {
				SQLDateTime <<  "0" << day;
			} else {
				SQLDateTime <<  "" << day;
			}

			// hour of the day
			if (hour < 10) {
				SQLDateTime <<  " 0" << hour;
			} else {
				SQLDateTime <<  " " << hour;
			}

			// minute of the hour
			if (minute < 10) {
				SQLDateTime <<  ":0" << minute;
			} else {
				SQLDateTime <<  ":" << minute;
			}

			if (second < 10) {
				SQLDateTime <<  ":0" << second;
			} else {
				SQLDateTime <<  ":" << second;
			}

			//std::cout << SQLDateTime.str() << std::endl;
			return SQLDateTime.str();

		}

		/** 
		 * Convert the current date and time to a valid SQL datetime string.
		 * @return the sql datetime string
		 */
		std::string strSQL() const {
			std::stringstream SQLDateTime; 

			if (year < 1000) {
				std::string sYear = Common::intToString(year);
				Common::padString(sYear, 4, '0');
				SQLDateTime << sYear;
			} else {
				SQLDateTime << year;
			}

			// month, starts at 0
			if (month < 10) {
				SQLDateTime <<  "-0" << month; 
			} else {
				SQLDateTime <<  "-" << month;
			}

			// day of month
			if (day < 10) {
				SQLDateTime <<  "-0" << day;
			} else {
				SQLDateTime <<  "-" << day;
			}

			// hour of the day
			if (hour < 10) {
				SQLDateTime <<  " 0" << hour;
			} else {
				SQLDateTime <<  " " << hour;
			}

			// minute of the hour
			if (minute < 10) {
				SQLDateTime <<  ":0" << minute;
			} else {
				SQLDateTime <<  ":" << minute;
			}

			if (second < 10) {
				SQLDateTime <<  ":0" << second;
			} else {
				SQLDateTime <<  ":" << second;
			}

			//std::cout << SQLDateTime.str() << std::endl;
			return SQLDateTime.str();
			
		}

		/**
		 * Compare two dates for equality.
		 * @return true if equal
		 */
		inline bool operator==(const DateTime&) const {
			return 
				year == year &&
				month == month &&
				day == day &&
				hour == hour &&
				minute == minute;
		}

		/**
		 * Compare if this date is lower than or equal to another date.
		 * @return true if lower or equal
		 */
		inline bool operator<=(const DateTime& other) const {
			// if we come here, the dates are equal
			return *this < other || *this == other;
		}

		/**
		 * Compare if this date is greater than or equal to another date.
		 * @return true if greater or equal
		 */
		inline bool operator>=(const DateTime& other) const {
			// if we come here, the dates are equal
			return *this > other || *this == other;
		}

		/**
		 * Compare if this date is lower than another date.
		 * @return true if lower
		 */
		inline bool operator<(const DateTime& other) const {
			if (year < other.year) {return true;}
			else if (year > other.year) {return false;}
			else {
				if (month < other.month) {return true;}
				else if (month > other.month) {return false;}
				else {
					if (day < other.day) {return true;}
					else if (day > other.day) {return false;}
					else {
						if (hour < other.hour) {return true;}
						else if (hour > other.hour) {return false;}
						else {
							if (minute < other.minute) {return true;}
							else if (minute > other.minute) {return false;}
							else {
								if (second < other.second) {return true;}
								else if (second > other.second) {return false;}
							}
						}
					}
				}
			}
			// if we come here, the dates are equal
			return false;
		}

		/**
		 * Compare if this date is greater than another date.
		 * @return true if greater
		 */
		inline bool operator>(const DateTime& other) const {
			if (year > other.year) {return true;}
			else if (year < other.year) {return false;}
			else {
				if (month > other.month) {return true;}
				else if (month < other.month) {return false;}
				else {
					if (day > other.day) {return true;}
					else if (day < other.day) {return false;}
					else {
						if (hour > other.hour) {return true;}
						else if (hour < other.hour) {return false;}
						else {
							if (minute > other.minute) {return true;}
							else if (minute < other.minute) {return false;}
							else {
								if (second > other.second) {return true;}
								else if (second < other.second) {return false;}
							}
						}
					}
				}
			}
			// if we come here, the dates are equal
			return false;
		}


};

/** Allow to be embedded in ostream. */
inline std::ostream& operator << (std::ostream& os, const DateTime& dateTime) {
	return os << static_cast<std::string>(dateTime);
}


}// end namespace
#endif // DateTime_h__