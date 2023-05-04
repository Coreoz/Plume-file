package com.coreoz.plume.file.validator;

import com.coreoz.plume.jersey.errors.WsException;

public interface FileUploadNameLengthValidator {
    /**
     * Verify that the file name length (with the extension part) is not longer than the limit (255 is the default)
     * @param fileNameMaxLength the maximum file name length authorized
     * @throws WsException if the file name length is longer than the limit
     */
    FileUploadGeneralExtensionAndTypeValidator fileNameMaxLength(long fileNameMaxLength);

    /**
     * Verify that the file name length (with the extension part) is not longer than the default limit, i.e. 255 characters
     * @throws WsException if the file extension length is longer than the 255 characters limit
     */
    FileUploadGeneralExtensionAndTypeValidator fileNameMaxDefaultLength();
}
