DROP TABLE IF EXISTS `PLM_FILE`;
DROP TABLE IF EXISTS `PLM_FILE_DATA`;

CREATE TABLE `PLM_FILE`
(
  `id`        bigint(20)   NOT NULL,
  `uid`       varchar(255) NOT NULL,
  `file_extension`  varchar(5) NULL,
  `file_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

/* If saving on database */

CREATE TABLE `PLM_FILE_DATA`
(
  `id_file` bigint(20) NOT NULL,
  `data`    MEDIUMBLOB NOT NULL,
  FOREIGN KEY (id_file) REFERENCES PLM_FILE (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
