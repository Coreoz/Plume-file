package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.services.metadata.FileMetadata;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class FileResponseServiceTest {

	private static final FileMetadata METADATA = new TestFileMetadata(
		"abc.pdf", "My Original Name.pdf", "pdf", "application/pdf", "checksum123"
	);

	private static final InputStream DATA = new ByteArrayInputStream("hello".getBytes());

	@Test
	void buildResponse_should_set_etag_when_checksum_is_present() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 0, false);

		assertThat(response.getHeaderString("ETag")).isEqualTo("checksum123");
	}

	@Test
	void buildResponse_should_not_set_etag_when_checksum_is_null() {
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", "name.pdf", "pdf", "application/pdf", null
		);
		Response response = FileResponseHelper.buildResponse(metadata, DATA, 0, false);

		assertThat(response.getHeaderString("ETag")).isNull();
	}

	@Test
	void buildResponse_should_set_content_type_when_mime_type_is_present() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 0, false);

		assertThat(response.getHeaderString("Content-Type")).isEqualTo("application/pdf");
	}

	@Test
	void buildResponse_should_not_set_content_type_when_mime_type_is_null() {
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", "name.pdf", "pdf", null, "checksum"
		);
		Response response = FileResponseHelper.buildResponse(metadata, DATA, 0, false);

		assertThat(response.getHeaderString("Content-Type")).isNull();
	}

	@Test
	void buildResponse_should_set_cache_control_when_max_age_is_positive() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 3600, false);

		assertThat(response.getHeaderString("Cache-Control")).isEqualTo("public, max-age=3600");
	}

	@Test
	void buildResponse_should_not_set_cache_control_when_max_age_is_zero_or_negative() {
		Response zero = FileResponseHelper.buildResponse(METADATA, DATA, 0, false);
		Response negative = FileResponseHelper.buildResponse(METADATA, DATA, -1, false);

		assertThat(zero.getHeaderString("Cache-Control")).isNull();
		assertThat(negative.getHeaderString("Cache-Control")).isNull();
	}

	@Test
	void buildResponse_should_set_attachment_disposition_when_attachment_is_requested() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 0, true);

		String disposition = response.getHeaderString("Content-Disposition");
		assertThat(disposition).startsWith("attachment;")
            .contains("filename=\"my-original-name.pdf\"")
            .contains("filename*=UTF-8''");
	}

	@Test
	void buildResponse_should_use_unique_name_as_fallback_filename() {
		FileMetadata metadata = new TestFileMetadata(
			"abc.pdf", null, "pdf", "application/pdf", "checksum"
		);
		Response response = FileResponseHelper.buildResponse(metadata, DATA, 0, true);

		assertThat(response.getHeaderString("Content-Disposition")).contains("filename=\"abc.pdf\"");
	}

	@Test
	void buildResponse_should_not_set_disposition_when_not_attachment() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 0, false);

		assertThat(response.getHeaderString("Content-Disposition")).isNull();
	}

	@Test
	void buildResponse_should_serve_the_input_stream_as_entity() {
		Response response = FileResponseHelper.buildResponse(METADATA, DATA, 0, false);

		assertThat(response.getEntity()).isSameAs(DATA);
		assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
	}
}
