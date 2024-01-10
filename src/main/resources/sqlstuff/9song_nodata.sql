/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `song` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `officialdisplayband` tinytext NOT NULL,
  `officialdisplaytitle` tinytext NOT NULL,
  `src_id` varchar(11) DEFAULT NULL,
  `multi_concat` enum('MINUS','X','AND') DEFAULT NULL,
  `lyrics` text DEFAULT NULL,
  `spotify_id` tinytext DEFAULT NULL,
  `deezer_id` tinytext DEFAULT NULL,
  `itunes_link` tinytext DEFAULT NULL,
  `tidal_link` tinytext DEFAULT NULL,
  `soundcloud_link` tinytext DEFAULT NULL,
  `basesong_id` mediumint(8) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_song_song` (`basesong_id`),
  CONSTRAINT `FK_song_song` FOREIGN KEY (`basesong_id`) REFERENCES `song` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=27578 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='make song_variant later';

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
