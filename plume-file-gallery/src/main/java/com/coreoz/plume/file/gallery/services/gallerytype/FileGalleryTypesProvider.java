package com.coreoz.plume.file.gallery.services.gallerytype;

import java.util.Collection;

public interface FileGalleryTypesProvider {

	Collection<? extends FileGalleryType> fileGalleryTypesAvailable();

}
