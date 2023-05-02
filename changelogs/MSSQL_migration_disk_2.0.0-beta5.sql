-- MSSQL metadata database from 2.0.0-beta5 (with UID) to V3

DROP TABLE IF EXISTS PLM_FILE_TRANSITION;

CREATE TABLE PLM_FILE_TRANSITION (
    UNIQUE_NAME        VARCHAR(255) NOT NULL,
    FILE_TYPE          VARCHAR(255) NOT NULL,
    FILE_EXTENSION     VARCHAR(5) NULL,
    FILE_ORIGINAL_NAME VARCHAR(255) NULL,
    MIME_TYPE          VARCHAR(255) NULL,
    CHECKSUM           VARCHAR(255) NULL,
    FILE_SIZE          DECIMAL(19, 0) NULL,
    CREATION_DATE      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- check on what name your file was stored
-- this name must be reported in the column in the select below
INSERT INTO PLM_FILE_TRANSITION (unique_name, file_type, mime_type, file_size, file_original_name, file_extension, checksum, creation_date)
SELECT FILENAME, -- change here
	file_type,
        null,
       null,
       filename,
       reverse(substring(reverse(FILENAME),1,charindex('.',reverse(FILENAME))-1)),
       CONVERT(NVARCHAR(255), HashBytes('MD5', CONCAT(id, uid, coalesce(filename, '-'), file_type)), 2),
       CURRENT_TIMESTAMP
from plm_file;

EXEC sp_rename 'PLM_FILE', 'PLM_FILE_HISTORY';
EXEC sp_rename 'PLM_FILE_TRANSITION', 'PLM_FILE';
EXEC sp_rename 'PLM_FILE_DISK', 'PLM_FILE_DISK_HISTORY';