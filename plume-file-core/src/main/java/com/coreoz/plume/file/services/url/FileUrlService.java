package com.coreoz.plume.file.services.url;

/**
 * Create or compute the URL of a file.
 *
 * Can be implemented by the download layer (Jersey) or the upload layer (S3)
 */
// TODO à implémenter
public interface FileUrlService {
    String url(String uniqueName);
}
