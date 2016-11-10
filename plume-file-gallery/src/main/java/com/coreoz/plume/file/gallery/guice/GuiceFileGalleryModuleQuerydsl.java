package com.coreoz.plume.file.gallery.guice;

import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.db.querydsl.FileGalleryDaoQuerydsl;
import com.google.inject.AbstractModule;

public class GuiceFileGalleryModuleQuerydsl extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileGalleryModule());

		bind(FileGalleryDao.class).to(FileGalleryDaoQuerydsl.class);
	}

}
