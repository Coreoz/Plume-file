package com.coreoz.plume.file.download.jersey.cache;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class FileCacheConfigurationServiceTest {

	@Test
	public void default_values_should_be_used_from_reference_conf() {
		FileCacheConfigurationService service = new FileCacheConfigurationService(ConfigFactory.empty());

		assertThat(service.fileDataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofDays(1));
		assertThat(service.fileMetadataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofDays(1));
		assertThat(service.fileDataCacheMaximumSizeInBytes()).isEqualTo(64_000_000L);
		assertThat(service.fileMetadataCacheMaximumSize()).isEqualTo(10000L);
	}

	@Test
	public void custom_values_should_override_reference_conf() {
		Config config = ConfigFactory.parseString(
			"file.cache.data.expires-after-access-duration = \"10 minutes\"\n" +
			"file.cache.data.max-cache-size = \"1 KiB\"\n" +
			"file.cache.metadata.expires-after-access-duration = \"2 hours\"\n" +
			"file.cache.metadata.max-elements = 42"
		);
		FileCacheConfigurationService service = new FileCacheConfigurationService(config);

		assertThat(service.fileDataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofMinutes(10));
		assertThat(service.fileMetadataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofHours(2));
		assertThat(service.fileDataCacheMaximumSizeInBytes()).isEqualTo(1024L);
		assertThat(service.fileMetadataCacheMaximumSize()).isEqualTo(42L);
	}
}
