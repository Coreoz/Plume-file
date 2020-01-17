package com.coreoz.plume.file.db.querydsl.disk;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QFileEntityDiskQuerydsl extends com.querydsl.sql.RelationalPathBase<FileEntityDiskQuerydsl>{

    private static final long serialVersionUID = 434543878;

    public static final QFileEntityDiskQuerydsl fileDisk = new QFileEntityDiskQuerydsl("PLM_FILE_DISK");

    public final StringPath path = createString("path");

    public final NumberPath<Long> idFile = createNumber("idFile", Long.class);

    public final com.querydsl.sql.ForeignKey<com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl> plmFileFk = createForeignKey(idFile, "ID");

    public QFileEntityDiskQuerydsl(String variable) {
        super(FileEntityDiskQuerydsl.class, forVariable(variable), "PUBLIC", "PLM_FILE_DISK");
        addMetadata();
    }

    public QFileEntityDiskQuerydsl(String variable, String schema, String table) {
        super(FileEntityDiskQuerydsl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFileEntityDiskQuerydsl(String variable, String schema) {
        super(FileEntityDiskQuerydsl.class, forVariable(variable), schema, "PLM_FILE_DISK");
        addMetadata();
    }

    public QFileEntityDiskQuerydsl(Path<? extends FileEntityDiskQuerydsl> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PLM_FILE_DISK");
        addMetadata();
    }

    public QFileEntityDiskQuerydsl(PathMetadata metadata) {
        super(FileEntityDiskQuerydsl.class, metadata, "PUBLIC", "PLM_FILE_DISK");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(path, ColumnMetadata.named("PATH").withIndex(1).ofType(Types.VARCHAR).withSize(255));
        addMetadata(idFile, ColumnMetadata.named("ID_FILE").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }
}
