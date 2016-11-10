package com.coreoz.plume.file.gallery.services.configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.typesafe.config.Config;

@Singleton
public class FileGalleryConfigurationService {

	private final Config config;

	@Inject
	public FileGalleryConfigurationService(Config config) {
		this.config = config;
	}

	public String cleaningHour() {
		return config.getString("file.gallery.cleaning-hour");
	}

}
