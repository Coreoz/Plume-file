package com.coreoz.plume.file.services.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileConfigurationService {

	private final Config config;

	@Inject
	public FileConfigurationService(Config config) {
		// the reference file is not located in src/main/resources/ to ensure
		// that it is not overridden by another config file when a "fat jar" is created.
		this.config = config.withFallback(
			ConfigFactory.parseResources(FileConfigurationService.class, "reference.conf")
		);
	}

	public String cleaningHour() {
		return config.getString("file.cleaning-hour");
	}

}
