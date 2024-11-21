package com.coreoz.plume.file.service;

import com.coreoz.plume.conf.guice.GuiceConfModule;
import com.coreoz.plume.db.guice.DataSourceModule;
import com.coreoz.plume.db.guice.GuiceDbTestModule;
import com.google.inject.AbstractModule;

public class FileTestModule extends AbstractModule {

	@Override
	protected void configure() {
        install(new GuiceConfModule());
        install(new DataSourceModule());
        install(new GuiceDbTestModule());
	}

}
