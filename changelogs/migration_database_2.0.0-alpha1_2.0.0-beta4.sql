# metadata database x storage database `2.0.0-alpha1 <= x <= 2.0.0-beta4` (without UID) to V3

DROP TABLE IF EXISTS `PLM_FILE_DATA_TRANSITION`;
DROP TABLE IF EXISTS `PLM_FILE_TRANSITION`;

# create a transition table for the metadata that will be the primary table later
CREATE TABLE `PLM_FILE_TRANSITION`
(
    `unique_name`        VARCHAR(255)   NOT NULL,
    `file_type`          VARCHAR(255)   NOT NULL,
    `mime_type`          VARCHAR(255)   NULL,
    `file_size`          DECIMAL(19, 0) NULL,
    `file_original_name` VARCHAR(255)   NULL,
    `file_extension`     VARCHAR(10)    NULL,
    `checksum`           VARCHAR(255)   NULL,
    `creation_date`      TIMESTAMP      NOT NULL,
    PRIMARY KEY (`unique_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# create a transition table that will be the primary table later
CREATE TABLE `PLM_FILE_DATA_TRANSITION`
(
    `unique_name` VARCHAR(255) NOT NULL PRIMARY KEY,
    `data`        MEDIUMBLOB   NOT NULL,
    FOREIGN KEY (unique_name) REFERENCES plm_file_transition (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# this temporary table will be used to create an UUID for every plm_file.id as there were none
CREATE TABLE `PLM_FILE_UID`
(
    `unique_name` VARCHAR(255) NOT NULL PRIMARY KEY,
    `file_id`     MEDIUMBLOB   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# creating UUIDs for every plm_file.id
INSERT INTO PLM_FILE_UID (unique_name, file_id)
SELECT uuid(), id
from plm_file;

# Inserting in the previously created metadata transition table every file that were in the database
INSERT INTO PLM_FILE_TRANSITION(unique_name,
                                file_type,
                                mime_type,
                                file_size,
                                file_original_name,
                                file_extension,
                                checksum,
                                creation_date)
# reporting the previously created UUID
SELECT PLM_FILE_UID.unique_name,
       file_type,
       null,
       null,
       filename,
       substring_index(filename, '.', -1),
       # creating a fake checksum from the filename
       SHA2(CONCAT(id, uid, coalesce(filename, '-'), file_type), 256),
       CURRENT_TIMESTAMP()
from plm_file
         inner join PLM_FILE_UID on PLM_FILE_UID.file_id = plm_file.id;

# Inserting in the previously created transition table every file data that were in the database
INSERT INTO PLM_FILE_DATA_TRANSITION (unique_name, data)
select plm_file_uid.unique_name, plm_file_data.data
from plm_file_data
         inner join plm_file_uid on plm_file_data.id_file = plm_file_uid.file_id;

# archiving old plm file table and renaming transition tables into primary tables
ALTER TABLE PLM_FILE RENAME PLM_FILE_HISTORY;
ALTER TABLE PLM_FILE_TRANSITION RENAME PLM_FILE;
ALTER TABLE PLM_FILE_DATA RENAME PLM_FILE_DATA_HISTORY;
ALTER TABLE PLM_FILE_DATA_TRANSITION RENAME PLM_FILE_DATA;
DROP TABLE PLM_FILE_UID;
