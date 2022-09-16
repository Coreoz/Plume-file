package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.service.FileStorageDatabaseService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.google.inject.AbstractModule;

public class GuiceFileStorageDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileStorageService.class).to(FileStorageDatabaseService.class);
    }
}
