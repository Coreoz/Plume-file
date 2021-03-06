package com.coreoz.plume.file.db;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileDao {

	FileEntry upload(String fileType, byte[] fileData, String fileName);

	long delete(Long fileId);

	/**
	 * Fetch the file name corresponding to the file identifier.
	 *
	 * @return The file name if the file exists and the file name is not null.
	 * If the file name is null an empty string is returned.
	 * If the file is null, null is returned
	 */
	String fileName(Long fileId);

	FileEntry findById(Long fileId);

	Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column);

}
