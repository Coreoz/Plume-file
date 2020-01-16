package com.coreoz.plume.file.services.file.data;

import com.coreoz.plume.file.db.FileEntry;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileData implements FileEntry {
	private final Long id;
	private final String uid;
	private final String filename;
	private final String fileType;
	private final String mimeType;
	private final String checksum;
	private final byte[] data;
}
