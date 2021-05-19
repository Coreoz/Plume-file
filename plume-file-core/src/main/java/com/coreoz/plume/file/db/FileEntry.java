package com.coreoz.plume.file.db;

public interface FileEntry {
	Long getId();
	String getUuid();
	String getFileName();
	String getFileExtension();
	String getFileType();
}
