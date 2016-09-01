package com.coreoz.plume.file.services.scheduled;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.scheduler.Scheduler;
import com.coreoz.plume.scheduler.schedule.Schedules;

@Singleton
public class ScheduledTasks {

	@Inject
	public ScheduledTasks(Scheduler scheduler,
			FileConfigurationService fileConfigurationService,
			FileService fileService) {
		scheduler.schedule(
			"File cleaner",
			fileService::deleteUnreferenced,
			Schedules.executeAt(fileConfigurationService.cleaningHour())
		);
	}

}
