CREATE TABLE  `plm_file_data` (
  `id_file` bigint(20) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  FOREIGN KEY (id_file) REFERENCES PLM_FILE(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO plm_file_data VALUES(4, X'00aabb');
INSERT INTO plm_file_data VALUES(5,  X'00aabb');
INSERT INTO plm_file_data VALUES(8,  X'00aabb');
