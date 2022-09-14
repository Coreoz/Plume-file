DROP TABLE IF EXISTS `PLM_FILE`;
DROP TABLE IF EXISTS `PLM_FILE_DATA`;

CREATE TABLE `PLM_FILE`
(
    `unique_name`        VARCHAR(255)   NOT NULL,
    `file_type`          VARCHAR(255)   NOT NULL,
    `mime_type`          VARCHAR(255)   NOT NULL,
    `file_size`          DECIMAL(19, 0) NOT NULL,
    `file_original_name` VARCHAR(255)   NULL,
    `file_extension`     VARCHAR(5)     NULL,
    `creation_date`      TIMESTAMP      NOT NULL,
    PRIMARY KEY (`unique_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/* If saving on database */

CREATE TABLE `PLM_FILE_DATA`
(
    `unique_name` VARCHAR(255) NOT NULL,
    `data`        MEDIUMBLOB   NOT NULL,
    FOREIGN KEY (unique_name) REFERENCES PLM_FILE (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
