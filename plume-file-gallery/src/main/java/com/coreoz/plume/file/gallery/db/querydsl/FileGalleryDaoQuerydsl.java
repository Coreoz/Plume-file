package com.coreoz.plume.file.gallery.db.querydsl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.gallery.db.FileGalleryDao;
import com.coreoz.plume.file.gallery.db.FileGalleryRaw;
import com.coreoz.plume.file.gallery.services.gallery.FileGalleryPosition;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;

@Singleton
public class FileGalleryDaoQuerydsl implements FileGalleryDao {

	private final TransactionManagerQuerydsl transactionManager;

	public FileGalleryDaoQuerydsl(TransactionManagerQuerydsl transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public void insert(long idFile, String galleryType, int initialPosition, Long idData) {
		transactionManager
			.insert(QFileGallerydsl.fileGallery)
			.columns(
				QFileGallerydsl.fileGallery.idFile,
				QFileGallerydsl.fileGallery.idData,
				QFileGallerydsl.fileGallery.galleryType,
				QFileGallerydsl.fileGallery.position
			)
			.values(idFile, idData, galleryType, initialPosition)
			.execute();
	}

	@Override
	public void updatePositions(Iterable<FileGalleryPosition> newPositions) {
		SQLUpdateClause updateQuery = transactionManager.update(QFileGallerydsl.fileGallery);

		for(FileGalleryPosition newPosition : newPositions) {
			updateQuery
				.set(QFileGallerydsl.fileGallery.position, newPosition.getPosition())
				.where(QFileGallerydsl.fileGallery.idFile.eq(newPosition.getIdFile()))
				.addBatch();
		}

		updateQuery.execute();
	}

	@Override
	public List<FileGalleryRaw> fetch(String galleryType, Long idData) {
		SQLQuery<Tuple> selectQuery = transactionManager
			.selectQuery()
			.select(QFileGallerydsl.fileGallery.idFile, QFileGallerydsl.fileGallery.position)
			.from(QFileGallerydsl.fileGallery)
			.where(QFileGallerydsl.fileGallery.galleryType.eq(galleryType));

		if(idData != null) {
			selectQuery.where(QFileGallerydsl.fileGallery.idData.eq(idData));
		}

		return selectQuery
			.fetch()
			.stream()
			.map(tuple -> {
				FileGalleryQuerydsl fileGallery = new FileGalleryQuerydsl();
				fileGallery.setIdFile(tuple.get(QFileGallerydsl.fileGallery.idFile));
				fileGallery.setPosition(tuple.get(QFileGallerydsl.fileGallery.position));

				return fileGallery;
			})
			.collect(Collectors.toList());
	}

	@Override
	public boolean checkFilesInGallery(Collection<Long> fileIds, String galleryType, Long idData) {
		SQLQuery<Integer> selectQuery = transactionManager
			.selectQuery()
			.select(SQLExpressions.selectOne())
			.from(QFileGallerydsl.fileGallery)
			.where(QFileGallerydsl.fileGallery.idFile.in(fileIds))
			.where(QFileGallerydsl.fileGallery.galleryType.eq(galleryType).not());

		if(idData != null) {
			selectQuery.where(QFileGallerydsl.fileGallery.idData.eq(idData).not());
		}

		return selectQuery.fetchOne() == null;
	}

	@Override
	public Long deleteUnreferenced(String galleryType, EntityPath<?> fileGalleryDataEntity,
			NumberPath<Long> joinColumn) {
		return transactionManager
			.delete(QFileGallerydsl.fileGallery)
			.where(QFileGallerydsl.fileGallery.galleryType.eq(galleryType))
			.where(QFileGallerydsl.fileGallery.idData.notIn(
				SQLExpressions
					.select(joinColumn)
					.from(fileGalleryDataEntity)
			))
			.execute();
	}

}
