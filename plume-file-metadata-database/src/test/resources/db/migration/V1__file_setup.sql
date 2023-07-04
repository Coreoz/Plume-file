CREATE TABLE plm_file
(
    unique_name        varchar(255)  NOT NULL,
    file_type          varchar(255)  NOT NULL,
    file_original_name varchar(255)  NULL,
    file_extension     varchar(10)   NULL,
    mime_type          varchar(255)  NULL,
    file_size          number(19, 0) NULL,
    checksum           varchar(255)  NULL,
    creation_date      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT plm_file_pk PRIMARY KEY (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO plm_file
VALUES ('7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1', 'TEST', 'original_4', 'pdf', 'application/pdf', 5,
        'checksum-f2801f1b9fd1', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd1', 'TEST', 'original_5', 'txt', 'text/plain', 5,
        'checksum-f2801f1b9fd1bis', now());
INSERT INTO plm_file
VALUES ('7b3cf3de-f973-11e8-8eb2-f2801f1b9fd2', 'TEST', 'original_8', 'pdf', 'application/pdf', 5,
        'checksum-f2801f1b9fd2', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd2', 'TEST', 'original_6', 'pdf', 'application/pdf', 5,
        'checksum-f2801f1b9fd2bis', now());
INSERT INTO plm_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd3', 'TEST', 'original_7', 'pdf', 'application/pdf', 5,
        'checksum-f2801f1b9fd3', now());
INSERT INTO plm_file
VALUES ('e868ea19-7c6d-4631-8c94-8b2883c926d3', 'TEST_DELETED', 'original_8', 'pdf',
        'application/pdf', 5, 'checksum-8b2883c926d3', now());


CREATE TABLE `plm_user_file`
(
    `unique_name` varchar(255) NOT NULL,
    `user_id`     bigint       NOT NULL,
    CONSTRAINT plm_user_file_plm_file_unique_name_fk FOREIGN KEY (unique_name) REFERENCES plm_file (unique_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO plm_user_file
VALUES ('846c36cc-f973-11e8-8eb2-f2801f1b9fd3', 1);

INSERT INTO plm_user_file
VALUES ('e868ea19-7c6d-4631-8c94-8b2883c926d3', 1);
