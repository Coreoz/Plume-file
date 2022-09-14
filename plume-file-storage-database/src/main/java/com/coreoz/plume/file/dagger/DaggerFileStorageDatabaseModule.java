package com.coreoz.plume.file.dagger;

import com.coreoz.plume.file.service.FileStorageDatabaseService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.scheduler.dagger.DaggerSchedulerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaggerSchedulerModule.class)
public class DaggerFileStorageDatabaseModule {
    @Provides
    @Singleton
    static FileStorageService provideFileService(FileStorageDatabaseService fileStorageDatabase) {
        return fileStorageDatabase;
    }
}
