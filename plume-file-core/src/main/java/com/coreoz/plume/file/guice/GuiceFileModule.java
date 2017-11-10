package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import com.coreoz.plume.file.services.scheduled.FileScheduledTasks;
import com.coreoz.plume.scheduler.guice.GuiceSchedulerModule;
import com.google.inject.AbstractModule;

public class GuiceFileModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ChecksumService.class).to(ChecksumServiceSha1.class);
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);

        install(new GuiceSchedulerModule());
        bind(FileScheduledTasks.class).asEagerSingleton();
    }

}
