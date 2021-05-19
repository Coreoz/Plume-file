package com.coreoz.plume.file.services.cache;

import java.util.Optional;
import java.util.function.Function;

import com.coreoz.plume.file.services.file.data.FileData;

public interface FileCacheService {

	void initializeFileDataCache(Function<String, Optional<FileData>> fileDataLoader);

	void initializeFileIdCache(Function<Long, Optional<String>> fileIdLoader);

	Optional<FileData> fetchFileData(String fileUuid);

	Optional<String> fetchFileUniqueName(Long fileId);

}
