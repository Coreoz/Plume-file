package com.coreoz.plume.file.services.file.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileUploaded {
	private final long id;
	private final String uid;
	private final String url;
}
