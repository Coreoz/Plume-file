package com.coreoz.plume.file.validator;

public interface FileUploadDataBuilder {
    /**
     * Returns the metadata associated to the validated file upload.
     */
    FileUploadData finish();
}
