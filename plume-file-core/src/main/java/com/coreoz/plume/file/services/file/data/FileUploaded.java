package com.coreoz.plume.file.services.file.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileUploaded {
	long id;
	String uniqueName;
	String url;
}
