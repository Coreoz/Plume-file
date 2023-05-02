# metadata database from 2.0.0-beta5 (with UID) to V3

DROP TABLE IF EXISTS `PLM_FILE_TRANSITION`;
 
CREATE TABLE `PLM_FILE_TRANSITION`
(
    `unique_name`        VARCHAR(255)   NOT NULL,
    `file_type`          VARCHAR(255)   NOT NULL,
    `mime_type`          VARCHAR(255)   NULL,
    `file_size`          DECIMAL(19, 0) NULL,
    `file_original_name` VARCHAR(255)   NULL,
    `file_extension`     VARCHAR(5)     NULL,
    `checksum`           VARCHAR(255)   NULL,
    `creation_date`      TIMESTAMP      NOT NULL,
    PRIMARY KEY (`unique_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# check on what name your file was stored
# this name must be reported in the column in the select below
INSERT INTO PLM_FILE_TRANSITION (unique_name, file_type, mime_type, file_size, file_original_name, file_extension, checksum, creation_date)
SELECT uid, # change here
	file_type,
	null,
	null,
	filename,
	substring_index(filename, '.', -1),
	MD5(CONCAT(id, uid, coalesce(filename, '-'), file_type)),
	CURRENT_TIMESTAMP()
from plm_file;


ALTER TABLE PLM_FILE RENAME PLM_FILE_HISTORY;
ALTER TABLE PLM_FILE_TRANSITION RENAME PLM_FILE;
ALTER TABLE PLM_FILE_DISK RENAME PLM_FILE_DISK_HISTORY;