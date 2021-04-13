package com.coreoz.plume.file.db;

public interface FileEntry {
	Long getId();
	String getUid();
	String getFileName();
	String getFileExtension();
	String getFileType();
}
