DROP TABLE IF EXISTS `PLM_FILE`;

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
