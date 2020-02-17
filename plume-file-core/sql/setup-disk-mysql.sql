/* If saving on disk */

DROP TABLE IF EXISTS `PLM_FILE`;
DROP TABLE IF EXISTS `PLM_FILE_DISK`;

CREATE TABLE `PLM_FILE`
(
  `id`        bigint(20)   NOT NULL,
  `uid`       varchar(255) NOT NULL,
  `filename`  varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE `PLM_FILE_DISK`
(
  `id_file` bigint(20)   NOT NULL,
  `path`    VARCHAR(255) NOT NULL,
  FOREIGN KEY (id_file) REFERENCES PLM_FILE (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
