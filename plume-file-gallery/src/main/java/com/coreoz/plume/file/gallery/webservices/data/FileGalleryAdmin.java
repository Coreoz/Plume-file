package com.coreoz.plume.file.gallery.webservices.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileGalleryAdmin {

	@JsonSerialize(using = ToStringSerializer.class)
	private final Long idFile;
	private final String fileUrl;
	@JsonSerialize(using = ToStringSerializer.class)
	private final Long idData;
	private final Integer position;

}
