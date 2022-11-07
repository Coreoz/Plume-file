package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.configuration.FileWebJerseyConfigurationService;
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
		FileWebJerseyConfigurationService config
	) {
		this.fileDownloadService = fileDownloadService;

		this.maxAgeCacheInSeconds = config.fileCacheMaxAge().getSeconds();
	}

	@GET
	@Path("/{uid}{filename: (/.*)?}")
	@Operation(description = "Serve a file")
	public Response fetch(
		@Parameter(required = true) @PathParam("uid") String fileUid,
		@Parameter @PathParam("filename") String filename,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		return this.fileDownloadService.fetchFile(fileUid)
			.map(fileData -> {
				if(ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileData.getChecksum())) {
					return Response.notModified().build();
				}

				ResponseBuilder response = Response.ok(fileData.getData())
					.header(HttpHeaders.ETAG, fileData.getChecksum());
				if(fileData.getMimeType() != null) {
					response.header(HttpHeaders.CONTENT_TYPE, fileData.getMimeType());
				}
				if(maxAgeCacheInSeconds > 0) {
					response.header(
						HttpHeaders.CACHE_CONTROL,
						"public, max-age=" + maxAgeCacheInSeconds
					);
				}
				return response
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.build();
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

}
