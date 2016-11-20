package com.coreoz.plume.file.gallery.services.file;

import com.coreoz.plume.file.gallery.db.hibernate.QFileGalleryHibernate;
import com.coreoz.plume.file.services.fileType.FileType;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GalleryFileTypeHibernate implements FileType {

	PLUME_GALLERY(QFileGalleryHibernate.fileGallery, QFileGalleryHibernate.fileGallery.idFile)
	;

	private final EntityPathBase<?> fileEntity;
	private final NumberPath<Long> joinColumn;

}
