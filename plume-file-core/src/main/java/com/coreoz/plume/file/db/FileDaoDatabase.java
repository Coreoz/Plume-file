package com.coreoz.plume.file.db;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.List;

public interface FileDaoDatabase {

	FileEntry upload(String fileType, byte[] fileData, String fileName);

	String delete(String fileUid);

	/**
	 * Fetch the file name corresponding to the file identifier.
	 *
	 * @return The file name if the file exists and the file name is not null.
	 * If the file name is null an empty string is returned.
	 * If the file is null, null is returned
	 */
	String fileName(String fileUid);

	FileEntry findByUid(String fileUid);

	FileEntry findById(Long fileId);

	List<String> deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column);

}
