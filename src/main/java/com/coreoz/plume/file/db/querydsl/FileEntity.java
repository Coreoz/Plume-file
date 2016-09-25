package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;
import com.coreoz.plume.file.db.FileEntry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "filename", "fileType" })
class FileEntity implements FileEntry, CrudEntity {

	private Long id;
	private String filename;
	private String fileType;
	private byte[] data;

}
