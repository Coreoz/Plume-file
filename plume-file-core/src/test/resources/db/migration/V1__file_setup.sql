CREATE TABLE  `plm_file` (
  `id` bigint(20) NOT NULL,
  `uid` varchat(255) NOT NULL,
  `filename` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  `data` MEDIUMBLOB NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO plm_file VALUES(4, '7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1', null, 'TEST', X'00aabb');
INSERT INTO plm_file VALUES(5, '846c36cc-f973-11e8-8eb2-f2801f1b9fd1', 'file.ext', 'TEST', X'00aabb');
