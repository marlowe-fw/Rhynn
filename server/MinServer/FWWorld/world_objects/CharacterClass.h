#ifndef CharacterClass_h__
#define CharacterClass_h__


#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "db_fieldtypes/AvailableStatus.h"
#include "db_fieldtypes/SystemStatus.h"
#include <string>
#include <vector>
#include <map>

namespace fwworld {

class FWWorld;

class CharacterClass : public DBSynchObject {

	private:
		unsigned int graphicId;
		AvailableStatus availableStatus;
		unsigned int graphicsX;
		unsigned int graphicsY;
		unsigned int graphicsDim;

		std::string systemName;
		std::string displayName;

		unsigned int healthBase;
		unsigned int healthModifier;
		unsigned int manaBase;
		unsigned int manaModifier;
		unsigned int attackBase;
		unsigned int attackModifier;
		unsigned int defenseBase;
		unsigned int defenseModifier;
		unsigned int damageBase;
		unsigned int damageModifier;
		unsigned int skillBase;
		unsigned int skillModifier;
		unsigned int magicBase;
		unsigned int magicModifier;
		unsigned int healthregenerateBase;
		unsigned int healthregenerateModifier;
		unsigned int manaregenerateBase;
		unsigned int manaregenerateModifier;

		SystemStatus systemStatus;
		fwutil::DateTime wbLastChanged;

	protected:

	public:
		CharacterClass(FWWorld& world);
		virtual ~CharacterClass();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicId, GraphicId, character_classess, graphic_id, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const AvailableStatus&, availableStatus, AvailableStatus, character_classes, available_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsX, GraphicsX, character_classess, graphics_x, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsY, GraphicsY, character_classess, graphics_y, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsDim, GraphicsDim, character_classess, graphics_dim, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, systemName, SystemName, character_classess, system_name, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string&, displayName, DisplayName, character_classess, display_name, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthBase, HealthBase, character_classess, health_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthModifier, HealthModifier, character_classess, health_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaBase, ManaBase, character_classess, mana_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaModifier, ManaModifier, character_classess, mana_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, attackBase, AttackBase, character_classess, attack_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, attackModifier, AttackModifier, character_classess, attack_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, defenseBase, DefenseBase, character_classess, defense_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, defenseModifier, DefenseModifier, character_classess, defense_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, damageBase, DamageBase, character_classess, damage_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, damageModifier, DamageModifier, character_classess, damage_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, skillBase, SkillBase, character_classess, skill_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, skillModifier, SkillModifier, character_classess, skill_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, magicBase, MagicBase, character_classess, magic_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, magicModifier, MagicModifier, character_classess, magic_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthregenerateBase, HealthregenerateBase, character_classess, healthregenerate_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthregenerateModifier, HealthregenerateModifier, character_classess, healthregenerate_effects_extra, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaregenerateBase, ManaregenerateBase, character_classess, manaregenerate_base, )
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaregenerateModifier, ManaregenerateModifier, character_classess, manaregenerate_effects_extra, )

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const SystemStatus&, systemStatus, SystemStatus, character_classes, system_status, .str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const fwutil::DateTime&, wbLastChanged, WbLastChanged, character_classes, wb_last_changed, .strSQL())

		bool loadFromResultRow(const mysqlpp::Row& row);

		static void getAll(FWWorld& world, mysqlpp::StoreQueryResult& res, const std::string& order = "id asc");
		static void getAll(FWWorld& world, std::vector<SPCharacterClass>& containerAll, const std::string& order = "id asc");
		static void getAll(FWWorld& world, std::map<unsigned int, SPCharacterClass>& containerAll, const std::string& order = "id asc");

	};

} // end namespace


#endif // CharacterClass_h__
