-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 15, 2025 at 11:21 AM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `a`
--

DELIMITER $$
--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `SPLIT_STR` (`x` VARCHAR(255), `delim` VARCHAR(12), `pos` INT) RETURNS VARCHAR(255) CHARSET utf8mb4 COLLATE utf8mb4_general_ci DETERMINISTIC BEGIN 
    RETURN REPLACE(SUBSTRING(SUBSTRING_INDEX(x, delim, pos),
       LENGTH(SUBSTRING_INDEX(x, delim, pos - 1)) + 1),
       delim, '');
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` text NOT NULL,
  `create_time` timestamp NULL DEFAULT current_timestamp(),
  `update_time` timestamp NULL DEFAULT current_timestamp(),
  `ban` tinyint(1) NOT NULL DEFAULT 0,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0,
  `last_time_login` timestamp NOT NULL DEFAULT '2002-07-30 17:00:00',
  `last_time_logout` timestamp NOT NULL DEFAULT '2002-07-30 17:00:00',
  `ip_address` varchar(50) DEFAULT NULL,
  `active` int(11) NOT NULL DEFAULT 0,
  `thoi_vang` int(11) NOT NULL DEFAULT 0,
  `server_login` int(11) NOT NULL DEFAULT -1,
  `bd_player` double DEFAULT 1,
  `is_gift_box` tinyint(1) DEFAULT 0,
  `gift_time` varchar(255) DEFAULT '0',
  `reward` longtext DEFAULT NULL,
  `cash` int(11) NOT NULL DEFAULT 0,
  `danap` int(11) NOT NULL DEFAULT 0,
  `token` text NOT NULL DEFAULT '',
  `xsrf_token` text NOT NULL DEFAULT '',
  `newpass` text NOT NULL DEFAULT '',
  `luotquay` int(11) NOT NULL DEFAULT 0,
  `vang` bigint(20) NOT NULL DEFAULT 0,
  `event_point` int(11) NOT NULL DEFAULT 0,
  `vip` int(11) NOT NULL DEFAULT 4,
  `vnd` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `username`, `password`, `email`, `create_time`, `update_time`, `ban`, `is_admin`, `last_time_login`, `last_time_logout`, `ip_address`, `active`, `thoi_vang`, `server_login`, `bd_player`, `is_gift_box`, `gift_time`, `reward`, `cash`, `danap`, `token`, `xsrf_token`, `newpass`, `luotquay`, `vang`, `event_point`, `vip`, `vnd`) VALUES
(1, 'admin', '123', '', '2023-03-07 10:10:43', '2023-03-07 10:10:43', 0, 1, '2025-04-15 08:51:38', '2025-04-15 08:55:58', '117.0.112.92', 1, 8799000, -1, 1, 0, '0', '', 2141384647, 0, '', '', '', 0, 9957485903344, 286612, 1104, 964979999),
(1000005, '1', '22', '', '2025-04-01 09:38:11', '2025-04-01 09:38:11', 0, 1, '2025-04-15 09:10:59', '2025-04-15 09:10:00', '14.226.48.41', 1, 0, -1, 1, 0, '0', NULL, 0, 0, '', '', '', 0, 0, 0, 1111, 976999999),
(1000006, '3', '3', '', '2025-04-04 02:06:14', '2025-04-04 02:06:14', 0, 1, '2025-04-04 05:53:50', '2025-04-04 05:54:12', '113.175.176.181', 0, 0, -1, 1, 0, '0', NULL, 0, 0, '', '', '', 0, 0, 0, 1111, 999999999);

-- --------------------------------------------------------

--
-- Table structure for table `clan`
--

CREATE TABLE `clan` (
  `id` int(11) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `NAME_2` varchar(4) NOT NULL,
  `slogan` varchar(255) NOT NULL DEFAULT '',
  `img_id` int(11) NOT NULL DEFAULT 0,
  `power_point` bigint(20) NOT NULL DEFAULT 0,
  `max_member` smallint(6) NOT NULL DEFAULT 10,
  `clan_point` int(11) NOT NULL DEFAULT 0,
  `LEVEL` int(11) NOT NULL DEFAULT 1,
  `members` text NOT NULL,
  `tops` text NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `clan`
--

INSERT INTO `clan` (`id`, `NAME`, `NAME_2`, `slogan`, `img_id`, `power_point`, `max_member`, `clan_point`, `LEVEL`, `members`, `tops`, `create_time`) VALUES
(0, 'cc', 'okk', '', 0, 0, 10, 0, 1, '[\"{\\\"role\\\":0,\\\"receive_donate\\\":0,\\\"member_point\\\":0,\\\"body\\\":16,\\\"join_time\\\":1730432617,\\\"leg\\\":17,\\\"head\\\":28,\\\"ask_pea_time\\\":0,\\\"name\\\":\\\"test12345\\\",\\\"clan_point\\\":0,\\\"id\\\":1001729,\\\"donate\\\":0,\\\"power\\\":4492}\"]', 'cc', '2024-11-01 03:43:37'),
(1, 'cccc', 'ccc', '', 0, 0, 10, 0, 1, '[\"{\\\"role\\\":0,\\\"receive_donate\\\":0,\\\"member_point\\\":0,\\\"body\\\":16,\\\"join_time\\\":1730432979,\\\"leg\\\":17,\\\"head\\\":28,\\\"ask_pea_time\\\":0,\\\"name\\\":\\\"test12345\\\",\\\"clan_point\\\":0,\\\"id\\\":1001729,\\\"donate\\\":0,\\\"power\\\":4492}\"]', 'cc', '2024-11-01 03:49:39'),
(2, 'cccc', 'ssss', '', 0, 0, 10, 0, 1, '[\"{\\\"role\\\":0,\\\"receive_donate\\\":0,\\\"member_point\\\":0,\\\"body\\\":16,\\\"join_time\\\":1730434440,\\\"leg\\\":17,\\\"head\\\":28,\\\"ask_pea_time\\\":0,\\\"name\\\":\\\"test12345\\\",\\\"clan_point\\\":0,\\\"id\\\":1001729,\\\"donate\\\":0,\\\"power\\\":4492}\"]', 'cc', '2024-11-01 04:14:00'),
(3, '34', '', '', 7, 0, 10, 0, 1, '[\"{\\\"role\\\":0,\\\"receive_donate\\\":0,\\\"member_point\\\":0,\\\"body\\\":889,\\\"join_time\\\":1744536906,\\\"leg\\\":890,\\\"head\\\":888,\\\"ask_pea_time\\\":0,\\\"name\\\":\\\"tester\\\",\\\"clan_point\\\":0,\\\"id\\\":1001733,\\\"donate\\\":0,\\\"power\\\":5334100820}\"]', 'cc', '2025-04-13 09:35:06');

-- --------------------------------------------------------

--
-- Table structure for table `giftcode`
--

CREATE TABLE `giftcode` (
  `id` int(11) NOT NULL,
  `code` text NOT NULL,
  `count_left` int(11) NOT NULL,
  `detail` text NOT NULL,
  `datecreate` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `expired` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- --------------------------------------------------------

--
-- Table structure for table `history_transaction`
--

CREATE TABLE `history_transaction` (
  `id` int(11) NOT NULL,
  `player_1` varchar(255) NOT NULL,
  `player_2` varchar(255) NOT NULL,
  `item_player_1` text NOT NULL,
  `item_player_2` text NOT NULL,
  `bag_1_before_tran` text NOT NULL,
  `bag_2_before_tran` text NOT NULL,
  `bag_1_after_tran` text NOT NULL,
  `bag_2_after_tran` text NOT NULL,
  `time_tran` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE `player` (
  `id` int(11) NOT NULL,
  `account_id` int(11) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `head` int(11) NOT NULL DEFAULT 102,
  `gender` int(11) NOT NULL,
  `have_tennis_space_ship` tinyint(1) DEFAULT 0,
  `clan_id` int(11) NOT NULL DEFAULT -1,
  `data_inventory` text NOT NULL,
  `data_location` text NOT NULL,
  `data_point` text NOT NULL,
  `data_magic_tree` text NOT NULL,
  `items_body` text NOT NULL,
  `items_bag` text NOT NULL,
  `items_box` text NOT NULL,
  `items_box_lucky_round` text NOT NULL,
  `items_daban` text NOT NULL,
  `friends` text NOT NULL,
  `enemies` text NOT NULL,
  `data_intrinsic` text NOT NULL,
  `data_item_time` text NOT NULL,
  `data_task` text NOT NULL,
  `data_mabu_egg` text NOT NULL,
  `data_charm` text NOT NULL,
  `skills` text NOT NULL,
  `skills_shortcut` text NOT NULL,
  `pet` text NOT NULL,
  `data_black_ball` text NOT NULL,
  `data_side_task` text NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `notify` text CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `baovetaikhoan` varchar(1000) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL ,
  `captcha` varchar(1000) NOT NULL ,
  `data_card` varchar(10000) NOT NULL ,
  `lasttimepkcommeson` bigint(20) NOT NULL DEFAULT 0,
  `bandokhobau` varchar(250) NOT NULL ,
  `doanhtrai` bigint(11) NOT NULL DEFAULT 0,
  `conduongrandoc` varchar(255) NOT NULL ,
  `masterDoesNotAttack` text NOT NULL,
  `nhanthoivang` varchar(200) NOT NULL ,
  `ruonggo` varchar(255) NOT NULL ,
  `sieuthanthuy` varchar(255) NOT NULL ,
  `vodaisinhtu` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL ,
  `rongxuong` bigint(20) NOT NULL DEFAULT 0,
  `data_item_event` varchar(1000) NOT NULL ,
  `data_luyentap` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL ,
  `data_clan_task` varchar(255) NOT NULL ,
  `data_vip` text ,
  `rank` int(11) NOT NULL DEFAULT 0,
  `data_achievement` text NOT NULL ,
  `giftcode` text NOT NULL DEFAULT '',
  `dataBadges` text DEFAULT NULL,
  `dataTaskBadges` text DEFAULT NULL,
  `BoughtSkill` text DEFAULT NULL,
  `LearnSkill` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player`
--

INSERT INTO `player` (`id`, `account_id`, `name`, `head`, `gender`, `have_tennis_space_ship`, `clan_id`, `data_inventory`, `data_location`, `data_point`, `data_magic_tree`, `items_body`, `items_bag`, `items_box`, `items_box_lucky_round`, `items_daban`, `friends`, `enemies`, `data_intrinsic`, `data_item_time`, `data_task`, `data_mabu_egg`, `data_charm`, `skills`, `skills_shortcut`, `pet`, `data_black_ball`, `data_side_task`, `create_time`, `notify`, `baovetaikhoan`, `captcha`, `data_card`, `lasttimepkcommeson`, `bandokhobau`, `doanhtrai`, `conduongrandoc`, `masterDoesNotAttack`, `nhanthoivang`, `ruonggo`, `sieuthanthuy`, `vodaisinhtu`, `rongxuong`, `data_item_event`, `data_luyentap`, `data_clan_task`, `data_vip`, `rank`, `data_achievement`, `giftcode`, `dataBadges`, `dataTaskBadges`, `BoughtSkill`, `LearnSkill`) VALUES
(1001733, 1, 'tester', 32, 1, 0, 3, '[1503053431,134684,25,0,0]', '[7,203,432]', '[0,17356924956,17293715448,1000,1000,2100,50200,210,0,0,0,2000000000,2000000000]', '[1,5,0,1744208232958,1744207919203]', '[\"[1,1,\\\"[\\\\\\\"[47,2]\\\\\\\",\\\\\\\"[77,999999999]\\\\\\\"]\\\",1744208063222]\",\"[7,1,\\\"[\\\\\\\"[6,20]\\\\\\\",\\\\\\\"[50,999999999]\\\\\\\"]\\\",1744208133931]\",\"[22,1,\\\"[\\\\\\\"[0,3]\\\\\\\",\\\\\\\"[103,93939393]\\\\\\\"]\\\",1744208562856]\",\"[-1,0,\\\"[]\\\",1744707098940]\",\"[12,1,\\\"[\\\\\\\"[14,1]\\\\\\\",\\\\\\\"[101,9999999]\\\\\\\"]\\\",1744208234615]\",\"[1731,1,\\\"[\\\\\\\"[50,33]\\\\\\\",\\\\\\\"[103,29]\\\\\\\",\\\\\\\"[5,19]\\\\\\\",\\\\\\\"[14,15]\\\\\\\"]\\\",1744540022732]\",\"[-1,0,\\\"[]\\\",1744707098940]\",\"[-1,0,\\\"[]\\\",1744707098940]\",\"[1722,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744540022732]\",\"[1541,1,\\\"[\\\\\\\"[77,7]\\\\\\\",\\\\\\\"[103,7]\\\\\\\",\\\\\\\"[84,0]\\\\\\\",\\\\\\\"[14,5]\\\\\\\"]\\\",1744208635182]\",\"[-1,0,\\\"[]\\\",1744707098941]\",\"[-1,0,\\\"[]\\\",1744707098941]\",\"[-1,0,\\\"[]\\\",1744707098941]\"]', '[\"[457,10,\\\"[\\\\\\\"[73,1]\\\\\\\"]\\\",1744207919203]\",\"[13,1,\\\"[\\\\\\\"[48,100]\\\\\\\"]\\\",1744207932129]\",\"[1,1,\\\"[\\\\\\\"[47,2]\\\\\\\"]\\\",1744207919203]\",\"[7,1,\\\"[\\\\\\\"[6,20]\\\\\\\"]\\\",1744207919203]\",\"[223,1,\\\"[\\\\\\\"[68,0]\\\\\\\"]\\\",1744208248791]\",\"[11,1,\\\"[\\\\\\\"[6,100]\\\\\\\",\\\\\\\"[27,20]\\\\\\\",\\\\\\\"[101,99999999]\\\\\\\"]\\\",1744208209520]\",\"[224,2,\\\"[\\\\\\\"[67,0]\\\\\\\"]\\\",1744208228456]\",\"[7,1,\\\"[\\\\\\\"[6,20]\\\\\\\",\\\\\\\"[130,0]\\\\\\\",\\\\\\\"[142,0]\\\\\\\",\\\\\\\"[30,0]\\\\\\\"]\\\",1744208305618]\",\"[12,1,\\\"[\\\\\\\"[14,1]\\\\\\\"]\\\",1744207919203]\",\"[7,1,\\\"[\\\\\\\"[6,20]\\\\\\\",\\\\\\\"[131,0]\\\\\\\",\\\\\\\"[143,0]\\\\\\\",\\\\\\\"[30,0]\\\\\\\"]\\\",1744208248776]\",\"[193,75,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744208525788]\",\"[194,1,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744208530916]\",\"[20,1,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744208711103]\",\"[19,4,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744242700528]\",\"[1721,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744539877273]\",\"[18,1,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744604981428]\",\"[1629,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744540022732]\",\"[611,1,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744538008800]\",\"[1999,10000,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744541106742]\",\"[1074,1,\\\"[\\\\\\\"[73,0]\\\\\\\"]\\\",1744538887242]\",\"[457,268,\\\"[\\\\\\\"[100,1]\\\\\\\"]\\\",1744539877273]\",\"[459,50,\\\"[\\\\\\\"[73,1]\\\\\\\"]\\\",1744539877273]\",\"[987,85,\\\"[\\\\\\\"[73,1]\\\\\\\"]\\\",1744539877273]\",\"[1732,1,\\\"[\\\\\\\"[50,25]\\\\\\\",\\\\\\\"[103,21]\\\\\\\",\\\\\\\"[5,11]\\\\\\\",\\\\\\\"[14,7]\\\\\\\",\\\\\\\"[97,30]\\\\\\\"]\\\",1744539877273]\",\"[1722,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744554058114]\",\"[1629,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744554058114]\",\"[1204,29174,\\\"[\\\\\\\"[73,1]\\\\\\\"]\\\",1744554123684]\",\"[1629,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744554086778]\",\"[1731,1,\\\"[\\\\\\\"[50,33]\\\\\\\",\\\\\\\"[103,29]\\\\\\\",\\\\\\\"[5,19]\\\\\\\",\\\\\\\"[14,15]\\\\\\\"]\\\",1744554079050]\",\"[1731,1,\\\"[\\\\\\\"[50,33]\\\\\\\",\\\\\\\"[103,29]\\\\\\\",\\\\\\\"[5,19]\\\\\\\",\\\\\\\"[14,15]\\\\\\\"]\\\",1744554058114]\",\"[1629,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744554079050]\",\"[1731,1,\\\"[\\\\\\\"[50,33]\\\\\\\",\\\\\\\"[103,29]\\\\\\\",\\\\\\\"[5,19]\\\\\\\",\\\\\\\"[14,15]\\\\\\\"]\\\",1744554086778]\",\"[1722,1,\\\"[\\\\\\\"[50,5]\\\\\\\",\\\\\\\"[77,5]\\\\\\\",\\\\\\\"[103,5]\\\\\\\",\\\\\\\"[101,5]\\\\\\\"]\\\",1744554086778]\",\"[726,1,\\\"[\\\\\\\"[30,0]\\\\\\\",\\\\\\\"[93,1]\\\\\\\"]\\\",1744693691334]\",\"[1653,1,\\\"[\\\\\\\"[30,0]\\\\\\\"]\\\",1744707127040]\",\"[1652,1,\\\"[\\\\\\\"[30,0]\\\\\\\"]\\\",1744707128274]\",\"[1635,1,\\\"[\\\\\\\"[30,0]\\\\\\\"]\\\",1744707129172]\",\"[-1,0,\\\"[]\\\",1744707098946]\",\"[-1,0,\\\"[]\\\",1744707098946]\",\"[-1,0,\\\"[]\\\",1744707098946]\"]', '[\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\",\"[-1,0,\\\"[]\\\",1744707098947]\"]', '[]', '[\"[722,1,\\\"[\\\\\\\"[30,1]\\\\\\\",\\\\\\\"[97,30]\\\\\\\"]\\\",1744208644115]\"]', '[]', '[]', '[0,0,0,0,false,0,0,0]', '[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]', '[32,0,0,0]', '[]', '[1746800164799,1746800165709,1746800166839,1746800167802,1744207919205,1746800168665,1746800172353,1746800174251,1746800171024,1746800169828]', '[\"[2,1,1744707283724,0]\",\"[3,0,0,0]\",\"[7,0,0,0]\",\"[11,0,0,0]\",\"[12,0,0,0]\",\"[17,0,0,0]\",\"[18,0,0,0]\",\"[19,0,0,0]\"]', '[2,-1,-1,-1,-1,-1,-1,-1,-1,-1]', '[\"[0,2,\\\"$Đệ tử\\\",0,0,0]\",\"[0,1993,0,1000,1000,1540,1600,44,11,1,1540,1600]\",\"[\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\",\\\"[-1,0,\\\\\\\"[]\\\\\\\",1744707098949]\\\"]\",\"[\\\"[0,1,0,0]\\\",\\\"[-1,0,0,0]\\\",\\\"[-1,0,0,0]\\\",\\\"[-1,0,0,0]\\\",\\\"[-1,0,0,0]\\\",\\\"[-1,0,0,0]\\\",\\\"[-1,0,0,0]\\\"]\"]', '[\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\"]', '[-1,0,0,0,20,0]', '2025-04-09 14:11:59', 'null', '[0,false,1744207919315]', '[]', '[{\"id\":\"1204\",\"amount\":\"78\",\"max\":\"120\",\"option\":[{\"id\":\"14\",\"active\":\"0\",\"param\":\"10\"}, {\"id\":\"49\",\"active\":\"1\",\"param\":\"10\"}, {\"id\":\"49\",\"active\":\"2\",\"param\":\"20\"}],\"level\":\"1\",\"used\":\"1\"},{\"id\":\"956\",\"amount\":\"80\",\"max\":\"120\",\"option\":[{\"id\":\"94\",\"active\":\"0\",\"param\":\"5\"}, {\"id\":\"77\",\"active\":\"1\",\"param\":\"5\"}, {\"id\":\"103\",\"active\":\"2\",\"param\":\"5\"}],\"level\":\"1\",\"used\":\"0\"}]', 0, '[1,1744207919315]', 0, '[false,0,false,false]', '0', '[false,0]', '[0,50000000,100,1744207919316,0]', '[false,1744632601164,false]', '[false,0,0,0]', 0, '[0,0,0,0,0,0]', '[1,false,-1,8521234,1744707358774,0,0,0,0,0]', '[-1,0,0,0,5,0]', '[0,0,false,false,false,0,0]', 16, '[\"[1794160928,false]\",\"[1794160928,false]\",\"[1,false]\",\"[0,false]\",\"[0,false]\",\"[3963,false]\",\"[28,false]\",\"[12,false]\",\"[6373000,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[7,false]\",\"[4,false]\",\"[0,false]\",\"[10,false]\",\"[1794160928,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\"]', '[]', '[{\"idBadGes\":\"218\",\"timeofUseBadges\":\"1746885963775\",\"isUse\":\"false\"},{\"idBadGes\":\"218\",\"timeofUseBadges\":\"1746885963775\",\"isUse\":\"false\"},{\"idBadGes\":\"220\",\"timeofUseBadges\":\"1746885968037\",\"isUse\":\"false\"},{\"idBadGes\":\"220\",\"timeofUseBadges\":\"1746885968037\",\"isUse\":\"false\"},{\"idBadGes\":\"243\",\"timeofUseBadges\":\"1746885981202\",\"isUse\":\"true\"},{\"idBadGes\":\"243\",\"timeofUseBadges\":\"1746885981202\",\"isUse\":\"true\"}]', '[{\"id\":\"1\",\"count\":\"1\",\"countMax\":\"1\",\"idBadgesReward\":\"218\"},{\"id\":\"2\",\"count\":\"0\",\"countMax\":\"100\",\"idBadgesReward\":\"219\"},{\"id\":\"3\",\"count\":\"0\",\"countMax\":\"300\",\"idBadgesReward\":\"220\"},{\"id\":\"4\",\"count\":\"0\",\"countMax\":\"5\",\"idBadgesReward\":\"221\"},{\"id\":\"5\",\"count\":\"0\",\"countMax\":\"1\",\"idBadgesReward\":\"222\"},{\"id\":\"6\",\"count\":\"0\",\"countMax\":\"10\",\"idBadgesReward\":\"223\"},{\"id\":\"7\",\"count\":\"0\",\"countMax\":\"20\",\"idBadgesReward\":\"-1\"},{\"id\":\"8\",\"count\":\"0\",\"countMax\":\"5\",\"idBadgesReward\":\"-1\"},{\"id\":\"9\",\"count\":\"0\",\"countMax\":\"500\",\"idBadgesReward\":\"224\"},{\"id\":\"10\",\"count\":\"0\",\"countMax\":\"30\",\"idBadgesReward\":\"225\"},{\"id\":\"11\",\"count\":\"0\",\"countMax\":\"30\",\"idBadgesReward\":\"-1\"}]', '[]', '[-1,-1,0]'),
(1001734, 1000005, 'fffsffwefe', 29, 1, 0, -1, '[2000000000,100000,0,0,0]', '[5,317,288]', '[0,2500,2500,1000,1000,100,200,10,0,0,0,102,182]', '[1,0,0,1744708175248,1744707197862]', '[\"[1,1,\\\"[\\\\\\\"[47,2]\\\\\\\"]\\\",1744707197862]\",\"[7,1,\\\"[\\\\\\\"[6,20]\\\\\\\"]\\\",1744707197862]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[12,1,\\\"[\\\\\\\"[14,1]\\\\\\\"]\\\",1744707197862]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\"]', '[\"[457,10,\\\"[\\\\\\\"[73,1]\\\\\\\"]\\\",1744707197862]\",\"[13,5,\\\"[\\\\\\\"[48,100]\\\\\\\"]\\\",1744708175242]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163601]\"]', '[\"[-1,0,\\\"[]\\\",1744708163601]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\",\"[-1,0,\\\"[]\\\",1744708163602]\"]', '[]', '[]', '[]', '[]', '[0,0,0,0,false,0,0,0]', '[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]', '[1,0,0,0]', '[]', '[1744707197863,1744707197863,1744707197863,1744707197863,1744707197863,1744707197863,1744707197863,1744707197863,1744707197863,1744707197863]', '[\"[2,1,0,0]\",\"[3,0,0,0]\",\"[7,0,0,0]\",\"[11,0,0,0]\",\"[12,0,0,0]\",\"[17,0,0,0]\",\"[18,0,0,0]\",\"[19,0,0,0]\"]', '[2,-1,-1,-1,-1,-1,-1,-1,-1,-1]', '[]', '[\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\",\"[0,0,0]\"]', '[-1,0,0,0,20,0]', '2025-04-15 08:53:17', 'null', '[0,false,1744707197888]', '[]', '[]', 0, '[0,1744707197889]', 0, '[false,0,false,false]', '0', '[false,0]', '[0,50000000,100,1744707197889,0]', '[false,0,false]', '[false,0,0,0]', 0, '[0,0,0,0,0,0]', '[0,false,-1,0,1744708200093,0,0,0,0,0]', '[-1,0,0,0,5,0]', '[0,0,false,false,false,0,0]', 17, '[\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[26000,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\",\"[0,false]\"]', '[]', '[]', '[{\"id\":\"1\",\"count\":\"0\",\"countMax\":\"1\",\"idBadgesReward\":\"218\"},{\"id\":\"2\",\"count\":\"0\",\"countMax\":\"100\",\"idBadgesReward\":\"219\"},{\"id\":\"3\",\"count\":\"0\",\"countMax\":\"300\",\"idBadgesReward\":\"220\"},{\"id\":\"4\",\"count\":\"0\",\"countMax\":\"5\",\"idBadgesReward\":\"221\"},{\"id\":\"5\",\"count\":\"0\",\"countMax\":\"1\",\"idBadgesReward\":\"222\"},{\"id\":\"6\",\"count\":\"0\",\"countMax\":\"10\",\"idBadgesReward\":\"223\"},{\"id\":\"7\",\"count\":\"0\",\"countMax\":\"20\",\"idBadgesReward\":\"-1\"},{\"id\":\"8\",\"count\":\"0\",\"countMax\":\"5\",\"idBadgesReward\":\"-1\"},{\"id\":\"9\",\"count\":\"0\",\"countMax\":\"500\",\"idBadgesReward\":\"224\"},{\"id\":\"10\",\"count\":\"0\",\"countMax\":\"30\",\"idBadgesReward\":\"225\"},{\"id\":\"11\",\"count\":\"0\",\"countMax\":\"30\",\"idBadgesReward\":\"-1\"}]', '[]', '[-1,-1,0]');

-- --------------------------------------------------------

--
-- Table structure for table `shop_ky_gui`
--

CREATE TABLE `shop_ky_gui` (
  `id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `player_name` text NOT NULL DEFAULT '',
  `tab` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `gold` int(11) NOT NULL,
  `gem` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `itemOption` varchar(10000) NOT NULL ,
  `lastTime` bigint(11) NOT NULL,
  `isBuy` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `shop_ky_gui`
--

INSERT INTO `shop_ky_gui` (`id`, `player_id`, `player_name`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `itemOption`, `lastTime`, `isBuy`) VALUES
(1, 1001730, '', 3, 13, 111, -1, 5, '[{\"id\":\"48\",\"param\":\"100\"}]', 1743501851880, 0);

-- --------------------------------------------------------

--
-- Table structure for table `super_rank`
--

CREATE TABLE `super_rank` (
  `id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `name` text NOT NULL,
  `rank` int(11) NOT NULL,
  `last_pk_time` bigint(20) NOT NULL,
  `last_reward_time` bigint(20) NOT NULL,
  `ticket` int(11) NOT NULL,
  `win` int(11) NOT NULL,
  `lose` int(11) NOT NULL,
  `history` text NOT NULL,
  `info` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `super_rank`
--

INSERT INTO `super_rank` (`id`, `player_id`, `name`, `rank`, `last_pk_time`, `last_reward_time`, `ticket`, `win`, `lose`, `history`, `info`) VALUES
(1, 1001731, 'fgsgfrgg', 13, 1743732311437, 1743732311981, 3, 0, 0, '[]', '{\"head\":29,\"def\":2,\"hp\":120,\"dame\":10,\"body\":10,\"leg\":11}'),
(14, 1001729, 'test12345', 14, 1743732327065, 1743732327077, 3, 0, 0, '[]', '{\"head\":1359,\"def\":3,\"hp\":120,\"dame\":15,\"body\":1360,\"leg\":1361}'),
(15, 1001732, 'ducrtioo', 15, 1743732388255, 1743732388267, 3, 0, 0, '[]', '{\"head\":64,\"def\":2,\"hp\":20222250,\"dame\":22222,\"body\":14,\"leg\":15}'),
(16, 1001733, 'tester', 16, 1744693443521, 1744693444048, 3, 0, 0, '[]', '{\"head\":1624,\"def\":2,\"hp\":2000000000,\"dame\":2000000000,\"body\":1628,\"leg\":1629}'),
(17, 1001734, 'fffsffwefe', 17, 1744707197893, 1744707197907, 3, 0, 0, '[]', '{\"head\":29,\"def\":2,\"hp\":120,\"dame\":10,\"body\":10,\"leg\":11}');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `clan`
--
ALTER TABLE `clan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `giftcode`
--
ALTER TABLE `giftcode`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `history_transaction`
--
ALTER TABLE `history_transaction`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `account_id` (`account_id`);

--
-- Indexes for table `shop_ky_gui`
--
ALTER TABLE `shop_ky_gui`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `super_rank`
--
ALTER TABLE `super_rank`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1000007;

--
-- AUTO_INCREMENT for table `giftcode`
--
ALTER TABLE `giftcode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `history_transaction`
--
ALTER TABLE `history_transaction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `player`
--
ALTER TABLE `player`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1001735;

--
-- AUTO_INCREMENT for table `shop_ky_gui`
--
ALTER TABLE `shop_ky_gui`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `super_rank`
--
ALTER TABLE `super_rank`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `player`
--
ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
