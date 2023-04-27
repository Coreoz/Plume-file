package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.utils.FileNameUtils;
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
	private final int fileUidLength;

	@Inject
	public FileWs(
		FileDownloadJerseyService fileDownloadService,
		FileDownloadConfigurationService config
	) {
		this.fileDownloadService = fileDownloadService;

		this.maxAgeCacheInSeconds = config.fileCacheControlMaxAge().getSeconds();
		this.keepOriginalNameOnDownload = config.keepOriginalNameOnDownload();
		// TODO pourquoi passer par de la configuration pour définir la taille ? Elle sera toujours la même
		this.fileUidLength = config.fileUidLength();
	}

	@GET
	@Path("/{fileUniqueName}")
	@Operation(description = "Serve a file")
	public Response fetch(
		@Parameter(required = true) @PathParam("fileUniqueName") String fileUniqueName,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		// TODO peut être ajouter une vérification sur le fait que fileUniqueName ne doit pas être null ?
		String fileExtension = FileNameUtils.getExtensionFromFilename(fileUniqueName);
		String fileUid = fileUniqueName.substring(0, fileUniqueName.length() - fileExtension.length() - 1);
		if (fileUid.length() != fileUidLength) {
			return Response.status(Status.NOT_FOUND).build();
		}

		return this.fileDownloadService
			.fetchMetadata(fileUniqueName)
			.flatMap(fileMetadata -> {
				if (!fileMetadata.getFileExtension().equals(fileExtension)) {
					return Optional.of(Response.status(Status.NOT_FOUND).build());
				}
				if (ifNoneMatchHeader != null && ifNoneMatchHeader.equals(fileMetadata.getChecksum())) {
					return Optional.of(Response.notModified().build());
				}

				return this.fileDownloadService.fetchData(fileUniqueName)
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
						// TODO en fait je me dis que plutôt que d'avoir un paramètre de conf qui oblige tous les fichiers à être téléchargés, ça serait surement plus souple d'avoir un query param optionnel que le front pourrait ajouter pour permettre d'ajouter ce header ou pas non ? Par exemple https://wedownload.coreoz.com/api/files/abcd.jpg?attachment=true
						// TODO j'ai l'impression que ça serait d'un côté plus souple et de l'autre que ça n'ajouterait pas de problème de sécurité
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
