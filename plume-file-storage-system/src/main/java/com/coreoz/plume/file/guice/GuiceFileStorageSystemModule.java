package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.service.FileStorageSystemService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.google.inject.AbstractModule;

public class GuiceFileStorageSystemModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileStorageService.class).to(FileStorageSystemService.class);
    }
}
