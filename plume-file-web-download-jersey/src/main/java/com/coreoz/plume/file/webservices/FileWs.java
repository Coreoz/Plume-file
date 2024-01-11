package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.cleaning.FileExtensionCleaning;
import com.coreoz.plume.file.cleaning.FileNameCleaning;
import com.coreoz.plume.jersey.security.permission.PublicApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Path("/files")
@Tag(name = "files", description = "Serve binary resources")
@Singleton
@PublicApi
public class FileWs {
    private static final int FILE_UID_LENGTH = 36;

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
		String fileExtension = FileExtensionCleaning.parseFileNameExtension(fileUniqueName);
		String fileUid = fileUniqueName.substring(
			0,
			fileUniqueName.length() - (fileExtension != null ? fileExtension.length() : 0) - 1
		);
		if (fileUid.length() != FILE_UID_LENGTH) {
			return Response.status(Status.NOT_FOUND).build();
		}

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
					.map(fileData -> {
						ResponseBuilder response = Response.ok(fileData);

						// Adding checksum in etag to enable client basic caching
						if (fileMetadata.getChecksum() != null) {
							response.header(HttpHeaders.ETAG, fileMetadata.getChecksum());
						}

						if (fileMetadata.getMimeType() != null) {
							response.header(HttpHeaders.CONTENT_TYPE, fileMetadata.getMimeType());
						}

						if(maxAgeCacheInSeconds > 0) {
							response.header(
								HttpHeaders.CACHE_CONTROL,
								"public, max-age=" + maxAgeCacheInSeconds
							);
						}
						if (attachment) {
                            String attachmentFilename = Optional.ofNullable(fileMetadata.getFileOriginalName())
                                .orElse(fileMetadata.getUniqueName());
							String utf8FileName = URLEncoder.encode(attachmentFilename, StandardCharsets.UTF_8)
								.replace("+", "%20");
							String sanitizedFileName = FileNameCleaning.cleanFileName(attachmentFilename);
                            response
                                .header(
									HttpHeaders.CONTENT_DISPOSITION,
									"attachment; filename=\"" + sanitizedFileName
										+ "\"; filename*=UTF-8''" + utf8FileName
								);
                        }
						return response.build();
					});
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
}
