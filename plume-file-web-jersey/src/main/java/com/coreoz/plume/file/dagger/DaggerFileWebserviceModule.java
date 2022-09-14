package com.coreoz.plume.file.dagger;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.hash.ChecksumService;
import com.coreoz.plume.file.hash.ChecksumServiceSha1;
import com.coreoz.plume.scheduler.dagger.DaggerSchedulerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaggerSchedulerModule.class)
public class DaggerFileWebserviceModule {
    @Provides
    @Singleton
    static ChecksumService provideChecksumService(ChecksumServiceSha1 checksumServiceSha1) {
        return checksumServiceSha1;
    }

    @Provides
    @Singleton
    static FileCacheService provideFileCacheService(FileCacheServiceGuava fileCacheServiceGuava) {
        return fileCacheServiceGuava;
    }
}
