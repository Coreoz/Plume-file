package com.coreoz.plume.file.gallery.webservices.permissions;

import java.util.Collection;

import com.coreoz.plume.file.gallery.services.galleryType.FileGalleryType;
import com.coreoz.plume.file.gallery.services.galleryType.FileGalleryTypesProvider;

public interface FileGalleryTypesAdminProvider extends FileGalleryTypesProvider {

	Collection<FileGalleryTypeAdmin> fileGalleryTypesAdminAvailable();

	@Override
	default Collection<? extends FileGalleryType> fileGalleryTypesAvailable() {
		return fileGalleryTypesAdminAvailable();
	}

}
