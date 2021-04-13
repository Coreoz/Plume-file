package com.coreoz.plume.file.gallery.db;

import com.coreoz.plume.file.gallery.db.querydsl.FileGalleryResponse;
import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.Collection;
import java.util.List;

public interface FileGalleryDao {

	void insert(long idFile, String galleryType, int initialPosition, Long idData);

	void updatePositions(Collection<? extends FileGalleryPosition> newPositions);

	List<FileGalleryResponse> fetch(String galleryType, Long idData);

	boolean checkFilesInGallery(Collection<Long> fileIds, String galleryType, Long idData);

	Long deleteUnreferenced(String galleryType, EntityPath<?> fileGalleryDataEntity, NumberPath<Long> joinColumn);

	void deleteFile(Long idFile);

}
