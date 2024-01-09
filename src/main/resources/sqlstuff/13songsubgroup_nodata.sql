/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `song_subgroup` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `song_id` mediumint(8) unsigned NOT NULL,
  `subgroup_id` mediumint(8) unsigned NOT NULL,
  `instrumental` enum('YES','NO') NOT NULL DEFAULT 'NO',
  `remix` enum('YES','NO') DEFAULT NULL,
  `src_id` varchar(11) DEFAULT NULL,
  `spotify_id` tinytext DEFAULT NULL,
  `deezer_id` tinytext DEFAULT NULL,
  `itunes_link` tinytext DEFAULT NULL,
  `tidal_link` tinytext DEFAULT NULL,
  `soundcloud_link` tinytext DEFAULT NULL,
  `ingame_display_band` tinytext DEFAULT NULL,
  `ingame_display_title` tinytext DEFAULT NULL,
  `position` int(10) unsigned DEFAULT NULL,
  `lyrics` text DEFAULT NULL,
  `info` varchar(250) DEFAULT NULL,
  `feat_title` tinyint(4) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_song_subgroup_subgroup` (`subgroup_id`),
  KEY `FK_song_subgroup_song` (`song_id`),
  CONSTRAINT `FK_song_subgroup_song` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_song_subgroup_subgroup` FOREIGN KEY (`subgroup_id`) REFERENCES `subgroup` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
