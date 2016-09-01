package com.coreoz.plume.file.services.configuration;

import java.time.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.Config;

@Singleton
public class FileConfigurationService {

	private final Config config;

	@Inject
	public FileConfigurationService(Config config) {
		this.config = config;
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
		return config.getString("cleaning-hour");
	}

}
