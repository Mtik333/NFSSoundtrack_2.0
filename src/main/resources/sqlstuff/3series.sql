-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: vps-4a7c24ce.vps.ovh.net    Database: nfs
-- ------------------------------------------------------
-- Server version	5.5.5-10.6.12-MariaDB-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `serie`
--

DROP TABLE IF EXISTS `serie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `serie` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `position` smallint(5) unsigned NOT NULL DEFAULT 10000,
  `name` tinytext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serie`
--

LOCK TABLES `serie` WRITE;
/*!40000 ALTER TABLE `serie` DISABLE KEYS */;
INSERT INTO `serie` VALUES (1,5,'Other known games');
INSERT INTO `serie` VALUES (2,1,'Need for Speed');
INSERT INTO `serie` VALUES (3,10,'Burnout');
INSERT INTO `serie` VALUES (4,17,'Wipeout');
INSERT INTO `serie` VALUES (5,8,'Midnight Club');
INSERT INTO `serie` VALUES (6,11,'FlatOut');
INSERT INTO `serie` VALUES (7,12,'Colin McRae Rally/DiRT');
INSERT INTO `serie` VALUES (8,2,'Asphalt');
INSERT INTO `serie` VALUES (9,3,'Forza');
INSERT INTO `serie` VALUES (10,4,'Gran Turismo');
INSERT INTO `serie` VALUES (11,13,'Test Drive');
INSERT INTO `serie` VALUES (12,14,'Driver');
INSERT INTO `serie` VALUES (13,23,'GRID/TOCA');
INSERT INTO `serie` VALUES (14,18,'Carmageddon');
INSERT INTO `serie` VALUES (15,19,'Formula 1');
INSERT INTO `serie` VALUES (16,16,'Trackmania');
INSERT INTO `serie` VALUES (17,28,'MotorStorm');
INSERT INTO `serie` VALUES (18,29,'Crazy Taxi');
INSERT INTO `serie` VALUES (19,26,'Nascar');
INSERT INTO `serie` VALUES (20,22,'The Fast and the Furious');
INSERT INTO `serie` VALUES (21,32,'Ridge Racer');
INSERT INTO `serie` VALUES (22,27,'Project Gotham Racing');
INSERT INTO `serie` VALUES (24,25,'Juiced');
INSERT INTO `serie` VALUES (25,30,'Initial D');
INSERT INTO `serie` VALUES (27,7,'MX vs ATV');
INSERT INTO `serie` VALUES (28,34,'Rush');
INSERT INTO `serie` VALUES (29,50,'Ford Racing');
INSERT INTO `serie` VALUES (30,15,'Mobile games');
INSERT INTO `serie` VALUES (31,51,'Twisted Metal');
INSERT INTO `serie` VALUES (32,21,'WRC');
INSERT INTO `serie` VALUES (33,53,'Cars');
INSERT INTO `serie` VALUES (34,9,'ATV');
INSERT INTO `serie` VALUES (35,55,'BMX');
INSERT INTO `serie` VALUES (36,56,'MotoGP');
INSERT INTO `serie` VALUES (37,58,'Tokyo Xtreme Racer');
INSERT INTO `serie` VALUES (38,60,'Supercross/Motocross');
INSERT INTO `serie` VALUES (39,61,'MXGP/Ride');
INSERT INTO `serie` VALUES (40,62,'SBK');
INSERT INTO `serie` VALUES (41,59,'Wangan Midnight');
INSERT INTO `serie` VALUES (42,54,'V-Rally');
INSERT INTO `serie` VALUES (43,24,'Trials');
INSERT INTO `serie` VALUES (44,33,'Monster Jam');
INSERT INTO `serie` VALUES (45,31,'Sega');
INSERT INTO `serie` VALUES (46,35,'SSX');
INSERT INTO `serie` VALUES (47,49,'Hot Wheels');
INSERT INTO `serie` VALUES (48,63,'Extreme G');
INSERT INTO `serie` VALUES (49,65,'Road Rash');
INSERT INTO `serie` VALUES (50,66,'Madness');
INSERT INTO `serie` VALUES (51,70,'Moto Racer');
INSERT INTO `serie` VALUES (52,72,'Jet Moto');
INSERT INTO `serie` VALUES (53,73,'Screamer');
INSERT INTO `serie` VALUES (54,75,'Vector Unit');
INSERT INTO `serie` VALUES (55,20,'Project CARS');
INSERT INTO `serie` VALUES (56,77,'Alarm fur Cobra 11');
INSERT INTO `serie` VALUES (57,78,'Outrun');
INSERT INTO `serie` VALUES (58,80,'Nintendo');
INSERT INTO `serie` VALUES (59,44,'Radio-controlled cars');
INSERT INTO `serie` VALUES (60,45,'Kart racing');
INSERT INTO `serie` VALUES (61,82,'F-Zero');
INSERT INTO `serie` VALUES (62,36,'Other rally / off-road games');
INSERT INTO `serie` VALUES (63,67,'Monster Energy Supercross');
INSERT INTO `serie` VALUES (64,87,'Targem Games');
INSERT INTO `serie` VALUES (65,89,'Cancelled games');
INSERT INTO `serie` VALUES (66,37,'Other car combat games');
INSERT INTO `serie` VALUES (67,38,'Other futuristic games');
INSERT INTO `serie` VALUES (68,39,'Other plot-focused games');
INSERT INTO `serie` VALUES (69,85,'Weirdos');
INSERT INTO `serie` VALUES (70,74,'Indy car / Speedway racing');
INSERT INTO `serie` VALUES (71,90,'Movie-based games');
INSERT INTO `serie` VALUES (72,68,'Capcom');
INSERT INTO `serie` VALUES (73,40,'Other sports games');
INSERT INTO `serie` VALUES (74,64,'Top Gear');
INSERT INTO `serie` VALUES (75,43,'Other less known games');
INSERT INTO `serie` VALUES (76,81,'Cruis\'n');
INSERT INTO `serie` VALUES (77,83,'Destruction Derby');
INSERT INTO `serie` VALUES (78,84,'MTV Games');
INSERT INTO `serie` VALUES (79,69,'LEGO Racers');
INSERT INTO `serie` VALUES (80,42,'Skate');
INSERT INTO `serie` VALUES (81,41,'Tony Hawk');
INSERT INTO `serie` VALUES (82,52,'CarX');
INSERT INTO `serie` VALUES (83,79,'Davilex Racer');
INSERT INTO `serie` VALUES (84,57,'Grand Prix');
INSERT INTO `serie` VALUES (85,71,'Dakar Rally');
INSERT INTO `serie` VALUES (86,88,'Play Publishing');
INSERT INTO `serie` VALUES (87,46,'Other snowboarding games');
INSERT INTO `serie` VALUES (88,47,'Other skateboarding games');
INSERT INTO `serie` VALUES (89,6,'The Crew');
INSERT INTO `serie` VALUES (90,48,'Other simulation games');
INSERT INTO `serie` VALUES (91,76,'Gear.Club');
INSERT INTO `serie` VALUES (92,91,'Taxi');
INSERT INTO `serie` VALUES (93,86,'Other Japanese stuff');
INSERT INTO `serie` VALUES (94,100,'Burnin\' Rubber');
/*!40000 ALTER TABLE `serie` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-17 21:11:27
