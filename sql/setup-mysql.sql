DROP TABLE IF EXISTS `plm_file`;
CREATE TABLE  `plm_file` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(255) NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;