CREATE TABLE  `plm_file` (
  `id` bigint(20) NOT NULL,
  `filename` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO plm_file VALUES(4, null, 'TEST');
INSERT INTO plm_file VALUES(5, 'file.ext', 'TEST');
