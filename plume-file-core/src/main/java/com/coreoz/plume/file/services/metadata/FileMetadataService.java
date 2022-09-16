package com.coreoz.plume.file.services.metadata;

import com.coreoz.plume.file.services.filetype.FileType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileMetadataService {
	void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType, long fileSize);
	void updateFileSize(String fileUniqueName, long fileSize);
	Optional<FileMetadata> fetch(String fileUniqueName);
	List<String> findUnreferencedFiles(Collection<FileType> fileTypes);
	void deleteAll(List<String> fileUniqueNamesDeleted);
}
