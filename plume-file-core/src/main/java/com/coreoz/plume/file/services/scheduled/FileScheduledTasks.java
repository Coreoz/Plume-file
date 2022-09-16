package com.coreoz.plume.file.services.scheduled;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileScheduledTasks {

    Scheduler scheduler;
    FileConfigurationService fileConfigurationService;
    FileService fileService;

    @Inject
    public FileScheduledTasks(
        Scheduler scheduler,
        FileConfigurationService fileConfigurationService,
        FileService fileService
    ) {
        this.scheduler = scheduler;
        this.fileConfigurationService = fileConfigurationService;
        this.fileService = fileService;
    }

    public void scheduleJobs() {
        scheduler.schedule(
            "File cleaner",
            fileService::deleteUnreferenced,
            Schedules.executeAt(fileConfigurationService.cleaningHour())
        );
    }

}
