package com.coreoz.plume.file.db;

import com.coreoz.plume.conf.guice.GuiceConfModule;
import com.coreoz.plume.db.guice.DataSourceModule;
import com.coreoz.plume.db.guice.GuiceDbTestModule;
import com.coreoz.plume.db.querydsl.guice.GuiceQuerydslModule;
import com.google.inject.AbstractModule;

public class FileTestModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceConfModule());
		install(new GuiceQuerydslModule());
		install(new DataSourceModule());
		install(new GuiceDbTestModule());
	}

}
