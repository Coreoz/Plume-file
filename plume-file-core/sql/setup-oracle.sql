
CREATE TABLE  plm_file (
  id NUMBER(19,0) NOT NULL,
  filename varchar(255) NULL,
  file_type varchar(255) NOT NULL,
  data BLOB NOT NULL,
  PRIMARY KEY  (id)
);