#ifndef DBSynchObject_h__
#define DBSynchObject_h__

#include "mem_dbg.h"

//#include "../FWServer.h"
#include "WorldObject.h"
#include "GenericClock.h"
#include "DBHelper.h"
#include "mysql++.h"



namespace fwworld {

class FWWorld;

/**
 * Abstract base class for all objects which should be synchronized with the db at some point.
 */
class DBSynchObject : public WorldObject {

	private:

	protected:
		/** Database Id. */
		unsigned int id;

		/** Flag to indicate whether or not this object is synchronized with the db, or not. */
		bool synchronized;

		/** The last time this object was in a synchronized state with the database. */
		clock_ms_t lastSynchronizedTime;


		/**
		 * Flag this object to indicate that this object needs synchronization with the DB
		 * (needs to use storeToDB, which calls the class specific doStorToDB).
		 * If the object was in a synchronized state with the db up to this point, the current time
		 * will be stored as the last time this object has been in a synchronized state with the DB.
		 */
		inline void setNeedsSynchronization() {
			if (synchronized) {
				lastSynchronizedTime = world.clock.getTimestampMS();
			}
			synchronized = false;
		}

		/**
		 * Use the given query string to store the object.
		 * @param query The sql to store the object
		 * @param msg The message containing a hint for possible error messages (e.g. "Store User")
		 * @return true on success, false otherwise
		 */
		bool storeByQuery(mysqlpp::Query& query, const std::string& msg) {
			if (fwutil::DBHelper::exec(query, msg)) {
				if (id == 0) {
					id = static_cast<unsigned int>(query.insert_id());
				}
				onSuccessfulStore();
				return true;
			}
			return false;
		}

		/**
		* Use the given query string to remove the object.
		* @param query The sql for removal the object
		* @param msg The message containing a hint for possible error messages (e.g. "Remove User")
		* @return true on success, false otherwise
		*/
		bool removeByQuery(mysqlpp::Query& query, const std::string& msg) {
			if (fwutil::DBHelper::exec(query, msg)) {
				onSuccessfulRemove();
				return true;
			}
			return false;
		}

	public:

		/**
		 * Create a new object which can be synchronized with the DB,
		 * the id will be set to zero, indicating that this object is not loaded.
		 */
		DBSynchObject(FWWorld& newWorld) :
			WorldObject(newWorld),
			//world(newWorld),
			id(0), synchronized(true), lastSynchronizedTime(0) {}


		/**
		 * Destroy the given object.
		 */
		virtual ~DBSynchObject() {}

		/**
		 * Retrieve the object's database id.
		 * @return the id
		 */
		inline unsigned int getId() const  {return id;}

		/**
		 * Check whether this object was successfully loaded from the database (in which case the id is non-zero).
		 * @return true if loaded, false otherwise
		 */
		inline unsigned int isLoaded() const {return id > 0;}


		/**
		 * Check if this object is synchronized with the DB.
		 * @return true if it is synchronized, false otherwise
		 */
		inline bool isSynchronized() const {return synchronized;}

		/**
		 * Get the time this object last been in a synchronized state with the DB.
		 * This allows to store objects which need to be synchronized only in certain intervals,
		 * which makes possible that several changes may happen to the object before storing for the next time.
		 * @return The last time this object was in a synchronized state with the DB
		 */
		inline clock_ms_t getlastSynchronizedTime() const {return lastSynchronizedTime;}


		/**
		* Class dependent store of the object to the database. If the id of the object is 0, this should result in a
		* new record and the newly inserted id should be set in the object.
		* Implement in subclasses.
		* @return true on success, false otherwise
		*/
		virtual bool storeToDB() = 0;

		/**
		* Class dependent load from the db, populate the object by reading it from the database.
		* Implement in subclasses.
		* @param existingId The id of the record to load the object from
		* @return true on success, false otherwise
		*/
		virtual bool loadFromDB(unsigned int existingId) = 0;

		/**
		* Class dependent remove of the object from the database.
		* Implement in subclasses.
		* @return true on success, false otherwise
		*/
		virtual bool removeFromDB() = 0;


		/**
		 * Store the object to the DB if it is not synchronized with it.
		 */
		inline bool storeToDBIfNotSynchronized() {
			if (!isSynchronized()) {
				return storeToDB();
			}
			return true;
		}


		/**
		 * Callback function invoked when an object has been stored.
		 */
		inline void onSuccessfulStore() {
			synchronized = true;
			lastSynchronizedTime = world.clock.getTimestampMS();
		}



		/**
		 * Callback function invoked when an object has been loaded, subclasses are responsible for calling this.
		 */
		inline void onSuccessfulLoad() {
			synchronized = true;
			lastSynchronizedTime = world.clock.getTimestampMS();
			// todo: load all associated event handlers
		}


		/**
		 * Callback function invoked when an object has been removed.
		 */
		inline void onSuccessfulRemove() {
			// a removed object will not need synchronizing afterwards as it is not in the DB anymore
			synchronized = true;
			lastSynchronizedTime = world.clock.getTimestampMS();
		}

		/*
		template <class TValPtr, class SPtr>
		static void putToSPMap(TValPtr val, std::map<unsigned int, SPtr> m) {
			m.insert(std::pair<unsigned int, SPtr>(val->getId(), SPtr(val)));
		}

		template <class TValPtr, class SPtr>
		static void putToSPVector(TValPtr val, std::vector<SPtr> m) {
			m.push_back(SPtr(val));
		}
		*/

};


}

#endif // DBSynchObject_h__
