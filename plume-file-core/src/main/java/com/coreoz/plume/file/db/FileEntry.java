package com.coreoz.plume.file.db;

public interface FileEntry {
	Long getId();
	String getUid();
	byte[] getData();
	String getFilename();
	String getFileType();
}
