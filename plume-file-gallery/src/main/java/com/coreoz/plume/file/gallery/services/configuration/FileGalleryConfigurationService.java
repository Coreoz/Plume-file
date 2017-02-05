package com.coreoz.plume.file.gallery.services.configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Singleton
public class FileGalleryConfigurationService {

	private final Config config;

	@Inject
	public FileGalleryConfigurationService(Config config) {
		// the reference file is not located in src/main/resources/ to ensure
		// that it is not overridden by another config file when a "fat jar" is created.
		this.config = config.withFallback(
			ConfigFactory.parseResources(FileGalleryConfigurationService.class, "reference.conf")
		);
	}

	public String cleaningHour() {
		return config.getString("file.gallery.cleaning-hour");
	}

}
