package com.coreoz.plume.file.gallery.services.file;

import com.coreoz.plume.file.gallery.db.querydsl.QFileGallerydsl;
import com.coreoz.plume.file.services.fileType.FileType;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPathBase;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GalleryFileTypeQuerydsl implements FileType {

	PLUME_GALLERY(QFileGallerydsl.fileGallery, QFileGallerydsl.fileGallery.idFile)
	;

	private final RelationalPathBase<?> fileEntity;
	private final NumberPath<Long> joinColumn;

}
