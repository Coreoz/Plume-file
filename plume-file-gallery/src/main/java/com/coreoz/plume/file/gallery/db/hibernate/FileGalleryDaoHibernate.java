package com.coreoz.plume.file.gallery.db.hibernate;

import java.util.Collection;
import java.util.List;

import javax.inject.Singleton;

import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.db.FileGalleryRaw;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryPosition;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;

@Singleton
public class FileGalleryDaoHibernate implements FileGalleryDao {

	@Override
	public void insert(long idFile, String galleryType, Integer initialPosition, Long idData) {
		// TODO to implement
	}

	@Override
	public void updatePositions(Iterable<FileGalleryPosition> newPositions) {
		// TODO to implement
	}

	@Override
	public List<FileGalleryRaw> fetch(String galleryType, Long idData) {
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

}
