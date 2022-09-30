package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.hash.ChecksumService;
import com.coreoz.plume.file.hash.ChecksumServiceSha1;
import com.google.inject.AbstractModule;

public class GuiceFileWebDownloadJerseyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ChecksumService.class).to(ChecksumServiceSha1.class);
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
    }
}
