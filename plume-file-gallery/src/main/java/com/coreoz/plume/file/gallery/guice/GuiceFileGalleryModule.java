package com.coreoz.plume.file.gallery.guice;

import com.coreoz.plume.file.gallery.services.gallery.FileGalleryService;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryServiceDb;
import com.coreoz.plume.file.gallery.services.scheduled.FileGalleryScheduledTasks;
import com.coreoz.plume.file.guice.GuiceFileModuleBase;
import com.coreoz.plume.scheduler.guice.GuiceSchedulerModule;
import com.google.inject.AbstractModule;

public class GuiceFileGalleryModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new GuiceFileModuleBase());

		bind(FileGalleryService.class).to(FileGalleryServiceDb.class);

		install(new GuiceSchedulerModule());
		bind(FileGalleryScheduledTasks.class).asEagerSingleton();
	}

}
