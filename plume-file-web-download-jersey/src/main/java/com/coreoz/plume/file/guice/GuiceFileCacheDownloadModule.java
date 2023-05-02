package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.cache.FileCacheServiceGuava;
import com.google.inject.AbstractModule;

public class GuiceFileCacheDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceFileDownloadModule());
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
    }
}
