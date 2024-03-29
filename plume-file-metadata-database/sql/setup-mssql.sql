CREATE TABLE PLM_FILE
(
    UNIQUE_NAME        VARCHAR(255) NOT NULL,
    FILE_TYPE          VARCHAR(255) NULL,
    FILE_EXTENSION     VARCHAR(10) NULL,
    FILE_ORIGINAL_NAME VARCHAR(255) NULL,
    MIME_TYPE          VARCHAR(255) NULL,
    CHECKSUM           VARCHAR(255) NULL,
    FILE_SIZE          DECIMAL(19, 0) NULL,
    CREATION_DATE      DATETIME DEFAULT CURRENT_TIMESTAMP
);

GO
