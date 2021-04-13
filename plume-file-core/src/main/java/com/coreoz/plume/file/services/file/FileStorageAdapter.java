package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileDao;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public abstract class FileStorageAdapter implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageAdapter.class);

    protected final FileDao fileDao;
    protected final FileTypesProvider fileTypesProvider;
    protected final ChecksumService checksumService;

    protected final String baseWsPath;

    private final LoadingCache<String, FileData> fileCache;
    private final LoadingCache<String, String> fileUrlCache;

    public FileStorageAdapter(
        FileDao fileDao,
        FileTypesProvider fileTypesProvider,
        ChecksumService checksumService,
        FileConfigurationService fileConfigurationService,
        FileCacheService cacheService
    ) {
        this.fileDao = fileDao;
        this.fileTypesProvider = fileTypesProvider;
        this.checksumService = checksumService;

        this.baseWsPath = fileConfigurationService.apiBasePath() + fileConfigurationService.fileWsPath();

        this.fileCache = cacheService.newFileDataCache(this::fetchUncached);
        this.fileUrlCache = cacheService.newFileUrlCache(this::fetchUrlUncached);
    }

    public abstract byte[] getData(FileEntry fileEntry);

    @Override
    public FileUploaded upload(FileType fileType, String fileExtension, @Nullable byte[] fileData) {
        FileEntry file = this.fileDao.upload(
            fileType.name(),
            fileExtension,
            fileData
        );

        return FileUploaded.of(
            file.getId(),
            file.getUid(),
            this.urlRaw(file.getUid())
        );
    }

    @Override
    public Optional<FileData> fetch(String fileUid) {
        if (fileUid == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.fileCache.get(fileUid));
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FileData> fetch(Long fileId) {
        return this.fileDao.findById(fileId)
            .flatMap(this.toFileData());
    }

    @Override
    public void delete(Long fileId) {
        this.fileDao.delete(fileId);
    }

    @Override
    public void delete(String fileUid) {
        this.fileDao.delete(fileUid);
    }

    @Override
    public void deleteUnreferenced() {
        long countDeleted = fileTypesProvider
            .fileTypesAvailable()
            .stream()
            .map(
                fileType ->
                    this.fileDao.deleteUnreferenced(
                        fileType.name(),
                        fileType.getFileEntity(),
                        fileType.getJoinColumn()
                    )
            )
            .count();

        if (countDeleted > 0) {
            logger.debug("{} unreferenced files deleted", countDeleted);
        }
    }

    @Override
    public Optional<String> url(String fileUid) {
        if (fileUid == null) {
            return Optional.empty();
        }

        FileData fileData = this.fileCache.getIfPresent(fileUid);
        if (fileData != null) {
            return Optional.of(urlRaw(fileUid));
        }

        return fileUrlCached(fileUid);
    }

    @Override
    public Optional<String> url(Long fileId) {
        if (fileId == null) {
            return Optional.empty();
        }
        return this.fileDao.findById(fileId)
            .flatMap(file -> this.url(file.getUid()));
    }

    @Override
    public String urlRaw(String fileUid) {
        if (fileUid == null) {
            return null;
        }
        return String.format("%s/%s", this.baseWsPath, fileUid);
    }

    private String fetchUrlUncached(String fileUid) {
        return this.fileDao.fileName(fileUid)
            .map(fileName -> String.format("%s/%s/%s", this.baseWsPath, fileUid, fileName))
            .orElseThrow(NotFoundException::new);
    }

    private FileData fetchUncached(String fileUid) {
        return this.fileDao.findByUid(fileUid)
            .flatMap(this.toFileData())
            .orElseThrow(NotFoundException::new);
    }

    private Optional<String> fileUrlCached(String fileUid) {
        try {
            return Optional.of(this.fileUrlCache.get(fileUid));
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
                return Optional.empty();
            }
            throw new RuntimeException(e);
        }
    }

    public Function<FileEntry, Optional<FileData>> toFileData() {
        return fileEntry -> {
            byte[] data = this.getData(fileEntry);
            if (data.length < 1) {
                return Optional.empty();
            }
            return Optional.of(
                FileData.of(
                    fileEntry.getId(),
                    fileEntry.getUid(),
                    fileEntry.getFileName(),
                    fileEntry.getFileExtension(),
                    fileEntry.getFileType(),
                    FileNameUtils.guessMimeType(fileEntry.getFileName()),
                    checksumService.hash(fileEntry.getFileName().getBytes()),
                    data
                )
            );
        };
    }

    private static class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 2621559513884831969L;
    }
}
