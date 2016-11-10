package com.coreoz.plume.file.gallery.guice;

import com.coreoz.plume.file.gallery.services.gallery.FileGalleryService;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryServiceDb;
import com.coreoz.plume.file.gallery.services.scheduled.FileGalleryScheduledTasks;
import com.coreoz.plume.file.guice.GuiceFileModule;
import com.coreoz.wisp.guice.GuiceWispSchedulerModule;
import com.google.inject.AbstractModule;

public class GuiceFileGalleryModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileModule());

		bind(FileGalleryService.class).to(FileGalleryServiceDb.class);

		install(new GuiceWispSchedulerModule());
		bind(FileGalleryScheduledTasks.class).asEagerSingleton();
	}

}
