package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.crud.CrudEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(of = "uid")
@ToString(of = { "uid", "filename", "fileType" })
public class FileEntityQuerydsl implements CrudEntity {
	private Long id;
	private String uid;
	private String filename;
	private String fileType;
}
