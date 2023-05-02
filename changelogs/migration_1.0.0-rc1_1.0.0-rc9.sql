# metadata database x storage database from `1.0.0-rc1 <= x <= 1.0.0-rc9` (one plm_file table) to V3

DROP TABLE IF EXISTS `PLM_FILE_DATA_TRANSITION`;
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

CREATE TABLE `PLM_FILE_DATA_TRANSITION`
(
    `unique_name` VARCHAR(255) NOT NULL PRIMARY KEY,
    `data`        MEDIUMBLOB   NOT NULL,
    FOREIGN KEY (unique_name) REFERENCES plm_file_transition (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `PLM_FILE_UID`
(
    `unique_name` VARCHAR(255) NOT NULL PRIMARY KEY,
    `file_id`        MEDIUMBLOB   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO PLM_FILE_UID (unique_name, file_id) SELECT uuid(), id from plm_file;

INSERT INTO PLM_FILE_TRANSITION (unique_name, file_type, mime_type, file_size, file_original_name, file_extension, checksum, creation_date)
SELECT PLM_FILE_UID.unique_name, file_type, null, null, filename, substring_index(filename, '.', -1), MD5(CONCAT(id, uid, coalesce(filename, '-'), file_type)), CURRENT_TIMESTAMP()
from plm_file
inner join PLM_FILE_UID on PLM_FILE_UID.file_id = plm_file.id;

INSERT INTO PLM_FILE_DATA_TRANSITION (unique_name, data)
select plm_file_uid.unique_name, plm_file.data
from plm_file
inner join plm_file_uid on plm_file.id = plm_file_uid.file_id;

ALTER TABLE PLM_FILE_DATA_TRANSITION RENAME PLM_FILE_DATA;
ALTER TABLE PLM_FILE RENAME PLM_FILE_HISTORY;
ALTER TABLE PLM_FILE_TRANSITION RENAME PLM_FILE;
DROP TABLE PLM_FILE_UID;
