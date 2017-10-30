package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.FileServiceDb;
import com.google.inject.AbstractModule;

public class GuiceFileModuleDb extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileModuleBase());

		bind(FileService.class).to(FileServiceDb.class);
	}

}
