package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.services.configuration.SignedUrlConfigurationService;
import com.coreoz.plume.file.services.url.SignedUrlFileService;
import com.coreoz.plume.jersey.errors.Validators;
import com.coreoz.plume.jersey.security.permission.PublicApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/files")
@Tag(name = "Public signed files", description = "Serve signed files")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PublicApi
@Singleton
public class SignedFileWs {
    private final Logger logger = LoggerFactory.getLogger(SignedFileWs.class);

    private final String fileSignedUrlExpiration;

    @Inject
    public SignedFileWs(SignedUrlConfigurationService configurationService) {
        this.fileSignedUrlExpiration = configurationService.fileSignedUrlHashKey();
    }

    @GET
    @Path("/{uniqueName}")
    @Operation(description = "Serve a signed file")
    public Response serveFile(
        @PathParam("uniqueName") String uniqueName,
        @QueryParam(SignedUrlFileService.SIGNATURE_QUERY_PARAM) String signature,
        @QueryParam(SignedUrlFileService.EXPIRATION_QUERY_PARAM) long expirationLinkTimeInMillis,
        @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatchHeader
    ) {
        Validators.checkRequired("expiration", expirationLinkTimeInMillis);
        Validators.checkRequired("signature", signature);

        if (System.currentTimeMillis() > expirationLinkTimeInMillis) {
            logger.info("File {} was tried to be accessed with an expired link", uniqueName);
            throw new ForbiddenException("Expired download link");
        }

        String currentFileSignature = SignedUrlFileService.generateSignature(
            this.fileSignedUrlExpiration,
            uniqueName,
            expirationLinkTimeInMillis
        );

        if (!signature.equals(currentFileSignature)) {
            logger.info("File {} was tried to be accessed with a wrong signature", uniqueName);
            throw new ForbiddenException("Wrong signature");
        }

        return this.fileWs.fetch(uniqueName, true, ifNoneMatchHeader);
    }
}
