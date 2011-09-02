#ifndef Character_h__
#define Character_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "db_fieldtypes/SystemStatus.h"
//#include "CharacterClass.h"
#include "components/HighscoreEntry.h"
#include "components/Direction.h"
#include "User.h"
#include "components/Inventory.h"
#include <string>
#include <vector>

namespace fwworld {

class Character : public DBSynchObject {

	public:
		enum ActiveStatus {as_active, as_inactive, as_inactive_loadPlayfield};
		static unsigned int s_initialPlayfieldId;
		static unsigned int s_initialPlayfieldX;
		static unsigned int s_initialPlayfieldY;
		static unsigned int s_ItemPickupRadius;

	private:
		unsigned int userId;
		unsigned int classId;
		unsigned int clanId;
		unsigned int playfieldId;
		unsigned int graphicId;
		unsigned int graphicsX;
		unsigned int graphicsY;
		unsigned int graphicsDim;
		unsigned int x;
		unsigned int respawnX;
		unsigned int y;
		unsigned int respawnY;
		unsigned int level;
		unsigned int levelPoints;
		unsigned int experience;
		unsigned int gold;
		fwutil::DateTime createdDate;

		std::string name;

		unsigned int healthBase;
		unsigned int healthEffectsExtra;
		unsigned int healthCurrent;
		unsigned int manaBase;
		unsigned int manaEffectsExtra;
		unsigned int manaCurrent;
		unsigned int attackBase;
		unsigned int attackEffectsExtra;
		unsigned int defenseBase;
		unsigned int defenseEffectsExtra;
		unsigned int damageBase;
		unsigned int damageEffectsExtra;
		unsigned int skillBase;
		unsigned int skillEffectsExtra;
		unsigned int magicBase;
		unsigned int magicEffectsExtra;
		unsigned int healthregenerateBase;
		unsigned int healthregenerateEffectsExtra;
		unsigned int manaregenerateBase;
		unsigned int manaregenerateEffectsExtra;

		/** The character class also contains the attribute modifiers. */
		CharacterClass* characterClass;

		std::string customStatusMsg;
		
		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;

		// non-db members
		ActiveStatus activeStatus;
		User* owningUser;
		Playfield* playfield;
		Direction direction;
		bool dead;
		clock_ms_t lastVitalityRefill;
		Inventory inventory;

	protected:

	public:
		
		Character(FWWorld& world);
		virtual ~Character();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();
		
		void removeFromWorld();

		bool loadFromResultRow(const mysqlpp::Row& row, bool addCharacterClass);
		bool storeWithDependencies();
		bool removeWithDependencies();

		void loadInventory();

		inline User* getUser() const {return owningUser;}
		inline void setUser(User* newUser) {owningUser = newUser;}

		void setCharacterClass(CharacterClass* newClass, bool setBaseValues = true);

		inline unsigned int getMaxHealth() const {return healthBase + healthEffectsExtra;}
		inline unsigned int getMaxMana() const {return manaBase + manaEffectsExtra;}
		inline unsigned int getMaxAttack() const {return attackBase + attackEffectsExtra;}
		inline unsigned int getMaxDefense() const {return defenseBase + defenseEffectsExtra;}
		inline unsigned int getMaxDamage() const {return damageBase + damageEffectsExtra;}
		inline unsigned int getMaxSkill() const {return skillBase + skillEffectsExtra;}
		inline unsigned int getMaxMagic() const {return magicBase + magicEffectsExtra;}
		inline unsigned int getMaxHealthregenerate() const {return healthregenerateBase + healthregenerateEffectsExtra;}
		inline unsigned int getMaxManaregenerate() const {return manaregenerateBase + manaregenerateEffectsExtra;}

		inline Direction getDirection() const {return direction;}
		inline void setDirection(Direction newDirection) {direction = newDirection;}

		inline bool isDead() const {return dead;}
		inline void setDead(bool val) {dead = val;}

		inline Playfield* getPlayfield() const {return playfield;}
		inline void setPlayfield(Playfield* newPf) {playfield = newPf;}

		inline Inventory& getInventory() {return inventory;}

		inline ActiveStatus getActiveStatus() const {return activeStatus;}
		inline void setActiveStatus(ActiveStatus newStatus) {activeStatus = newStatus;}

		inline unsigned int getXCenter() {
			return x + (graphicsDim>>1);
		}

		inline unsigned int getYCenter() {
			return y + (graphicsDim>>1);
		}

		inline clock_ms_t const getLastVitalityRefill() {return lastVitalityRefill;}
		inline void setLastVitalityRefill(clock_ms_t val) {lastVitalityRefill = val;}

		static unsigned int getIdForNameOfUserCharacter(FWWorld& world, const std::string& newName);
		static bool validName(const std::string& newName);

		static SPCharacter createNewCharacterForClass(FWWorld& world, unsigned int classId, unsigned int userId, const std::string& name, bool store);

		static bool getHighscores(FWWorld& world, unsigned int startRank, unsigned int numRanks, std::vector<HighscoreEntry>& hsList);

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, userId, UserId, characters, user_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, classId, ClassId, characters, class_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, clanId, ClanId, characters, clan_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, playfieldId, PlayfieldId, characters, playfield_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicId, GraphicId, characters, graphic_id, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsX, GraphicsX, characters, graphics_x, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsY, GraphicsY, characters, graphics_y, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsDim, GraphicsDim, characters, graphics_dim, )
		
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, x, X, characters, x, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int,respawnX,RespawnX,characters,respawn_x,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, y, Y, characters, Y, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int,respawnY,RespawnY,characters,respawn_y,)

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, level, Level, characters, level, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, levelPoints, LevelPoints, characters, level_points, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, experience, Experience, characters, experience, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, gold, Gold, characters, gold, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, createdDate, CreatedDate, characters, created_date, .strSQL())		

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, name, Name, characters, name, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthBase, HealthBase, characters, health_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthEffectsExtra, HealthEffectsExtra, characters, health_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthCurrent, HealthCurrent, characters, health_current, )
		
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaBase, ManaBase, characters, mana_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaEffectsExtra, ManaEffectsExtra, characters, mana_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaCurrent, ManaCurrent, characters, mana_current, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, attackBase, AttackBase, characters, attack_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, attackEffectsExtra, AttackEffectsExtra, characters, attack_effects_extra, )
		
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, defenseBase, DefenseBase, characters, defense_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, defenseEffectsExtra, DefenseEffectsExtra, characters, defense_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, damageBase, DamageBase, characters, damage_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, damageEffectsExtra, DamageEffectsExtra, characters, damage_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, skillBase, SkillBase, characters, skill_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, skillEffectsExtra, SkillEffectsExtra, characters, skill_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, magicBase, MagicBase, characters, magic_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, magicEffectsExtra, MagicEffectsExtra, characters, magic_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthregenerateBase, HealthregenerateBase, characters, healthregenerate_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthregenerateEffectsExtra, HealthregenerateEffectsExtra, characters, healthregenerate_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaregenerateBase, ManaregenerateBase, characters, manaregenerate_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaregenerateEffectsExtra, ManaregenerateEffectsExtra, characters, manaregenerate_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, customStatusMsg, CustomStatusMsg, characters, custom_status_msg, )
		
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, characters, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, characters, wb_last_changed, .strSQL())		


};

} // end namespace


#endif // Character_h__