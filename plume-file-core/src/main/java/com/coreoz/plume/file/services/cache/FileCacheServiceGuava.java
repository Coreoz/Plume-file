package com.coreoz.plume.file.services.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Singleton;

import com.coreoz.plume.file.services.file.data.FileData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Singleton
public class FileCacheServiceGuava implements FileCacheService {

	@Override
	public LoadingCache<Long, FileData> newFileDataCache(Function<Long, FileData> loadingData) {
		return CacheBuilder
			.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.maximumSize(100)
			.build(CacheLoader.from(loadingData::apply));
	}

	@Override
	public LoadingCache<Long, String> newFileUrlCache(Function<Long, String> loadingData) {
		return CacheBuilder
			.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.maximumSize(100)
			.build(CacheLoader.from(loadingData::apply));
	}

}
