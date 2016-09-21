package com.coreoz.plume.file.db.daos;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.querydsl.crud.CrudDaoQuerydsl;
import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.generated.File;
import com.coreoz.plume.file.db.generated.QFile;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.sql.RelationalPathBase;

@Singleton
public class FileDaoHibernate extends CrudDaoQuerydsl<File> implements FileDao {

	@Inject
	public FileDaoHibernate(TransactionManagerQuerydsl transactionManagerQuerydsl) {
		super(transactionManagerQuerydsl, QFile.file);
	}

	@Override
	public File upload(String fileType, byte[] fileData, String fileName) {
		File file = new File();
		file.setFileType(fileType);
		file.setData(fileData);
		file.setFilename(fileName);
		return save(file);
	}

	@Override
	public List<FileWithName> findFileNames(List<Long> fileIds) {
		return transactionManagerQuerydsl
				.selectQuery()
				.select(QFile.file.id, QFile.file.filename)
				.from(QFile.file)
				.where(QFile.file.id.in(fileIds))
				.fetch()
				.stream()
				.map(row -> FileWithName.of(
					row.get(QFile.file.id),
					row.get(QFile.file.filename)
				))
				.collect(Collectors.toList());
	}

	@Override
	public String fileName(Long fileId) {
		return transactionManagerQuerydsl
			.selectQuery()
			.select(QFile.file.filename)
			.from(QFile.file)
			.where(QFile.file.id.eq(fileId))
			.fetchOne();
	}

	@Override
	public Long deleteUnreferenced(String fileType, RelationalPathBase<?> relationalPathBase, NumberPath<Long> column) {
		// it would be better to left join qfileEntity to fileEntity
		// and look for row where the left part is null,
		// but does not allow to join table in a delete query :/
		
		return transactionManagerQuerydsl
				.delete(QFile.file)
				.where(QFile.file.fileType.eq(fileType))
				.where(QFile.file.id.notIn(
					JPAExpressions
						.select(column)
						.from(relationalPathBase)
				))
				.execute();
	}

}
