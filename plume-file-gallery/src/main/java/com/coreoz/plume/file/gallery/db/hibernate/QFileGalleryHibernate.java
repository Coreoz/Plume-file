package com.coreoz.plume.file.gallery.db.hibernate;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QFileGalleryHibernate extends EntityPathBase<FileGalleryHibernate> {

	private static final long serialVersionUID = -1594138963L;

	public static final QFileGalleryHibernate fileGallery = new QFileGalleryHibernate("fileGallery");

	public final StringPath galleryType = createString("galleryType");

	public final NumberPath<Long> idData = createNumber("idData", Long.class);

	public final NumberPath<Long> idFile = createNumber("idFile", Long.class);

	public final NumberPath<Integer> position = createNumber("position", Integer.class);

	public QFileGalleryHibernate(String variable) {
		super(FileGalleryHibernate.class, forVariable(variable));
	}

	public QFileGalleryHibernate(Path<? extends FileGalleryHibernate> path) {
		super(path.getType(), path.getMetadata());
	}

	public QFileGalleryHibernate(PathMetadata metadata) {
		super(FileGalleryHibernate.class, metadata);
	}

}
