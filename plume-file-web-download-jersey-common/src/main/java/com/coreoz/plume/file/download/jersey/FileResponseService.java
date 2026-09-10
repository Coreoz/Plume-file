package com.coreoz.plume.file.download.jersey;

import com.coreoz.plume.file.cleaning.FileNameCleaning;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.google.common.net.UrlEscapers;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.io.InputStream;
import java.util.Optional;

public class FileResponseService {

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
