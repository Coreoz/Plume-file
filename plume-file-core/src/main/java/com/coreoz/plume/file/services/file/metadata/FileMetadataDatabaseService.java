package com.coreoz.plume.file.services.file.metadata;

import com.coreoz.plume.file.db.dao.FileMetadata;
import com.coreoz.plume.file.db.dao.FileMetadataDao;
import com.coreoz.plume.file.services.filetype.FileType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
	public Optional<FileMetadata> fetch(String fileUniqueName) {
		return fileMetadataDao.fetch(fileUniqueName);
	}

	@Override
	public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
		// TODO vérifier qu'on récupère les FileType du bon type database
		// if cast fileType instanfOF FileTypeDatabase
		// TODO Auto-generated method stub
		return this.fileMetadataDao.findUnreferencedFiles(fileTypes);
	}

	@Override
	public void deleteFiles(List<String> fileUniqueNamesDeleted) {
		this.fileMetadataDao.deleteFilesMetadata(fileUniqueNamesDeleted);
	}

}
