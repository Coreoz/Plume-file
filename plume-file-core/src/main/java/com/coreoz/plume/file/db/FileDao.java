package com.coreoz.plume.file.db;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public interface FileDao {

	FileEntry upload(String fileType, byte[] fileData, String fileName);

	long delete(Long fileId);
//	long delete(String fileUid);

	/**
	 * Fetch the file name corresponding to the file identifier.
	 *
	 * @return The file name if the file exists and the file name is not null.
	 * If the file name is null an empty string is returned.
	 * If the file is null, null is returned
	 */
//	String fileName(Long fileId);

	// TODO Doit on laisser la première ?
	String fileName(String fileUid);

	FileEntry findById(Long fileId);
	FileEntry findByUid(String fileUid);

	Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column);

}
