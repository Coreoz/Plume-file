package com.coreoz.plume.file.services.file;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

@Singleton
public class FileServiceDb implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceDb.class);

	private final FileDao fileDao;
	private final FileTypesProvider fileTypesProvider;
	private final ChecksumService checksumService;

	private final String fileWsBasePath;
	private final LoadingCache<Long, FileData> fileCache;
	private final LoadingCache<Long, String> fileUrlCache;

	@Inject
	public FileServiceDb(FileDao fileDao,
			FileTypesProvider fileTypesProvider,
			ChecksumService checksumService,
			FileConfigurationService config,
			FileCacheService cacheService) {
		this.fileDao = fileDao;
		this.fileTypesProvider = fileTypesProvider;
		this.checksumService = checksumService;

		this.fileWsBasePath = config.apiBasePath() + config.fileWsPath();
		this.fileCache = cacheService.newFileDataCache(this::fetchUncached);
		this.fileUrlCache = cacheService.newFileUrlCache(this::fileUrl);
	}

	@Override
	public FileUploaded upload(FileType fileType, byte[] fileData, String fileName) {
		FileEntry file = fileDao.upload(
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

		FileData fileData = fileCache.getIfPresent(fileId);
		if(fileData != null) {
			return Optional.of(fullFileUrl(fileId, fileData.getFilename()));
		}

		return fileUrlCached(fileId);
	}

	@Override
	public String urlRaw(Long fileId) {
		return fileId == null ? null : (fileWsBasePath + "/" + fileId);
	}

	@Override
	public Optional<FileData> fetch(Long fileId) {
		if(fileId == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(fileCache.get(fileId));
		} catch (Exception e) {
			if(e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
				return Optional.empty();
			}
			throw Throwables.propagate(e);
		}
	}

	private FileData fetchUncached(Long fileId) {
		return Optional
			.ofNullable(fileDao.findById(fileId))
			.map(file -> FileData.of(
				file.getId(),
				file.getFilename(),
				file.getFileType(),
				FileNameUtils.guessMimeType(file.getFilename()),
				checksumService.hash(file.getData()),
				file.getData()
			))
			.orElseThrow(NotFoundException::new);
	}

	private Optional<String> fileUrlCached(Long fileId) {
		try {
			return Optional.of(fileUrlCache.get(fileId));
		} catch (Exception e) {
			if(e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
				return Optional.empty();
			}
			throw Throwables.propagate(e);
		}
	}

	private String fileUrl(Long fileId) {
		return Optional
			.ofNullable(fileDao.fileName(fileId))
			.map(fileName -> fullFileUrl(fileId, Strings.emptyToNull(fileName)))
			.orElseThrow(NotFoundException::new);
	}

	private String fullFileUrl(Long fileId, String fileName) {
		if(fileName == null) {
			return urlRaw(fileId);
		}
		return fileId == null ? null : (urlRaw(fileId) + "/" + fileName);
	}

	private static class NotFoundException extends RuntimeException {
		private static final long serialVersionUID = 2621559513884831969L;
	}

}
