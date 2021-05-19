package com.coreoz.plume.file.services.file.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.coreoz.plume.file.services.filetype.FileType;

public class FileMetadataDatabaseService implements FileMetadataService {

	@Override
	public long upload(String fileUniqueName, String fileType, long fileSize) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Optional<String> fetchUniqueName(Long fileId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<FileMetadata> fetch(String fileUniqueName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
		// TODO vérifier qu'on récupère les FileType du bon type database
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFiles(List<String> fileUniqueNamesDeleted) {
		// TODO Auto-generated method stub

	}

}
