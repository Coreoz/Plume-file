package com.coreoz.plume.file.validator;

import com.coreoz.plume.jersey.errors.WsException;

import java.util.Set;

public interface FileUploadExtensionValidator {
    /**
     * Verify that the file extension length (with the first .) is not longer than the limit. The default limit is 10.
     * @param fileExtensionMaxLength the maximum file extension length authorized
     * @throws WsException if the file extension length is longer than the limit
     */
    FileUploadFinisher fileExtensionMaxLength(long fileExtensionMaxLength);

    /**
     * Verify that the file extension length (with the first .) is not longer than 10 (the default limit)
     * @throws WsException if the file extension length is longer than the limit
     */
    FileUploadFinisher fileExtensionMaxDefaultLength();

    /**
     * Compares the file extension with a given authorized extensions Set
     * @param authorizedExtension the authorized extensions
     * @throws WsException if the file extension is not in the authorized extensions
     */
    FileUploadFinisher fileExtensions(Set<String> authorizedExtension);
}
