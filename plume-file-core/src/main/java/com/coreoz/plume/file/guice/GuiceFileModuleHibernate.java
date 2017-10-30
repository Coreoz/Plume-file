package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.hibernate.FileDaoHibernate;
import com.google.inject.AbstractModule;

public class GuiceFileModuleHibernate extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileModuleDb());

		bind(FileDao.class).to(FileDaoHibernate.class);
	}

}
