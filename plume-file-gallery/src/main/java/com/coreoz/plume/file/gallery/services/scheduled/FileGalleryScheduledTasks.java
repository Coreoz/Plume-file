package com.coreoz.plume.file.gallery.services.scheduled;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.file.gallery.services.configuration.FileGalleryConfigurationService;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryService;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;

@Singleton
public class FileGalleryScheduledTasks {

	@Inject
	public FileGalleryScheduledTasks(Scheduler scheduler,
			FileGalleryConfigurationService configurationService,
			FileGalleryService fileGalleryService) {
		scheduler.schedule(
			"File gallery cleaner",
			fileGalleryService::deleteUnreferenced,
			Schedules.executeAt(configurationService.cleaningHour())
		);
	}

}
