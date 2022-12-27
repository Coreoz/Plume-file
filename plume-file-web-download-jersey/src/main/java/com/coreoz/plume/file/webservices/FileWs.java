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
	// TODO c'est pas filename en fait, c'est file extension non ??
	@Path("/{uid}{filename: (/.*)?}")
	@Operation(description = "Serve a file")
	public Response fetch(
		@Parameter(required = true) @PathParam("uid") String fileUid,
		@Parameter(required = false) @PathParam("filename") String filename,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		// TODO vérifier la taille de l'UID, ça évite de pourrir le cache et les appels inutiles
		// => si ça match pas l'UID, on renvoie une 404 directement

		return this.fileDownloadService
			.fetchMetadata(fileUid)
			.flatMap(fileMetadata -> {
				// TODO il faut valider l'extension du fichier si elle est transmise avec les métadonnées, si c'est différent, il faut retourner une 404
				// TODO sinon on risque une attaque de quelqu'un qui upload un fichier .txt et ensuite l'expose en .exe

				if (ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileMetadata.getChecksum())) {
					return Optional.of(Response.notModified().build());
				}

				return this.fileDownloadService.fetchData(fileUid)
					.map(fileData -> {
						ResponseBuilder response = Response.ok(fileData);

						// Adding checksum in etag to enable client basic caching
						response.header(HttpHeaders.ETAG, fileMetadata.getChecksum());

						if (fileMetadata.getMimeType() != null) {
							response.header(HttpHeaders.CONTENT_TYPE, fileMetadata.getMimeType());
						}

						if(maxAgeCacheInSeconds > 0) {
							response.header(
								HttpHeaders.CACHE_CONTROL,
								"public, max-age=" + maxAgeCacheInSeconds
							);
						}
						if (keepOriginalNameOnDownload && fileMetadata.getFileOriginalName() != null) {
							response
								.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMetadata.getFileOriginalName() + "\"");
						}
						return response.build();
					});
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
}
