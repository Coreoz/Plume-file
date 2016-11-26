package com.coreoz.plume.file.gallery.services.gallery;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.services.gallery.data.FileGallery;
import com.coreoz.plume.file.gallery.services.gallery.data.FileGalleryPosition;
import com.coreoz.plume.file.gallery.services.gallerytype.FileGalleryType;
import com.coreoz.plume.file.gallery.services.gallerytype.FileGalleryTypesProvider;
import com.coreoz.plume.file.services.file.FileService;
import com.coreoz.plume.file.services.file.data.FileUploaded;

@Singleton
public class FileGalleryServiceDb implements FileGalleryService {

	private static final Logger logger = LoggerFactory.getLogger(FileGalleryServiceDb.class);

	private final FileGalleryDao fileGalleryDao;
	private final FileService fileService;
	private final FileGalleryTypesProvider fileGalleryTypesProvider;

	@Inject
	public FileGalleryServiceDb(FileGalleryDao fileGalleryDao, FileService fileService,
			FileGalleryTypesProvider fileGalleryTypesProvider) {
		this.fileGalleryDao = fileGalleryDao;
		this.fileService = fileService;
		this.fileGalleryTypesProvider = fileGalleryTypesProvider;
	}

	@Override
	public FileGallery add(FileUploaded fileUploaded, FileGalleryType galleryType, int initialPosition,
			Long idData) {
		fileGalleryDao.insert(fileUploaded.getId(), galleryType.name(), initialPosition, idData);
		return FileGallery.of(
			fileUploaded.getId(),
			fileUploaded.getUrl(),
			idData,
			initialPosition
		);
	}

	@Override
	public void updatePositions(Collection<? extends FileGalleryPosition> newPositions) {
		if(!newPositions.isEmpty()) {
			fileGalleryDao.updatePositions(newPositions);
		}
	}

	@Override
	public void deleteFile(Long idFile) {
		fileGalleryDao.deleteFile(idFile);
	}

	@Override
	public void deleteUnreferenced() {
		long countDeleted = fileGalleryTypesProvider
			.fileGalleryTypesAvailable()
			.stream()
			.filter(fileType -> fileType.getFileGalleryDataEntity() != null)
			.mapToLong(fileType ->
				fileGalleryDao.deleteUnreferenced(
					fileType.name(),
					fileType.getFileGalleryDataEntity(),
					fileType.getJoinColumn()
				)
			)
			.sum();

		if(countDeleted > 0) {
			logger.debug("{} unreferenced files deleted", countDeleted);
		}
	}

	@Override
	public List<FileGallery> fetch(FileGalleryType galleryType, Long idData) {
		return fileGalleryDao
			.fetch(galleryType.name(), idData)
			.stream()
			.map(fileGalleryRaw -> FileGallery.of(
				fileGalleryRaw.getIdFile(),
				fileService
					.url(fileGalleryRaw.getIdFile())
					.orElse(null),
				idData,
				fileGalleryRaw.getPosition()
			))
			.filter(fileGallery -> fileGallery.getFileUrl() != null)
			.collect(Collectors.toList());
	}

	@Override
	public boolean checkFilesInGallery(Collection<Long> fileIds, FileGalleryType galleryType, Long idData) {
		if(fileIds.isEmpty()) {
			return true;
		}

		return fileGalleryDao.checkFilesInGallery(fileIds, galleryType.name(), idData);
	}

}
