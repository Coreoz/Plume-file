CREATE TABLE  `plm_file` (
  `id` bigint(20) NOT NULL,
  `uid` varchar(255) NOT NULL,
  `file_extension` varchar(255) NULL,
  `file_type` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO plm_file VALUES(4, '7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1', 'txt', 'TEST');
INSERT INTO plm_file VALUES(5, '846c36cc-f973-11e8-8eb2-f2801f1b9fd1', 'ext', 'TEST');
INSERT INTO plm_file VALUES(8, '7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2', 'ext', 'TEST');
INSERT INTO plm_file VALUES(6, '846c36cc-f973-11e8-8eb2-f2801f1b9fd2', 'ext', 'TEST');
INSERT INTO plm_file VALUES(7, '846c36cc-f973-11e8-8eb2-f2801f1b9fd3', 'ext', 'TEST');
