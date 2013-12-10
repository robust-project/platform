DROP TABLE IF EXISTS `cluster`;
CREATE TABLE `cluster` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_format` int(11) DEFAULT NULL,
  `cluster_index` int(11) DEFAULT NULL,
  `orig_index` int(11) DEFAULT NULL,
  `slice_id` int(11) NOT NULL,
  `flag` varchar(16) DEFAULT NULL,
  `betweenness` double DEFAULT NULL,
  `norm_betweenness` double DEFAULT NULL,
  `norm_overall_betweenness` double DEFAULT NULL,
  `begin_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `fk_cluster_sliceid` (`slice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cluster_structure`;
CREATE TABLE `cluster_structure` (
  `cluster_id` int(11) NOT NULL,
  `vertexid` bigint(20) NOT NULL,
  `clustering_coef` double DEFAULT NULL,
  `betweenness` double DEFAULT NULL,
  KEY `fk_cluster_structure_clusterid` (`cluster_id`),
  KEY `cluster_structure_authorid` (`vertexid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `network_slice`;
 CREATE TABLE `network_slice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `begin_date` date DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `network_slice_structure`;
 CREATE TABLE `network_slice_structure` (
  `slice_id` int(11) NOT NULL,
  `source` decimal(19,0) DEFAULT NULL,
  `sink` decimal(19,0) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `betweeness` double DEFAULT NULL,
  KEY `fk_network_slice_structure_sliceid` (`slice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `network_slice_vertex`;
CREATE TABLE `network_slice_vertex` (
  `slice_id` int(11) NOT NULL,
  `vertexid` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cluster_features`;
 CREATE TABLE `cluster_features` (
 `cluster_id` int(11) NOT NULL,
 `feature_type` int(11) NOT NULL,
 `feature_value` double NOT NULL
 );