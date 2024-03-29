CREATE TABLE plm_file
(
    unique_name        varchar(255) NOT NULL,
    file_type          varchar(255) NOT NULL,
    file_original_name varchar(255) NULL,
    file_extension     varchar(10) NULL,
    mime_type          varchar(255) NULL,
    file_size          number (19, 0) NULL,
    checksum           varchar(255) NULL,
    creation_date      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT plm_file_pk PRIMARY KEY (unique_name)
);
