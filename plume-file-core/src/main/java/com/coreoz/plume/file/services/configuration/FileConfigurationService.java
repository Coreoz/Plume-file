package com.coreoz.plume.file.services.configuration;

import java.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

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

	public String apiBasePath() {
		return config.getString("application.api-base-path");
	}

	public String fileWsPath() {
		return config.getString("file.ws-path");
	}

	public Duration fileMaxAgeCache() {
		return config.getDuration("file.max-age-cache");
	}

	public String cleaningHour() {
		return config.getString("file.cleaning-hour");
	}

}
