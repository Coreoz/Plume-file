package com.coreoz.plume.file.gallery.services.gallery.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileGallery {

	private final Long idFile;
	private final String fileUrl;
	private final Long idData;
	private final Integer position;

}
