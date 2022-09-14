package com.coreoz.plume.file.db.beans;

import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.querydsl.sql.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Setter
@Getter
@ToString
public class FileMetadataQuerydsl implements FileMetadata {
	@Column("unique_name")
    private String uniqueName;
	@Column("file_original_name")
    private String fileOriginalName;
	@Column("file_type")
    private String fileType;
	@Column("file_extension")
    private String fileExtension;
	@Column("mime_type")
    private String mimeType;
	@Column("file_size")
    private Long fileSize;
	@Column("creation_date")
    private Instant creationDate;
}
