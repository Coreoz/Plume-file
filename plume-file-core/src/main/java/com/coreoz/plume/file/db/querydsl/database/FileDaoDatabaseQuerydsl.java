package com.coreoz.plume.file.db.querydsl.database;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileDao;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.QFileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDatabase;
import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.util.function.Function;

@Singleton
public class FileDaoDatabaseQuerydsl extends FileDao {

    @Inject
    public FileDaoDatabaseQuerydsl(TransactionManagerQuerydsl transactionManager) {
        super(transactionManager);
    }

    @Override
    public FileEntry upload(String fileType, String fileExtension, byte[] fileData) {
        return transactionManager.executeAndReturn(connection -> {
            FileEntityQuerydsl file = super.uploadEntity(fileType, fileExtension, connection);

            transactionManager
                .insert(QFileDatabaseEntityQuerydsl.fileData, connection)
                .columns(
                    QFileDatabaseEntityQuerydsl.fileData.idFile,
                    QFileDatabaseEntityQuerydsl.fileData.data
                )
                .values(file.getId(), fileData)
                .execute();

            return FileEntryDatabase.of(
                file.getId(),
                file.getUid(),
                file.getFileExtension(),
                fileType,
                fileData
            );
        });
    }

    @Override
    public boolean delete(Long id, Connection connection) {
        transactionManager
            .delete(QFileDatabaseEntityQuerydsl.fileData, connection)
            .where(QFileDatabaseEntityQuerydsl.fileData.idFile.eq(id))
            .execute();
        return super.delete(id, connection);
    }

    @Override
    public SQLQuery<Tuple> fileQuery() {
        return transactionManager.selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.uid,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.fileExtension,
                QFileDatabaseEntityQuerydsl.fileData.data
            )
            .from(QFileEntityQuerydsl.file)
            .innerJoin(QFileDatabaseEntityQuerydsl.fileData)
            .on(QFileEntityQuerydsl.file.id.eq(QFileDatabaseEntityQuerydsl.fileData.idFile));
    }

    @Override
    public Function<Tuple, FileEntry> toFileEntry() {
        return row ->
            FileEntryDatabase.of(
                row.get(QFileEntityQuerydsl.file.id),
                row.get(QFileEntityQuerydsl.file.uid),
                row.get(QFileEntityQuerydsl.file.fileExtension),
                row.get(QFileEntityQuerydsl.file.fileType),
                row.get(QFileDatabaseEntityQuerydsl.fileData.data)
            );
    }

}
