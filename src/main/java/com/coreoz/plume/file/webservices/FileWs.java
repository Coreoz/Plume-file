package com.coreoz.plume.file.webservices;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.services.cache.CachedFile;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.FileNameUtils;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/files")
@Api(value = "Serve binary resources")
@Singleton
public class FileWs {

	private static final Logger logger = LoggerFactory.getLogger(FileWs.class);

	private final FileService fileService;
	private final ChecksumService checksumService;
	private final LoadingCache<Long, CachedFile> fileCache;
	private final long maxAgeCacheInSeconds;

	@Inject
	public FileWs(FileService fileService,
			ChecksumService checksumService,
			FileCacheService cacheService,
			FileConfigurationService config) {
		this.fileService = fileService;
		this.checksumService = checksumService;

		this.fileCache = cacheService.newCache(this::loadFile);
		this.maxAgeCacheInSeconds = config.fileMaxAgeCache().getSeconds();
	}

	@GET
	@Path("/{id}{filename: (/.*)?}")
	@ApiOperation(value = "Serve a file")
	public Response fetch(@ApiParam(required = true) @PathParam("id") long fileId,
			@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader) {
		try {
			CachedFile fileData = fileCache.get(fileId);

			if(ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileData.getChecksum())) {
				return Response.notModified().build();
			}

			ResponseBuilder response = Response
				.ok(fileData.getData())
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
			return response.build();
		} catch (Exception e) {
			if(e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
				return Response.status(Status.NOT_FOUND).build();
			}
			logger.error("", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private CachedFile loadFile(Long fileId) {
		return fileService
			.fetch(fileId)
			.map(file -> CachedFile.of(
				FileNameUtils.guessMimeType(file.getFilename()),
				checksumService.hash(file.getData()),
				file.getData()
			))
			.orElseThrow(NotFoundException::new);
	}

	private static class NotFoundException extends RuntimeException {
		private static final long serialVersionUID = 2621559513884831969L;
	}

}
