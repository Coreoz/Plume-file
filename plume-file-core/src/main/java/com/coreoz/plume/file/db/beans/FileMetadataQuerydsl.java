package com.coreoz.plume.file.db.beans;

import java.time.Instant;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;
import com.querydsl.sql.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileMetadataQuerydsl implements CrudEntity {
	@Column("id")
    private Long id;
	@Column("uniqueName")
    private String uniqueName;
	@Column("fileType")
    private String fileType;
	@Column("mimeType")
    private String mimeType;
	@Column("fileSize")
    private Long fileSize;
	@Column("creationDate")
    private Instant creationDate;
}
