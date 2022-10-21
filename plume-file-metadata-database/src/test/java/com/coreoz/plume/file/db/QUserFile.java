package com.coreoz.plume.file.db;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QUserFile extends com.querydsl.sql.RelationalPathBase<UserFile> {

    private static final long serialVersionUID = 43454386;

    public static final QUserFile userFile = new QUserFile("plm_user_file");

    public final StringPath uniqueName = createString("uniqueName");
    public final NumberPath<Long> userId = createNumber("fileOriginalName", Long.class);

    public QUserFile(String variable) {
        super(UserFile.class, forVariable(variable), "plm", "plm_user_file");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(uniqueName, ColumnMetadata.named("unique_name").withIndex(1).ofType(Types.CHAR).withSize(36).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    }
}
