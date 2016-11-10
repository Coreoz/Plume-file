package com.coreoz.plume.file.db;

public interface FileEntry {

	Long getId();
	byte[] getData();
	String getFilename();
	String getFileType();

}
