package com.coreoz.plume.file.gallery.services.gallerytype;

import com.coreoz.plume.file.gallery.db.FileGalleryRaw;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileGalleryType {

	String name();

	/**
	 * The entity that contains a column that referenced the {@link FileGalleryRaw#getIdData()}.
	 * Null should be returned if the gallery does not relate to any table.
	 */
	EntityPath<?> getFileGalleryDataEntity();

	/**
	 * The column that has a reference to the column {@link FileGalleryRaw#getIdData()}.
	 * Null should be returned if the gallery does not relate to any table.
	 */
	NumberPath<Long> getJoinColumn();

}
