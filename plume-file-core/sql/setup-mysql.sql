DROP TABLE IF EXISTS `PLM_FILE`;
CREATE TABLE  `PLM_FILE` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;