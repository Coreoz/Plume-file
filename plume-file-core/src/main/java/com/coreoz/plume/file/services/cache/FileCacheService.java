package com.coreoz.plume.file.services.cache;

import java.util.function.Function;

import com.google.common.cache.LoadingCache;

public interface FileCacheService {

	LoadingCache<Long, CachedFile> newCache(Function<Long, CachedFile> loadingData);

}
