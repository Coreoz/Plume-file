
CREATE TABLE  plm_file (
  id NUMBER(19,0) NOT NULL,
  filename varchar(255) NULL,
  file_type varchar(255) NOT NULL,
  CONSTRAINT plm_file_pk PRIMARY KEY  (id)
);

CREATE TABLE  plm_file_data (
  id NUMBER(19,0) NOT NULL,
  id_file NUMBER(19,0) NOT NULL,
  data BLOB NOT NULL,
  CONSTRAINT plm_file_pk PRIMARY KEY  (id),
  CONSTRAINT plm_file_fk FOREIGN KEY (id_file)
  REFERENCES plm_file (id)
);
