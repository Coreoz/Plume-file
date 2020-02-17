package com.coreoz.plume.file.db.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.hibernate.TransactionManagerHibernate;
import com.coreoz.plume.db.hibernate.crud.CrudDaoHibernate;
import com.coreoz.plume.file.db.FileDaoDatabase;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.QFileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.database.QFileDatabaseEntityQuerydsl;
import com.google.common.base.Strings;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class FileDaoHibernate extends CrudDaoHibernate<FileEntityHibernate> implements FileDaoDatabase {

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
    public String delete(String fileUid) {
        return this.transactionManager.queryDslExecuteAndReturn
            (
                query -> {
                    Long toDelete = query.select(QFileEntityQuerydsl.file.id).where(QFileEntityQuerydsl.file.uid.eq(fileUid)).fetchFirst();

                    query.delete(QFileEntityQuerydsl.file)
                        .where(QFileEntityQuerydsl.file.id.eq(toDelete));

                    query.delete(QFileDatabaseEntityQuerydsl.fileData)
                        .where(QFileDatabaseEntityQuerydsl.fileData.idFile.eq(toDelete));

                    return fileUid;
                });
    }

	@Override
	public String fileName(String fileUid) {
		return transactionManager.queryDslExecuteAndReturn(query -> {
			Tuple tuple = query
				.select(QFileEntityHibernate.fileEntity.id, QFileEntityHibernate.fileEntity.filename)
				.from(QFileEntityHibernate.fileEntity)
				.where(QFileEntityHibernate.fileEntity.uid.eq(fileUid))
				.fetchOne();

			return tuple == null ?
				null
				: Strings.nullToEmpty(tuple.get(QFileEntityHibernate.fileEntity.filename));
		});
	}

	@Override
	public FileEntry findByUid(String fileUid) {
		return null;
	}

	@Override
	public List<String> deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
		return transactionManager.queryDslExecuteAndReturn(query ->
		{
			query
				.delete(QFileEntityHibernate.fileEntity)
				.where(QFileEntityHibernate.fileEntity.fileType.eq(fileType))
				.where(QFileEntityHibernate.fileEntity.id.notIn(
					JPAExpressions
						.select(column)
						.from(fileEntity)
				))
				.execute();
			return new ArrayList<>();
		});
	}

}
