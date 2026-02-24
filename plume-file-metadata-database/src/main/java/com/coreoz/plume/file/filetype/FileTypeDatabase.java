package com.coreoz.plume.file.filetype;

import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.services.filetype.FileType;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * A file type that is stored in the database, and that has a reference to the {@link FileMetadataQuerydsl} uniqueName
 */
public interface FileTypeDatabase extends FileType {
	/**
	 * The entity that contains a column that references the {@link FileMetadataQuerydsl} uniqueName
	 */
	EntityPath<?> getFileEntity();

	/**
	 * The column that has a reference to {@link FileMetadataQuerydsl} uniqueName
	 */
	StringPath getJoinColumn();
}
