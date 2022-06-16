package com.coreoz.plume.file.services.file.metadata;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.services.filetype.FileType;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileTypeDatabase extends FileType {
	/**
	 * The entity that contains a column that referenced the {@link FileEntry} ID
	 */
	EntityPath<?> getFileEntity();

	/**
	 * The column that has a reference to {@link FileEntry} ID
	 */
	NumberPath<Long> getJoinColumn();
}
