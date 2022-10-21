package com.coreoz.plume.file.services.metadata;

import java.util.List;
import java.util.Optional;

/**
 * Manages files metadata
 */
public interface FileMetadataService {
	void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType, Long fileSize);
	void updateFileSize(String fileUniqueName, long fileSize);
	Optional<FileMetadata> fetch(String fileUniqueName);
	List<String> findUnreferencedFiles();
	void deleteAll(List<String> fileUniqueNamesDeleted);
}
