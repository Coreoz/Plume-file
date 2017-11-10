package com.coreoz.plume.file.db.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.sql.Types;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

public class QFileEntityQuerydsl extends com.querydsl.sql.RelationalPathBase<FileEntityQuerydsl> {

	private static final long serialVersionUID = 117412621;

	public static final QFileEntityQuerydsl file = new QFileEntityQuerydsl("PLM_FILE");

	public final StringPath filename = createString("filename");

	public final StringPath fileType = createString("fileType");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final com.querydsl.sql.PrimaryKey<FileEntityQuerydsl> constraintB = createPrimaryKey(id);

	public QFileEntityQuerydsl(String variable) {
		super(FileEntityQuerydsl.class, forVariable(variable), "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public QFileEntityQuerydsl(String variable, String schema, String table) {
		super(FileEntityQuerydsl.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QFileEntityQuerydsl(Path<? extends FileEntityQuerydsl> path) {
		super(path.getType(), path.getMetadata(), "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public QFileEntityQuerydsl(PathMetadata metadata) {
		super(FileEntityQuerydsl.class, metadata, "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public void addMetadata() {
		addMetadata(filename, ColumnMetadata.named("FILENAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
		addMetadata(fileType, ColumnMetadata.named("FILE_TYPE").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
		addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
	}

}
