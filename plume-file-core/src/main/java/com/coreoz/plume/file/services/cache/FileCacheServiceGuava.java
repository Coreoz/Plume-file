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
	public LoadingCache<String, FileData> newFileDataCache(Function<String, FileData> loadingData) {
		return CacheBuilder
			.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.maximumSize(100)
			.build(CacheLoader.from(loadingData::apply));
	}

	@Override
	public LoadingCache<String, String> newFileUrlCache(Function<String, String> loadingData) {
		return CacheBuilder
			.newBuilder()
			.expireAfterAccess(1, TimeUnit.DAYS)
			.maximumSize(100)
			.build(CacheLoader.from(loadingData::apply));
	}

}
