package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.FileServiceDisk;
import com.google.inject.AbstractModule;

public class GuiceFileModuleDisk extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceFileModuleBase());

        bind(FileService.class).to(FileServiceDisk.class);
    }

}
