package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.SneakyThrows;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Singleton
public class FileCacheServiceGuava implements FileCacheService {

    private final LoadingCache<String, byte[]> fileDataCache;
    private final LoadingCache<String, FileMetadata> fileMetadataCache;

    @Inject
    public FileCacheServiceGuava(FileService fileService, FileDownloadConfigurationService configurationService) {
        this.fileDataCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .maximumWeight(configurationService.fileCacheMaxSizeInMb() * 1000000)
            .weigher((Weigher<String, byte[]>) (k, g) -> g.length)
            .build(CacheLoader.from(optionalLoaderToExceptionLoader(
                uid -> fileService.fetchData(uid)
                    .flatMap(data -> {
                        try {
                            return Optional.of(ByteStreams.toByteArray(data));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
            )));

        this.fileMetadataCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .maximumSize(1000)
            .build(CacheLoader.from(optionalLoaderToExceptionLoader(fileService::fetchMetadata)));
    }

	@Override
	public Optional<byte[]> fetchFileData(String fileUniqueName) {
		return fetch(fileDataCache, fileUniqueName);
	}

	@Override
	public Optional<FileMetadata> fetchFileMetadata(String fileUniqueName) {
		return fetch(fileMetadataCache, fileUniqueName);
	}

	private <K, V> com.google.common.base.Function<K, V> optionalLoaderToExceptionLoader(
			Function<K, Optional<V>> fileUrlLoader) {
		return fileUrlLoader.andThen(result -> result.orElseThrow(NotFoundException::new))::apply;
	}

	@SneakyThrows
    private <K, V> Optional<V> fetch(LoadingCache<K, V> cache, K cacheKey) {
		Preconditions.checkNotNull(cache, "Cache has not been initialized");
        if (cacheKey == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(cache.get(cacheKey));
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (e instanceof UncheckedExecutionException && e.getCause() instanceof NotFoundException) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private static class NotFoundException extends RuntimeException {
        private static final long serialVersionUID = 2621559513884831969L;
    }
}
