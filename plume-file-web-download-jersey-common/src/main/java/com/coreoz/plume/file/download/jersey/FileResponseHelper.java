package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.cleaning.FileExtensionCleaning;
import com.coreoz.plume.file.cleaning.FileNameCleaning;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.google.common.net.UrlEscapers;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.io.InputStream;
import java.util.Optional;

public class FileResponseHelper {
    private static final int FILE_UID_LENGTH = 36;

    private FileResponseHelper() {
        // Prevent instantiation
    }

    /**
     * Parse a file unique name into its file uid and its extension, and validate the file uid length.
     *
     * @param fileUniqueName the file unique name (cannot be null)
     * @return the parsed result if the file uid is valid, else {@link Optional#empty()}
     */
    public static Optional<FileUniqueNameParsed> parseAndValidateFileUniqueName(String fileUniqueName) {
        String fileExtension = FileExtensionCleaning.parseFileNameExtension(fileUniqueName);
        String fileUid = fileUniqueName.substring(
            0,
            fileExtension != null
                ? fileUniqueName.length() - fileExtension.length() - 1
                : fileUniqueName.length()
        );
        if (fileUid.length() != FILE_UID_LENGTH) {
            return Optional.empty();
        }
        return Optional.of(new FileUniqueNameParsed(fileUid, fileExtension));
    }

    /**
     * The result of parsing a file unique name into its file uid and its extension.
     */
    public record FileUniqueNameParsed(String fileUid, String fileExtension) {
    }

    public static Response buildResponse(
        FileMetadata fileMetadata,
        InputStream fileData,
        long maxAgeCacheInSeconds,
        boolean attachment
    ) {
        ResponseBuilder response = Response.ok(fileData);

        if (fileMetadata.getChecksum() != null) {
            response.header(HttpHeaders.ETAG, fileMetadata.getChecksum());
        }

        if (fileMetadata.getMimeType() != null) {
            response.header(HttpHeaders.CONTENT_TYPE, fileMetadata.getMimeType());
        }

        if (maxAgeCacheInSeconds > 0) {
            response.header(
                HttpHeaders.CACHE_CONTROL,
                "public, max-age=" + maxAgeCacheInSeconds
            );
        }

        if (attachment) {
            String attachmentFilename = Optional.ofNullable(fileMetadata.getFileOriginalName())
                .orElse(fileMetadata.getUniqueName());
            String utf8FileName = UrlEscapers.urlFragmentEscaper().escape(attachmentFilename);
            String sanitizedFileName = FileNameCleaning.cleanFileName(attachmentFilename);
            response.header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + sanitizedFileName
                    + "\"; filename*=UTF-8''" + utf8FileName
            );
        }

        return response.build();
    }
}
