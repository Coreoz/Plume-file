package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.FileServiceDb;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import com.coreoz.plume.file.services.scheduled.ScheduledTasks;
import com.coreoz.wisp.guice.GuiceWispSchedulerModule;
import com.google.inject.AbstractModule;

public class GuiceFileModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ChecksumService.class).to(ChecksumServiceSha1.class);
		bind(FileService.class).to(FileServiceDb.class);
		bind(FileCacheService.class).to(FileCacheServiceGuava.class);

		install(new GuiceWispSchedulerModule());
		bind(ScheduledTasks.class).asEagerSingleton();
	}

}
