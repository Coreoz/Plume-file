package com.coreoz.plume.file.dagger;

import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.file.FileStorageDatabase;
import com.coreoz.plume.file.services.file.storage.FileStorageService;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import com.coreoz.plume.scheduler.dagger.DaggerSchedulerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaggerSchedulerModule.class)
public class DaggerFileModule {

	@Provides
	@Singleton
	static ChecksumService provideChecksumService(ChecksumServiceSha1 checksumServiceSha1) {
		return checksumServiceSha1;
	}

	@Provides
	@Singleton
	static FileStorageService provideFileService(FileStorageDatabase fileStorageDatabase) {
		return fileStorageDatabase;
	}

	@Provides
	@Singleton
	static FileCacheService provideFileCacheService(FileCacheServiceGuava fileCacheServiceGuava) {
		return fileCacheServiceGuava;
	}

}

