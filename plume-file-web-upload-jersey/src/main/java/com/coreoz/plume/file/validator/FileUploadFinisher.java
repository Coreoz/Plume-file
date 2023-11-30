package com.coreoz.plume.file.validator;

import java.util.function.UnaryOperator;

/**
 * Handle file names operations.<br>
 * <br>
 * This interface will be renamed to <code>FileUploadFileNameOperations</code> in Plume File v4
 */
public interface FileUploadFinisher {
    /**
     * Returns the metadata associated to the validated file upload.
     * @deprecated Before calling the <code>finish()</code>, an operation on the file name should be performed.
     * This method will be removed in Plume File v4
     */
    @Deprecated(forRemoval = true)
    FileUploadData finish();

    FileUploadDataBuilder keepOriginalFileName();
    FileUploadDataBuilder sanitizeFileName();
    FileUploadDataBuilder changeFileName(UnaryOperator<String> sanitizer);
}
