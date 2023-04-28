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
import java.util.Optional;

@Singleton
public class FileCacheServiceGuava implements FileCacheService {

    private final LoadingCache<String, Optional<byte[]>> fileDataCache;
    private final LoadingCache<String, Optional<FileMetadata>> fileMetadataCache;

    @Inject
    public FileCacheServiceGuava(FileService fileService, FileDownloadConfigurationService configurationService) {
        this.fileDataCache = CacheBuilder.newBuilder()
            .expireAfterAccess(configurationService.fileDataCacheExpiresAfterAccessDuration())
            .maximumWeight(configurationService.fileDataCacheMaximumSizeInBytes())
            .weigher((String key, Optional<byte[]> fileData) -> fileData
                .map(data -> data.length)
                // Orphan keys handling, this case will almost never happen because the metadata will be fetched first
                .orElse(key.length())
            )
            .build(CacheLoader.from(fileUniqueName -> fileService.fetchData(fileUniqueName)
                .map(data -> {
                    try {
                        return ByteStreams.toByteArray(data);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
            ));

        this.fileMetadataCache = CacheBuilder.newBuilder()
            .expireAfterAccess(configurationService.fileMetadataCacheExpiresAfterAccessDuration())
            .maximumSize(configurationService.fileMetadataCacheMaximumSize())
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
