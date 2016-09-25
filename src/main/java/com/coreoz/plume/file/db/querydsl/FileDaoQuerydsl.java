package com.coreoz.plume.file.db.querydsl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.querydsl.crud.CrudDaoQuerydsl;
import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.FileWithName;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;

@Singleton
public class FileDaoQuerydsl extends CrudDaoQuerydsl<FileEntity> implements FileDao {

	@Inject
	public FileDaoQuerydsl(TransactionManagerQuerydsl transactionManager) {
		super(transactionManager, QFileEntity.file);
	}

	@Override
	public FileEntry upload(String fileType, byte[] fileData, String fileName) {
		FileEntity file = new FileEntity();
		file.setFileType(fileType);
		file.setData(fileData);
		file.setFilename(fileName);

		return save(file);
	}

	@Override
	public List<FileWithName> findFileNames(List<Long> fileIds) {
		return transactionManager
			.selectQuery()
			.select(QFileEntity.file.id, QFileEntity.file.filename)
			.from(QFileEntity.file)
			.where(QFileEntity.file.id.in(fileIds))
			.fetch()
			.stream()
			.map(row -> FileWithName.of(
				row.get(QFileEntity.file.id),
				row.get(QFileEntity.file.filename)
			))
			.collect(Collectors.toList());
	}

	@Override
	public String fileName(Long fileId) {
		return transactionManager
			.selectQuery()
			.select(QFileEntity.file.filename)
			.from(QFileEntity.file)
			.where(QFileEntity.file.id.eq(fileId))
			.fetchOne();
	}

	@Override
	public Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
		return transactionManager
			.delete(QFileEntity.file)
			.where(QFileEntity.file.fileType.eq(fileType))
			.where(QFileEntity.file.id.notIn(
				SQLExpressions
					.select(column)
					.from(fileEntity)
			))
			.execute();
	}

}
