package com.coreoz.plume.file.download.jersey.configuration;

import com.coreoz.plume.file.download.jersey.cache.FileCacheConfigurationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class FileDownloadConfigurationServiceTest {

	@Test
	public void default_values_should_be_used_from_reference_conf() {
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(ConfigFactory.empty());

		assertThat(service.fileCacheControlMaxAge()).isEqualTo(Duration.ofDays(365));
		assertThat(service.fileUrlBasePath()).isEqualTo("/api/files");
	}

	@Test
	public void fileCacheControlMaxAge_should_use_custom_value() {
		Config config = ConfigFactory.parseString("file.cache.http.max-age = \"1 hour\"");
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(config);

		assertThat(service.fileCacheControlMaxAge()).isEqualTo(Duration.ofHours(1));
	}

	@Test
	public void fileUrlBasePath_should_strip_trailing_slash() {
		Config config = ConfigFactory.parseString("file.url.base-path = \"/files/\"");
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(config);

		assertThat(service.fileUrlBasePath()).isEqualTo("/files");
	}

	@Test
	public void fileUrlBasePath_should_keep_path_without_trailing_slash() {
		Config config = ConfigFactory.parseString("file.url.base-path = \"/files\"");
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(config);

		assertThat(service.fileUrlBasePath()).isEqualTo("/files");
	}

	@Test
	public void deprecated_cache_methods_should_delegate_to_cache_configuration_service() {
		Config config = ConfigFactory.parseString(
			"file.cache.data.expires-after-access-duration = \"10 minutes\"\n" +
			"file.cache.data.max-cache-size = \"1 KiB\"\n" +
			"file.cache.metadata.expires-after-access-duration = \"2 hours\"\n" +
			"file.cache.metadata.max-elements = 42"
		);
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(config);

		assertThat(service.fileDataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofMinutes(10));
		assertThat(service.fileMetadataCacheExpiresAfterAccessDuration()).isEqualTo(Duration.ofHours(2));
		assertThat(service.fileDataCacheMaximumSizeInBytes()).isEqualTo(1024L);
		assertThat(service.fileMetadataCacheMaximumSize()).isEqualTo(42L);
	}

	@Test
	public void deprecated_cache_methods_should_share_same_instance_as_cache_configuration() {
		Config config = ConfigFactory.parseString(
			"file.cache.data.expires-after-access-duration = \"5 minutes\"\n" +
			"file.cache.data.max-cache-size = \"2 KiB\"\n" +
			"file.cache.metadata.expires-after-access-duration = \"30 minutes\"\n" +
			"file.cache.metadata.max-elements = 7"
		);
		FileDownloadConfigurationService service = new FileDownloadConfigurationService(config);

		FileCacheConfigurationService cacheConfiguration = new FileCacheConfigurationService(config);
		assertThat(service.fileDataCacheExpiresAfterAccessDuration())
			.isEqualTo(cacheConfiguration.fileDataCacheExpiresAfterAccessDuration());
		assertThat(service.fileMetadataCacheExpiresAfterAccessDuration())
			.isEqualTo(cacheConfiguration.fileMetadataCacheExpiresAfterAccessDuration());
		assertThat(service.fileDataCacheMaximumSizeInBytes())
			.isEqualTo(cacheConfiguration.fileDataCacheMaximumSizeInBytes());
		assertThat(service.fileMetadataCacheMaximumSize())
			.isEqualTo(cacheConfiguration.fileMetadataCacheMaximumSize());
	}
}

