package com.coreoz.plume.file.validator;

import com.coreoz.plume.jersey.errors.WsException;

import java.util.Set;

public interface FileUploadTypeValidator {
    /**
     * Compares the file mime type with a given authorized mime types Set
     * @param authorizedMimeTypes the authorized mime types
     * @throws WsException if the file mime type is not in the authorized mime types
     */
    FileUploadFinisher mimeTypes(Set<String> authorizedMimeTypes);
}
