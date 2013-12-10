DROP TABLE IF EXISTS doi;
CREATE TABLE `doi` (
  `doi` varchar(100) NOT NULL,
  `articleid` bigint(20) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS keyword_id;
CREATE TABLE `keyword_id` (
  `id` bigint(20) unsigned NOT NULL,
  `keyword` varchar(500) NOT NULL,
  `orig` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS keywords_em;
CREATE TABLE `keywords_em` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `articleid` bigint(20) unsigned NOT NULL,
  `keyword` varchar(500) NOT NULL,
  `frequency` int(11) DEFAULT NULL,
  `doi` varchar(100) NOT NULL,
  `keywordid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;