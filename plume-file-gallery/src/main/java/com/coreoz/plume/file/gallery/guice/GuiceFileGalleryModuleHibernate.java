package com.coreoz.plume.file.gallery.guice;

import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.db.hibernate.FileGalleryDaoHibernate;
import com.google.inject.AbstractModule;

public class GuiceFileGalleryModuleHibernate extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileGalleryModule());

		bind(FileGalleryDao.class).to(FileGalleryDaoHibernate.class);
	}

}
