package com.coreoz.plume.file.db.dao;

import lombok.Value;

@Value
public class FileMetadata {
	private String fileType;
	private String mimeType;
}
