package com.coreoz.plume.file.services.cache;

import java.util.function.Function;

import com.coreoz.plume.file.services.file.data.FileData;
import com.google.common.cache.LoadingCache;

public interface FileCacheService {

	LoadingCache<String, FileData> newFileDataCache(Function<String, FileData> loadingData);

	LoadingCache<String, String> newFileUrlCache(Function<String, String> loadingData);

}
