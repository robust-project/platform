DROP TABLE IF EXISTS `header`;
CREATE TABLE `header` (
  `mailid` bigint(20) NOT NULL,
  `value` varchar(123) DEFAULT NULL,
  `name` varchar(123) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail` (
  `subject` varchar(500) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` text,
  `sentDate` date DEFAULT NULL,
  `receivedDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `receiver`;
CREATE TABLE `receiver` (
  `mailid` bigint(20) NOT NULL,
  `recipient` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sender`;
 CREATE TABLE `sender` (
  `mailid` bigint(20) NOT NULL,
  `sender` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `name` varchar(123) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;