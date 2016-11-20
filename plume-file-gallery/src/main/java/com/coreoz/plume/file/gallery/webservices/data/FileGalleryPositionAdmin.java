package com.coreoz.plume.file.gallery.webservices.data;

import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileGalleryPositionAdmin implements FileGalleryPosition {

	private Long idFile;
	private Long idData;
	private Integer position;

}
