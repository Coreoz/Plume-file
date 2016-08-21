package com.coreoz.plume.file.services.file;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.coreoz.plume.file.db.daos.FileDao;
import com.coreoz.plume.file.db.entities.FileEntity;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;

@Singleton
public class FileServiceDb implements FileService {

	private final FileDao fileDao;
	private final String fileWsBasePath;

	@Inject
	public FileServiceDb(FileDao fileDao,
			FileConfigurationService config) {
		this.fileDao = fileDao;
		this.fileWsBasePath = config.apiBasePath() + config.fileWsPath();
	}

	@Override
	public FileUploaded upload(byte[] fileData, String fileName, EntityManager transaction) {
		FileEntity file = fileDao.upload(
			fileData,
			FileNameUtils.sanitize(fileName),
			transaction
		);

		return FileUploaded.of(
			file.getId(),
			fullFileUrl(file.getId(), file.getFilename())
		);
	}

	@Override
	public void delete(Long fileId, EntityManager transaction) {
		fileDao.delete(fileId, transaction);
	}

	@Override
	public Optional<String> url(Long fileId) {
		if(fileId == null) {
			return Optional.empty();
		}
		return Optional
				.ofNullable(fileDao.fileName(fileId))
				.map(fileName -> fullFileUrl(fileId, fileName));
	}

	@Override
	public String urlRaw(Long fileId) {
		return fileWsBasePath + "/" + fileId;
	}

	@Override
	public List<FileUploaded> urlBatch(List<Long> fileIds) {
		return fileDao
				.findFileNames(fileIds)
				.stream()
				.map(file -> FileUploaded.of(
					file.getId(),
					fullFileUrl(file.getId(), file.getFileName())
				))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<FileEntity> fetch(Long fileId) {
		return Optional.ofNullable(fileDao.findById(fileId));
	}

	private String fullFileUrl(Long fileId, String fileName) {
		if(fileName == null) {
			return urlRaw(fileId);
		}
		return urlRaw(fileId) + "/" + fileName;
	}

}
