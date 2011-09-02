-- MySQL dump 10.13  Distrib 5.5.15, for Win32 (x86)
--
-- Host: localhost    Database: fw_core
-- ------------------------------------------------------
-- Server version	5.5.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_recovery`
--

DROP TABLE IF EXISTS `account_recovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_recovery` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `email` varchar(255) NOT NULL DEFAULT '',
  `reset_code` varchar(10) NOT NULL DEFAULT '',
  `created_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `sent` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` enum('pending','sent','used','discarded') NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `reset_code` (`reset_code`),
  KEY `status` (`status`),
  KEY `created` (`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_recovery`
--

LOCK TABLES `account_recovery` WRITE;
/*!40000 ALTER TABLE `account_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_classes`
--

DROP TABLE IF EXISTS `character_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_classes` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `graphic_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'default image to use',
  `available_status` enum('none','all','bots_only','premium_only','testing_only') NOT NULL DEFAULT 'none' COMMENT 'restricts the use of the character class',
  `graphics_x` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'default start x position in the image to locate the frames',
  `graphics_y` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'default start y position in the image to locate the frames',
  `graphics_dim` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'default size in pixels',
  `system_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'name used in admin tools etc.',
  `display_name` varchar(16) NOT NULL DEFAULT '' COMMENT 'name displayed to end users',
  `health_base` int(10) unsigned NOT NULL DEFAULT '0',
  `health_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_base` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `attack_base` int(10) unsigned NOT NULL DEFAULT '0',
  `attack_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `defense_base` int(10) unsigned NOT NULL DEFAULT '0',
  `defense_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_base` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_base` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `magic_base` int(10) unsigned NOT NULL DEFAULT '0',
  `magic_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `healthregenerate_base` int(10) unsigned NOT NULL DEFAULT '0',
  `healthregenerate_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `manaregenerate_base` int(10) unsigned NOT NULL DEFAULT '0',
  `manaregenerate_modifier` int(10) unsigned NOT NULL DEFAULT '0',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`),
  KEY `available_status` (`available_status`),
  KEY `graphics_id` (`graphic_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Basic character classes defining initial character values';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_classes`
--

LOCK TABLES `character_classes` WRITE;
/*!40000 ALTER TABLE `character_classes` DISABLE KEYS */;
INSERT INTO `character_classes` VALUES (100001,100005,'all',0,0,20,'Player: Human','Human',200,10,10,10,200,10,200,10,200,10,200,10,200,10,10,1,10,10,'0000-00-00 00:00:00','normal'),(100002,100005,'all',0,20,20,'Player: Elf','Elf',200,10,10,10,200,10,200,10,200,10,200,10,200,10,10,1,10,10,'0000-00-00 00:00:00','normal'),(100098,100005,'all',0,40,20,'Player: Dwarf','Dwarf',200,10,10,10,200,10,200,10,200,10,200,10,200,10,10,1,10,10,'0000-00-00 00:00:00','normal'),(100099,100005,'all',0,60,20,'Player: Orc','Orc',200,10,10,10,200,10,200,10,200,10,200,10,200,10,10,1,10,10,'0000-00-00 00:00:00','normal'),(100100,100005,'all',0,80,20,'Player: Wizard','Wizard',200,10,10,10,200,10,200,10,200,10,200,10,200,10,10,1,10,10,'0000-00-00 00:00:00','normal');
/*!40000 ALTER TABLE `character_classes` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_character_classes` BEFORE INSERT ON `character_classes` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(8,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_character_classes` AFTER DELETE ON `character_classes` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `characters`
--

DROP TABLE IF EXISTS `characters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `characters` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'this is 0 for characters operated by bots',
  `class_id` int(10) unsigned NOT NULL DEFAULT '0',
  `clan_id` int(10) unsigned NOT NULL DEFAULT '0',
  `playfield_id` int(10) unsigned NOT NULL DEFAULT '0',
  `graphic_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'which image to use for display',
  `name` varchar(12) NOT NULL DEFAULT '',
  `created_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `graphics_x` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'x position in the character image where the individual frames of the character are starting',
  `graphics_y` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'y position in the character image where the individual frames of the character are starting',
  `graphics_dim` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'size of the character in pixels - assuming square size w = h = dim pixels',
  `x` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_x` int(10) unsigned NOT NULL DEFAULT '0',
  `y` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_y` int(10) unsigned NOT NULL DEFAULT '0',
  `level` int(10) unsigned NOT NULL DEFAULT '0',
  `level_points` int(10) unsigned NOT NULL DEFAULT '0',
  `experience` int(10) unsigned NOT NULL DEFAULT '0',
  `gold` int(10) unsigned NOT NULL DEFAULT '0',
  `health_base` int(10) unsigned NOT NULL DEFAULT '0',
  `health_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `health_current` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_base` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_current` int(10) unsigned NOT NULL DEFAULT '0',
  `attack_base` int(10) unsigned NOT NULL DEFAULT '0',
  `attack_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `defense_base` int(10) unsigned NOT NULL DEFAULT '0',
  `defense_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_base` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_base` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `magic_base` int(10) unsigned NOT NULL DEFAULT '0',
  `magic_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `healthregenerate_base` int(10) unsigned NOT NULL DEFAULT '0',
  `healthregenerate_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `manaregenerate_base` int(10) unsigned NOT NULL DEFAULT '0',
  `manaregenerate_effects_extra` int(10) unsigned NOT NULL DEFAULT '0',
  `custom_status_msg` varchar(32) NOT NULL COMMENT 'a message describing the current status of the character for others to see (like ''I am currently away''), has nothing to do with the system_status field',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal' COMMENT 'the system status of this character, has nothing to do with the custom_status_msg',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `clan_id` (`clan_id`),
  KEY `name` (`name`),
  KEY `system_status` (`system_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Characters of users and bots';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `characters`
--

LOCK TABLES `characters` WRITE;
/*!40000 ALTER TABLE `characters` DISABLE KEYS */;
INSERT INTO `characters` VALUES (100131,100025,100098,0,100109,100005,'tor','2010-07-17 13:01:13',0,40,20,963,0,2142,0,0,0,0,0,200,0,200,10,0,10,200,0,200,0,200,0,200,0,200,0,10,0,10,0,'','normal','0000-00-00 00:00:00');
/*!40000 ALTER TABLE `characters` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_characters` BEFORE INSERT ON `characters` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(1,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_characters` AFTER DELETE ON `characters` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `default_items`
--

DROP TABLE IF EXISTS `default_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `default_items` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` int(10) unsigned NOT NULL DEFAULT '0',
  `template_item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `sort_order` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `default_items`
--

LOCK TABLES `default_items` WRITE;
/*!40000 ALTER TABLE `default_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `default_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gameservers`
--

DROP TABLE IF EXISTS `gameservers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gameservers` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `ip` varchar(24) NOT NULL DEFAULT '127.0.0.1',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gameservers`
--

LOCK TABLES `gameservers` WRITE;
/*!40000 ALTER TABLE `gameservers` DISABLE KEYS */;
INSERT INTO `gameservers` VALUES (100003,'1.4 server','127.0.0.1','0000-00-00 00:00:00','normal');
/*!40000 ALTER TABLE `gameservers` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_gameservers` BEFORE INSERT ON `gameservers` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(9,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_gameservers` AFTER DELETE ON `gameservers` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `graphics`
--

DROP TABLE IF EXISTS `graphics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphics` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `type` enum('generic','background','character','item') NOT NULL DEFAULT 'generic' COMMENT 'what kind of graphic',
  `filename` varchar(128) NOT NULL DEFAULT '',
  `load_for_world` enum('yes','no') NOT NULL DEFAULT 'yes' COMMENT 'whether or not to load this into memory of the game server',
  `description` text NOT NULL,
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`),
  KEY `type` (`type`),
  KEY `load_for_world` (`load_for_world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Used for management of graphics assets';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graphics`
--

LOCK TABLES `graphics` WRITE;
/*!40000 ALTER TABLE `graphics` DISABLE KEYS */;
INSERT INTO `graphics` VALUES (100005,'character','players01.png','yes','','0000-00-00 00:00:00','normal'),(100006,'item','items01.png','no','','0000-00-00 00:00:00','normal'),(100007,'item','items02.png','no','','0000-00-00 00:00:00','normal'),(100008,'background','back_grass_human_01.png','yes','','0000-00-00 00:00:00','normal'),(100009,'background','back_grass_dwarf_01.png','yes','','0000-00-00 00:00:00','normal'),(100010,'background','back_grass_elf_01.png','yes','','0000-00-00 00:00:00','normal'),(100011,'character','players01.png','yes','','0000-00-00 00:00:00','normal'),(100110,'background','back_plains_desert_harbour_01.png','yes','','0000-00-00 00:00:00','normal'),(100111,'background','back_plains_desert_harbour_02.png','yes','','0000-00-00 00:00:00','normal'),(100112,'background','back_desert_pyramids_01.png','yes','','0000-00-00 00:00:00','normal');
/*!40000 ALTER TABLE `graphics` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_graphics` BEFORE INSERT ON `graphics` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(10,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_graphics` AFTER DELETE ON `graphics` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `item_client_types`
--

DROP TABLE IF EXISTS `item_client_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_client_types` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `usage_type` enum('unknown','equip','use','gold') NOT NULL DEFAULT 'equip',
  `name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_client_types`
--

LOCK TABLES `item_client_types` WRITE;
/*!40000 ALTER TABLE `item_client_types` DISABLE KEYS */;
INSERT INTO `item_client_types` VALUES (1,'equip','weapon_1'),(2,'equip','shield_1'),(3,'equip','armor'),(4,'equip','helmet'),(5,'equip','boots'),(6,'equip','gloves'),(7,'equip','reserved_s1'),(8,'equip','reserved_s2'),(9,'equip','reserved_s3'),(10,'equip','reserved_s4'),(11,'equip','reserved_s5'),(12,'equip','reserved_s6'),(13,'equip','reserved_s7'),(14,'equip','reserved_s8'),(15,'equip','reserved_s9'),(16,'equip','reserved_s10');
/*!40000 ALTER TABLE `item_client_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `template_id` int(10) unsigned NOT NULL DEFAULT '0',
  `client_type_id` int(11) NOT NULL DEFAULT '1',
  `owner_id` int(10) unsigned NOT NULL DEFAULT '0',
  `set_id` int(10) unsigned NOT NULL DEFAULT '0',
  `graphic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `playfield_id` int(10) unsigned NOT NULL DEFAULT '0',
  `graphics_x` int(10) unsigned NOT NULL DEFAULT '0',
  `graphics_y` int(10) unsigned NOT NULL DEFAULT '0',
  `x` int(10) unsigned NOT NULL DEFAULT '0',
  `y` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(24) NOT NULL,
  `description` varchar(255) NOT NULL,
  `available_status` enum('none','all','bots_only','premium_only','testing_only') NOT NULL DEFAULT 'all',
  `can_sell` enum('yes','no') NOT NULL DEFAULT 'yes',
  `can_drop` enum('yes','no') NOT NULL DEFAULT 'yes',
  `units` int(10) unsigned NOT NULL DEFAULT '1',
  `units_sell` int(10) unsigned NOT NULL DEFAULT '0',
  `price` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn` enum('yes','no') NOT NULL DEFAULT 'no',
  `respawn_delay` int(11) NOT NULL DEFAULT '120',
  `equipped_status` enum('not_equipped','equipped','belt') NOT NULL DEFAULT 'not_equipped',
  `health_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `mana_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `attack_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `defense_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `magic_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `healthregenerate_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `manaregenerate_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `action_effect_1` int(10) unsigned NOT NULL DEFAULT '0',
  `action_effect_2` int(10) unsigned NOT NULL DEFAULT '0',
  `effect_duration` int(11) NOT NULL DEFAULT '0',
  `required_skill` int(10) unsigned NOT NULL DEFAULT '0',
  `required_magic` int(10) unsigned NOT NULL DEFAULT '0',
  `frequency` int(10) unsigned NOT NULL DEFAULT '0',
  `range` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (100030,0,1,0,0,100006,100019,0,0,10,10,'Incredible Mace','really incredible mace','all','yes','yes',1,0,0,'yes',10,'not_equipped',0,0,10,0,2,0,0,0,0,0,0,0,0,0,80,20),(100118,0,1,0,0,100006,100109,0,0,387,151,'Mace','Weak Mace','all','yes','yes',1,0,0,'yes',60,'not_equipped',0,0,10,0,2,0,0,0,0,0,0,0,0,0,80,10),(100119,0,1,0,0,100006,100109,15,0,1710,2095,'Sword','Plain Sword','all','yes','yes',1,0,0,'yes',60,'not_equipped',0,0,25,0,11,0,0,0,0,0,0,0,0,0,60,20),(100120,0,1,0,0,100006,100109,15,60,1613,796,'Axe','Simple Axe','all','yes','yes',1,0,0,'yes',60,'not_equipped',0,0,31,0,12,0,0,0,0,0,0,0,0,0,40,30),(100121,0,4,0,0,100007,100109,0,45,653,699,'Helmet','Bronze Helmet','all','yes','yes',1,0,0,'yes',120,'not_equipped',1,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0),(100122,0,2,0,0,100007,100109,30,0,1205,651,'Shield','Protector','all','yes','yes',1,0,0,'yes',120,'not_equipped',0,0,0,14,0,0,0,0,0,0,0,0,0,0,0,0),(100123,0,5,0,0,100007,100109,15,15,1399,990,'Boots','Light Boots','all','yes','yes',1,0,0,'yes',120,'not_equipped',0,0,0,6,3,0,0,0,0,0,0,0,0,0,0,0),(100124,0,6,0,0,100007,100109,15,60,149,2000,'Gloves','Leather Gloves','all','yes','yes',1,0,0,'yes',120,'not_equipped',0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0),(100125,0,3,0,0,100007,100109,0,75,1950,1060,'Armor','Leather Armor','all','yes','yes',1,0,0,'yes',120,'not_equipped',0,0,0,22,0,0,0,0,0,0,0,0,0,0,0,0),(100129,100124,6,100131,0,100007,0,15,60,0,0,'Gloves','Leather Gloves','all','yes','yes',1,0,0,'no',0,'not_equipped',0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_items` BEFORE INSERT ON `items` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(2,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_items` AFTER DELETE ON `items` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `playfield_graphics`
--

DROP TABLE IF EXISTS `playfield_graphics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playfield_graphics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playfield_id` int(10) unsigned NOT NULL DEFAULT '0',
  `graphic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100116 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playfield_graphics`
--

LOCK TABLES `playfield_graphics` WRITE;
/*!40000 ALTER TABLE `playfield_graphics` DISABLE KEYS */;
INSERT INTO `playfield_graphics` VALUES (100012,100020,100008,'2009-08-19 00:00:00','normal'),(100013,100020,100009,'2009-08-19 00:00:00','normal'),(100014,100020,100010,'2009-08-19 00:00:00','normal'),(100015,100019,100008,'2009-08-19 00:00:00','normal'),(100016,100019,100009,'2009-08-19 00:00:00','normal'),(100017,100019,100010,'2009-08-19 00:00:00','normal'),(100113,100109,100110,'2009-08-19 00:00:00','normal'),(100114,100109,100111,'2009-08-19 00:00:00','normal'),(100115,100109,100112,'2009-08-19 00:00:00','normal');
/*!40000 ALTER TABLE `playfield_graphics` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_playfield_graphics` BEFORE INSERT ON `playfield_graphics` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(11,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_playfield_graphics` AFTER DELETE ON `playfield_graphics` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `playfields`
--

DROP TABLE IF EXISTS `playfields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playfields` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `icon_graphic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `world_part_id` int(10) unsigned NOT NULL DEFAULT '0',
  `world_part_x` int(10) unsigned NOT NULL DEFAULT '0',
  `world_part_y` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(32) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `width` int(10) unsigned NOT NULL DEFAULT '0',
  `height` int(10) unsigned NOT NULL DEFAULT '0',
  `system_status` enum('normal','deleted','wb_new') NOT NULL DEFAULT 'normal',
  `available_status` enum('none','all','bots_only','premium_only','testing_only') NOT NULL DEFAULT 'none',
  `created_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `data` blob NOT NULL COMMENT 'holds the actual binary playfield data',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playfields`
--

LOCK TABLES `playfields` WRITE;
/*!40000 ALTER TABLE `playfields` DISABLE KEYS */;
INSERT INTO `playfields` VALUES (100018,0,0,0,0,'Cubic Green','First Test Level',1,1,'normal','all','2009-08-01 00:00:00','\0\0','2009-08-01 00:00:00'),(100019,0,0,0,0,'Black Forest','Second Test Level',10,12,'normal','all','2009-08-01 01:00:00','\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%\0	\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%\0	\0	\0%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\0\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2009-08-01 01:00:00'),(100020,0,0,0,0,'Da Woild','Yass manh!',2,2,'normal','all','0000-00-00 00:00:00','\0@\0A\0J\0C','0000-00-00 00:00:00'),(100021,0,0,0,0,'Smurf World','harrr!',2,2,'normal','testing_only','0000-00-00 00:00:00','\0\0\0\0\0\0\0\0','0000-00-00 00:00:00'),(100022,0,0,0,0,'Newton\'s Lab','The old man\'s playground.',5,5,'wb_new','testing_only','2009-10-13 19:32:24','\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2009-10-13 19:32:24'),(100023,0,0,0,0,'saefswf','sdfsadf',32,32,'wb_new','testing_only','2009-10-13 19:55:52','\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2009-10-13 19:55:52'),(100024,0,0,0,0,'dsfcxvxcvxcvxycv','cvxvcvxcvx',32,32,'wb_new','testing_only','2009-10-13 19:58:45','\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','2009-10-13 19:58:45'),(100109,0,0,0,0,'Coast','Initial Playfield',96,96,'wb_new','all','2010-06-09 13:03:48','\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B$\0L\0LB\0\0\0\0%\0L\0\0\0\0\0L\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+&\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.				\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0LB\0L\0L\0\0C\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\0\0\0LC\0L\0\0\0\0\0\0$		\0L\0L\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0D\0\0D\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\0\0G\0\0C\0\0\0\0\0\0B$				$%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0AAAAAAA\0@H\0\0H\0\0AAAAAAAA\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B			B\0\0\0\0B\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0FB\0\0B\0\0C\0\0E\0\0\0\0\0\0E\0\0C\0\0B\0\0BMF\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0	\0\0\0\0		$\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\rB\0L\0\0\r\r	B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0C\0\0\0\0\0\0\0(\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0		\0\0\0\0\0\0\0\0\0\0\0\0\0\0BB\0L\0\0B\0LB\0L		B\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0BB\0\0\0\0\0\0B\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0AAAAAAAAAAAA\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0			\n\0\0\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0BCC\0\0B		$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0*C\0\0\0L\0L\0%\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0B\0\0BC\0L		\n$B\0\0\0\0\0\0\0\0\0\0\0\0\0\0BBC\0LBB\0LB\0L\0L%\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\r\r\0\0\0\0\0\0\0\0\0\0\0L\0\0\0LB\0L\0\0	\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0LB\0L\0L		\0\0\0LB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\n\0\0\0\0\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0BC\0\0\0\0		\nB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L\0L	\nC\0L\0L		\n$C\0\0CC\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\0\0\0\0\n\0\0\0\0\0\0\0\0C\0L\0\0\0\0\0\0\0\0C\0\0		\n$\0@\0\0\0\0\0\0\0\0\0\0\0\0BB\0L\n\0\0B\0\0\0L\r\0LB\0\0\0L\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\n\0@\0@B\0\0\0\0\0\0\0\0\0\0\0\0\0L\0L\0\0\nB\0\0\0\0CB%\0M\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0									\0					\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0		\0@B\0\0\0\0\0\0\0\0\0\0C\0\0\n\0\0\0\0BB\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\0						!\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\0\n$\0\0C\0\0\0\0\0\0\0%C\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\0	\r	\" \0\0\0\0\0\0FC\0\0\0\0\0\0\0\0\0\0\0\0				\0	\0\0\0\0\0\0\0\0\0%\0\0\0\0CC\0\0\0\0$\0\0\0L\0\0\0\0BB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0						\0\0\0\0		\n\0\0E\0\0FBE\0\0\0\0\0\0\0\0\0\0\0\0	\r\0\r\r\r	\0\0\0\0\0\0\0%\0\0\0\0BB\0\0$\0\0\0\0\0\0$B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0						\n\0\0\0\0\n\0\0\0\0\0\0\0\0\0I\0I\0I\0I\0I\0I\0I\0I\0\0\0\0\0\0\0\0\0\0\0\0	\n$\0\0\0\0B\0\0						\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\0\0\0\0		\n\0\0ECFCFB\0\0\0\0\0\0\0\0+\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0$\r\r\r\r\r					\0\0\0L$B\0\0\0\0\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0						\n\0\0\0\0\0\0\0\0	\0\0\0\0\0\0B\0\0F\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\0\0\0\0\0\0B$\0L$\0\0\0\0\0\0\r						\n$\0\0\0\0BBB\0\0\0\0$\0\0\0\0\0\0$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0						\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\r\r\0\0\0\0\0\0\0\0\0\0$\0\0\0\0\0\0B\0\0$\r\r			B\0\0\0\0\0L$B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0									\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0LB\0\0\0\0\r\r		\0\0\0\0\0\0\0\0\0\0$\0\0\0\0$\0\0B$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0													\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0#\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\r	\0\0B\0\0\0\0BB\0\0\0\0\0\0\0\0B\0\0$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0												\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\'\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0L\0\0\0L$$		$\0\0\0LB$B\0\0$\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0)\0\0\0\0\0\0\0\0\0\0\0\0/#\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0$$$B$			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0BB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0*\'\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$B\0LB$$B\r\r		\0\0\0\0\0B$B\0\0$\0\0$B\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0						\0\0\0\0\0\0\r\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0(\0\0\0.,\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0\0L\0\0BB$B$$\r	\0\0\0\0\0\0\0\0\0B$\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0						\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0L\0\0B\0L$BBBBBB$\r\0				\0\0\0\0\0\0\0\0B\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&&\0\0\0\0\0\0					\r\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0$\0\0\0\0\0L\0LB\0L$BB$$$\0\0\0L						\0\0\0\0\0\0\0\0\0\0B\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0BBBB\0\0BB\0L\0\0\0\0\0\0B$\0\0\0\0\0L\0\0\0L$\r\r\r\r\r\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0C\0\0\0\0\0\0\0\0					\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0$B\0LB$B\0L\0L\0L\0\0\0L\0L\0\0B\0\0\0L%$\n$$\0\0$$\0\r\r\0	\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0							\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B$BB\0\0C\0L\0LB\0L\0L\0\0\0L$$%\n$\0\0\0\0\0\0B$\0\0\0\0\0\0\r\r	\0\0\0\0\0\0\0\0							\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0L	\n\0L\0\0\0\0B$%B$\0\0\0\0\0\0\0\0$\0\0B\0\0\0\0$$\r\r\r\r			\0\0							\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$BB\0L\0L\0L\0\0$%B$B	\n\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0$B\r\r\r\r	\0									\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B$$\0L$\0LB\r\0LB$%	\n\0\0\0\0$\0\0\0\0\0\0\0\0$\0\0$\0\0B\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\r\0\r										\0\0\0.\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0BBB$\0L\0\0\0L\0L\0L\0L\0\0\0\0$%\n\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0$B$\0\0\0\0B\0\0\0\0\0\0\0										\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L\0\0\0\0\0\0\0\0C\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$B$$B\0LBC\0\0\0\0BCB	\nB\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0										\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0LC\0\0\0\0\0\0\0\0\r\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0$B$CC\0\0\0L\0LB$B$				\nB\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0											\0\0\0\0\0LBL\0L\0\0\0\0\0\0C\r\0\0\0\0\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$$B$\0L\0\0B\0\0\0\0$$E\"					\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0														\n%\0L\0\0\0\0%\n\0L\0\0\0L\0\0\0\0C\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0LC$\0\0BC\0\0C\0\0\0\0%\0\0						B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0													\r\r\0L\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0BB\0L\0\0\0\0\0\0\0\0$\0\0\0\0		\r		\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0													\nC\r\r,\r\0&\0\0\0\0\0\0\0\0\0\0\0L			\0L\0\0%\0\0\0\0\0\0\0L\0\0\0\0B\0\0$$%\0\0\0\0\0\0\0		B\r$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0														\nCAAAL\0\0\0)\0L\r\r\0\r\" \0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L			\n\0L\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0L$%%%\rB\0\0\0\0%$%\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.															IIE\0\0\0\0\0\0\0\0B\0LE\0I\0L\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\r\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0C\0\0\0\0%%$$$	\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0															\0\0\0\0\0\0\0\0\0\0B\0\0\0LC\0\0\0ICB\0L\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L%%%$%%B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0@\0@\0\0\0\0\0\0														\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0I\0IF\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L\0\0$%$%$%\0\nCC\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0														\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0IC\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%$%$%%%\0	\n\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0															\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0L$$%$%\0L$\0	\r%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+M\0\0\0\0														\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0%\0L$%\0L\0\0\0	B\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0										\r\0\0\r\r\0\0C\0\0\0\0\0\0G\0\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%$\0\0\0\0\0\0\0\0\0\0\0\0\0\rC\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0										\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%$\0L\0\0\0\0\0\0\0B\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\r\0\0\0\0\0\0\0\0G\0\0CB\0LC\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0\0\0\0\0		\0\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0(\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\n\0\0\0\0,\0\0\0\0\0\0\0\0\0\0\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0	\r\0\0\0%C\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0/\0)\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\0\0\0\0\0\0\0\0\0\0\0.C	\nB\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0C			C\0I\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\n\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				)\0I\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0									\n\0\0\0\0\0\0\0\0/\0\0\0\0B\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\r		\n\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\r\r					\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0L\0\0\0L\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0CB	C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0												\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\0					\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0C\0\0\0\0C	\n%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0										\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0	\n							\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%	\n%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\r\r\0\0\0\0&\0\0\0\0\0\0\0\0\0\0											\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0JK\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0C\0\0	\n\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\r\r			\0\0\0\0\0\0M\0\0+\0\0\0\0\0\0\0\0			\0\0\0\0												\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0NO\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0			\n\0\0\0\0GG\0\0\0\0\0\0\0\0\0\0\0\0\r		\0\0\0\0													\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0C\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\r\0\0+\0\0M\0\0\0\0\0\0\0\0\0\0\0\0		\0\0\0\0\0\0											\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0*\0\0\0\0\0\0\0\0JK\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\n\0\0G\0\0\0\0\0\0										\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0NO\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0%\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0	\n\0\0\0\0\0\0											\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0C\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0													\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0JK\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0				\n\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\r\r											\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0NO\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\n\0\0\0\0	\n\0\0+\0\0\0.\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\r									\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0.\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\n\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0								\n\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0											\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\0\0	\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0												\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0										\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0												\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0													\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0$\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0												\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0													\n\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0L\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0															\0\0\0\0\0\0M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0									\r\r\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0															\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0																	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0\0\0\0\0\0\0M\0\0\0\0								\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0																	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0G\0\0							\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0								\r\r\r\r					\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\n\0\0\0\0\0\0\0\0\0\0#\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\r\0\0\0\0&&					+M\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\n\0\0\0\0\0\0\0\0*\'\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0GG\0\0\0\0\0\0	\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0							\0\0\0\0\0\0+\0\0\0\0\0\0\r\r			\n\0\0\0\0\0\0\0\0\0\0\0\0	\0				\n\0\0						\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0						\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0		\0\0\0\0\0				\0													\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\r\0\0\0\0\0\0\0\0G\0\0\0\0						\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0										\0																\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\nG\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0\0\0													\r\0\r\r\r													\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0		\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\0\0\0\0\0\0+\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0													\0\0\0I\0\0\0\0\0\0\r\r									\0\0\0\0M\0\0\0\0\0\0\0\0		\0\0\0\0\0\0\0\0					\r\r\0\0\0\0\0\0\0\0\0\0\0\0				\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\r					\r\0\0\0\0\0I\0\0\0\0F\0\0\0\0\0\0\r								\0\0\0\0\0\0\0\0												\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\0\0\0\0\0\0\0\0\0\0\0\0*\0\0\0\0\0\0			\0\0\r		\n\0\0\0\0D\0\0\0I\0\0\0\0F\0\0\0\0\0\0\0\0\0\0\"								\0\0\0\0\0\0																			\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\n\0\0LC\r\0\0\0\0HF\0IF\0\0\0\0\0\0\0\0\0\0\0\0!\r								\n\0\0\0\0\0\0																			\0\0\0\0\0\0\0\0&\0\0\0\0\0\0\0\0\0\0						\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0			\0\0AA\0\0C\0\0\0\0\0\0\0\0\0I\0I\0I\0\0\0\0\0\0\0\0\0\0E\0\0\0\0\0\0!								\n\0\0\0\0																						\0\0\0\0\0\0\0\0\0\0\0\0					\0\0\0\0\0\0\0\0+\0\0\0\0\0\0			\" !\0\0FF\0\0LB\0\0\0I\0I\0I\0I\0I\0\0\0I\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0																																	\0\0\0\0\0\0					\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0					\0\0\0\0\0I\0I\0I\0I\0I\0I\0IAAAA\0\0\0IAAAAAAA\0\0\0\0\0\0																																						\0\0							\n\0\0&\0\0\0\0\0\0\0\0\0\0\0\0&\0\0\0\0				\n\0\0\0\0AAAAAAAA\0\0\0\0\0\0\0\0\0\0/B\0\0\0LC\0\0\0\0\0\0\0\0																																								\n\0\0								\0\0\0\0\0\0D\0\0\0\0\0\0\0\0\0\0\0\0\0\0				\n\0\0\0\0G\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0C\0\0																																																		H	\0\0\0\0				\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0		\0\0																																																																																																																																																																													','2010-06-09 13:03:48');
/*!40000 ALTER TABLE `playfields` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_playfields` BEFORE INSERT ON `playfields` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(3,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_playfields` AFTER DELETE ON `playfields` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_sessions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `character_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'is set once per session when the user selects the character to play with',
  `session_start` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `session_end` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `session_duration` bigint(20) unsigned NOT NULL DEFAULT '0',
  `key_action_log` text NOT NULL COMMENT 'logs the most important actions of the user',
  `error_log` text NOT NULL COMMENT 'also logs cheat attempts',
  `suspicious_log` text NOT NULL COMMENT 'logs things which might need investigation',
  `debug_log` text NOT NULL,
  `status` enum('normal','needs_investigation','abnormal','critical') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `status` (`status`),
  KEY `user_status` (`user_id`,`status`),
  KEY `session_start` (`session_start`)
) ENGINE=InnoDB AUTO_INCREMENT=869 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sessions`
--

LOCK TABLES `user_sessions` WRITE;
/*!40000 ALTER TABLE `user_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `type` enum('user','moderator','observer','admin') NOT NULL DEFAULT 'user',
  `name` varchar(10) NOT NULL,
  `password` varchar(40) NOT NULL DEFAULT '',
  `email` varchar(64) NOT NULL DEFAULT '',
  `user_system_status` enum('active','inactive','banned') NOT NULL DEFAULT 'active',
  `premium_status` enum('none','expired','premium') NOT NULL DEFAULT 'none',
  `premium_expiry_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `registered_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `wb_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `debug_loglevel` int(10) unsigned NOT NULL DEFAULT '0',
  `system_status` enum('normal','deleted') NOT NULL DEFAULT 'normal',
  PRIMARY KEY (`id`),
  KEY `name` (`name`),
  KEY `name_pass` (`name`,`password`),
  KEY `type` (`type`),
  KEY `email` (`email`),
  KEY `premium_status` (`premium_status`),
  KEY `premium_expiry_date` (`premium_expiry_date`),
  KEY `system_status` (`user_system_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (100025,'user','acaca','df51e37c269aa94d38f93e537bf6e202b21406c','','active','none','0000-00-00 00:00:00','2010-01-09 15:31:04','0000-00-00 00:00:00',1,'normal');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `check_guid_users` BEFORE INSERT ON `users` FOR EACH ROW begin
declare newId INT;
if NEW.id = 0 then
  call getNextObjectId(4,0,newId);
  SET NEW.id = newId;
end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `set_guid_deleted_users` AFTER DELETE ON `users` FOR EACH ROW begin
update world_object_registry set system_status = 'deleted' where guid = OLD.id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `world_object_registry`
--

DROP TABLE IF EXISTS `world_object_registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `world_object_registry` (
  `guid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `object_type_id` int(10) unsigned NOT NULL DEFAULT '1',
  `creation_type` enum('worldbuilder','system') NOT NULL DEFAULT 'system',
  `worldbuilder_initial_release_id` int(10) unsigned NOT NULL DEFAULT '0',
  `worldbuilder_last_release_id` int(10) unsigned NOT NULL DEFAULT '0',
  `worldbuilder_pending_changes` enum('yes','no') NOT NULL DEFAULT 'no',
  `worldbuilder_date_last_changed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `system_status` enum('active','deleted') NOT NULL DEFAULT 'active',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=100132 DEFAULT CHARSET=utf8 COMMENT='Responsible for generating the object guids';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `world_object_registry`
--

LOCK TABLES `world_object_registry` WRITE;
/*!40000 ALTER TABLE `world_object_registry` DISABLE KEYS */;
/*!40000 ALTER TABLE `world_object_registry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `world_object_types`
--

DROP TABLE IF EXISTS `world_object_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `world_object_types` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `world_object_types`
--

LOCK TABLES `world_object_types` WRITE;
/*!40000 ALTER TABLE `world_object_types` DISABLE KEYS */;
INSERT INTO `world_object_types` VALUES (1,'Character',''),(2,'Item',''),(3,'Playfield',''),(4,'User',''),(5,'Quest',''),(6,'Quest Item',''),(7,'Chest',''),(8,'UserSession',''),(9,'Graphic',''),(10,'','');
/*!40000 ALTER TABLE `world_object_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `world_releases`
--

DROP TABLE IF EXISTS `world_releases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `world_releases` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `world_releases`
--

LOCK TABLES `world_releases` WRITE;
/*!40000 ALTER TABLE `world_releases` DISABLE KEYS */;
/*!40000 ALTER TABLE `world_releases` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-08-31 18:19:19
