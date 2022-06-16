package com.coreoz.plume.file.db.beans;


import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.sql.Types;
import java.time.Instant;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

public class QFileMetadataQuerydsl extends com.querydsl.sql.RelationalPathBase<FileMetadataQuerydsl>{

    private static final long serialVersionUID = 43454386;

    public static final QFileMetadataQuerydsl fileMetadata = new QFileMetadataQuerydsl("PLM_FILE");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final StringPath uniqueName = createString("unique_name");
    public final StringPath fileType = createString("file_type");
    public final StringPath mimeType = createString("mime_type");
    public final NumberPath<Long> fileSize = createNumber("file_size", Long.class);
    public final DateTimePath<Instant> creationDate = createDateTime("creation_date", Instant.class);

    public QFileMetadataQuerydsl(String variable) {
        super(FileMetadataQuerydsl.class, forVariable(variable), "PLM", "PLM_FILE");
        addMetadata();
    }

    public QFileMetadataQuerydsl(String variable, String schema, String table) {
        super(FileMetadataQuerydsl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFileMetadataQuerydsl(String variable, String schema) {
        super(FileMetadataQuerydsl.class, forVariable(variable), schema, "PLM_FILE");
        addMetadata();
    }

    public QFileMetadataQuerydsl(Path<? extends FileMetadataQuerydsl> path) {
        super(path.getType(), path.getMetadata(), "PLM", "PLM_FILE");
        addMetadata();
    }

    public QFileMetadataQuerydsl(PathMetadata metadata) {
        super(FileMetadataQuerydsl.class, metadata, "PLM", "PLM_FILE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.DECIMAL).withSize(19).notNull());
        addMetadata(uniqueName, ColumnMetadata.named("unique_name").withIndex(2).ofType(Types.CHAR).withSize(36).notNull());
        addMetadata(fileType, ColumnMetadata.named("file_type").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(mimeType, ColumnMetadata.named("mime_type").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(fileSize, ColumnMetadata.named("file_size").withIndex(5).ofType(Types.DECIMAL).withSize(19).notNull());
        addMetadata(creationDate, ColumnMetadata.named("creation_date").withIndex(6).ofType(Types.TIMESTAMP).withSize(19).notNull());
    }
}
