package com.coreoz.plume.file.services.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import javax.inject.Inject;

import com.coreoz.plume.file.db.querydsl.FileEntryUploaded;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.querydsl.FileDaoQuerydsl;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import com.google.common.cache.LoadingCache;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileServiceDbTest {

	@Inject
	private FileConfigurationService configurationService;

	@Inject
	private FileCacheServiceGuava fileCacheService;

	@Inject
	private ChecksumServiceSha1 checksumService;

	// testing url(Long fileId)

	@Test
	public void url__should_return_empty_if_id_file_is_null() {
		FileServiceDb fileService = new FileServiceDb(null, null, null, configurationService, fileCacheService);

		assertThat(fileService.url(null)).isEmpty();
	}

	@Test
	public void url__should_return_use_file_data_cache_if_present() {
		FileServiceDb fileService = new FileServiceDb(null, null, null, configurationService, new FileCacheService() {
			@SuppressWarnings("unchecked")
			@Override
			public LoadingCache<Long, FileData> newFileDataCache(Function<Long, FileData> loadingData) {
				return new LoadingCacheTest<Long, FileData>(fileId -> {
					if(Long.valueOf(1).equals(fileId)) {
						return FileData.of(1L, "file.ext", null, null, null, null);
					}

					return null;
				});
			}

			@Override
			public LoadingCache<Long, String> newFileUrlCache(Function<Long, String> loadingData) {
				return null;
			}
		});

		assertThat(fileService.url(1L)).hasValue("/api/files/1/file.ext");
	}

	@Test
	public void url__should_load_file_name_from_dao() {
		FileServiceDb fileService = new FileServiceDb(
			fileDaoMock(), null, null, configurationService, fileCacheService
		);

		assertThat(fileService.url(5L)).hasValue("/api/files/5/file.ext");
	}

	@Test
	public void url__should_return_raw_url_if_file_name_is_null() {
		FileServiceDb fileService = new FileServiceDb(
			fileDaoMock(), null, null, configurationService, fileCacheService
		);

		assertThat(fileService.url(4L)).hasValue("/api/files/4");
	}

	@Test
	public void url__should_return_empty_if_no_file_exists() {
		FileServiceDb fileService = new FileServiceDb(
			fileDaoMock(), null, null, configurationService, fileCacheService
		);

		assertThat(fileService.url(1L)).isEmpty();
	}

	// testing fetch(Long fileId)

	@Test
	public void fetch__should_load_file_from_dao() {
		FileServiceDb fileService = new FileServiceDb(
			fileDaoMock(), null, checksumService, configurationService, fileCacheService
		);

		assertThat(fileService.fetch(5L).map(FileData::getFilename)).hasValue("file.ext");
	}

	@Test
	public void fetch__should_return_empty_if_no_file_exists() {
		FileServiceDb fileService = new FileServiceDb(
			fileDaoMock(), null, checksumService, configurationService, fileCacheService
		);

		assertThat(fileService.fetch(1L)).isEmpty();
	}

	// utils

	private FileDao fileDaoMock() {
		return new FileDaoQuerydsl(null) {
			@Override
			public String fileName(Long fileId) {
				if(5L == fileId) {
					return "file.ext";
				}
				if(4L == fileId) {
					return "";
				}
				return null;
			}
			@Override
			public FileEntryUploaded findById(Long id) {
				if(id == 5L) {
					return FileEntryUploaded.of(
						5L,
						"file.ext",
						null,
						null
					);
				}
				return null;
			}
		};
	}

}
