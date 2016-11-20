package com.coreoz.plume.file.gallery.webservices.data;

import com.coreoz.plume.file.services.file.data.FileUploadBase64;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileGalleryUpload {

	private FileUploadBase64 data;
	private Integer initialPosition;

}
