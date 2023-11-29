package com.coreoz.plume.file.validator;

import java.util.function.UnaryOperator;

public interface FileUploadFinisher {
    /**
     * Returns the metadata associated to the validated file upload.
     * @deprecated for removal in v4
     */
    @Deprecated(forRemoval = true)
    FileUploadData finish();

    FileUploadDataBuilder keepOriginalFilename();

    FileUploadDataBuilder sanitizeOriginalFileName();
    FileUploadDataBuilder mapOriginalFileName(UnaryOperator<String> sanitizer);
}
