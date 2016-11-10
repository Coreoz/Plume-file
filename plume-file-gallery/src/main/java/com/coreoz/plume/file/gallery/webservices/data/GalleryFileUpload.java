package com.coreoz.plume.file.gallery.webservices.data;

import com.coreoz.plume.file.services.file.FileUploadBase64;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GalleryFileUpload {

	private FileUploadBase64 data;
	private String galleryType;
	private Integer initialPosition;
	private Long idData;

}
