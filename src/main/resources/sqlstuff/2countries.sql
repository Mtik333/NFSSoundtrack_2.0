/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `country` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `countryname` varchar(40) NOT NULL,
  `countrylink` varchar(200) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(1, 'England', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Flag_of_England.svg/34px-Flag_of_England.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(2, 'Australia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Flag_of_Australia.svg/34px-Flag_of_Australia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(3, 'Canada', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Flag_of_Canada.svg/34px-Flag_of_Canada.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(4, 'Sweden', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Flag_of_Sweden.svg/34px-Flag_of_Sweden.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(5, 'United States', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Flag_of_the_United_States.svg/34px-Flag_of_the_United_States.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(6, 'France', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Flag_of_France.svg/34px-Flag_of_France.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(7, 'Scotland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Flag_of_Scotland.svg/34px-Flag_of_Scotland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(8, 'Chile', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Flag_of_Chile.svg/34px-Flag_of_Chile.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(9, 'Denmark', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Flag_of_Denmark.svg/34px-Flag_of_Denmark.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(10, 'Italy', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Flag_of_Italy.svg/34px-Flag_of_Italy.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(11, 'Brazil', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Flag_of_Brazil.svg/34px-Flag_of_Brazil.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(12, 'Portugal', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Flag_of_Portugal.svg/34px-Flag_of_Portugal.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(13, 'Poland', 'https://upload.wikimedia.org/wikipedia/en/thumb/1/12/Flag_of_Poland.svg/34px-Flag_of_Poland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(14, 'Spain', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Spain.svg/34px-Flag_of_Spain.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(15, 'Ukraine', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/Flag_of_Ukraine.svg/34px-Flag_of_Ukraine.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(16, 'Japan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Flag_of_Japan.svg/34px-Flag_of_Japan.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(17, 'Germany', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Flag_of_Germany.svg/34px-Flag_of_Germany.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(18, 'Mexico', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Flag_of_Mexico.svg/34px-Flag_of_Mexico.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(19, 'Cuba', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Flag_of_Cuba.svg/34px-Flag_of_Cuba.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(20, 'Netherlands', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Flag_of_the_Netherlands.svg/34px-Flag_of_the_Netherlands.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(21, 'Hungary', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Flag_of_Hungary.svg/34px-Flag_of_Hungary.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(22, 'Norway', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Norway.svg/34px-Flag_of_Norway.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(23, 'Wales', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/Flag_of_Wales.svg/34px-Flag_of_Wales.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(24, 'Greece', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Flag_of_Greece.svg/34px-Flag_of_Greece.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(25, 'Austria', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Flag_of_Austria.svg/34px-Flag_of_Austria.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(26, 'Switzerland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Flag_of_Switzerland.svg/34px-Flag_of_Switzerland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(27, 'South Africa', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Flag_of_South_Africa.svg/34px-Flag_of_South_Africa.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(28, 'Northern Ireland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Flag_of_Northern_Ireland_(1953%E2%80%931972).svg/34px-Flag_of_Northern_Ireland_(1953%E2%80%931972).svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(29, 'New Zealand', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Flag_of_New_Zealand.svg/34px-Flag_of_New_Zealand.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(30, 'Finland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Flag_of_Finland.svg/34px-Flag_of_Finland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(31, 'Estonia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Flag_of_Estonia.svg/34px-Flag_of_Estonia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(32, 'China', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/34px-Flag_of_the_People%27s_Republic_of_China.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(33, 'Russia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Flag_of_Russia.svg/34px-Flag_of_Russia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(34, 'Czech Republic', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Flag_of_the_Czech_Republic.svg/34px-Flag_of_the_Czech_Republic.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(35, 'Jamaica', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Flag_of_Jamaica.svg/34px-Flag_of_Jamaica.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(36, 'Belgium', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Flag_of_Belgium.svg/34px-Flag_of_Belgium.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(37, 'Argentina', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Flag_of_Argentina.svg/34px-Flag_of_Argentina.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(38, 'Iran', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Flag_of_Iran.svg/34px-Flag_of_Iran.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(39, 'Tanzania', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/Flag_of_Tanzania.svg/34px-Flag_of_Tanzania.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(40, 'United Kingdom', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Flag_of_the_United_Kingdom.svg/34px-Flag_of_the_United_Kingdom.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(41, 'Ireland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Flag_of_Ireland.svg/34px-Flag_of_Ireland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(42, 'Israel', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Flag_of_Israel.svg/34px-Flag_of_Israel.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(43, 'Tunisia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/Flag_of_Tunisia.svg/34px-Flag_of_Tunisia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(44, 'India', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Flag_of_India.svg/34px-Flag_of_India.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(45, 'Iceland', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/Flag_of_Iceland.svg/34px-Flag_of_Iceland.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(46, 'Angola', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Flag_of_Angola.svg/34px-Flag_of_Angola.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(47, 'Puerto Rico', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Flag_of_Puerto_Rico.svg/34px-Flag_of_Puerto_Rico.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(48, 'South Korea', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Flag_of_South_Korea.svg/34px-Flag_of_South_Korea.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(49, 'Colombia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/21/Flag_of_Colombia.svg/34px-Flag_of_Colombia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(50, 'Ethiopia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Flag_of_Ethiopia.svg/34px-Flag_of_Ethiopia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(51, 'Dominican Republic', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Flag_of_the_Dominican_Republic.svg/34px-Flag_of_the_Dominican_Republic.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(52, 'Faroe Islands', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Flag_of_the_Faroe_Islands.svg/34px-Flag_of_the_Faroe_Islands.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(53, 'Belarus', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/Flag_of_Belarus.svg/34px-Flag_of_Belarus.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(54, 'Panama', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Flag_of_Panama.svg/34px-Flag_of_Panama.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(55, 'United Arab Emirates', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Flag_of_the_United_Arab_Emirates.svg/34px-Flag_of_the_United_Arab_Emirates.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(56, 'Romania', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Flag_of_Romania.svg/34px-Flag_of_Romania.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(57, 'Serbia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Flag_of_Serbia.svg/34px-Flag_of_Serbia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(58, 'Latvia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Flag_of_Latvia.svg/34px-Flag_of_Latvia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(59, 'Belize', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Flag_of_Belize.svg/34px-Flag_of_Belize.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(60, 'Uruguay', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Flag_of_Uruguay.svg/34px-Flag_of_Uruguay.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(61, 'Lithuania', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Flag_of_Lithuania.svg/34px-Flag_of_Lithuania.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(62, 'Indonesia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Flag_of_Indonesia.svg/34px-Flag_of_Indonesia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(63, 'Nigeria', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Flag_of_Nigeria.svg/34px-Flag_of_Nigeria.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(64, 'Liberia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Flag_of_Liberia.svg/34px-Flag_of_Liberia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(65, 'Singapore', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/Flag_of_Singapore.svg/34px-Flag_of_Singapore.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(66, 'Zambia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Flag_of_Zambia.svg/34px-Flag_of_Zambia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(67, 'Bahamas', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/93/Flag_of_the_Bahamas.svg/34px-Flag_of_the_Bahamas.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(68, 'Peru', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Flag_of_Peru.svg/34px-Flag_of_Peru.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(69, 'Croatia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Flag_of_Croatia.svg/34px-Flag_of_Croatia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(70, 'Slovakia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Flag_of_Slovakia.svg/34px-Flag_of_Slovakia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(71, 'Thailand', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Flag_of_Thailand.svg/34px-Flag_of_Thailand.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(72, 'Slovenia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f0/Flag_of_Slovenia.svg/34px-Flag_of_Slovenia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(73, 'Kenya', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/Flag_of_Kenya.svg/34px-Flag_of_Kenya.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(74, 'Turkey', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Flag_of_Turkey.svg/34px-Flag_of_Turkey.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(75, 'Salvador', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Flag_of_El_Salvador.svg/34px-Flag_of_El_Salvador.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(76, 'Vietnam', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/21/Flag_of_Vietnam.svg/34px-Flag_of_Vietnam.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(77, 'Qatar', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/Flag_of_Qatar.svg/34px-Flag_of_Qatar.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(78, 'Taiwan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Flag_of_the_Republic_of_China.svg/34px-Flag_of_the_Republic_of_China.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(79, 'Sierra Leone', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Flag_of_Sierra_Leone.svg/34px-Flag_of_Sierra_Leone.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(80, 'Venezuela', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Flag_of_Venezuela.svg/34px-Flag_of_Venezuela.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(81, 'Macedonia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Flag_of_North_Macedonia.svg/34px-Flag_of_North_Macedonia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(82, 'Bosnia and Herzegovina', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/Flag_of_Bosnia_and_Herzegovina.svg/34px-Flag_of_Bosnia_and_Herzegovina.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(83, 'DR Congo', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Flag_of_the_Democratic_Republic_of_the_Congo.svg/34px-Flag_of_the_Democratic_Republic_of_the_Congo.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(84, 'Uganda', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Flag_of_Uganda.svg/34px-Flag_of_Uganda.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(85, 'Zimbabwe', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Flag_of_Zimbabwe.svg/34px-Flag_of_Zimbabwe.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(86, 'Saudi Arabia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0d/Flag_of_Saudi_Arabia.svg/34px-Flag_of_Saudi_Arabia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(87, 'Egypt', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Flag_of_Egypt.svg/34px-Flag_of_Egypt.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(88, 'Palestine', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Flag_of_Palestine.svg/34px-Flag_of_Palestine.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(89, 'Algeria', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Flag_of_Algeria.svg/22px-Flag_of_Algeria.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(90, 'Ecuador', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e8/Flag_of_Ecuador.svg/22px-Flag_of_Ecuador.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(91, 'Malaysia', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/Flag_of_Malaysia.svg/22px-Flag_of_Malaysia.svg.png');
INSERT INTO `country` (`id`, `countryname`, `countrylink`) VALUES
	(92, 'Philippines', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/99/Flag_of_the_Philippines.svg/22px-Flag_of_the_Philippines.svg.png');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
