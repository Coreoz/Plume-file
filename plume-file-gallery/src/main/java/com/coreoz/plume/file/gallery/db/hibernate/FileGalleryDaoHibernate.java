package com.coreoz.plume.file.gallery.db.hibernate;

import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.db.querydsl.FileGalleryResponse;
import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Singleton
public class FileGalleryDaoHibernate implements FileGalleryDao {

	@Override
	public void insert(long idFile, String galleryType, int initialPosition, Long idData) {
		// TODO to implement
	}

	@Override
	public void updatePositions(Collection<? extends FileGalleryPosition> newPositions) {
		// TODO to implement
	}

	@Override
	public List<FileGalleryResponse> fetch(String galleryType, Long idData) {
		// TODO to implement
		return null;
	}

	@Override
	public boolean checkFilesInGallery(Collection<Long> fileIds, String galleryType, Long idData) {
		// TODO to implement
		return false;
	}

	@Override
	public Long deleteUnreferenced(String galleryType, EntityPath<?> fileGalleryDataEntity,
			NumberPath<Long> joinColumn) {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteFile(Long idFile) {
		// TODO to implement
	}

}
