CREATE TABLE PLM_FILE_DATA
(
  UNIQUE_NAME VARCHAR(255) PRIMARY KEY,
  DATA    VARBINARY(MAX) NOT NULL,
  CONSTRAINT PLM_FILE_FK FOREIGN KEY (UNIQUE_NAME)
  REFERENCES PLM_FILE(UNIQUE_NAME)
);

GO
