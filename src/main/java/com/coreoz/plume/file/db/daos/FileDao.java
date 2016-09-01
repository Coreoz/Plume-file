package com.coreoz.plume.file.db.daos;

import java.util.List;

import com.coreoz.plume.file.db.entities.FileEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileDao {

	FileEntity upload(String fileType, byte[] fileData, String fileName);

	void delete(Long fileId);

	List<FileWithName> findFileNames(List<Long> fileIds);

	String fileName(Long fileId);

	FileEntity findById(Long fileId);

	Long deleteUnreferenced(String fileType, EntityPathBase<?> fileEntity, NumberPath<Long> column);

}
