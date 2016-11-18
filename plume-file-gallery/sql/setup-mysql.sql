DROP TABLE IF EXISTS `plm_file_gallery`;
CREATE TABLE  `plm_file_gallery` (
  `id_file` bigint(20) NOT NULL,
  `id_data` bigint(20) NULL,
  `gallery_type` varchar(255) NOT NULL,
  `position` int(11) NOT NULL,
  PRIMARY KEY  (`id_file`),
  CONSTRAINT `plm_file_gallery` FOREIGN KEY (`id_file`) REFERENCES `plm_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;