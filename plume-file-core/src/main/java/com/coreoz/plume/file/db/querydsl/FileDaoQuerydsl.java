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
public class FileDaoQuerydsl extends CrudDaoQuerydsl<FileEntityQuerydsl> implements FileDao {

	@Inject
	public FileDaoQuerydsl(TransactionManagerQuerydsl transactionManager) {
		super(transactionManager, QFileEntityQuerydsl.file);
	}

	@Override
	public FileEntry upload(String fileType, byte[] fileData, String fileName) {
		FileEntityQuerydsl file = new FileEntityQuerydsl();
		file.setFileType(fileType);
		file.setData(fileData);
		file.setFilename(fileName);

		return save(file);
	}

	@Override
	public List<FileWithName> findFileNames(List<Long> fileIds) {
		return transactionManager
			.selectQuery()
			.select(QFileEntityQuerydsl.file.id, QFileEntityQuerydsl.file.filename)
			.from(QFileEntityQuerydsl.file)
			.where(QFileEntityQuerydsl.file.id.in(fileIds))
			.fetch()
			.stream()
			.map(row -> FileWithName.of(
				row.get(QFileEntityQuerydsl.file.id),
				row.get(QFileEntityQuerydsl.file.filename)
			))
			.collect(Collectors.toList());
	}

	@Override
	public String fileName(Long fileId) {
		return transactionManager
			.selectQuery()
			.select(QFileEntityQuerydsl.file.filename)
			.from(QFileEntityQuerydsl.file)
			.where(QFileEntityQuerydsl.file.id.eq(fileId))
			.fetchOne();
	}

	@Override
	public Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
		return transactionManager
			.delete(QFileEntityQuerydsl.file)
			.where(QFileEntityQuerydsl.file.fileType.eq(fileType))
			.where(QFileEntityQuerydsl.file.id.notIn(
				SQLExpressions
					.select(column)
					.from(fileEntity)
			))
			.execute();
	}

}
