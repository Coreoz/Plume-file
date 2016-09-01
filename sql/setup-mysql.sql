DROP TABLE IF EXISTS `plm_file`;
CREATE TABLE  `plm_file` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;