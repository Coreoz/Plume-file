package com.coreoz.plume.file.services.file.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.file.db.dao.FileMetadata;
import com.coreoz.plume.file.db.dao.FileMetadataDao;
import com.coreoz.plume.file.services.filetype.FileType;

@Singleton
public class FileMetadataDatabaseService implements FileMetadataService {

	private final FileMetadataDao fileMetadataDao;

	@Inject
	public FileMetadataDatabaseService(FileMetadataDao fileMetadataDao) {
		this.fileMetadataDao = fileMetadataDao;
	}

	@Override
	public long upload(String fileUniqueName, String fileType, String mimeType, long fileSize) {
		return fileMetadataDao
			.upload(fileUniqueName, fileType, mimeType, fileSize)
			.getId();
	}

	@Override
	public Optional<String> fetchUniqueName(Long fileId) {
		return Optional.ofNullable(fileMetadataDao.fetchUniqueName(fileId));
	}

	@Override
	public Optional<FileMetadata> fetch(String fileUniqueName) {
		return fileMetadataDao.fetch(fileUniqueName);
	}

	@Override
	public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
		// TODO vérifier qu'on récupère les FileType du bon type database
		// if cast fileType instanfOF FileTypeDatabase
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFiles(List<String> fileUniqueNamesDeleted) {
		// TODO Auto-generated method stub

	}

}
