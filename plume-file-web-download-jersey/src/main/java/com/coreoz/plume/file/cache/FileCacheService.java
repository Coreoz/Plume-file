package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.util.Optional;

public interface FileCacheService {
	Optional<byte[]> fetchFileData(String fileUniqueName);

	Optional<FileMetadata> fetchFileMetadata(String fileUniqueName);

}
