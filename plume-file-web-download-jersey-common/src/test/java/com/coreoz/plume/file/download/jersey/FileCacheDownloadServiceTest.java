package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.download.jersey.cache.FileCacheService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileCacheDownloadServiceTest {

	@Test
	public void fetchMetadata_should_delegate_to_file_cache_service() {
		FileCacheService fileCacheService = mock(FileCacheService.class);
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", "name.pdf", "pdf", "application/pdf", "checksum"
		);
		when(fileCacheService.fetchFileMetadata("abc.pdf")).thenReturn(Optional.of(metadata));

		FileCacheDownloadService service = new FileCacheDownloadService(fileCacheService);

		assertThat(service.fetchMetadata("abc.pdf")).contains(metadata);
		verify(fileCacheService).fetchFileMetadata("abc.pdf");
	}

	@Test
	public void fetchMetadata_should_return_empty_when_cache_is_empty() {
		FileCacheService fileCacheService = mock(FileCacheService.class);
		when(fileCacheService.fetchFileMetadata("abc.pdf")).thenReturn(Optional.empty());

		FileCacheDownloadService service = new FileCacheDownloadService(fileCacheService);

		assertThat(service.fetchMetadata("abc.pdf")).isEmpty();
	}

	@Test
	public void fetchData_should_wrap_cached_bytes_into_a_stream() {
		FileCacheService fileCacheService = mock(FileCacheService.class);
		byte[] data = "hello".getBytes();
		when(fileCacheService.fetchFileData("abc.pdf")).thenReturn(Optional.of(data));

		FileCacheDownloadService service = new FileCacheDownloadService(fileCacheService);

		Optional<InputStream> stream = service.fetchData("abc.pdf");
		assertThat(stream).isPresent();
		assertThat(stream.get()).isInstanceOf(ByteArrayInputStream.class);
		assertThat(readAll(stream.get())).isEqualTo(data);
		verify(fileCacheService).fetchFileData("abc.pdf");
	}

	@Test
	public void fetchData_should_return_empty_when_cache_is_empty() {
		FileCacheService fileCacheService = mock(FileCacheService.class);
		when(fileCacheService.fetchFileData("abc.pdf")).thenReturn(Optional.empty());

		FileCacheDownloadService service = new FileCacheDownloadService(fileCacheService);

		assertThat(service.fetchData("abc.pdf")).isEmpty();
	}

	private static byte[] readAll(InputStream stream) {
		try {
			return stream.readAllBytes();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
