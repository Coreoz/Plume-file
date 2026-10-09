package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileDownloadServiceTest {

	@Test
	public void fetchMetadata_should_delegate_to_file_service() {
		FileService fileService = mock(FileService.class);
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", "name.pdf", "pdf", "application/pdf", "checksum"
		);
		when(fileService.fetchMetadata("abc.pdf")).thenReturn(Optional.of(metadata));

		FileDownloadService service = new FileDownloadService(fileService);

		assertThat(service.fetchMetadata("abc.pdf")).contains(metadata);
		verify(fileService).fetchMetadata("abc.pdf");
	}

	@Test
	public void fetchMetadata_should_return_empty_when_file_service_returns_empty() {
		FileService fileService = mock(FileService.class);
		when(fileService.fetchMetadata("abc.pdf")).thenReturn(Optional.empty());

		FileDownloadService service = new FileDownloadService(fileService);

		assertThat(service.fetchMetadata("abc.pdf")).isEmpty();
	}

	@Test
	public void fetchData_should_delegate_to_file_service() {
		FileService fileService = mock(FileService.class);
		InputStream data = new ByteArrayInputStream("hello".getBytes());
		when(fileService.fetchData("abc.pdf")).thenReturn(Optional.of(data));

		FileDownloadService service = new FileDownloadService(fileService);

		assertThat(service.fetchData("abc.pdf")).contains(data);
		verify(fileService).fetchData("abc.pdf");
	}

	@Test
	public void fetchData_should_return_empty_when_file_service_returns_empty() {
		FileService fileService = mock(FileService.class);
		when(fileService.fetchData("abc.pdf")).thenReturn(Optional.empty());

		FileDownloadService service = new FileDownloadService(fileService);

		assertThat(service.fetchData("abc.pdf")).isEmpty();
	}
}
