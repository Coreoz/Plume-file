package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.FileResponseService;
import com.coreoz.plume.file.download.jersey.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.cleaning.FileExtensionCleaning;
import com.coreoz.plume.file.signed.SignatureValidationResult;
import com.coreoz.plume.file.signed.SignedFileUrlService;
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
public class SignedFileWs {
    private static final int FILE_UID_LENGTH = 36;

	private final FileDownloadJerseyService fileDownloadService;
	private final SignedFileUrlService signedFileUrlService;
	private final long maxAgeCacheInSeconds;

	@Inject
	public SignedFileWs(
		FileDownloadJerseyService fileDownloadService,
		SignedFileUrlService signedFileUrlService,
		FileDownloadConfigurationService config
	) {
		this.fileDownloadService = fileDownloadService;
		this.signedFileUrlService = signedFileUrlService;

		this.maxAgeCacheInSeconds = config.fileCacheControlMaxAge().getSeconds();
	}

	@GET
	@Path("/{fileUniqueName}")
	@Operation(description = "Serve a signed file")
	public Response fetch(
		@Parameter(required = true) @PathParam("fileUniqueName") String fileUniqueName,
		@QueryParam("expiresAt") Long expiresAt,
		@QueryParam("signature") String signature,
		@DefaultValue("false") @QueryParam("attachment") boolean attachment,
		@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
	) {
		if (expiresAt == null || signature == null) {
			return Response.status(Status.FORBIDDEN).build();
		}

		SignatureValidationResult validationResult = this.signedFileUrlService.validate(fileUniqueName, expiresAt, signature);
		if (validationResult != SignatureValidationResult.VALID) {
			return Response.status(Status.FORBIDDEN).build();
		}

		// fileUniqueName cannot be null as it is required by jersey PathParam
		String fileExtension = FileExtensionCleaning.parseFileNameExtension(fileUniqueName);
        String fileUid = fileUniqueName.substring(
            0,
            fileExtension != null
                ? fileUniqueName.length() - fileExtension.length() - 1
                : fileUniqueName.length()
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
					.map(fileData -> FileResponseService.buildResponse(
						fileMetadata,
						fileData,
						maxAgeCacheInSeconds,
						attachment
					));
			})
			.orElseGet(() -> Response.status(Status.NOT_FOUND).build());
	}
}
