package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.FileDaoDatabase;
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
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Singleton
public class FileServiceDatabase implements FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceDatabase.class);

	private final FileDaoDatabase fileDaoDatabase;
	private final FileTypesProvider fileTypesProvider;
	private final ChecksumService checksumService;

	private final String fileWsBasePath;
	private final LoadingCache<String, FileData> fileCache;
	private final LoadingCache<String, String> fileUrlCache;

	@Inject
	public FileServiceDatabase(FileDaoDatabase fileDaoDatabase,
							   FileTypesProvider fileTypesProvider,
							   ChecksumService checksumService,
							   FileConfigurationService config,
							   FileCacheService cacheService) {
		this.fileDaoDatabase = fileDaoDatabase;
		this.fileTypesProvider = fileTypesProvider;
		this.checksumService = checksumService;

		this.fileWsBasePath = config.apiBasePath() + config.fileWsPath();
		this.fileCache = cacheService.newFileDataCache(this::fetchUncached);
		this.fileUrlCache = cacheService.newFileUrlCache(this::fileUrl);
	}

	@Override
	public FileUploaded upload(FileType fileType, byte[] fileData, @Nullable String fileName) {
		FileEntry file = this.fileDaoDatabase.upload(
			fileType.name(),
			fileData,
			FileNameUtils.sanitize(fileName)
		);

		return FileUploaded.of(
			file.getId(),
			file.getUid(),
			this.fullFileUrl(file.getUid(), file.getFilename())
		);
	}

	@Override
	public void delete(String fileUid) {
		fileDaoDatabase.delete(fileUid);
	}

	@Override
	public void deleteUnreferenced() {
		long countDeleted = fileTypesProvider
			.fileTypesAvailable()
			.stream()
			.map(fileType ->
				fileDaoDatabase.deleteUnreferenced(
					fileType.name(),
					fileType.getFileEntity(),
					fileType.getJoinColumn()
				)
			).count();

		if(countDeleted > 0) {
			logger.debug("{} unreferenced files deleted", countDeleted);
		}
	}

	@Override
	public Optional<String> url(String fileUid) {
		if(fileUid == null) {
			return Optional.empty();
		}

		FileData fileData = fileCache.getIfPresent(fileUid);
		if(fileData != null) {
			return Optional.of(fullFileUrl(fileUid, fileData.getFilename()));
		}

		return fileUrlCached(fileUid);
	}

	@Override
	public String urlRaw(String fileUid) {
		return fileUid == null ? null : (fileWsBasePath + "/" + fileUid);
	}

	@Override
	public Optional<FileData> fetch(String fileUid) {
		if(fileUid == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(this.fileCache.get(fileUid));
		} catch (ExecutionException | UncheckedExecutionException e) {
			if(e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
				return Optional.empty();
			}
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<FileData> fetch(Long fileId) {
		FileEntry fileEntry = this.fileDaoDatabase.findById(fileId);
		if (fileEntry != null) {
			return this.fetch(fileEntry.getUid());
		}
		return Optional.empty();
	}

	private FileData fetchUncached(String fileUid) {
		return Optional
			.ofNullable(fileDaoDatabase.findByUid(fileUid))
			.map(file -> FileData.of(
				file.getId(),
				file.getUid(),
				file.getFilename(),
				file.getFileType(),
				FileNameUtils.guessMimeType(file.getFilename()),
				checksumService.hash(file.getData()),
				file.getData()
			))
			.orElseThrow(NotFoundException::new);
	}

	private Optional<String> fileUrlCached(String fileUid) {
		try {
			return Optional.of(this.fileUrlCache.get(fileUid));
		} catch (ExecutionException | UncheckedExecutionException e) {
			if(e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
				return Optional.empty();
			}
			throw new RuntimeException(e);
		}
	}

	private String fileUrl(String fileUid) {
		return Optional
			.ofNullable(fileDaoDatabase.fileName(fileUid))
			.map(fileName -> fullFileUrl(fileUid, Strings.emptyToNull(fileName)))
			.orElseThrow(NotFoundException::new);
	}

	private String fullFileUrl(String fileUid, String fileName) {
		if(fileName == null) {
			return urlRaw(fileUid);
		}
		return fileUid == null ? null : (urlRaw(fileUid) + "/" + fileName);
	}

	private static class NotFoundException extends RuntimeException {
		private static final long serialVersionUID = 2621559513884831969L;
	}

}
