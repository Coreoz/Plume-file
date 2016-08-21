package com.coreoz.plume.file.db.daos;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.coreoz.plume.db.TransactionManager;
import com.coreoz.plume.db.crud.CrudDao;
import com.coreoz.plume.file.db.entities.FileEntity;
import com.coreoz.plume.file.db.entities.QFileEntity;

@Singleton
public class FileDaoHibernate extends CrudDao<FileEntity> implements FileDao{

	@Inject
	public FileDaoHibernate(TransactionManager transactionManager) {
		super(QFileEntity.fileEntity, transactionManager);
	}

	@Override
	public FileEntity upload(byte[] fileData, String fileName, EntityManager transaction) {
		FileEntity file = new FileEntity()
				.setData(fileData)
				.setFilename(fileName);
		return transaction == null ?
			save(file)
			: save(file, transaction);
	}

	@Override
	public void delete(Long id, EntityManager em) {
		if(em == null) {
			delete(id);
		} else {
			super.delete(id, em);
		}
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

}
