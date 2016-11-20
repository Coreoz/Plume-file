package com.coreoz.plume.file.gallery.db;

import java.util.Collection;
import java.util.List;

import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

public interface FileGalleryDao {

	void insert(long idFile, String galleryType, int initialPosition, Long idData);

	void updatePositions(Collection<? extends FileGalleryPosition> newPositions);

	List<FileGalleryRaw> fetch(String galleryType, Long idData);

	boolean checkFilesInGallery(Collection<Long> fileIds, String galleryType, Long idData);

	Long deleteUnreferenced(String galleryType, EntityPath<?> fileGalleryDataEntity, NumberPath<Long> joinColumn);

	void deleteFile(Long idFile);

}
