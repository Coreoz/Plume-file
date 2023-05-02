# metadata database x storage database from 2.0.0-beta5 (with UID) to V3

DROP TABLE IF EXISTS `PLM_FILE_TRANSITION`;
DROP TABLE IF EXISTS `PLM_FILE_DATA_TRANSITION`;
 
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
    FOREIGN KEY (unique_name) REFERENCES PLM_FILE_TRANSITION (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO PLM_FILE_TRANSITION (unique_name, file_type, mime_type, file_size, file_original_name, file_extension, checksum, creation_date)
SELECT uid, file_type, null, null, filename, substring_index(filename, '.', -1), MD5(CONCAT(id, uid, coalesce(filename, '-'), file_type)), CURRENT_TIMESTAMP() from plm_file;

INSERT INTO PLM_FILE_DATA_TRANSITION (unique_name, data)
select uid, plm_file_data.data from plm_file
inner join plm_file_data on plm_file_data.id_file = plm_file.id;

ALTER TABLE PLM_FILE RENAME PLM_FILE_HISTORY;
ALTER TABLE PLM_FILE_TRANSITION RENAME PLM_FILE;
ALTER TABLE PLM_FILE_DATA RENAME PLM_FILE_DATA_HISTORY;
ALTER TABLE PLM_FILE_DATA_TRANSITION RENAME PLM_FILE_DATA;