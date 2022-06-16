package com.coreoz.plume.file.services.cache;

import com.coreoz.plume.file.services.file.data.FileData;

import java.util.Optional;
import java.util.function.Function;

public interface FileCacheService {

	void initializeFileDataCache(Function<String, Optional<FileData>> fileDataLoader);

	Optional<FileData> fetchFileData(String fileUuid);

	Optional<String> fetchFileUniqueName(Long fileId);

}
