package com.coreoz.plume.file.dagger;

import javax.inject.Singleton;

import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.FileServiceDatabase;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import com.coreoz.plume.scheduler.dagger.DaggerSchedulerModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = DaggerSchedulerModule.class)
public class DaggerFileModule {

	@Provides
	@Singleton
	static ChecksumService provideChecksumService(ChecksumServiceSha1 checksumServiceSha1) {
		return checksumServiceSha1;
	}

	@Provides
	@Singleton
	static FileService provideFileService(FileServiceDatabase fileServiceDatabase) {
		return fileServiceDatabase;
	}

	@Provides
	@Singleton
	static FileCacheService provideFileCacheService(FileCacheServiceGuava fileCacheServiceGuava) {
		return fileCacheServiceGuava;
	}

}

