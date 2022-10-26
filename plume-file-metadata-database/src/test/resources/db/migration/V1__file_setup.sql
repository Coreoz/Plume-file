CREATE TABLE `plm_file`
(
    `unique_name`        varchar(255) NOT NULL,
    `file_original_name` varchar(255) NOT NULL,
    `mime_type`          varchar(255) NULL,
    `file_type`          varchar(255) NOT NULL,
    `file_extension`     varchar(255) NOT NULL,
    `file_size`          varchar(255) NULL,
    `checksum`           varchar(255) NULL,
    `creation_date`      timestamp    NOT NULL default now(),
    PRIMARY KEY (`unique_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO plm_file
VALUES ('7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1', 'original_4', 'application/pdf', 'TEST', 'pdf', 5,
        'checksum-f2801f1b9fd1', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd1', 'original_5', 'text/plain', 'TEST', 'pdf', 5,
        'checksum-f2801f1b9fd1bis', now());
INSERT INTO plm_file
VALUES ('7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2', 'original_8', 'application/pdf', 'pdf', 'TEST', 5,
        'checksum-f2801f1b9fd2', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd2', 'original_6', 'application/pdf', 'pdf', 'TEST', 5,
        'checksum-f2801f1b9fd2bis', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd3', 'original_7', 'application/pdf', 'pdf', 'TEST', 5,
        'checksum-f2801f1b9fd3', now());


CREATE TABLE `plm_user_file`
(
    `unique_name` varchar(255) NOT NULL,
    `user_id`     bigint       NOT NULL,
    CONSTRAINT plm_user_file_plm_file_unique_name_fk FOREIGN KEY (unique_name) REFERENCES plm_file (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO plm_user_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd3', 1);
