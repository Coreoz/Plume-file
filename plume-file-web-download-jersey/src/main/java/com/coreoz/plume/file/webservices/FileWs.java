package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.FileResponseHelper;
import com.coreoz.plume.file.download.jersey.FileResponseHelper.FileUniqueNameParsed;
import com.coreoz.plume.file.download.jersey.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.jersey.security.permission.PublicApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Objects;
import java.util.Optional;

@Path("/files")
@Tag(name = "files", description = "Serve binary resources")
@Singleton
@PublicApi
public class FileWs {
	private final FileDownloadJerseyService fileDownloadService;
	private final long maxAgeCacheInSeconds;

	@Inject
	public FileWs(
		FileDownloadJerseyService fileDownloadService,
		FileDownloadConfigurationService config
	) {
		this.fileDownloadService = fileDownloadService;
		this.maxAgeCacheInSeconds = config.fileCacheControlMaxAge().getSeconds();
	}

	@GET
	@Path("/{fileUniqueName}")
	@Operation(description = "Serve a file")
	public Response fetch(
		@Parameter(required = true) @PathParam("fileUniqueName") String fileUniqueName,
		@DefaultValue("false") @QueryParam("attachment") boolean attachment,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		// fileUniqueName cannot be null as it is required by jersey PathParam
		Optional<FileUniqueNameParsed> parsedFileUniqueName = FileResponseHelper.parseAndValidateFileUniqueName(fileUniqueName);
		if (parsedFileUniqueName.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		String fileExtension = parsedFileUniqueName.get().fileExtension();

		return this.fileDownloadService
			.fetchMetadata(fileUniqueName)
			.flatMap(fileMetadata -> {
				// if both are null, returns true
				// if both are same, returns true
				// else returns false
				boolean extensionsAreSame = Objects.equals(fileExtension, fileMetadata.getFileExtension());
				if (!extensionsAreSame) {
					return Optional.of(Response.status(Status.NOT_FOUND).build());
				}
				if (ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileMetadata.getChecksum())) {
					return Optional.of(Response.notModified().build());
				}

				return this.fileDownloadService.fetchData(fileUniqueName)
					.map(fileData -> FileResponseHelper.buildResponse(
						fileMetadata,
						fileData,
						maxAgeCacheInSeconds,
						attachment
					));
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
}
