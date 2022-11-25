package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.jersey.security.permission.PublicApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;

@Path("/files")
@Tag(name = "files", description = "Serve binary resources")
@Singleton
@PublicApi
public class FileWs {

	private final FileDownloadJerseyService fileDownloadService;
	private final long maxAgeCacheInSeconds;
	private final boolean keepOriginalNameOnDownload;

	@Inject
	public FileWs(
		FileDownloadJerseyService fileDownloadService,
		FileDownloadConfigurationService config
	) {
		this.fileDownloadService = fileDownloadService;

		this.maxAgeCacheInSeconds = config.fileCacheMaxAge().getSeconds();
		this.keepOriginalNameOnDownload = config.keepOriginalNameOnDownload();
	}

	@GET
	@Path("/{uid}{filename: (/.*)?}")
	@Operation(description = "Serve a file")
	public Response fetch(
		@Parameter(required = true) @PathParam("uid") String fileUid,
		@Parameter @PathParam("filename") String filename,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		Optional<FileMetadata> fileMetadata = this.fileDownloadService.fetchMetadata(fileUid);
		if (fileMetadata.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return this.fileDownloadService.fetchData(fileUid)
			.map(fileData -> {
				if (ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileMetadata.get().getChecksum())) {
					return Response.notModified().build();
				}

				ResponseBuilder response = Response.ok(fileData);

				// adding checksum in etag to avoid corrupted data
				response.header(HttpHeaders.ETAG, fileMetadata.get().getChecksum());

				if (fileMetadata.get().getMimeType() != null) {
					response.header(HttpHeaders.CONTENT_TYPE, fileMetadata.get().getMimeType());
				}
				if(maxAgeCacheInSeconds > 0) {
					response.header(
						HttpHeaders.CACHE_CONTROL,
						"public, max-age=" + maxAgeCacheInSeconds
					);
				}
				String contentFilename = filename;
				if (keepOriginalNameOnDownload && fileMetadata.get().getFileOriginalName() != null) {
					contentFilename = fileMetadata.get().getFileOriginalName();
				}
				return response
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + contentFilename + "\"")
					.build();
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

}
