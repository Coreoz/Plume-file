package com.coreoz.plume.file.services.file.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.coreoz.plume.file.services.filetype.FileType;

public interface FileMetadataService {
	long upload(String fileUniqueName, String fileType, long fileSize);
	@Deprecated
	Optional<String> fetchUniqueName(Long fileId);
	Optional<FileMetadata> fetch(String fileUniqueName);
	List<String> findUnreferencedFiles(Collection<FileType> fileTypes);
	void deleteFiles(List<String> fileUniqueNamesDeleted);
}
