CREATE TABLE plm_file_data
(
    unique_name varchar(255) NOT NULL,
    data        BLOB         NOT NULL,
    CONSTRAINT plm_file_fk FOREIGN KEY (unique_name)
        REFERENCES plm_file (unique_name)
);
