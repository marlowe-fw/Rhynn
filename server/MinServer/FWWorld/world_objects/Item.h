#ifndef Item_h__
#define Item_h__

#include "DBSynchObject.h"
#include "DBSynchObjectMacros.h"
#include "WorldObjectTypeDefs.h"
#include "DBHelper.h"
#include "DateTime.h"
#include "GenericClock.h"
#include "db_fieldtypes/ItemUsageType.h"
#include "db_fieldtypes/ItemEquippedStatus.h"
#include "db_fieldtypes/YesNoEnum.h"
#include "db_fieldtypes/AvailableStatus.h"

namespace fwworld {

	class FWWorld;

	class Item : public DBSynchObject {

	public:
		static unsigned int s_defaultCleanupSeconds;
		static const unsigned int c_defaultWidth = 15;
        static const unsigned int c_defaultHeight = 15;


		enum ScheduleType {st_unknown, st_respawn, st_cleanup};
		enum AttributeType {at_health, at_mana, at_attack, at_defense, at_damage, at_skill, at_magic, at_healthregenerate, at_manaregenerate};

		Item(FWWorld& world);
		virtual ~Item();

		virtual bool storeToDB();
		virtual bool loadFromDB(unsigned int existingId);
		virtual bool removeFromDB();

		void removeFromWorld();
		Item* instanciate();

		bool loadFromResultRow(const mysqlpp::Row& row);


		inline void setScheduledTime(clock_ms_t newVal) {
			scheduledTime = newVal;
		}

		inline clock_ms_t getScheduledTime() {
			return scheduledTime;
		}

		inline void setScheduleType(ScheduleType newType) {
			scheduleType = newType;
		}

		inline ScheduleType getScheduleType() {
			return scheduleType;

		}

		inline unsigned int getXCenter() {
			return x + (7);
		}

		inline unsigned int getYCenter() {
			return y + (7);
		}

		inline ItemUsageType getUsageType() const {
			return usageType;
		}

		static void getAllForInventory(FWWorld& world, unsigned int characterId, std::vector<SPItem>& inventoryItems);

		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, templateId,TemplateId,items,template_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const int, clientTypeId,ClientTypeId,items,client_type_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, ownerId,OwnerId,items,owner_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, setId,SetId,items,set_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicId,GraphicId,items,graphic_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, playfieldId,PlayfieldId,items,playfield_id,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, x,X,items,x,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, y,Y,items,y,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsX,GraphicsX,items,graphics_x,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, graphicsY,GraphicsY,items,graphics_y,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string, name,Name,items,name,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const std::string, description,Description,items,description,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const AvailableStatus, availableStatus,AvailableStatus,items,available_status,.str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const YesNoEnum, canSell,CanSell,items,can_sell,.str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const YesNoEnum, canDrop,CanDrop,items,can_drop,.str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, units,Units,items,units,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, unitsSell,UnitsSell,items,units_sell,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, price,Price,items,price,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const YesNoEnum, respawn,Respawn,items,respawn,.str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const int, respawnDelay,RespawnDelay,items,respawn_delay,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const ItemEquippedStatus, equippedStatus,EquippedStatus,items,equipped_status,.str())
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthEffect,HealthEffect,items,health_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaEffect,ManaEffect,items,mana_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, attackEffect,AttackEffect,items,attack_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, defenseEffect,DefenseEffect,items,defense_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, damageEffect,DamageEffect,items,damage_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, skillEffect,SkillEffect,items,skill_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, magicEffect,MagicEffect,items,magic_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, healthregenerateEffect,HealthregenerateEffect,items,healthregenerate_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, manaregenerateEffect,ManaregenerateEffect,items,manaregenerate_effect,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, actionEffect1,ActionEffect1,items,action_effect_1,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, actionEffect2,ActionEffect2,items,action_effect_2,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const int, effectDuration,EffectDuration,items,effect_duration,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, requiredSkill,RequiredSkill,items,required_skill,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, requiredMagic,RequiredMagic,items,required_magic,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, frequency,Frequency,items,frequency,)
		DBSYNCHOBJECT_DECL_ATTR_FUNCTIONS( const unsigned int, range,Range,items,range,)


	private:

		unsigned int templateId;
		unsigned int categoryId;
		unsigned int clientTypeId;
		unsigned int ownerId;
		unsigned int setId;
		unsigned int graphicId;
		unsigned int playfieldId;
		unsigned int x;
		unsigned int y;
		unsigned int graphicsX;
		unsigned int graphicsY;
		std::string name;
		std::string description;
		AvailableStatus availableStatus;
		YesNoEnum canSell;
		YesNoEnum canDrop;
		unsigned int units;
		unsigned int unitsSell;
		unsigned int price;
		YesNoEnum respawn;
		int respawnDelay;
		ItemEquippedStatus equippedStatus;
		unsigned int healthEffect;
		unsigned int manaEffect;
		unsigned int attackEffect;
		unsigned int defenseEffect;
		unsigned int damageEffect;
		unsigned int skillEffect;
		unsigned int magicEffect;
		unsigned int healthregenerateEffect;
		unsigned int manaregenerateEffect;
		unsigned int actionEffect1;
		unsigned int actionEffect2;
		unsigned int effectDuration;
		unsigned int requiredSkill;
		unsigned int requiredMagic;
		unsigned int frequency;
		unsigned int range;

		// joined members from other tables
		ItemUsageType usageType;

		// non-db members
		clock_ms_t scheduledTime;
		ScheduleType scheduleType;
	};

}

#endif // Item_h__
