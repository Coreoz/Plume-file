package com.coreoz.plume.file.db.querydsl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.querydsl.crud.CrudDaoQuerydsl;
import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.FileEntry;
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
