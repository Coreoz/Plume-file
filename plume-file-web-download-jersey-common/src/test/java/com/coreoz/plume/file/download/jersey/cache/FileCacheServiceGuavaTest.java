package com.coreoz.plume.file.download.jersey.cache;

import com.coreoz.plume.file.download.jersey.TestFileMetadata;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileCacheServiceGuavaTest {

	private FileCacheService createCache(FileService fileService) {
		Config config = ConfigFactory.parseString("");
		return new FileCacheServiceGuava(fileService, new FileCacheConfigurationService(config));
	}

	@Test
	public void fetchFileData_should_load_and_cache_file_data() {
		FileService fileService = mock(FileService.class);
		byte[] data = "hello world".getBytes();
		when(fileService.fetchData("abc.pdf"))
			.thenReturn(Optional.of(new ByteArrayInputStream(data)));

		FileCacheService cache = createCache(fileService);

		assertThat(cache.fetchFileData("abc.pdf")).contains(data);
		assertThat(cache.fetchFileData("abc.pdf")).contains(data);

		verify(fileService, times(1)).fetchData("abc.pdf");
	}

	@Test
	public void fetchFileData_should_cache_an_empty_optional() {
		FileService fileService = mock(FileService.class);
		when(fileService.fetchData("abc.pdf")).thenReturn(Optional.empty());

		FileCacheService cache = createCache(fileService);

		assertThat(cache.fetchFileData("abc.pdf")).isEmpty();
		assertThat(cache.fetchFileData("abc.pdf")).isEmpty();

		verify(fileService, times(1)).fetchData("abc.pdf");
	}

	@Test
	public void fetchFileMetadata_should_load_and_cache_metadata() {
		FileService fileService = mock(FileService.class);
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", "name.pdf", "pdf", "application/pdf", "checksum"
		);
		when(fileService.fetchMetadata("abc.pdf")).thenReturn(Optional.of(metadata));

		FileCacheService cache = createCache(fileService);

		assertThat(cache.fetchFileMetadata("abc.pdf")).contains(metadata);
		assertThat(cache.fetchFileMetadata("abc.pdf")).contains(metadata);

		verify(fileService, times(1)).fetchMetadata("abc.pdf");
	}

	@Test
	public void fetchFileMetadata_should_cache_an_empty_optional() {
		FileService fileService = mock(FileService.class);
		when(fileService.fetchMetadata("abc.pdf")).thenReturn(Optional.empty());

		FileCacheService cache = createCache(fileService);

		assertThat(cache.fetchFileMetadata("abc.pdf")).isEmpty();
		assertThat(cache.fetchFileMetadata("abc.pdf")).isEmpty();

		verify(fileService, times(1)).fetchMetadata("abc.pdf");
	}
}
