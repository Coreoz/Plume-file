package com.coreoz.plume.file.db.querydsl.disk;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileDao;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.QFileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Function;

@Singleton
public class FileDaoDiskQuerydsl extends FileDao {

    @Inject
    public FileDaoDiskQuerydsl(TransactionManagerQuerydsl transactionManager) {
        super(transactionManager);
    }

    @Override
    public FileEntryDisk upload(String fileType, String fileExtension, @Nullable byte[] fileData) {
        return transactionManager.executeAndReturn(connection -> {
            FileEntityQuerydsl file = super.uploadEntity(fileType, fileExtension, connection);

            return FileEntryDisk.of(
                file.getId(),
                file.getUid(),
                file.getFileExtension(),
                file.getFileType()
            );
        });
    }

    @Override
    public SQLQuery<Tuple> fileQuery() {
        return transactionManager
            .selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.uid,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.fileExtension
            ).from(QFileEntityQuerydsl.file);
    }

    @Override
    public Function<Tuple, FileEntry> toFileEntry() {
        return file -> FileEntryDisk.of(
            file.get(QFileEntityQuerydsl.file.id),
            file.get(QFileEntityQuerydsl.file.uid),
            file.get(QFileEntityQuerydsl.file.fileExtension),
            file.get(QFileEntityQuerydsl.file.fileType)
        );
    }
}
