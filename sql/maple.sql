/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 8.0.20 : Database - maple
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`maple` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `maple`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(13) NOT NULL,
  `password` char(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `loginState` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:未登录 1:transition 2:已登录',
  `lastLogin` bigint DEFAULT '0' COMMENT '上次登录时间',
  `isBanned` tinyint NOT NULL DEFAULT '0',
  `banReason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `cash` int unsigned NOT NULL DEFAULT '0' COMMENT '点券',
  `maplePoint` int unsigned NOT NULL DEFAULT '0' COMMENT '抵用券',
  `point` int unsigned NOT NULL DEFAULT '0' COMMENT '积分',
  `characterSlots` tinyint unsigned NOT NULL DEFAULT '6' COMMENT '角色位',
  `gm` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '账号等级 0:普通用户',
  `trunkId` int unsigned DEFAULT NULL,
  `cashTrunk` int unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `account_info` */

DROP TABLE IF EXISTS `account_info`;

CREATE TABLE `account_info` (
  `int` int unsigned NOT NULL AUTO_INCREMENT,
  `accId` int unsigned NOT NULL,
  `world` tinyint unsigned NOT NULL,
  `charslots` tinyint unsigned NOT NULL DEFAULT '6',
  `trunkId` int unsigned DEFAULT NULL,
  `cashTrunkId` int unsigned DEFAULT NULL,
  PRIMARY KEY (`int`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `android` */

DROP TABLE IF EXISTS `android`;

CREATE TABLE `android` (
  `itemId` bigint unsigned NOT NULL,
  `skin` smallint DEFAULT NULL,
  `hair` smallint DEFAULT NULL,
  `face` smallint DEFAULT NULL,
  `name` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'Unknown',
  `type` tinyint DEFAULT NULL,
  PRIMARY KEY (`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `cashshop_item` */

DROP TABLE IF EXISTS `cashshop_item`;

CREATE TABLE `cashshop_item` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `itemId` int unsigned DEFAULT NULL,
  `sn` int unsigned DEFAULT NULL,
  `shopItemFlag` tinyint DEFAULT NULL,
  `price` int DEFAULT NULL,
  `originalPrice` int DEFAULT NULL,
  `bundleQuantity` int DEFAULT NULL,
  `availableDays` int DEFAULT NULL,
  `pbCash` smallint DEFAULT NULL,
  `pbPoint` smallint DEFAULT NULL,
  `pbGift` smallint DEFAULT NULL,
  `meso` smallint DEFAULT NULL,
  `gender` int DEFAULT NULL,
  `likes` int DEFAULT NULL,
  `requiredLevel` int DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `showUp` tinyint DEFAULT NULL,
  `flag` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `cashshop_log` */

DROP TABLE IF EXISTS `cashshop_log`;

CREATE TABLE `cashshop_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `itemId` bigint unsigned NOT NULL,
  `sn` int unsigned NOT NULL,
  `charId` int unsigned NOT NULL,
  `quantity` int unsigned NOT NULL,
  `type` tinyint unsigned NOT NULL,
  `logTime` bigint unsigned NOT NULL,
  `ps` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `character` */

DROP TABLE IF EXISTS `character`;

CREATE TABLE `character` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `accId` int unsigned NOT NULL,
  `world` tinyint unsigned NOT NULL,
  `name` varchar(13) NOT NULL,
  `map` int unsigned NOT NULL,
  `spawnPoint` tinyint unsigned NOT NULL DEFAULT '0',
  `gm` tinyint unsigned NOT NULL DEFAULT '0',
  `party` int unsigned NOT NULL DEFAULT '0',
  `buddyCapacity` tinyint unsigned NOT NULL DEFAULT '20',
  `deleteTime` bigint NOT NULL DEFAULT '0',
  `equippedInventory` int unsigned DEFAULT NULL,
  `equipInventory` int unsigned DEFAULT NULL,
  `consumeInventory` int unsigned DEFAULT NULL,
  `installInventory` int unsigned DEFAULT NULL,
  `etcInventory` int unsigned DEFAULT NULL,
  `cashInventory` int unsigned NOT NULL,
  `charStats` int unsigned DEFAULT NULL,
  `quickslots` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `keymap` int unsigned DEFAULT NULL,
  `questmanager` int DEFAULT NULL,
  `damageSkinSlotSize` smallint unsigned NOT NULL DEFAULT '1',
  `isDeleted` tinyint unsigned NOT NULL DEFAULT '0',
  `lastLogout` bigint unsigned NOT NULL DEFAULT '0',
  `extendedPendant` bigint unsigned NOT NULL DEFAULT '0',
  `createdTime` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`,`cashInventory`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `character_potential` */

DROP TABLE IF EXISTS `character_potential`;

CREATE TABLE `character_potential` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `potKey` tinyint unsigned DEFAULT NULL,
  `skillId` int unsigned DEFAULT NULL,
  `slv` tinyint unsigned DEFAULT NULL,
  `grade` tinyint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `charId` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `charlook` */

DROP TABLE IF EXISTS `charlook`;

CREATE TABLE `charlook` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `gender` tinyint unsigned NOT NULL DEFAULT '0',
  `skin` tinyint unsigned DEFAULT '0',
  `hair` int unsigned NOT NULL,
  `face` int unsigned NOT NULL,
  `mark` int unsigned DEFAULT '0',
  `hairColorBase` tinyint NOT NULL DEFAULT '-1',
  `hairColorMixed` tinyint NOT NULL DEFAULT '0',
  `hairColorProb` tinyint NOT NULL DEFAULT '0',
  `zero` tinyint unsigned NOT NULL DEFAULT '0',
  `ears` int DEFAULT '0',
  `tail` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `charstat` */

DROP TABLE IF EXISTS `charstat`;

CREATE TABLE `charstat` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `job` smallint unsigned NOT NULL DEFAULT '0',
  `subJob` smallint unsigned NOT NULL DEFAULT '0',
  `remainingAp` int unsigned NOT NULL DEFAULT '0',
  `remainingSp` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0,0,0,0,0,0',
  `level` int unsigned NOT NULL DEFAULT '0',
  `str` smallint unsigned NOT NULL DEFAULT '0',
  `dex` smallint unsigned NOT NULL DEFAULT '0',
  `int_` smallint unsigned NOT NULL DEFAULT '0',
  `luk` smallint unsigned NOT NULL DEFAULT '0',
  `def` smallint unsigned NOT NULL DEFAULT '0',
  `speed` smallint unsigned NOT NULL DEFAULT '0',
  `jump` smallint unsigned NOT NULL DEFAULT '0',
  `hp` int unsigned NOT NULL DEFAULT '0',
  `maxHp` int unsigned NOT NULL DEFAULT '0',
  `mp` int unsigned NOT NULL DEFAULT '0',
  `maxMp` int unsigned NOT NULL DEFAULT '0',
  `fame` int unsigned NOT NULL DEFAULT '0',
  `honerPoint` int unsigned NOT NULL DEFAULT '0',
  `meso` bigint unsigned NOT NULL DEFAULT '0',
  `exp` bigint unsigned NOT NULL DEFAULT '0',
  `charismaExp` int unsigned NOT NULL DEFAULT '0',
  `insightExp` int unsigned NOT NULL DEFAULT '0',
  `willExp` int unsigned NOT NULL DEFAULT '0',
  `craftExp` int unsigned NOT NULL DEFAULT '0',
  `senseExp` int unsigned NOT NULL DEFAULT '0',
  `charmExp` int unsigned NOT NULL DEFAULT '0',
  `fatigue` int unsigned NOT NULL DEFAULT '0',
  `fatigueUpdated` int unsigned NOT NULL DEFAULT '0',
  `weaponPoint` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `damage_skin` */

DROP TABLE IF EXISTS `damage_skin`;

CREATE TABLE `damage_skin` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `damageSkinId` int unsigned DEFAULT NULL,
  `itemId` int unsigned DEFAULT NULL,
  `notSave` tinyint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `equip` */

DROP TABLE IF EXISTS `equip`;

CREATE TABLE `equip` (
  `itemId` bigint unsigned NOT NULL,
  `options` varchar(255) DEFAULT NULL,
  `sockets` varchar(255) DEFAULT NULL,
  `tuc` smallint unsigned NOT NULL DEFAULT '0',
  `cuc` smallint unsigned NOT NULL DEFAULT '0',
  `istr` smallint unsigned NOT NULL DEFAULT '0',
  `idex` smallint NOT NULL,
  `iint` smallint NOT NULL,
  `iluk` smallint DEFAULT NULL,
  `imaxhp` smallint DEFAULT NULL,
  `imaxmp` smallint DEFAULT NULL,
  `ipad` smallint DEFAULT NULL,
  `imad` smallint DEFAULT NULL,
  `ipdd` smallint DEFAULT NULL,
  `imdd` smallint DEFAULT NULL,
  `iacc` smallint DEFAULT NULL,
  `ieva` smallint DEFAULT NULL,
  `icraft` smallint DEFAULT NULL,
  `ispeed` smallint DEFAULT NULL,
  `ijump` smallint DEFAULT NULL,
  `attribute` smallint DEFAULT NULL,
  `leveluptype` smallint DEFAULT NULL,
  `level` smallint DEFAULT NULL,
  `durability` smallint DEFAULT NULL,
  `iuc` smallint DEFAULT NULL,
  `specialattribute` smallint DEFAULT NULL,
  `durabilitymax` smallint DEFAULT NULL,
  `iincreq` smallint DEFAULT NULL,
  `growthenchant` smallint DEFAULT NULL,
  `psenchant` smallint DEFAULT NULL,
  `hyperupgrade` smallint DEFAULT NULL,
  `bdr` smallint DEFAULT NULL,
  `imdr` smallint DEFAULT NULL,
  `damr` smallint DEFAULT NULL,
  `statr` smallint DEFAULT NULL,
  `cuttable` smallint DEFAULT NULL,
  `flame` bigint DEFAULT NULL,
  `itemstate` smallint DEFAULT NULL,
  `grade` smallint DEFAULT NULL,
  `chuc` smallint DEFAULT NULL,
  `souloptionid` smallint DEFAULT NULL,
  `soulsocketid` smallint DEFAULT NULL,
  `souloption` smallint DEFAULT NULL,
  `rstr` smallint DEFAULT NULL,
  `rdex` smallint DEFAULT NULL,
  `rint` smallint DEFAULT NULL,
  `rluk` smallint DEFAULT NULL,
  `rlevel` smallint DEFAULT NULL,
  `rjob` smallint DEFAULT NULL,
  `rpop` smallint DEFAULT NULL,
  `specialgrade` smallint DEFAULT NULL,
  `fixedpotential` tinyint(1) DEFAULT NULL,
  `tradeblock` tinyint(1) DEFAULT NULL,
  `isonly` tinyint(1) DEFAULT NULL,
  `notsale` tinyint(1) DEFAULT NULL,
  `attackspeed` int DEFAULT NULL,
  `price` int DEFAULT NULL,
  `charmexp` int DEFAULT NULL,
  `expireonlogout` tinyint(1) DEFAULT NULL,
  `setitemid` int DEFAULT NULL,
  `exitem` tinyint(1) DEFAULT NULL,
  `equiptradeblock` tinyint(1) DEFAULT NULL,
  `islot` varchar(255) DEFAULT NULL,
  `vslot` varchar(255) DEFAULT NULL,
  `fixedgrade` int DEFAULT NULL,
  `nopotential` tinyint(1) DEFAULT NULL,
  `bossreward` tinyint(1) DEFAULT NULL,
  `superioreqp` tinyint(1) DEFAULT NULL,
  `equipSkin` int DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `exp` int DEFAULT NULL,
  `ireducereq` smallint DEFAULT NULL,
  `limitBreak` int DEFAULT NULL,
  `tradeAvailable` tinyint DEFAULT '0',
  `showEffect` tinyint DEFAULT '1',
  PRIMARY KEY (`itemId`),
  CONSTRAINT `equip_ibfk_1` FOREIGN KEY (`itemId`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `equip_flame` */

DROP TABLE IF EXISTS `equip_flame`;

CREATE TABLE `equip_flame` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `fstr` tinyint unsigned NOT NULL DEFAULT '0',
  `fdex` tinyint unsigned NOT NULL DEFAULT '0',
  `fint` tinyint unsigned NOT NULL DEFAULT '0',
  `fluk` tinyint unsigned NOT NULL DEFAULT '0',
  `fatt` tinyint unsigned NOT NULL DEFAULT '0',
  `fstrdex` tinyint unsigned NOT NULL DEFAULT '0',
  `fstrint` tinyint unsigned NOT NULL DEFAULT '0',
  `fstrluk` tinyint unsigned NOT NULL DEFAULT '0',
  `fdexint` tinyint unsigned NOT NULL DEFAULT '0',
  `fdexluk` tinyint unsigned NOT NULL DEFAULT '0',
  `fintluk` tinyint unsigned NOT NULL DEFAULT '0',
  `fmatt` tinyint unsigned NOT NULL DEFAULT '0',
  `fdef` tinyint unsigned NOT NULL DEFAULT '0',
  `fhp` tinyint unsigned NOT NULL DEFAULT '0',
  `fmp` tinyint unsigned NOT NULL DEFAULT '0',
  `fspeed` tinyint unsigned NOT NULL DEFAULT '0',
  `fjump` tinyint unsigned NOT NULL DEFAULT '0',
  `fallstat` tinyint unsigned NOT NULL DEFAULT '0',
  `fboss` tinyint unsigned NOT NULL DEFAULT '0',
  `fdamage` tinyint unsigned NOT NULL DEFAULT '0',
  `flevel` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `error_packet_log` */

DROP TABLE IF EXISTS `error_packet_log`;

CREATE TABLE `error_packet_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `accId` int unsigned DEFAULT NULL,
  `opcode` smallint unsigned DEFAULT NULL,
  `logTime` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `familiar` */

DROP TABLE IF EXISTS `familiar`;

CREATE TABLE `familiar` (
  `itemId` bigint unsigned NOT NULL,
  `charId` int unsigned DEFAULT NULL,
  `familiarId` int unsigned DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `level` tinyint DEFAULT NULL,
  `grade` tinyint unsigned DEFAULT NULL,
  `exp` int unsigned DEFAULT NULL,
  `skill` int unsigned DEFAULT NULL,
  `option1` int DEFAULT NULL,
  `option2` int DEFAULT NULL,
  `option3` int DEFAULT NULL,
  PRIMARY KEY (`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `friend` */

DROP TABLE IF EXISTS `friend`;

CREATE TABLE `friend` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `accId` int unsigned DEFAULT NULL,
  `friendId` int unsigned DEFAULT NULL,
  `name` varchar(13) DEFAULT NULL,
  `flag` tinyint DEFAULT NULL,
  `groupName` varchar(17) DEFAULT NULL,
  `friendAccountId` int unsigned DEFAULT NULL,
  `nickName` varchar(13) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `grade_name` */

DROP TABLE IF EXISTS `grade_name`;

CREATE TABLE `grade_name` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `gradeName` varchar(255) DEFAULT NULL,
  `guildId` int unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `guild` */

DROP TABLE IF EXISTS `guild`;

CREATE TABLE `guild` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL DEFAULT '',
  `leaderId` int unsigned NOT NULL DEFAULT '0',
  `worldId` tinyint unsigned NOT NULL DEFAULT '0',
  `markBg` int unsigned NOT NULL DEFAULT '0',
  `markBgColor` int unsigned NOT NULL DEFAULT '0',
  `mark` int unsigned NOT NULL DEFAULT '0',
  `markColor` int unsigned NOT NULL DEFAULT '0',
  `maxMembers` int unsigned NOT NULL DEFAULT '10',
  `notice` varchar(255) DEFAULT '',
  `potints` int unsigned NOT NULL DEFAULT '0',
  `level` int unsigned NOT NULL DEFAULT '0',
  `rank` int unsigned NOT NULL DEFAULT '0',
  `ggp` int unsigned NOT NULL DEFAULT '0',
  `appliable` tinyint(1) NOT NULL DEFAULT '1',
  `joinSetting` int unsigned NOT NULL DEFAULT '0',
  `reqLevel` int unsigned NOT NULL DEFAULT '0',
  `battleSp` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `guild_member` */

DROP TABLE IF EXISTS `guild_member`;

CREATE TABLE `guild_member` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `guildId` int unsigned NOT NULL,
  `grade` int unsigned NOT NULL DEFAULT '0',
  `allianceGrade` int unsigned NOT NULL DEFAULT '0',
  `commitment` int unsigned NOT NULL DEFAULT '0',
  `dayCommitment` int unsigned NOT NULL DEFAULT '0',
  `igp` int unsigned NOT NULL DEFAULT '0',
  `commitmentIncTime` bigint unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `job` int unsigned NOT NULL,
  `level` int unsigned NOT NULL,
  `loggedIn` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `guild_requestor` */

DROP TABLE IF EXISTS `guild_requestor`;

CREATE TABLE `guild_requestor` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `requestor_id` int unsigned NOT NULL,
  `charId` int unsigned NOT NULL,
  `guildId` int unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `job` int unsigned NOT NULL,
  `level` int unsigned NOT NULL,
  `loggedIn` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `inventory` */

DROP TABLE IF EXISTS `inventory`;

CREATE TABLE `inventory` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL,
  `slots` smallint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id` (`id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `item` */

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint unsigned NOT NULL COMMENT 'Item Type',
  `itemId` int unsigned NOT NULL,
  `invType` tinyint unsigned NOT NULL COMMENT 'Inventory Type',
  `pos` smallint unsigned NOT NULL DEFAULT '0',
  `quantity` int unsigned NOT NULL DEFAULT '0',
  `owner` varchar(128) DEFAULT NULL,
  `expireTime` bigint unsigned NOT NULL,
  `flag` int unsigned NOT NULL DEFAULT '0',
  `isCash` tinyint unsigned DEFAULT NULL,
  `inventoryId` int unsigned DEFAULT NULL,
  `sn` bigint unsigned DEFAULT '0' COMMENT 'CashItem Only',
  `storage` int unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `item_log` */

DROP TABLE IF EXISTS `item_log`;

CREATE TABLE `item_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `itemId` bigint unsigned NOT NULL,
  `logInfo` varchar(255) DEFAULT NULL,
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `item_storage` */

DROP TABLE IF EXISTS `item_storage`;

CREATE TABLE `item_storage` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `accId` int unsigned NOT NULL,
  `type` varchar(13) NOT NULL,
  `meso` bigint unsigned DEFAULT NULL,
  `slots` int unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `key_binding` */

DROP TABLE IF EXISTS `key_binding`;

CREATE TABLE `key_binding` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `keymapId` int unsigned DEFAULT NULL,
  `key` int DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `action` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `key_map` */

DROP TABLE IF EXISTS `key_map`;

CREATE TABLE `key_map` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `macro` */

DROP TABLE IF EXISTS `macro`;

CREATE TABLE `macro` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `muted` tinyint(1) NOT NULL DEFAULT '1',
  `skills` varchar(128) NOT NULL DEFAULT '0,0,0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `status` tinyint unsigned NOT NULL,
  `fromId` int unsigned NOT NULL,
  `fromChr` varchar(13) NOT NULL,
  `toId` int unsigned NOT NULL,
  `toChr` varchar(13) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `createdTime` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `mob_drop` */

DROP TABLE IF EXISTS `mob_drop`;

CREATE TABLE `mob_drop` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `mobId` int DEFAULT NULL,
  `itemId` int unsigned DEFAULT NULL,
  `chance` smallint unsigned DEFAULT NULL,
  `minQuantity` int unsigned DEFAULT NULL,
  `maxQuantity` int unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `non_combat_stat_day_limit` */

DROP TABLE IF EXISTS `non_combat_stat_day_limit`;

CREATE TABLE `non_combat_stat_day_limit` (
  `id` int unsigned NOT NULL,
  `charisma` smallint unsigned NOT NULL DEFAULT '0',
  `charm` smallint unsigned NOT NULL DEFAULT '0',
  `insight` smallint unsigned NOT NULL DEFAULT '0',
  `will` smallint unsigned NOT NULL DEFAULT '0',
  `craft` smallint unsigned NOT NULL DEFAULT '0',
  `sense` smallint unsigned NOT NULL DEFAULT '0',
  `lastUpdateCharmByCashPR` bigint NOT NULL DEFAULT '0',
  `charmByCashPR` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `npc_shop` */

DROP TABLE IF EXISTS `npc_shop`;

CREATE TABLE `npc_shop` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `npcId` int unsigned NOT NULL,
  `shopId` int unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `pet` */

DROP TABLE IF EXISTS `pet`;

CREATE TABLE `pet` (
  `itemId` bigint NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `level` tinyint DEFAULT NULL,
  `tameness` smallint DEFAULT NULL,
  `repleteness` smallint DEFAULT NULL,
  `petSkill` int DEFAULT NULL,
  `petAttribute` smallint DEFAULT NULL,
  `deadDate` bigint DEFAULT NULL,
  `remainLife` int DEFAULT NULL,
  `attribute` smallint DEFAULT NULL,
  `activeState` tinyint DEFAULT NULL,
  `autoBuffSkill` int DEFAULT NULL,
  `petHue` int DEFAULT NULL,
  `giantRate` smallint DEFAULT NULL,
  PRIMARY KEY (`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `pet_exception_item` */

DROP TABLE IF EXISTS `pet_exception_item`;

CREATE TABLE `pet_exception_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `petId` bigint unsigned DEFAULT NULL,
  `itemId` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest` */

DROP TABLE IF EXISTS `quest`;

CREATE TABLE `quest` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `status` tinyint DEFAULT NULL,
  `completedTime` bigint DEFAULT NULL,
  `qrKey` int DEFAULT NULL,
  `qrValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest_ex` */

DROP TABLE IF EXISTS `quest_ex`;

CREATE TABLE `quest_ex` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `questId` int NOT NULL,
  `qrValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest_ex_shared` */

DROP TABLE IF EXISTS `quest_ex_shared`;

CREATE TABLE `quest_ex_shared` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `qrKey` int unsigned DEFAULT NULL,
  `accId` int unsigned DEFAULT NULL,
  `qrValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest_list` */

DROP TABLE IF EXISTS `quest_list`;

CREATE TABLE `quest_list` (
  `questlist_id` int unsigned NOT NULL AUTO_INCREMENT,
  `questmanager_id` int unsigned DEFAULT NULL,
  `questId` int unsigned DEFAULT NULL,
  PRIMARY KEY (`questlist_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest_manager` */

DROP TABLE IF EXISTS `quest_manager`;

CREATE TABLE `quest_manager` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `quest_progressrequirement` */

DROP TABLE IF EXISTS `quest_progressrequirement`;

CREATE TABLE `quest_progressrequirement` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `orderNum` int DEFAULT NULL,
  `progressType` varchar(255) DEFAULT NULL,
  `questId` int unsigned DEFAULT NULL,
  `unitId` int DEFAULT NULL,
  `requiredCount` int DEFAULT NULL,
  `currentCount` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `record` */

DROP TABLE IF EXISTS `record`;

CREATE TABLE `record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `accId` int unsigned DEFAULT NULL,
  `charId` int unsigned DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `key` int unsigned NOT NULL,
  `value` int unsigned NOT NULL,
  `lastUpdated` bigint DEFAULT NULL,
  `lastReset` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `shop_item` */

DROP TABLE IF EXISTS `shop_item`;

CREATE TABLE `shop_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `shopId` int unsigned NOT NULL DEFAULT '0',
  `itemId` int unsigned NOT NULL DEFAULT '0',
  `price` int NOT NULL DEFAULT '0',
  `tokenItemId` int NOT NULL DEFAULT '0',
  `tokenPrice` int NOT NULL DEFAULT '0',
  `pointQuestId` int NOT NULL DEFAULT '0',
  `pointPrice` int NOT NULL DEFAULT '0',
  `starCoin` int NOT NULL DEFAULT '0',
  `questExId` int NOT NULL DEFAULT '0',
  `questExKey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `questExValue` int NOT NULL DEFAULT '0',
  `itemPeriod` int NOT NULL DEFAULT '0',
  `levelLimited` int NOT NULL DEFAULT '0',
  `showLevMin` int NOT NULL DEFAULT '0',
  `showLevMax` int NOT NULL DEFAULT '0',
  `questId` int NOT NULL DEFAULT '0',
  `sellStart` bigint NOT NULL DEFAULT '0',
  `sellEnd` bigint NOT NULL DEFAULT '0',
  `tabIndex` int NOT NULL DEFAULT '0',
  `worldBlock` tinyint NOT NULL DEFAULT '0',
  `potentialGrade` int NOT NULL DEFAULT '0',
  `buyLimit` int NOT NULL DEFAULT '0',
  `quantity` smallint NOT NULL DEFAULT '1',
  `unitPrice` bigint NOT NULL DEFAULT '0',
  `maxPerSlot` smallint NOT NULL DEFAULT '1',
  `discountPerc` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `skill` */

DROP TABLE IF EXISTS `skill`;

CREATE TABLE `skill` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `skillId` int DEFAULT NULL,
  `rootId` int DEFAULT NULL,
  `maxLevel` int DEFAULT NULL,
  `currentLevel` int DEFAULT NULL,
  `masterLevel` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `skill_cooldown` */

DROP TABLE IF EXISTS `skill_cooldown`;

CREATE TABLE `skill_cooldown` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `skillId` int unsigned NOT NULL,
  `nextUsableTime` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `trade_log` */

DROP TABLE IF EXISTS `trade_log`;

CREATE TABLE `trade_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned NOT NULL,
  `otherId` int unsigned NOT NULL,
  `charItems` bigint unsigned NOT NULL,
  `otherItems` bigint unsigned NOT NULL,
  `charMeso` bigint unsigned NOT NULL DEFAULT '0',
  `otherMeso` bigint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `trunk` */

DROP TABLE IF EXISTS `trunk`;

CREATE TABLE `trunk` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `money` bigint unsigned DEFAULT NULL,
  `type` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `slots` tinyint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `wished_item` */

DROP TABLE IF EXISTS `wished_item`;

CREATE TABLE `wished_item` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `charId` int unsigned DEFAULT NULL,
  `itemId` int unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
