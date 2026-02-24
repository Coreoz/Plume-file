package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.util.Optional;

/**
 * Service to manage the file data and metadata cache. It provides methods to fetch the file data and metadata from the cache, and if not found, it will fetch them from the database and store them in the cache for future requests.
 */
public interface FileCacheService {

	/**
	 * Fetch the file data from the cache. If not found, it will be fetched from the database and stored in the cache for future requests.
	 * @param fileUniqueName the unique name of the file
	 * @return the file data as a byte array, or an empty Optional if the file data is not found in the cache and database
	 * @throws RuntimeException if an error occurs while fetching the file data from the database
	 */
	Optional<byte[]> fetchFileData(String fileUniqueName);

	/**
	 * Fetch the file metadata from the cache. If not found, it will be fetched from the database and stored in the cache for future requests.
	 * @param fileUniqueName the unique name of the file
	 * @return the file metadata, or an empty Optional if the file metadata is not found in the cache and database
	 * @throws RuntimeException if an error occurs while fetching the file metadata from the database
	 */
	Optional<FileMetadata> fetchFileMetadata(String fileUniqueName);

}
