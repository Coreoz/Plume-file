package com.coreoz.plume.file;

import com.coreoz.plume.conf.guice.GuiceConfModule;
import com.google.inject.AbstractModule;

public class FileUploadTestModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceConfModule());
	}

}
