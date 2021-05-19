package com.coreoz.plume.file.services.file.storage;

import java.util.List;
import java.util.Optional;

public interface FileStorageService {
	void upload(String fileType, String fileUniqueName, byte[] fileData);

	Optional<byte[]> fetch(String fileType, String fileUniqueName);

	/**
	 * Must not failed if the files have already been deleted
	 * @param fileUniqueNames The file unique names for which files should be deleted
	 * @return The file unique names that have been deleted OR that did not exist.
	 * Only files that exist and that have failed to be deleted must not be present in this list
	 */
	List<String> deleteFiles(List<String> fileUniqueNames);
}
