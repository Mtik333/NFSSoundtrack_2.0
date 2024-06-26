/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `game` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `series_id` tinyint(3) unsigned NOT NULL,
  `position` smallint(5) unsigned NOT NULL DEFAULT 0,
  `gametitle` tinytext NOT NULL,
  `displaytitle` tinytext DEFAULT NULL,
  `gameshort` tinytext NOT NULL,
  `prefix` tinytext DEFAULT NULL,
  `spotify_id` tinytext DEFAULT NULL,
  `deezer_id` tinytext DEFAULT NULL,
  `tidal_id` tinytext DEFAULT NULL,
  `soundcloud_id` tinytext DEFAULT NULL,
  `youtube_id` tinytext DEFAULT NULL,
  `game_status` enum('RELEASED','UNRELEASED','UNPLAYABLE','CANCELED') NOT NULL DEFAULT 'UNRELEASED',
  `disqus_link` tinytext DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_game_serie` (`series_id`),
  CONSTRAINT `FK_game_serie` FOREIGN KEY (`series_id`) REFERENCES `serie` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=979 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
