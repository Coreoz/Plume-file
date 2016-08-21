package com.coreoz.plume.file.services.file;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileUploaded {

	private final long id;
	private final String url;

}
