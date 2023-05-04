package com.coreoz.plume.file.validator;

import com.coreoz.plume.jersey.errors.WsException;

public interface FileUploadGeneralExtensionAndTypeValidator {
    FileUploadExtensionValidator fileExtensionNotEmpty();
    default FileUploadExtensionValidator fileExtensionAllowEmpty() {
        return (FileUploadExtensionValidator) this;
    }
    FileUploadTypeValidator fileTypeNotEmpty();
    default FileUploadTypeValidator fileTypeAllowEmpty() {
        return (FileUploadTypeValidator) this;
    }
    /**
     * Verify that a file is an image
     * @throws WsException if the file mime type is not recognized as an image
     */
    void fileImage();
}
