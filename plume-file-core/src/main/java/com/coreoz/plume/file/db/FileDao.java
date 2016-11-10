package com.coreoz.plume.file.db;

import java.util.List;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileDao {

	FileEntry upload(String fileType, byte[] fileData, String fileName);

	long delete(Long fileId);

	List<FileWithName> findFileNames(List<Long> fileIds);

	String fileName(Long fileId);

	FileEntry findById(Long fileId);

	Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column);

}
