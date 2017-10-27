DROP TABLE IF EXISTS `PLM_FILE`;
DROP TABLE IF EXISTS `PLM_FILE_DATA`;
CREATE TABLE  `PLM_FILE` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `PLM_FILE_DATA` (
  `id` bigint(20) NOT NULL,
  `id_file` bigint(20) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`),
  FOREIGN KEY (id_file) REFERENCES PLM_FILE(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
