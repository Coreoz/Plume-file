package com.coreoz.plume.file.gallery.services.gallery;

import java.util.Collection;

public interface FileGalleryTypesProvider {

	Collection<? extends FileGalleryType> fileGalleryTypesAvailable();

}
