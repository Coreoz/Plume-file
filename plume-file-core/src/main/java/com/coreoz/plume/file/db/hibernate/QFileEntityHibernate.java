package com.coreoz.plume.file.db.hibernate;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
public class QFileEntityHibernate extends EntityPathBase<FileEntityHibernate> {

	private static final long serialVersionUID = -1594138963L;

	public static final QFileEntityHibernate fileEntity = new QFileEntityHibernate("fileEntity");

	public final ArrayPath<byte[], Byte> data = createArray("data", byte[].class);

	public final StringPath filename = createString("filename");

	public final StringPath fileType = createString("fileType");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath uid = createString("uid");

	public QFileEntityHibernate(String variable) {
		super(FileEntityHibernate.class, forVariable(variable));
	}

	public QFileEntityHibernate(Path<? extends FileEntityHibernate> path) {
		super(path.getType(), path.getMetadata());
	}

	public QFileEntityHibernate(PathMetadata metadata) {
		super(FileEntityHibernate.class, metadata);
	}

}
