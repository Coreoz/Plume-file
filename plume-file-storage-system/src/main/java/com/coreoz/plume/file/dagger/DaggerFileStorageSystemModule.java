package com.coreoz.plume.file.dagger;

import com.coreoz.plume.file.service.FileStorageSystemService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.scheduler.dagger.DaggerSchedulerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaggerSchedulerModule.class)
public class DaggerFileStorageSystemModule {
    @Provides
    @Singleton
    static FileStorageService provideFileService(FileStorageSystemService fileStorageSystemService) {
        return fileStorageSystemService;
    }
}
