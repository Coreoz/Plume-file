package com.coreoz.plume.file.services.file.data;

import com.coreoz.plume.file.db.FileEntry;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileData implements FileEntry {
	Long id;
	String uid;
	String filename;
	String fileType;
	String mimeType;
	String checksum;
	byte[] data;
}
