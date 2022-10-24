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
import com.querydsl.sql.PrimaryKey;

/**
 * QFileMetadataQuerydsl is a Querydsl query type for FileMetadataQuerydsl
 */
public class QFileMetadataQuerydsl extends com.querydsl.sql.RelationalPathBase<FileMetadataQuerydsl> {

    private static final long serialVersionUID = 43454386;

    public static final QFileMetadataQuerydsl fileMetadata = new QFileMetadataQuerydsl("PLM_FILE");

    public final StringPath uniqueName = createString("uniqueName");
    public final StringPath fileOriginalName = createString("fileOriginalName");
    public final StringPath fileExtension = createString("fileExtension");
    public final StringPath fileType = createString("fileType");
    public final StringPath mimeType = createString("mimeType");
    public final StringPath checksum = createString("checksum");
    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);
    public final DateTimePath<Instant> creationDate = createDateTime("creationDate", Instant.class);

    public final PrimaryKey<FileMetadataQuerydsl> primary = createPrimaryKey(uniqueName);

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
        addMetadata(uniqueName, ColumnMetadata.named("unique_name").withIndex(1).ofType(Types.CHAR).withSize(36).notNull());
        addMetadata(fileType, ColumnMetadata.named("file_type").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(mimeType, ColumnMetadata.named("mime_type").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(fileSize, ColumnMetadata.named("file_size").withIndex(4).ofType(Types.DECIMAL).withSize(19).notNull());
        addMetadata(fileOriginalName, ColumnMetadata.named("file_original_name").withIndex(5).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(fileExtension, ColumnMetadata.named("file_extension").withIndex(6).ofType(Types.VARCHAR).withSize(5).notNull());
        addMetadata(creationDate, ColumnMetadata.named("creation_date").withIndex(7).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(checksum, ColumnMetadata.named("checksum").withIndex(8).ofType(Types.VARCHAR).withSize(255).notNull());
    }
}
