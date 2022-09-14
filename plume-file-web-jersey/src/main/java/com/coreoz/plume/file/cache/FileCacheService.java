package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.util.Optional;
import java.util.function.Function;

public interface FileCacheService {

	void initializeFileDataCache(Function<String, Optional<byte[]>> fileDataLoader);

	void initializeFileMetadataCache(Function<String, Optional<FileMetadata>> fileMetadataLoader);

	Optional<byte[]> fetchFileData(String fileUniqueName);

	Optional<FileMetadata> fetchFileMetadata(String fileUniqueName);

}
