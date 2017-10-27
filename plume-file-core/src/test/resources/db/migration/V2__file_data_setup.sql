CREATE TABLE  `plm_file_data` (
  `id` bigint(20) NOT NULL,
  `id_file` bigint(20) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`),
  FOREIGN KEY (id_file) REFERENCES PLM_FILE(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO plm_file_data VALUES(1, 4, X'00aabb');
INSERT INTO plm_file_data VALUES(2, 5,  X'00aabb');
