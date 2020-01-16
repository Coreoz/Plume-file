package com.coreoz.plume.file.db.querydsl.database;


import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QFileDatabaseEntityQuerydsl extends com.querydsl.sql.RelationalPathBase<FileDatabaseEntityQuerydsl>{

    private static final long serialVersionUID = 43454386;

    public static final QFileDatabaseEntityQuerydsl fileData = new QFileDatabaseEntityQuerydsl("PLM_FILE_DATA");

    public final SimplePath<byte[]> data = createSimple("data", byte[].class);

    public final NumberPath<Long> idFile = createNumber("idFile", Long.class);

    public final com.querydsl.sql.ForeignKey<com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl> plmFileFk = createForeignKey(idFile, "ID");

    public QFileDatabaseEntityQuerydsl(String variable) {
        super(FileDatabaseEntityQuerydsl.class, forVariable(variable), "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDatabaseEntityQuerydsl(String variable, String schema, String table) {
        super(FileDatabaseEntityQuerydsl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFileDatabaseEntityQuerydsl(String variable, String schema) {
        super(FileDatabaseEntityQuerydsl.class, forVariable(variable), schema, "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDatabaseEntityQuerydsl(Path<? extends FileDatabaseEntityQuerydsl> path) {
        super(path.getType(), path.getMetadata(), "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDatabaseEntityQuerydsl(PathMetadata metadata) {
        super(FileDatabaseEntityQuerydsl.class, metadata, "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(this.data, ColumnMetadata.named("DATA").withIndex(4).ofType(2004).withSize(2147483647).notNull());
        addMetadata(idFile, ColumnMetadata.named("ID_FILE").withIndex(2).ofType(Types.DECIMAL).withSize(19).notNull());
    }
}
