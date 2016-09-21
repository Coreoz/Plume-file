package com.coreoz.plume.file.db.daos;

import java.util.List;

import com.coreoz.plume.file.db.generated.File;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPathBase;

public interface FileDao {

	File upload(String fileType, byte[] fileData, String fileName);

	long delete(Long fileId);

	List<FileWithName> findFileNames(List<Long> fileIds);

	String fileName(Long fileId);

	File findById(Long fileId);

	Long deleteUnreferenced(String fileType, RelationalPathBase<?> relationalPathBase, NumberPath<Long> column);

}
