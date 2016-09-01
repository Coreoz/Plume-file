package com.coreoz.plume.file.services.file;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.db.daos.FileDao;
import com.coreoz.plume.file.db.entities.FileEntity;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;

@Singleton
public class FileServiceDb implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceDb.class);

	private final FileDao fileDao;
	private final FileTypesProvider fileTypesProvider;
	private final String fileWsBasePath;

	@Inject
	public FileServiceDb(FileDao fileDao,
			FileTypesProvider fileTypesProvider,
			FileConfigurationService config) {
		this.fileDao = fileDao;
		this.fileTypesProvider = fileTypesProvider;
		this.fileWsBasePath = config.apiBasePath() + config.fileWsPath();
	}

	@Override
	public FileUploaded upload(FileType fileType, byte[] fileData, String fileName) {
		FileEntity file = fileDao.upload(
			fileType.name(),
			fileData,
			FileNameUtils.sanitize(fileName)
		);

		return FileUploaded.of(
			file.getId(),
			fullFileUrl(file.getId(), file.getFilename())
		);
	}

	@Override
	public void delete(Long fileId) {
		fileDao.delete(fileId);
	}

	@Override
	public void deleteUnreferenced() {
		long countDeleted = fileTypesProvider
			.fileTypesAvailable()
			.stream()
			.mapToLong(fileType ->
				fileDao.deleteUnreferenced(
					fileType.name(),
					fileType.getFileEntity(),
					fileType.getJoinColumn()
				)
			)
			.sum();

		if(countDeleted > 0) {
			logger.debug("{} unreferenced files deleted", countDeleted);
		}
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
