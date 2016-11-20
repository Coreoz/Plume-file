package com.coreoz.plume.file.services.cache;

import java.util.function.Function;

import com.coreoz.plume.file.services.file.data.FileData;
import com.google.common.cache.LoadingCache;

public interface FileCacheService {

	LoadingCache<Long, FileData> newFileDataCache(Function<Long, FileData> loadingData);

	LoadingCache<Long, String> newFileUrlCache(Function<Long, String> loadingData);

}
