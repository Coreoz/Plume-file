package com.coreoz.plume.file.validator;

import com.coreoz.plume.jersey.errors.WsException;

public interface FileUploadSizeValidator {
    /**
     * Verify the file size in bytes with a given maximum size
     * @implNote 1 000 000 bytes = 1 000 000 octets = 1MB ~= 8Mb ~= 0,95Mio
     * @param fileMaxSizeInBytes the maximum file length in bytes authorized
     * @throws WsException if the file size in bytes is bigger than the maximum authorized
     */
    FileUploadEmptyNameValidator fileMaxSize(long fileMaxSizeInBytes);
}
