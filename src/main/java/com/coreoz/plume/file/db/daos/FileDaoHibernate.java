package com.coreoz.plume.file.db.daos;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.hibernate.TransactionManagerHibernate;
import com.coreoz.plume.db.hibernate.crud.CrudDaoHibernate;
import com.coreoz.plume.file.db.entities.FileEntity;
import com.coreoz.plume.file.db.entities.QFileEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;

@Singleton
public class FileDaoHibernate extends CrudDaoHibernate<FileEntity> implements FileDao {

	@Inject
	public FileDaoHibernate(TransactionManagerHibernate transactionManager) {
		super(QFileEntity.fileEntity, transactionManager);
	}

	@Override
	public FileEntity upload(String fileType, byte[] fileData, String fileName) {
		FileEntity file = new FileEntity()
				.setFileType(fileType)
				.setData(fileData)
				.setFilename(fileName);
		return save(file);
	}

	@Override
	public List<FileWithName> findFileNames(List<Long> fileIds) {
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.select(QFileEntity.fileEntity.id, QFileEntity.fileEntity.filename)
				.from(QFileEntity.fileEntity)
				.where(QFileEntity.fileEntity.id.in(fileIds))
				.fetch()
		)
		.stream()
		.map(row -> FileWithName.of(
			row.get(QFileEntity.fileEntity.id),
			row.get(QFileEntity.fileEntity.filename)
		))
		.collect(Collectors.toList());
	}

	@Override
	public String fileName(Long fileId) {
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.select(QFileEntity.fileEntity.filename)
				.from(QFileEntity.fileEntity)
				.where(QFileEntity.fileEntity.id.eq(fileId))
				.fetchOne()
		);
	}

	@Override
	public Long deleteUnreferenced(String fileType, EntityPathBase<?> fileEntity, NumberPath<Long> column) {
		// it would be better to left join qfileEntity to fileEntity
		// and look for row where the left part is null,
		// but does not allow to join table in a delete query :/
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.delete(QFileEntity.fileEntity)
				.where(QFileEntity.fileEntity.fileType.eq(fileType))
				.where(QFileEntity.fileEntity.id.notIn(
					JPAExpressions
						.select(column)
						.from(fileEntity)
				))
				.execute()
		);
	}

}
