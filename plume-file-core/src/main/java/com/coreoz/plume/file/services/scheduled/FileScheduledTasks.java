package com.coreoz.plume.file.services.scheduled;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;

@Singleton
public class FileScheduledTasks {

	@Inject
	public FileScheduledTasks(Scheduler scheduler,
			FileConfigurationService fileConfigurationService,
			FileService fileService) {
		scheduler.schedule(
			"File cleaner",
			fileService::deleteUnreferenced,
			Schedules.executeAt(fileConfigurationService.cleaningHour())
		);
	}

}
