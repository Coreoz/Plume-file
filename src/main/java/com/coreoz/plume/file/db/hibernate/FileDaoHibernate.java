package com.coreoz.plume.file.db.hibernate;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.hibernate.TransactionManagerHibernate;
import com.coreoz.plume.db.hibernate.crud.CrudDaoHibernate;
import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.FileWithName;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;

@Singleton
public class FileDaoHibernate extends CrudDaoHibernate<FileEntityHibernate> implements FileDao {

	@Inject
	public FileDaoHibernate(TransactionManagerHibernate transactionManager) {
		super(QFileEntityHibernate.fileEntity, transactionManager);
	}

	@Override
	public FileEntityHibernate upload(String fileType, byte[] fileData, String fileName) {
		FileEntityHibernate file = new FileEntityHibernate()
				.setFileType(fileType)
				.setData(fileData)
				.setFilename(fileName);
		return save(file);
	}

	@Override
	public List<FileWithName> findFileNames(List<Long> fileIds) {
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.select(QFileEntityHibernate.fileEntity.id, QFileEntityHibernate.fileEntity.filename)
				.from(QFileEntityHibernate.fileEntity)
				.where(QFileEntityHibernate.fileEntity.id.in(fileIds))
				.fetch()
		)
		.stream()
		.map(row -> FileWithName.of(
			row.get(QFileEntityHibernate.fileEntity.id),
			row.get(QFileEntityHibernate.fileEntity.filename)
		))
		.collect(Collectors.toList());
	}

	@Override
	public String fileName(Long fileId) {
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.select(QFileEntityHibernate.fileEntity.filename)
				.from(QFileEntityHibernate.fileEntity)
				.where(QFileEntityHibernate.fileEntity.id.eq(fileId))
				.fetchOne()
		);
	}

	@Override
	public Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
		return transactionManager.queryDslExecuteAndReturn(query ->
			query
				.delete(QFileEntityHibernate.fileEntity)
				.where(QFileEntityHibernate.fileEntity.fileType.eq(fileType))
				.where(QFileEntityHibernate.fileEntity.id.notIn(
					JPAExpressions
						.select(column)
						.from(fileEntity)
				))
				.execute()
		);
	}

}
