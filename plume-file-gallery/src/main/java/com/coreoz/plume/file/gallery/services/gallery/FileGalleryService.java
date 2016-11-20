package com.coreoz.plume.file.gallery.services.gallery;

import java.util.Collection;
import java.util.List;

import com.coreoz.plume.file.gallery.services.gallery.data.FileGallery;
import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;
import com.coreoz.plume.file.gallery.services.galleryType.FileGalleryType;
import com.coreoz.plume.file.services.file.data.FileUploaded;

import jersey.repackaged.com.google.common.collect.ImmutableList;

public interface FileGalleryService {

	// insert

	FileGallery add(FileUploaded fileUploaded, FileGalleryType galleryType, int initialPosition, Long idData);

	default FileGallery add(FileUploaded fileUploaded, FileGalleryType galleryType, int initialPosition) {
		return add(fileUploaded, galleryType, initialPosition, null);
	}

	default FileGallery add(FileUploaded fileUploaded, FileGalleryType galleryType) {
		return add(fileUploaded, galleryType, 0, null);
	}

	// update

	void updatePositions(Collection<? extends FileGalleryPosition> newPositions);

	// delete

	void deleteFile(Long idFile);

	// clean

	void deleteUnreferenced();

	// search

	List<FileGallery> fetch(FileGalleryType galleryType, Long idData);

	default List<FileGallery> fetch(FileGalleryType galleryType) {
		return fetch(galleryType, null);
	}

	// check

	boolean checkFilesInGallery(Collection<Long> fileIds, FileGalleryType galleryType, Long idData);

	default boolean checkFilesInGallery(Collection<Long> fileIds, FileGalleryType galleryType) {
		return checkFilesInGallery(fileIds, galleryType, null);
	}

	default boolean checkFileInGallery(Long fileId, FileGalleryType galleryType, Long idData) {
		return checkFilesInGallery(ImmutableList.of(fileId), galleryType, idData);
	}

	default boolean checkFileInGallery(Long fileId, FileGalleryType galleryType) {
		return checkFilesInGallery(ImmutableList.of(fileId), galleryType);
	}

}
