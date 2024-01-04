/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `content` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `method` tinyint(4) DEFAULT NULL,
  `pos` tinyint(4) DEFAULT NULL,
  `visibility` tinyint(4) DEFAULT NULL,
  `content_title` text DEFAULT NULL,
  `content_short` text DEFAULT NULL,
  `content_data` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `content` (`id`, `method`, `pos`, `visibility`, `content_title`, `content_short`, `content_data`) VALUES
	(1, 1, 1, 1, 'Greetings', 'home', 'Welcome to NFSSoundtrack! Here you will find almost all songs from Need For Speed, Burnout and other series. Just click one of the buttons on left side to get a list of songs.\r\n <a href="#Disqus">You can share your opinions about the site under Discord window.</a>\r\n <br/>\r\n <div align="center">\r\n <iframe src="https://discordapp.com/widget?id=627557502237671425&theme=dark" width="650" height="320" allowtransparency="true" frameborder="0"></iframe>\r\n </div>\r\n </br>\r\n <div class="socialplguin">\r\n <div class="fb-page" data-href="https://www.facebook.com/nfssoundtrack" data-small-header="true data-hide-cover="false" data-show-facepile="false" data-adapt-container-width="true" data-width="500" data-show-posts="true"><div class="fb-xfbml-parse-ignore"></div></div></div>\r\n </br>\r\n \r\n <a name="Disqus">\r\n  <div id="disqus_thread"></div>\r\n     <script type="text/javascript">\r\n         /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */\r\n         var disqus_shortname = \'nfssoundtrack\'; // required: replace example with your forum shortname\r\n \r\n \r\n         (function () { // DON\'T EDIT BELOW THIS LINE\r\n                                        var d = document, s = d.createElement(\'script\');\r\n                                        s.src = \'https://nfssoundtrack.disqus.com/embed.js\';\r\n                                        s.setAttribute(\'data-timestamp\', +new Date());\r\n                                        (d.head || d.body).appendChild(s);\r\n                                    })();\r\n                                </script>\r\n                                <noscript>Please enable JavaScript to view the <a\r\n                                        href="https://disqus.com/?ref_noscript">comments powered by\r\n                                        Disqus.</a></noscript>\r\n ');
INSERT INTO `content` (`id`, `method`, `pos`, `visibility`, `content_title`, `content_short`, `content_data`) VALUES
	(2, 1, 1, 1, 'About us', 'aboutus', '<div id="info-div"> NFSSoundtrack isn`t an official site for Need For Speed soundtracks, but only a fan-made page. The site has started in July 2012. The idea is to group all soundtracks from racing games in one place, where everybody can listen to his/her favorite songs from those games, without searching them at YouTube. <br/>\r\n It is currently developed by two bored students: Mateusz Walendziuk, and someone else.<br/>If you have any questions, please contact us to email listed below.<br/>Contact: <a href="mailto:help@nfssoundtrack.com">help@nfssoundtrack.com</a></br>Emails are usually answered within 24 hours.<br/><br/>\r\n Thanks to <a href="http://www.youtube.com/user/BVB1992PAO" title="BVB1992PAO @ YouTube"> BVB1992PAO</a> and <a href="http://www.youtube.com/user/pankeiks7777" title="pankeiks7777 @ YouTube">pankeiks7777</a>  for soundtrack videos.</div>\r\n \r\n \r\n ');
INSERT INTO `content` (`id`, `method`, `pos`, `visibility`, `content_title`, `content_short`, `content_data`) VALUES
	(3, 1, 1, 1, 'Donate', 'donate', '<div id="info-div"> NFSSoundtrack is a fan-made website. If you appreciate our work, you can donate by clicking button below.\r\n 				</br></br>\r\n <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">\r\n <input type="hidden" name="cmd" value="_s-xclick">\r\n <input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----\r\n ">\r\n <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">\r\n <img alt="" border="0" src="https://www.paypalobjects.com/pl_PL/i/scr/pixel.gif" width="1" height="1">\r\n </form>\r\n </div>');
INSERT INTO `content` (`id`, `method`, `pos`, `visibility`, `content_title`, `content_short`, `content_data`) VALUES
	(4, 1, 1, 1, 'Cookies', 'cookies', '<h2>What Are Cookies</h2>\n<p>As is common practice with almost all professional websites this site uses cookies, which are tiny files that are downloaded to your computer, to improve your experience. This page describes what information they gather, how we use it and why we sometimes need to store these cookies. We will also share how you can prevent these cookies from being stored however this may downgrade or \'break\' certain elements of the sites functionality.</p>\n\n<p>For more general information on cookies see the <strong><a href="https://en.wikipedia.org/wiki/HTTP_cookie" target="_blank">Wikipedia article on HTTP Cookies.  <img src="https://nfssoundtrack.com/images/spacer.gif" width="1" height="1" class="external_icon"></a></strong></p>\n<h2>How We Use Cookies</h2>\n<p>We use cookies for a variety of reasons detailed below. Unfortunately in most cases there are no industry standard options for disabling cookies without completely disabling the functionality and features they add to this site. It is recommended that you leave on all cookies if you are not sure whether you need them or not in case they are used to provide a service that you use.</p>\n\n<h2>Disabling Cookies</h2>\n<p>You can prevent the setting of cookies by adjusting the settings on your browser (see your browser Help for how to do this). Be aware that disabling cookies will affect the functionality of this and many other websites that you visit. Disabling cookies will usually result in also disabling certain functionality and features of the this site.</p>\n\n<h2>Third Party Cookies</h2>\n<p>In some special cases we also use cookies provided by trusted third parties. The following section details which third party cookies you might encounter through this site.</p>\n\n<p>This site uses Google Analytics which is one of the most widespread and trusted analytics solution on the web for helping us to understand how you use the site and ways that we can improve your experience. These cookies may track things such as how long you spend on the site and the pages that you visit so we can continue to produce engaging content.</p>\n\n<p>For more information on Google Analytics cookies, see the <strong><a href="https://support.google.com/analytics/answer/6004245" target="_blank">official Google Analytics page. <img src="https://nfssoundtrack.com/images/spacer.gif" width="1" height="1" class="external_icon"></a></strong></p>\n\n<p>The Google AdSense service we use to serve advertising uses a DoubleClick cookie to serve more relevant ads across the web and limit the number of times that a given ad is shown to you.</p>\n\n<p>For more information on Google AdSense see the <strong><a href="https://www.google.com/policies/technologies/ads/" target="_blank">official Google AdSense privacy FAQ. <img src="https://nfssoundtrack.com/images/spacer.gif" width="1" height="1" class="external_icon"></a></strong></p>\n\n<p>You can read more about how Google uses data when you use their partners\' sites or apps by clicking <strong><a href="https://www.google.com/intl/en/policies/privacy/partners/" target="_blank">here. <img src="https://nfssoundtrack.com/images/spacer.gif" alt="" width="1" height="1" class="external_icon"></a></strong></p>\n\n\n<p>We also use social media buttons and/or plugins on this site that allow you to connect with your social network in various ways. For these to work the following social media sites including; Facebook, will set cookies through our site which may be used to enhance your profile on their site or contribute to the data they hold for various purposes outlined in their respective privacy policies.</p>\n<p><strong><a href="https://help.disqus.com/customer/portal/articles/466235-use-of-cookies" target="_blank">Click here <img src="https://nfssoundtrack.com/images/spacer.gif" alt="" width="1" height="1" class="external_icon"></a></strong> to read how Disqus use cookies.</p>\n<p><strong><a href="https://www.facebook.com/help/cookies/" target="_blank">Click here <img src="https://nfssoundtrack.com/images/spacer.gif" alt="" width="1" height="1" class="external_icon"></a></strong> to read how Facebook use cookies.</p>\n\n\n<h2>Cookie we set</h2>\n<p>In addition to cookies set by by third party services, we set our persistent cookie named <strong>COOKIEINFO_SHOWN3</strong> to determine if you accepted our cookie policy. This cookie is set when you click on "I decline" (then it sets value to "NO") or "I accept" (then it sets value to "YES") button in cookie information alert and will persist for 1 month).\n<strong>ADP3</strong> to determine your ad personalisation setting.\n<strong>STATS3</strong> to determine your statistics setting.\n<br> We also set cookie PAGETHEME to determine which page theme you have chosen. </p>\n\n<h2>More Information</h2>\n<p>Hopefully that has clarified things for you and as was previously mentioned if there is something that you aren\'t sure whether you need or not it\'s usually safer to leave cookies enabled in case it does interact with one of the features you use on our site. However if you are still looking for more information then you can contact us through one of our preferred contact methods.</p>\n\n<p>Email: help@nfssoundtrack.com</p>\n');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

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
  PRIMARY KEY (`id`),
  KEY `FK_game_serie` (`series_id`),
  CONSTRAINT `FK_game_serie` FOREIGN KEY (`series_id`) REFERENCES `serie` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `author` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` tinytext NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `author_alias` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `author_id` mediumint(8) unsigned NOT NULL,
  `alias` tinytext NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_author_alias_author` (`author_id`),
  CONSTRAINT `FK_author_alias_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `author_country` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `author_id` mediumint(8) unsigned NOT NULL,
  `country_id` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_author_country_country` (`country_id`),
  KEY `FK_author_country_author` (`author_id`),
  CONSTRAINT `FK_author_country_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_author_country_country` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `maingroup` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `groupname` tinytext DEFAULT NULL,
  `game_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_maingroup_game` (`game_id`),
  CONSTRAINT `FK_maingroup_game` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='make song_variant later';

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `genre` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `genre_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Indeks 2` (`genre_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `song_genre` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `song_id` mediumint(8) unsigned NOT NULL,
  `genre_id` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_song_genre_genre` (`genre_id`),
  KEY `FK_song_genre_song` (`song_id`),
  CONSTRAINT `FK_song_genre_genre` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_song_genre_song` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `subgroup` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `subgroup_name` tinytext NOT NULL,
  `group_id` smallint(5) unsigned NOT NULL,
  `position` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_subgroup_maingroup` (`group_id`),
  CONSTRAINT `FK_subgroup_maingroup` FOREIGN KEY (`group_id`) REFERENCES `maingroup` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

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
  `position` smallint(5) unsigned DEFAULT NULL,
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

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `author_song` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `alias_id` mediumint(8) unsigned NOT NULL,
  `song_id` mediumint(8) unsigned NOT NULL,
  `role` enum('COMPOSER','SUBCOMPOSER','FEAT','REMIX') NOT NULL DEFAULT 'COMPOSER',
  `remix_concat` tinytext DEFAULT NULL,
  `feat_concat` tinytext DEFAULT NULL,
  `subcomposer_concat` tinytext DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_author_song_song` (`song_id`),
  KEY `FK_author_song_author_alias` (`alias_id`),
  CONSTRAINT `FK_author_song_author_alias` FOREIGN KEY (`alias_id`) REFERENCES `author_alias` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_author_song_song` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

UPDATE game SET game_status = 'RELEASED';

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE IF NOT EXISTS `user` (
  `id` smallint(5) unsigned NOT NULL DEFAULT 0,
  `login` varchar(100) NOT NULL,
  `pass` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Indeks 2` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `user` (`id`, `login`, `pass`) VALUES
	(1, 'NFSManiak', '$2a$12$0VT2kiCnZRBMN3e7LxWbh.0q2Ps11B5HzLQxZOGYSGKXd/of0NE32');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
