package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.querydsl.FileDaoQuerydsl;
import com.google.inject.AbstractModule;

public class GuiceFileModuleQuerydsl extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileModule());

		bind(FileDao.class).to(FileDaoQuerydsl.class);
	}

}
