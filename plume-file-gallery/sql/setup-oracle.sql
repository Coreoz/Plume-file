
CREATE TABLE  plm_file_gallery (
  id_file NUMBER(19,0) NOT NULL,
  id_data NUMBER(19,0) NOT NULL,
  gallery_type varchar(255) NOT NULL,
  position NUMBER(8) NOT NULL,
  PRIMARY KEY  (id),
  CONSTRAINT plm_file_gallery FOREIGN KEY (id_file) REFERENCES plm_file (id)
);