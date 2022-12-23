package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Optional;

@Singleton
public class FileCacheServiceGuava implements FileCacheService {

    private final LoadingCache<String, Optional<byte[]>> fileDataCache;
    private final LoadingCache<String, Optional<FileMetadata>> fileMetadataCache;

    @Inject
    public FileCacheServiceGuava(FileService fileService, FileDownloadConfigurationService configurationService) {
        this.fileDataCache = CacheBuilder.newBuilder()
            // TODO rendre la durée configurable
            .expireAfterAccess(Duration.ofDays(1))
            .maximumWeight(configurationService.fileCacheMaxSize())
            .weigher((String key, Optional<byte[]> fileData) -> fileData
                .map(data -> data.length)
                // Orphan keys handling
                .orElse(Integer.MAX_VALUE)
            )
            .build(CacheLoader.from(uid -> fileService.fetchData(uid)
                .map(data -> {
                    try {
                        return ByteStreams.toByteArray(data);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
            ));

        this.fileMetadataCache = CacheBuilder.newBuilder()
            // TODO rendre la durée configurable
            .expireAfterAccess(Duration.ofDays(1))
            // TODO rendre la taille configurable
            .maximumSize(1000)
            .build(CacheLoader.from(fileService::fetchMetadata));
    }

    @SneakyThrows
	@Override
	public Optional<byte[]> fetchFileData(String fileUniqueName) {
		return fileDataCache.get(fileUniqueName);
	}

    @SneakyThrows
	@Override
	public Optional<FileMetadata> fetchFileMetadata(String fileUniqueName) {
		return fileMetadataCache.get(fileUniqueName);
	}
}
