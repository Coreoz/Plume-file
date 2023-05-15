package com.coreoz.plume.file.services.metadata;

import java.util.List;
import java.util.Optional;

/**
 * Manages files metadata
 */
public interface FileMetadataService {
	void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType);
	void updateFileSizeAndChecksum(String fileUniqueName, long fileSize, String checksum);
	Optional<FileMetadata> fetch(String fileUniqueName);
	List<String> findUnreferencedFiles();
	List<String> findFilesForDeletedTypes(List<String> deletedFileTypes);
	void deleteAll(List<String> fileUniqueNamesDeleted);
}
