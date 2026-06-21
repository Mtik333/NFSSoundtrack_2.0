CREATE TABLE IF NOT EXISTS `game_playlist` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `game_id` int unsigned NOT NULL,
  `platform` enum('SPOTIFY','DEEZER','TIDAL','YOUTUBE','SOUNDCLOUD','APPLE_MUSIC') NOT NULL,
  `playlist_url` varchar(500) NOT NULL,
  `label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_game_playlist_game` (`game_id`),
  CONSTRAINT `FK_game_playlist_game` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
