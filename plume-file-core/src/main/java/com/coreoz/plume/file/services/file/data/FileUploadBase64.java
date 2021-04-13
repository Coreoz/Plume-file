package com.coreoz.plume.file.services.file.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class FileUploadBase64 {
	private String filename;
	private String base64;

}
