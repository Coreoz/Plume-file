package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.services.FileWebJerseyService;
import com.coreoz.plume.file.services.configuration.FileWebJerseyConfigurationService;
import com.coreoz.plume.jersey.security.permission.PublicApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;

@Path("/files")
@Api(value = "Serve binary resources")
@Singleton
@PublicApi
public class FileWs {

	private final FileWebJerseyService fileService;
	private final long maxAgeCacheInSeconds;

	@Inject
	public FileWs(
		FileWebJerseyService fileService,
		FileWebJerseyConfigurationService config
	) {
		this.fileService = fileService;

		this.maxAgeCacheInSeconds = config.fileCacheMaxAge().getSeconds();
	}

	@GET
	@Path("/{uid}{filename: (/.*)?}")
	@ApiOperation(value = "Serve a file")
	public Response fetch(
		@ApiParam(required = true) @PathParam("uid") String fileUid,
		@ApiParam @PathParam("filename") String filename,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		return this.fileService.fetchCachedFile(fileUid)
			.map(fileData -> {
				if(ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileData.getChecksum())) {
					return Response.notModified().build();
				}

				ResponseBuilder response = Response.ok(fileData)
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
					.header(CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.build();
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}

}
