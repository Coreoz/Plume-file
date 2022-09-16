package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.service.FileMetadataDatabaseService;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.google.inject.AbstractModule;

public class GuiceFileMetadataDatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileMetadataService.class).to(FileMetadataDatabaseService.class);
    }
}
