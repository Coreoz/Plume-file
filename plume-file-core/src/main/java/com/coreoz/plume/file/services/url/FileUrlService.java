package com.coreoz.plume.file.services.url;

/**
 * Stand-alone service to compute the URL of a file through its unique name.
 */
public interface FileUrlService {
    /**
     * Create or compute the URL of a file
     * @param uniqueName the uniqueName of a file
     * @return the file URL
     */
    String url(String uniqueName);
}
